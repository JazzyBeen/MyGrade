package com.android.mygrade.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.android.mygrade.data.local.SubjectDao;
import com.android.mygrade.data.local.entity.SubjectEntity;
import com.android.mygrade.data.remote.GoogleSheetsApi;
import com.android.mygrade.data.remote.dto.SheetResponse;
import com.android.mygrade.domain.model.Subject;
import com.android.mygrade.domain.repository.SubjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import retrofit2.Response;

public class SubjectRepositoryImpl implements SubjectRepository {

    private final SubjectDao subjectDao;
    private final GoogleSheetsApi api;
    private final ExecutorService executor;
    private static final String API_KEY = "YOUR_API";

    @Inject
    public SubjectRepositoryImpl(SubjectDao subjectDao, GoogleSheetsApi api) {
        this.subjectDao = subjectDao;
        this.api = api;
        this.executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public LiveData<List<Subject>> getAllSubjects() {
        return Transformations.map(subjectDao.getAllSubjects(), entities -> {
            List<Subject> subjects = new ArrayList<>();
            for (SubjectEntity entity : entities) {
                subjects.add(entity.toDomainModel());
            }
            return subjects;
        });
    }

    @Override
    public void insertSubject(Subject subject) {

        executor.execute(() -> {
            android.util.Log.d("MyGrade_DB", "Inserting: " + subject.getName());
            subjectDao.insert(SubjectEntity.fromDomainModel(subject));
        });
    }

    @Override
    public void deleteSubject(Subject subject) {
        executor.execute(() -> subjectDao.delete(SubjectEntity.fromDomainModel(subject)));
    }

    @Override
    public void updateSubject(Subject subject) {
        executor.execute(() -> subjectDao.update(SubjectEntity.fromDomainModel(subject)));
    }

    @Override
    public void syncGrades(SubjectRepository.SyncCallback callback) {
        executor.execute(() -> {
            try {
                List<SubjectEntity> entities = subjectDao.getAllSubjectsSync();

                if (entities.isEmpty()) {
                    callback.onSuccess();
                    return;
                }

                boolean hasErrors = false;

                for (SubjectEntity entity : entities) {
                    String sheetId = extractIdFromUrl(entity.sheetUrl);
                    if (sheetId == null) {
                        markAsError(entity);
                        hasErrors = true;
                        continue;
                    }

                    String range = entity.sheetName + "!" + entity.column + entity.row;

                    Response<SheetResponse> response = api.getCell(sheetId, range, API_KEY).execute();

                    if (response.isSuccessful() && response.body() != null && response.body().values != null && !response.body().values.isEmpty()) {
                        String newValue = response.body().values.get(0).get(0);
                        entity.currentValue = newValue;
                        subjectDao.update(entity);
                    } else {
                        android.util.Log.e("MyGrade_API", "Ошибка запроса. Код: " + response.code());
                        if (response.errorBody() != null) {
                            android.util.Log.e("MyGrade_API", "Текст ошибки от Google: " + response.errorBody().string());
                        }
                        android.util.Log.e("MyGrade_API", "Sheet ID: " + sheetId + ", Range: " + range);

                        markAsError(entity);
                        hasErrors = true;
                    }
                }

                if (hasErrors) {
                    callback.onError("Некоторые предметы не удалось обновить. Проверьте ссылки.");
                } else {
                    callback.onSuccess();
                }

            } catch (Exception e) {
                callback.onError("Ошибка сети: " + e.getLocalizedMessage());
            }
        });
    }

    private void markAsError(SubjectEntity entity) {
        entity.currentValue = "Error";
        subjectDao.update(entity);
    }

    private String extractIdFromUrl(String url) {
        if (url == null) return null;
        Pattern pattern = Pattern.compile("/spreadsheets/d/([a-zA-Z0-9-_]+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
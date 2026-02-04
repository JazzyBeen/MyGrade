package com.android.mygrade;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SheetUpdateWorker extends Worker {

    private static final String TAG = "SheetUpdateWorker";
    private final Context context;
    private final AppDatabase database;
    private static final String API_KEY = "AIzaSyDBSknTVisowI6Tf7rirm7c96wKc_yB7HE";  // Замените на ваш ключ

    public SheetUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        this.database = AppDatabase.getDatabase(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        List<Subject> subjects = database.subjectDao().getAllSubjectsSync();
        RequestQueue queue = Volley.newRequestQueue(context);

        if (subjects.isEmpty()) {
            return Result.success();
        }

        for (Subject subject : subjects) {
            String spreadsheetId = extractSheetIdFromUrl(subject.sheetUrl);
            if (spreadsheetId == null) {
                updateSubjectWithError(subject);
                continue;
            }

            String range = subject.sheetName + "!" + subject.column + subject.row;
            String url = String.format("https://sheets.googleapis.com/v4/spreadsheets/%s/values/%s?key=%s",
                    spreadsheetId, range, API_KEY);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            JSONArray values = response.getJSONArray("values");
                            if (values.length() > 0 && values.getJSONArray(0).length() > 0) {
                                String value = values.getJSONArray(0).getString(0);
                                subject.currentValue = value;
                                new Thread(() -> database.subjectDao().update(subject)).start();
                            } else {
                                updateSubjectWithError(subject);
                            }
                        } catch (JSONException e) {
                            updateSubjectWithError(subject);
                        }
                    },
                    error -> updateSubjectWithError(subject));

            queue.add(jsonObjectRequest);
        }
        return Result.success();
    }

    private void updateSubjectWithError(Subject subject) {
        subject.currentValue = "Error";
        new Thread(() -> database.subjectDao().update(subject)).start();
    }

    private String extractSheetIdFromUrl(String url) {
        Pattern pattern = Pattern.compile("/spreadsheets/d/([a-zA-Z0-9-_]+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
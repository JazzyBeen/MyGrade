package com.android.mygrade.domain.repository;

import androidx.lifecycle.LiveData;
import com.android.mygrade.domain.model.Subject;
import java.util.List;


public interface SubjectRepository {
    LiveData<List<Subject>> getAllSubjects();
    void insertSubject(Subject subject);
    void deleteSubject(Subject subject);
    void updateSubject(Subject subject);
    void syncGrades(syncCallback callback);

    interface syncCallback {
        void onSuccess();
        void onError(String error);
    }
}

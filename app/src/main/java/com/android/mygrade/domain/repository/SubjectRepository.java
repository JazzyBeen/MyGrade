package com.android.mygrade.domain.repository;

import androidx.lifecycle.LiveData;
import com.android.mygrade.domain.model.Subject;
import java.util.List;

import javax.security.auth.callback.Callback;

public interface SubjectRepository {
    LiveData<List<Subject>> getAllSubjects();
    void insertSubject(Subject subject);
    void deleteSubject(Subject subject);
    void updateSubject(Subject subject);
    void syncGrades(Callback callback);
}

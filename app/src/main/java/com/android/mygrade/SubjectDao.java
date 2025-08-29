package com.android.mygrade;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface SubjectDao {
    @Insert
    void insert(Subject subject);

    @Update
    void update(Subject subject);

    @Delete
    void delete(Subject subject);

    @Query("SELECT * FROM subjects ORDER BY id DESC")
    LiveData<List<Subject>> getAllSubjects();

    @Query("SELECT * FROM subjects")
    List<Subject> getAllSubjectsSync();

    @Query("SELECT * FROM subjects WHERE id = :subjectId")
    LiveData<Subject> getSubjectById(int subjectId);
}
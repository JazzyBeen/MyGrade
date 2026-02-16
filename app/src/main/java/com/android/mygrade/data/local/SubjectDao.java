package com.android.mygrade.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


import com.android.mygrade.data.local.entity.SubjectEntity;

import java.util.List;


@Dao
public interface SubjectDao {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insert(SubjectEntity subject);

    @Update
    void update(SubjectEntity subject);

    @Delete
    void delete(SubjectEntity subject);

    @Query("SELECT * FROM subjects ORDER BY id DESC")
    LiveData<List<SubjectEntity>> getAllSubjects();
    @Query("SELECT * from subjects")
    List<SubjectEntity> getAllSubjectsSync();
}

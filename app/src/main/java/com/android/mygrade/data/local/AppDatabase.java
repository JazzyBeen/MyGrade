package com.android.mygrade.data.local;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.android.mygrade.data.local.entity.SubjectEntity;
import com.android.mygrade.domain.model.Subject;

@Database(entities = {SubjectEntity.class},version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase{
    public abstract SubjectDao subjectDao();
}
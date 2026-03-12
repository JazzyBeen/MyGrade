package com.android.mygrade.di;

import android.content.Context;

import androidx.room.Room;

import com.android.mygrade.data.local.AppDatabase;
import com.android.mygrade.data.local.SubjectDao;
import com.android.mygrade.data.remote.GoogleSheetsApi;
import com.android.mygrade.data.repository.SubjectRepositoryImpl;
import com.android.mygrade.domain.repository.SubjectRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    @Singleton
    public AppDatabase provideDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "mygrade_db")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    public SubjectDao provideSubjectDao(AppDatabase database) {
        return database.subjectDao();
    }

    @Provides
    @Singleton
    public GoogleSheetsApi provideGoogleSheetsApi() {
        return new Retrofit.Builder()
                .baseUrl("https://sheets.googleapis.com/v4/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GoogleSheetsApi.class);
    }

    @Provides
    @Singleton
    public SubjectRepository provideSubjectRepository(SubjectDao dao, GoogleSheetsApi api) {
        return new SubjectRepositoryImpl(dao, api);
    }
}
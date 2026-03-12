package com.android.mygrade.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.mygrade.domain.model.Subject;
import com.android.mygrade.domain.repository.SubjectRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends ViewModel {

    private final SubjectRepository repository;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    @Inject
    public MainViewModel(SubjectRepository repository) {
        this.repository = repository;
    }

    public LiveData<java.util.List<Subject>> getSubjects() {
        return repository.getAllSubjects();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void addSubject(Subject subject) {
        repository.insertSubject(subject);
        refreshData();
    }

    public void updateSubject(Subject subject) {
        repository.updateSubject(subject);
        refreshData();
    }

    public void deleteSubject(Subject subject) {
        repository.deleteSubject(subject);
    }

    public void refreshData() {
        isLoading.postValue(true);
        repository.syncGrades(new SubjectRepository.SyncCallback() {
            @Override
            public void onSuccess() {
                isLoading.postValue(false);
            }

            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }
}
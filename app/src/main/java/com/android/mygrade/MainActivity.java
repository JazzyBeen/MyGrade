package com.android.mygrade; // Убедитесь, что это имя вашего пакета

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements SubjectAdapter.OnSubjectInteractionListener {

    private AppDatabase appDatabase;
    private ExecutorService executorService;
    private SubjectAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private WorkManager workManager;
    private static final String MANUAL_UPDATE_WORK_NAME = "ManualSheetUpdate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        executorService = Executors.newSingleThreadExecutor();
        appDatabase = AppDatabase.getDatabase(this);
        workManager = WorkManager.getInstance(getApplicationContext());

        setupRecyclerView();
        setupSwipeToRefresh();

        ImageButton buttonPlus = findViewById(R.id.button_plus);
        buttonPlus.setOnClickListener(v -> showAddOrEditSubjectDialog(null));

        appDatabase.subjectDao().getAllSubjects().observe(this, subjects -> {
            adapter.submitList(subjects);
        });

        schedulePeriodicWork();
        observeWorkInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        triggerImmediateUpdate();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new SubjectAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void setupSwipeToRefresh() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::triggerImmediateUpdate);
    }

    private void observeWorkInfo() {
        workManager.getWorkInfosForUniqueWorkLiveData(MANUAL_UPDATE_WORK_NAME).observe(this, workInfos -> {
            if (workInfos == null || workInfos.isEmpty()) {
                return;
            }
            WorkInfo workInfo = workInfos.get(0);
            if (workInfo.getState() == WorkInfo.State.RUNNING || workInfo.getState() == WorkInfo.State.ENQUEUED) {
                swipeRefreshLayout.setRefreshing(true);
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onSubjectLongClicked(View view, Subject subject) {
        showPopupMenu(view, subject);
    }



    private void showPopupMenu(View view, final Subject subject) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.subject_context_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_edit) {
                showAddOrEditSubjectDialog(subject);
                return true;
            } else if (itemId == R.id.menu_delete) {
                showDeleteConfirmationDialog(subject);
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void showAddOrEditSubjectDialog(final Subject subjectToEdit) {
        // Используем вашу кастомную тему, если она есть
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        LayoutInflater inflater = this.getLayoutInflater();
        // Убедитесь, что здесь правильное имя вашего макета для диалога
        View dialogView = inflater.inflate(R.layout.dialog_add_item, null);
        builder.setView(dialogView);

        final EditText editName = dialogView.findViewById(R.id.editTextSubjectName);
        final EditText editUrl = dialogView.findViewById(R.id.editTextSheetUrl);
        final EditText editSheetName = dialogView.findViewById(R.id.editTextSheetName);
        final EditText editColumn = dialogView.findViewById(R.id.editTextColumn);
        final EditText editRow = dialogView.findViewById(R.id.editTextRow);
        final EditText editMaxValue = dialogView.findViewById(R.id.editTextMaxValue);

        if (subjectToEdit != null) {
            builder.setTitle("Редактировать предмет");
            editName.setText(subjectToEdit.name);
            editUrl.setText(subjectToEdit.sheetUrl);
            editSheetName.setText(subjectToEdit.sheetName);
            editColumn.setText(subjectToEdit.column);
            editRow.setText(String.valueOf(subjectToEdit.row));
            editMaxValue.setText(String.valueOf(subjectToEdit.maxValue));
        } else {
            builder.setTitle("Добавить предмет");
        }

        builder.setPositiveButton("OK", (dialog, id) -> {
            String name = editName.getText().toString().trim();
            String url = editUrl.getText().toString().trim();
            String sheetName = editSheetName.getText().toString().trim();
            String column = editColumn.getText().toString().trim().toUpperCase();
            String rowStr = editRow.getText().toString().trim();
            String maxValueStr = editMaxValue.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(url) || TextUtils.isEmpty(sheetName) ||
                    TextUtils.isEmpty(column) || TextUtils.isEmpty(rowStr) || TextUtils.isEmpty(maxValueStr)) {
                Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
                return;
            }

            int row = Integer.parseInt(rowStr);
            int maxValue = Integer.parseInt(maxValueStr);

            if (subjectToEdit != null) {
                subjectToEdit.name = name;
                subjectToEdit.sheetUrl = url;
                subjectToEdit.sheetName = sheetName;
                subjectToEdit.column = column;
                subjectToEdit.row = row;
                subjectToEdit.maxValue = maxValue;
                updateSubject(subjectToEdit);
            } else {
                Subject newSubject = new Subject(name, url, column, row, sheetName, maxValue);
                insertSubject(newSubject);
            }
        });
        builder.setNegativeButton("Отмена", (dialog, id) -> dialog.cancel());

        // Создаем экземпляр диалога
        AlertDialog dialog = builder.create();
        dialog.show();
        // --- ДОБАВЛЕНА ЛОГИКА ДЛЯ ПРОЗРАЧНОСТИ И РАЗМЫТИЯ ---
        Window window = dialog.getWindow();
        if (window != null) {
            // Делаем фон самого окна полностью прозрачным.
            // Это позволит видеть наш кастомный фон из XML и размытие за ним.
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            // Включаем затемнение фона за диалогом (стандартный эффект)
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.getAttributes().dimAmount = 0.4f; // Настраиваем силу затемнения

            // Проверяем версию Android, чтобы безопасно применить размытие
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12 (API 31) и выше
                // Устанавливаем радиус размытия фона за окном
                window.setBackgroundBlurRadius(90);
            }
            // На версиях Android ниже 12 фон будет просто полупрозрачным и затемненным.
        }
        // --------------------------------------------------------

        // Показываем полностью настроенный диалог

    }

    private void showDeleteConfirmationDialog(final Subject subject) {
        new AlertDialog.Builder(this)
                .setTitle("Удалить предмет")
                .setMessage("Вы уверены, что хотите удалить '" + subject.name + "'?")
                .setPositiveButton("Удалить", (dialog, which) -> deleteSubject(subject))
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void insertSubject(Subject subject) {
        executorService.execute(() -> {
            appDatabase.subjectDao().insert(subject);
            // Запускаем обновление UI в главном потоке
            runOnUiThread(this::triggerImmediateUpdate);
        });
    }

    private void updateSubject(Subject subject) {
        executorService.execute(() -> {
            appDatabase.subjectDao().update(subject);
            // Запускаем обновление UI в главном потоке
            runOnUiThread(this::triggerImmediateUpdate);
        });
    }

    private void deleteSubject(Subject subject) {
        executorService.execute(() -> appDatabase.subjectDao().delete(subject));
    }

    private void triggerImmediateUpdate() {
        OneTimeWorkRequest updateRequest = new OneTimeWorkRequest.Builder(SheetUpdateWorker.class).build();
        workManager.enqueueUniqueWork(MANUAL_UPDATE_WORK_NAME, ExistingWorkPolicy.REPLACE, updateRequest);
    }

    private void schedulePeriodicWork() {
        PeriodicWorkRequest periodicWorkRequest =
                new PeriodicWorkRequest.Builder(SheetUpdateWorker.class, 15, TimeUnit.MINUTES).build();
        workManager.enqueueUniquePeriodicWork(
                "SheetUpdateWork", ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);
    }
}
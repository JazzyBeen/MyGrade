package com.android.mygrade.presentation;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.mygrade.R;
import com.android.mygrade.presentation.subjects.SubjectAdapter;
import com.android.mygrade.domain.model.Subject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements SubjectAdapter.OnSubjectInteractionListener {

    private MainViewModel viewModel;
    private SubjectAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setupRecyclerView();
        setupSwipeToRefresh();

        ImageButton buttonPlus = findViewById(R.id.button_plus);
        buttonPlus.setOnClickListener(v -> showAddOrEditSubjectDialog(null));

        viewModel.getSubjects().observe(this, subjects -> {
            adapter.submitList(subjects);
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            swipeRefreshLayout.setRefreshing(isLoading);
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.refreshData();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new SubjectAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void setupSwipeToRefresh() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.refreshData());
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        LayoutInflater inflater = this.getLayoutInflater();
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
            editName.setText(subjectToEdit.getName());
            editUrl.setText(subjectToEdit.getSheetUrl());
            editSheetName.setText(subjectToEdit.getSheetName());
            editColumn.setText(subjectToEdit.getColumn());
            editRow.setText(String.valueOf(subjectToEdit.getRow()));
            editMaxValue.setText(String.valueOf(subjectToEdit.getMaxValue()));
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
            android.util.Log.d("MyGrade_DEBUG", "Creating Subject with name: " + name + " and url: " + url);
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(url) || TextUtils.isEmpty(sheetName) ||
                    TextUtils.isEmpty(column) || TextUtils.isEmpty(rowStr) || TextUtils.isEmpty(maxValueStr)) {
                Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
                return;
            }

            int row = Integer.parseInt(rowStr);
            int maxValue = Integer.parseInt(maxValueStr);

            if (subjectToEdit != null) {
                subjectToEdit.setName(name);
                subjectToEdit.setSheetUrl(url);
                subjectToEdit.setSheetName(sheetName);
                subjectToEdit.setColumn(column);
                subjectToEdit.setRow(row);
                subjectToEdit.setMaxValue(maxValue);

                viewModel.updateSubject(subjectToEdit);
            } else {
                Subject newSubject = new Subject(name, url, column, row, sheetName, maxValue);
                Log.d("MyGrade_DEBUG", "Saving subject: " + name + ", URL: " + url);
                viewModel.addSubject(newSubject);
            }
        });
        builder.setNegativeButton("Отмена", (dialog, id) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.getAttributes().dimAmount = 0.4f;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                window.setBackgroundBlurRadius(90);
            }
        }
    }

    private void showDeleteConfirmationDialog(final Subject subject) {
        new AlertDialog.Builder(this)
                .setTitle("Удалить предмет")
                .setMessage("Вы уверены, что хотите удалить '" + subject.getName() + "'?")
                .setPositiveButton("Удалить", (dialog, which) -> viewModel.deleteSubject(subject))
                .setNegativeButton("Отмена", null)
                .show();
    }
}
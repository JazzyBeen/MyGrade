package com.android.mygrade.presentation.subjects;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.android.mygrade.R;
import com.android.mygrade.domain.model.Subject;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class SubjectAdapter extends ListAdapter<Subject, SubjectAdapter.SubjectViewHolder> {

    private final OnSubjectInteractionListener listener;

    public SubjectAdapter(OnSubjectInteractionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Subject> DIFF_CALLBACK = new DiffUtil.ItemCallback<Subject>() {
        @Override
        public boolean areItemsTheSame(@NonNull Subject oldItem, @NonNull Subject newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Subject oldItem, @NonNull Subject newItem) {
            return java.util.Objects.equals(oldItem.getName(), newItem.getName()) &&
                    java.util.Objects.equals(oldItem.getCurrentValue(), newItem.getCurrentValue()) &&
                    oldItem.getMaxValue() == newItem.getMaxValue();
        }
    };

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = getItem(position);
        holder.bind(subject, listener);
    }

    static class SubjectViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView progressTextView;
        private final CircularProgressIndicator progressBar;
        private final View clickableArea;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            progressTextView = itemView.findViewById(R.id.progressTextView);
            progressBar = itemView.findViewById(R.id.circularProgressBar);
            clickableArea = itemView.findViewById(R.id.background_clickable_area);
        }

        public void bind(final Subject subject, final OnSubjectInteractionListener listener) {
            nameTextView.setText(subject.getName());
            progressTextView.setText(subject.getCurrentValue());

            int progress = subject.getProgressPercent();
            progressBar.setProgress(progress);

            clickableArea.setOnLongClickListener(v -> {
                listener.onSubjectLongClicked(v, subject);
                return true;
            });
        }
    }

    public interface OnSubjectInteractionListener {
        void onSubjectLongClicked(View view, Subject subject);
    }
}
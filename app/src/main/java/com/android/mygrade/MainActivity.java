package com.android.mygrade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
public class MainActivity extends AppCompatActivity {

    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ImageButton buttonPlus = findViewById(R.id.button_plus);
        ImageButton buttonHistory = findViewById(R.id.button_history);
        LinearLayout container = findViewById(R.id.container);

        buttonPlus.setOnClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(this);

            View newItemView = inflater.inflate(R.layout.list_item, container, false);

            TextView name = newItemView.findViewById(R.id.name);
            TextView ball = newItemView.findViewById(R.id.circularProgressBar);

            counter++;
            name.setText("Алгосы");
            ball.setText("60");


            container.addView(newItemView);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
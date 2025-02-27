package com.example.restaurantorderapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private GridLayout tableGrid;
    private Button buttonKitchen;


    private String[] tables = {"Bord 1", "Bord 2", "Bord 3", "Bord 4", "Bord 5", "Bord 6"};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tableGrid = findViewById(R.id.tableGrid);

        for (String table : tables) {
            Button tableButton = new Button(this);
            tableButton.setText(table);
            tableButton.setTextSize(22);
            tableButton.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            tableButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
            tableButton.setPadding(24, 24, 24, 24);
            tableButton.setAllCaps(false);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 300;
            params.height = 150;
            params.setMargins(16, 16, 16, 16);
            tableButton.setLayoutParams(params);

            tableButton.setOnClickListener(v -> openOrderActivity(table));

            tableGrid.addView(tableButton);
        }
        // Initialize the Kitchen button
        buttonKitchen = findViewById(R.id.button_kitchen);
        buttonKitchen.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, KitchenActivity.class);
            startActivity(intent);
        });

    }

    private void openOrderActivity(String tableNumber) {
        Intent intent = new Intent(MainActivity.this, OrderActivity.class);
        intent.putExtra("TABLE_NUMBER", tableNumber);
        startActivity(intent);
    }

}

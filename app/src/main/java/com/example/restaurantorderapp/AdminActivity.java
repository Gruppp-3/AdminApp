package com.example.restaurantorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    private Button manageOrdersBtn, manageMenuBtn, viewStatsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Koppla knapparna från layout-filen
        manageOrdersBtn = findViewById(R.id.manageOrdersBtn);
        manageMenuBtn = findViewById(R.id.manageMenuBtn);
        viewStatsBtn = findViewById(R.id.viewStatsBtn);

        // Hantera beställningar
        manageOrdersBtn.setOnClickListener(view -> {
            Intent intent = new Intent(AdminActivity.this, ManageOrdersActivity.class);
            startActivity(intent);
        });

        // Hantera meny
        manageMenuBtn.setOnClickListener(view -> {
            Intent intent = new Intent(AdminActivity.this, ManageMenuActivity.class);
            startActivity(intent);
        });

        // Visa statistik
        viewStatsBtn.setOnClickListener(view -> {
            Intent intent = new Intent(AdminActivity.this, ViewStatsActivity.class);
            startActivity(intent);
        });
    }
}

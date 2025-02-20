package com.example.restaurantorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    private Button manageOrdersBtn, manageMenuBtn, bookingBtn; // Ändrat från viewStatsBtn till bookingBtn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Koppla knapparna från layout-filen
        manageOrdersBtn = findViewById(R.id.manageOrdersBtn);
        manageMenuBtn = findViewById(R.id.manageMenuBtn);
        bookingBtn = findViewById(R.id.bookingBtn); // Ny knapp för bokning

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

        // Bokning (ersatt "Visa Statistik")
        bookingBtn.setOnClickListener(view -> {
            Intent intent = new Intent(AdminActivity.this, BookingActivity.class);
            startActivity(intent);
        });
    }
}

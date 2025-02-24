package com.example.restaurantorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
public class AdminActivity extends AppCompatActivity {
    private static final String TAG = "AdminActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Log.d(TAG, "AdminActivity created");

        setupButtons();
    }

    private void setupButtons() {
        // Find and setup Orders button
        Button manageOrdersBtn = findViewById(R.id.manageOrdersBtn);
        manageOrdersBtn.setOnClickListener(view -> {
            Log.d(TAG, "Orders button clicked");
            startActivityWithErrorHandling(ManageOrdersActivity.class, "Kunde inte öppna beställningshantering");
        });

        // Find and setup Lunch Management button
        Button lunchManagementBtn = findViewById(R.id.lunchManagementBtn);
        lunchManagementBtn.setOnClickListener(v -> {
            Log.d(TAG, "Lunch management button clicked");
            startActivityWithErrorHandling(LunchManagementActivity.class, "Kunde inte öppna lunchhantering");
        });

        // Find and setup Booking button
        Button bookingBtn = findViewById(R.id.bookingBtn);
        bookingBtn.setOnClickListener(view -> {
            Log.d(TAG, "Booking button clicked");
            startActivityWithErrorHandling(BookingActivity.class, "Kunde inte öppna bokningar");
        });
    }

    private void startActivityWithErrorHandling(Class<?> activityClass, String errorMessage) {
        try {
            Intent intent = new Intent(AdminActivity.this, activityClass);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error starting " + activityClass.getSimpleName() + ": " + e.getMessage());
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }
}
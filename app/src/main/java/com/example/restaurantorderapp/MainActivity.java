package com.example.restaurantorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupButtons();
    }

    private void setupButtons() {
        Button manageStaffBtn = findViewById(R.id.manageStaffBtn);
        manageStaffBtn.setOnClickListener(view -> {
            Log.d(TAG, "Manage staff button clicked");
            startActivityWithErrorHandling(ManagePersonalActivity.class, "Kunde inte öppna personalhantering");
        });

        Button lunchManagementBtn = findViewById(R.id.lunchManagementBtn);
        lunchManagementBtn.setOnClickListener(v -> {
            Log.d(TAG, "Lunch management button clicked");
            startActivityWithErrorHandling(LunchManagementActivity.class, "Kunde inte öppna lunchhantering");
        });

        Button bookingBtn = findViewById(R.id.bookingBtn);
        bookingBtn.setOnClickListener(view -> {
            Log.d(TAG, "Booking button clicked");
            startActivityWithErrorHandling(BookingActivity.class, "Kunde inte öppna bokningar");
        });
    }

    private void startActivityWithErrorHandling(Class<?> activityClass, String errorMessage) {
        try {
            Intent intent = new Intent(MainActivity.this, activityClass);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error starting " + activityClass.getSimpleName() + ": " + e.getMessage());
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }
}

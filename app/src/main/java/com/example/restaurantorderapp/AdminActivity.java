package com.example.restaurantorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {
    private static final String TAG = "AdminActivity";
    private Button manageStaffBtn, manageMenuBtn, bookingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Log.d(TAG, "AdminActivity created");

        // Find buttons
        manageStaffBtn = findViewById(R.id.manageStaffBtn);
        manageMenuBtn = findViewById(R.id.manageMenuBtn);
        bookingBtn = findViewById(R.id.bookingBtn);

        // Set up click listeners with error handling
        manageStaffBtn.setOnClickListener(view -> {
            Log.d(TAG, "Manage Staff button clicked");
            try {
                Intent intent = new Intent(AdminActivity.this, ManagePersonalActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error starting ManagePersonalActivity: " + e.getMessage());
                Toast.makeText(this, "Kunde inte öppna personalhantering", Toast.LENGTH_SHORT).show();
            }
        });

        manageMenuBtn.setOnClickListener(view -> {
            Log.d(TAG, "Menu button clicked");
            try {
                Intent intent = new Intent(AdminActivity.this, ManageMenuActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error starting ManageMenuActivity: " + e.getMessage());
                Toast.makeText(this, "Kunde inte öppna menyhantering", Toast.LENGTH_SHORT).show();
            }
        });

        bookingBtn.setOnClickListener(view -> {
            Log.d(TAG, "Booking button clicked");
            try {
                Intent intent = new Intent(AdminActivity.this, BookingActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error starting BookingActivity: " + e.getMessage());
                Toast.makeText(this, "Kunde inte öppna bokningar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
package com.example.restaurantorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantorderapp.alacarte.AlacarteMenuManagementActivity;
import com.example.restaurantorderapp.employee.EmployeeManagementActivity;
import com.example.restaurantorderapp.lunch.LunchManagementActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find buttons
        Button manageStaffBtn = findViewById(R.id.manageStaffBtn);
        Button lunchManagementBtn = findViewById(R.id.lunchManagementBtn);
        Button alacarteManagementBtn = findViewById(R.id.alacarteManagementBtn);
        Button bookingBtn = findViewById(R.id.bookingBtn);

        // Set click listeners
        manageStaffBtn.setOnClickListener(v -> {
            // Navigate to the Employee Management screen
            Intent intent = new Intent(MainActivity.this, EmployeeManagementActivity.class);
            startActivity(intent);
        });

        // Lunch Management
        lunchManagementBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LunchManagementActivity.class);
            startActivity(intent);
        });

        alacarteManagementBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AlacarteMenuManagementActivity.class);
            startActivity(intent);
        });

        bookingBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BookingActivity.class);
            startActivity(intent);
        });
    }
}
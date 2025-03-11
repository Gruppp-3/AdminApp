package com.example.restaurantorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantorderapp.employee.EmployeeManagementActivity;

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

        // Add other button click listeners here as needed
        lunchManagementBtn.setOnClickListener(v -> {
            // TODO: Implement lunch management navigation
        });

        alacarteManagementBtn.setOnClickListener(v -> {
            // TODO: Implement à la carte management navigation
        });

        bookingBtn.setOnClickListener(v -> {
            // TODO: Implement booking management navigation
        });
    }
}
package com.example.restaurantorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantorderapp.alacarte.AlacarteMenuManagementActivity;
import com.example.restaurantorderapp.lunch.LunchManagementActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupButtons();
    }

    private void setupButtons() {
        // Staff management button
        Button manageStaffBtn = findViewById(R.id.manageStaffBtn);
        manageStaffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ManageStaffActivity.class);
                startActivity(intent);
            }
        });

        // Lunch management button
        Button lunchManagementBtn = findViewById(R.id.lunchManagementBtn);
        lunchManagementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LunchManagementActivity.class);
                startActivity(intent);
            }
        });

        // À la carte management button
        Button alacarteManagementBtn = findViewById(R.id.alacarteManagementBtn);
        alacarteManagementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AlacarteMenuManagementActivity.class);
                startActivity(intent);
            }
        });

        // Booking button
        Button bookingBtn = findViewById(R.id.bookingBtn);
        bookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BookingActivity.class);
                startActivity(intent);
            }
        });
    }
}
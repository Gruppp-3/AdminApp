package com.example.restaurantorderapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ViewStatsActivity extends AppCompatActivity {

    private Button viewOrdersStatsBtn, viewRevenueStatsBtn, viewCustomerStatsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stats);

        viewOrdersStatsBtn = findViewById(R.id.viewOrdersStatsBtn);
        viewRevenueStatsBtn = findViewById(R.id.viewRevenueStatsBtn);
        viewCustomerStatsBtn = findViewById(R.id.viewCustomerStatsBtn);

        viewOrdersStatsBtn.setOnClickListener(view -> {
            // Lägg till funktionalitet för att visa orderstatistik
        });

        viewRevenueStatsBtn.setOnClickListener(view -> {
            // Lägg till funktionalitet för att visa intäktsstatistik
        });

        viewCustomerStatsBtn.setOnClickListener(view -> {
            // Lägg till funktionalitet för att visa kundstatistik
        });
    }
}

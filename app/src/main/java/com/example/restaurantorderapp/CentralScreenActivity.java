package com.example.restaurantorderapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CentralScreenActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central_screen);

        LinearLayout ordersContainer = findViewById(R.id.ordersContainer);

        // Fetch all completed orders
        List<String> readyOrders = OrderManager.getInstance().getReadyOrders();
        // Store orders grouped by tables
        Map<String, StringBuilder> tablesOrdersMap = new HashMap<>();

        for (String order : readyOrders) {
            String tableNumber = extractTableNumber(order);

            // Ensure "klar" is only added once
            if (!tablesOrdersMap.containsKey(tableNumber)) {
                tablesOrdersMap.put(tableNumber, new StringBuilder());
            }

            // Add order details to the corresponding table
            Objects.requireNonNull(tablesOrdersMap.get(tableNumber)).append(order).append("\n");
        }

        // Display grouped orders
        if (!tablesOrdersMap.isEmpty()) {
            for (Map.Entry<String, StringBuilder> entry : tablesOrdersMap.entrySet()) {
                String tableNumber = entry.getKey();
                //String orderDetails = entry.getValue().toString();

                // Display table number with "klar"
                TextView tableTextView = new TextView(this);
                tableTextView.setText("Bord " + tableNumber + " klar!");
                tableTextView.setTextSize(22);
                tableTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

                // Display order details
                TextView orderDetailsTextView = new TextView(this);
                //orderDetailsTextView.setText(orderDetails);
                orderDetailsTextView.setTextSize(18);

                // Add elements to UI
                ordersContainer.addView(tableTextView);
                ordersContainer.addView(orderDetailsTextView);
            }
        } else {
            // Show "No Orders" message
            TextView noOrdersTextView = new TextView(this);
            noOrdersTextView.setText("Inga färdiga beställningar än");
            noOrdersTextView.setTextSize(20);
            ordersContainer.addView(noOrdersTextView);
        }
    }

    public static String extractTableNumber(String orderSummary) {
        if (orderSummary.startsWith("Beställning för Bord ")) {
            return orderSummary.split(":")[0].replace("Beställning för Bord ", "").trim();
        }
        return "Okänt bord";
    }
}
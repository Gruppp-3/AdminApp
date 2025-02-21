package com.example.restaurantorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitchenActivity extends AppCompatActivity {
    private LinearLayout ordersContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen_screen);

        ordersContainer = findViewById(R.id.ordersContainer);


        // Fetch all orders
        List<String> allOrders = OrderManager.getInstance().getOrders();
        Map<String, StringBuilder> tableOrdersMap = new HashMap<>();

        // Organize orders by table
        for (String order : allOrders) {
            String tableNumber = extractTableNumber(order);
            if (!tableOrdersMap.containsKey(tableNumber)) {
                tableOrdersMap.put(tableNumber, new StringBuilder());
            }
            tableOrdersMap.get(tableNumber).append(order.replace("Beställning för Bord " + tableNumber + ": ", "")).append("\n");
        }


        // Display tables and orders
        if (!tableOrdersMap.isEmpty()) {
            for (Map.Entry<String, StringBuilder> entry : tableOrdersMap.entrySet()) {
                String tableNumber = entry.getKey();
                String orderDetails = entry.getValue().toString();

                // Linear Layout for each table row
                LinearLayout orderRow = new LinearLayout(this);
                orderRow.setOrientation(LinearLayout.HORIZONTAL);
                orderRow.setPadding(0, 10, 0, 10);

                // Table Number Text
                TextView tableTextView = new TextView(this);
                tableTextView.setText("Bord " + tableNumber);
                tableTextView.setTextSize(20);
                tableTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                tableTextView.setPadding(0, 0, 20, 0);

                // "Mat klar" Button
                Button foodReadyButton = new Button(this);
                foodReadyButton.setText("Mat klar");
                foodReadyButton.setOnClickListener(v -> {
                    OrderManager.getInstance().markOrderReady(tableNumber);
                    Intent intent = new Intent(KitchenActivity.this, CentralScreenActivity.class);
                    startActivity(intent);
                });



                // Add to orderRow layout
                orderRow.addView(tableTextView);
                orderRow.addView(foodReadyButton);

                // Add to main container
                ordersContainer.addView(orderRow);

                // Order Details Below the Row
                TextView orderDetailsText = new TextView(this);
                orderDetailsText.setText(orderDetails);
                orderDetailsText.setTextSize(18);
                orderDetailsText.setPadding(40, 5, 0, 15);
                ordersContainer.addView(orderDetailsText);
            }
        } else {
            // No orders available
            TextView noOrdersText = new TextView(this);
            noOrdersText.setText("Inga beställningar än");
            noOrdersText.setTextSize(20);
            noOrdersText.setPadding(0, 20, 0, 0);
            ordersContainer.addView(noOrdersText);
        }


    }



    private String extractTableNumber(String orderSummary) {
        if (orderSummary.startsWith("Beställning för Bord ")) {
            return orderSummary.split(":")[0].replace("Beställning för Bord ", "").trim();
        }
        return "Okänt bord";
    }
}

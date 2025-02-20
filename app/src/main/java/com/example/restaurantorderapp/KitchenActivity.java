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
        Button backButton = findViewById(R.id.button_back_to_main);

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
                foodReadyButton.setOnClickListener(v -> markOrderAsCompleted(tableNumber));

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

        // Back button functionality
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(KitchenActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void markOrderAsCompleted(String tableNumber) {
        OrderManager.getInstance().removeOrderByTable(tableNumber);

        // Find and remove only the completed order from the UI
        runOnUiThread(() -> {
            for (int i = 0; i < ordersContainer.getChildCount(); i++) {
                View view = ordersContainer.getChildAt(i);
                if (view instanceof LinearLayout) {
                    TextView tableTextView = (TextView) ((LinearLayout) view).getChildAt(0);
                    if (tableTextView.getText().toString().contains("Bord " + tableNumber)) {
                        ordersContainer.removeViewAt(i); // Remove the specific order row
                        break; // Exit loop since order is removed
                    }
                }
            }

            // If all orders are completed, show "Inga beställningar än"
            if (ordersContainer.getChildCount() == 0) {
                TextView noOrdersText = new TextView(KitchenActivity.this);
                noOrdersText.setText("Inga beställningar än");
                noOrdersText.setTextSize(20);
                noOrdersText.setPadding(0, 20, 0, 0);
                ordersContainer.addView(noOrdersText);
            }
        });
    }


    private String extractTableNumber(String orderSummary) {
        if (orderSummary.startsWith("Beställning för Bord ")) {
            return orderSummary.split(":")[0].replace("Beställning för Bord ", "").trim();
        }
        return "Okänt bord";
    }
}

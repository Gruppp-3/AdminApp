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

        // Hämta alla beställningar
        List<String> allOrders = OrderManager.getInstance().getOrders();
        Map<String, StringBuilder> tableOrdersMap = new HashMap<>();


        for (String order : allOrders) {
            String tableNumber = extractTableNumber(order);
            if (!tableOrdersMap.containsKey(tableNumber)) {
                tableOrdersMap.put(tableNumber, new StringBuilder());
            }
            tableOrdersMap.get(tableNumber).append(order.replace("Beställning för Bord " + tableNumber + ": ", "")).append("\n");
        }


        if (!tableOrdersMap.isEmpty()) {
            for (Map.Entry<String, StringBuilder> entry : tableOrdersMap.entrySet()) {
                String tableNumber = entry.getKey();
                String orderDetails = entry.getValue().toString();


                LinearLayout orderRow = new LinearLayout(this);
                orderRow.setOrientation(LinearLayout.HORIZONTAL);
                orderRow.setPadding(0, 10, 0, 10);


                TextView tableTextView = new TextView(this);
                tableTextView.setText("Bord " + tableNumber);
                tableTextView.setTextSize(20);
                tableTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                tableTextView.setPadding(0, 0, 20, 0);


                Button foodReadyButton = new Button(this);
                foodReadyButton.setText("Mat klar");


                final TextView orderDetailsText = new TextView(this);
                orderDetailsText.setText(orderDetails);
                orderDetailsText.setTextSize(18);
                orderDetailsText.setPadding(40, 5, 0, 15);


                orderRow.addView(tableTextView);
                orderRow.addView(foodReadyButton);
                ordersContainer.addView(orderRow);
                ordersContainer.addView(orderDetailsText);


                foodReadyButton.setOnClickListener(v -> {

                    OrderManager.getInstance().markOrderReady(tableNumber);
                    OrderManager.getInstance().removeOrder(tableNumber);


                    ordersContainer.removeView(orderRow);
                    ordersContainer.removeView(orderDetailsText);

                    if (ordersContainer.getChildCount() == 0) {
                        TextView noOrdersText = new TextView(KitchenActivity.this);
                        noOrdersText.setText("Inga beställningar än");
                        noOrdersText.setTextSize(20);
                        noOrdersText.setPadding(0, 20, 0, 0);
                        ordersContainer.addView(noOrdersText);
                    }

                    // Skicka ordern till centralaskärmen
                    Intent intent = new Intent(KitchenActivity.this, CentralScreenActivity.class);
                    startActivity(intent);
                });
            }
        } else {

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

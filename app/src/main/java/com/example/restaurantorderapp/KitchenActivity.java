package com.example.restaurantorderapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button; // Använd Button istället för ImageButton
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class KitchenActivity extends AppCompatActivity {
    private TextView orderTextView;
    private String tableNumber;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen_screen);

        orderTextView = findViewById(R.id.orderTextView);
        // Ändrat från ImageButton till Button
        Button backButton = findViewById(R.id.button_back_to_main); // ID matchar XML-filen
        Button buttonFoodReady = findViewById(R.id.button_food_ready);

        // Hämta alla beställningar från OrderManager
        List<String> allOrders = OrderManager.getInstance().getOrders();

        // Visa beställningarna i TextView
        if (!allOrders.isEmpty()) {
            StringBuilder displayOrders = new StringBuilder();
            for (String order : allOrders) {
                displayOrders.append(order).append("\n\n");
            }
            orderTextView.setText(displayOrders.toString());
            // Extract table number from first order
            tableNumber = extractTableNumber(allOrders.get(0));
        } else {
            orderTextView.setText("Inga beställningar än");
            tableNumber = "Okänt bord";
        }

        // Klick-hanterare för backButton - Gå tillbaka till huvudmenyn
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(KitchenActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Food ready button: Mark only the current table as ready
        buttonFoodReady.setOnClickListener(v -> {
            if(tableNumber != null && !tableNumber.equals("Okänt bord")){

                // Remove only this table's orders from the list
                allOrders.removeIf(order -> order.contains("Bord " + tableNumber));

                // Update order manager with new list (without this table's order)
                OrderManager.getInstance().setOrders(allOrders);

                // Display "Bord X är klar with green color"
                orderTextView.setText("Bord" + tableNumber + "är klar!");
                orderTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));


                // Navigate to Central Screen after a short delay
                orderTextView.postDelayed(() -> {
                    Intent intent = new Intent(KitchenActivity.this, CentralScreenActivity.class);
                    intent.putExtra("TABLE_NUMBER", tableNumber); // Send table number to central screen
                    startActivity(intent);
                    finish();
                }, 10000); // Dilay for 2 seconds to show the message before switching screen
            }
        });
    }

    private String extractTableNumber(String orderSummary) {
        if(orderSummary.startsWith("Beställning för Bord ")){
            String [] parts = orderSummary.split(":");
            if(parts.length > 0){
                return parts[0].replace("Beställning för Bord","").trim();
            }
        }
        return "Okänt bord"; // If extraction fials, return default text

    }
}

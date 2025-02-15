package com.example.restaurantorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button; // Använd Button istället för ImageButton
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class KitchenActivity extends AppCompatActivity {
    private TextView orderTextView;
    private Button backButton; // Ändrat från ImageButton till Button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen_screen);

        orderTextView = findViewById(R.id.orderTextView);
        backButton = findViewById(R.id.button_back_to_main); // ID matchar XML-filen

        // Hämta alla beställningar från OrderManager
        List<String> allOrders = OrderManager.getInstance().getOrders();

        // Visa beställningarna i TextView
        if (!allOrders.isEmpty()) {
            StringBuilder displayOrders = new StringBuilder();
            for (String order : allOrders) {
                displayOrders.append(order).append("\n\n");
            }
            orderTextView.setText(displayOrders.toString());
        } else {
            orderTextView.setText("Inga beställningar än");
        }

        // Klick-hanterare för backButton
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(KitchenActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}

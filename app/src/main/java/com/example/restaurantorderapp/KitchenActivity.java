package com.example.restaurantorderapp;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class KitchenActivity extends AppCompatActivity {
    private TextView orderTextView;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);
        orderTextView = findViewById(R.id.orderTextView);
        backButton = findViewById(R.id.button_back_to_main);

        // Fetch all stored orders from OrderManager
        List<String> allOrders = OrderManager.getInstance().getOrders();

        // Display orders in the TextView
        if (!allOrders.isEmpty()){
            StringBuilder displayOrders = new StringBuilder();
            for (String order : allOrders){
                displayOrders.append(order).append("\n\n");
            }
            orderTextView.setText(displayOrders.toString());
        } else{
            orderTextView.setText("Inga beställningar än");
        }

        // Set up click listener to return to the MainActivity (Table Selection Screen)
        backButton.setOnClickListener(v ->{
            Intent intent = new Intent(KitchenActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

    }

}
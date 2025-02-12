package com.example.restaurantorderapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;

public class OrderActivity extends AppCompatActivity {

    private TextView tableTextView;
    private Spinner categorySpinner;
    private ListView menuListView;
    private Button placeOrderButton;

    private HashMap<String, ArrayList<String>> menuCategories;
    private HashMap<String, Integer> orderCount;
    private ArrayAdapter<String> menuAdapter;
    private ArrayList<String> selectedItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        tableTextView = findViewById(R.id.tableTextView);
        categorySpinner = findViewById(R.id.categorySpinner);
        menuListView = findViewById(R.id.menuListView);
        placeOrderButton = findViewById(R.id.placeOrderButton);

        // Hämta valt bord
        String tableNumber = getIntent().getStringExtra("TABLE_NUMBER");
        tableTextView.setText("Bord: " + tableNumber);

        selectedItems = new ArrayList<>();
        orderCount = new HashMap<>();

        // Skapa menyer
        menuCategories = new HashMap<>();
        menuCategories.put("Förrätter", new ArrayList<String>() {{ add("Bruschetta"); add("Soppa"); add("Caprese Sallad"); }});
        menuCategories.put("Huvudrätter", new ArrayList<String>() {{ add("Pizza"); add("Hamburgare"); add("Pasta Carbonara"); }});
        menuCategories.put("Efterrätter", new ArrayList<String>() {{ add("Tiramisu"); add("Chokladkaka"); add("Glass"); }});

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<>(menuCategories.keySet()));
        categorySpinner.setAdapter(categoryAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateMenuList(categoryAdapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Hitta tillbaka-knappen
        Button backButton = findViewById(R.id.backButton);

        // När man trycker på tillbaka-knappen
        backButton.setOnClickListener(v -> finish()); // Stänger OrderActivity och går tillba
        menuListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDish = (String) menuListView.getItemAtPosition(position);
            showQuantityDialog(selectedDish);
        });

        placeOrderButton.setOnClickListener(v -> placeOrder(tableNumber));
    }

    private void updateMenuList(String category) {
        menuAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, menuCategories.get(category));
        menuListView.setAdapter(menuAdapter);
    }

    private void showQuantityDialog(String dish) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Välj antal för: " + dish);

        final NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(10);
        builder.setView(numberPicker);

        builder.setPositiveButton("OK", (dialog, which) -> {
            orderCount.put(dish, numberPicker.getValue());
            selectedItems.add(dish);
            Toast.makeText(OrderActivity.this, dish + " x" + numberPicker.getValue() + " tillagd", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Avbryt", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void placeOrder(String tableNumber) {
        String orderSummary = "Beställning för " + tableNumber + ":\n";
        for (String item : selectedItems) {
            orderSummary += item + " x" + orderCount.get(item) + "\n";
        }
        Toast.makeText(this, orderSummary, Toast.LENGTH_LONG).show();

    }
}

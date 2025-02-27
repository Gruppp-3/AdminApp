package com.example.restaurantorderapp.alacarte;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.restaurantorderapp.R;
import com.example.restaurantorderapp.adapter.AlacarteAdapter;
import com.example.restaurantorderapp.api.ApiService;
import com.example.restaurantorderapp.api.RetrofitClient;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlacarteMenuManagementActivity extends AppCompatActivity {
    private static final String TAG = "AlacarteMenuActivity";
    private static final int ADD_DISH_REQUEST = 1;
    private static final int EDIT_DISH_REQUEST = 2;

    private RecyclerView recyclerView;
    private AlacarteAdapter adapter;
    private FloatingActionButton addFab;
    private ApiService apiService;
    private Spinner categorySpinner;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private String currentCategory = "ALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alacarte_management);

        apiService = RetrofitClient.getInstance().getApi();
        setupViews();
        setupSwipeRefresh();
        setupCategorySpinner();
        setupListeners();
        loadMenuData();
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.alacarteRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addFab = findViewById(R.id.addDishFab);
        categorySpinner = findViewById(R.id.categorySpinner);
        progressBar = findViewById(R.id.progressBar);

        adapter = new AlacarteAdapter(new ArrayList<>(), this::showEditDialog);
        recyclerView.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this::loadMenuData);
    }

    private void setupCategorySpinner() {
        // Use the actual category names from your database
        String[] categories = {"Alla kategorier", "Förrätter", "Varmrätter", "Vegetariska", "Efterrätter"};

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    currentCategory = "ALL";
                } else {
                    // Use the Swedish category name that matches your database
                    currentCategory = categories[position];
                }
                loadMenuData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupListeners() {
        addFab.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddMenuItemActivity.class);
            intent.putExtra("category", currentCategory);
            startActivityForResult(intent, ADD_DISH_REQUEST);
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void loadMenuData() {
        showLoading(true);
        Log.d(TAG, "Loading menu data for category: " + currentCategory);

        // Always fetch all menu items
        apiService.getMenu().enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call,
                                   Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, Object>> allItems = response.body();
                    List<Map<String, Object>> filteredItems;

                    // Filter items on the client side if a specific category is selected
                    if (!"ALL".equals(currentCategory)) {
                        filteredItems = new ArrayList<>();
                        for (Map<String, Object> item : allItems) {
                            String itemCategory = (String) item.get("DISH_TYPE_NAME");
                            if (currentCategory.equals(itemCategory)) {
                                filteredItems.add(item);
                            }
                        }
                    } else {
                        filteredItems = allItems;
                    }

                    // Process and display the filtered items
                    List<Map<String, Object>> displayItems = groupItemsByCategory(filteredItems);
                    adapter.updateData(displayItems);
                } else {
                    showError("Kunde inte ladda menyn. Kod: " + response.code());
                }
                showLoading(false);
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Log.e(TAG, "Network failure", t);
                showError("Nätverksfel: " + t.getMessage());
                showLoading(false);
            }
        });
    }

    private List<Map<String, Object>> groupItemsByCategory(List<Map<String, Object>> items) {
        // Group items by category
        Map<String, List<Map<String, Object>>> groupedItems = new HashMap<>();
        for (Map<String, Object> item : items) {
            String category = (String) item.get("DISH_TYPE_NAME");
            if (category == null) category = "Övrigt";

            if (!groupedItems.containsKey(category)) {
                groupedItems.put(category, new ArrayList<>());
            }
            groupedItems.get(category).add(item);
        }

        // Flatten the grouped items with category headers
        List<Map<String, Object>> displayItems = new ArrayList<>();
        for (Map.Entry<String, List<Map<String, Object>>> entry : groupedItems.entrySet()) {
            // Add category header
            Map<String, Object> header = new HashMap<>();
            header.put("isHeader", true);
            header.put("categoryName", entry.getKey());
            displayItems.add(header);

            // Add category items
            displayItems.addAll(entry.getValue());
        }

        return displayItems;
    }

    private void showError(String message) {
        Log.e(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showEditDialog(Map<String, Object> item) {
        if (item.containsKey("isHeader")) {
            return; // Don't show edit dialog for headers
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("Hantera maträtt")
                .setItems(new String[]{"Redigera", "Ta bort"}, (dialog, which) -> {
                    if (which == 0) {
                        Intent intent = new Intent(this, EditMenuItemActivity.class);
                        intent.putExtra("menuItem", (Serializable) item);
                        startActivityForResult(intent, EDIT_DISH_REQUEST);
                    } else {
                        showDeleteConfirmationDialog(item);
                    }
                })
                .show();
    }

    private void showDeleteConfirmationDialog(Map<String, Object> item) {
        Log.d("DEBUG", "Item data: " + item.toString()); // Log the full item object

        Object idObject = item.get("id"); // Extract the ID from the map

        Log.d("DEBUG", "Extracted ID object: " + idObject); // Log the extracted ID

        if (idObject == null) {
            Log.e("ERROR", "ID is null! Item content: " + item); // Log full item content
            Toast.makeText(this, "Fel: ID saknas för maträtten", Toast.LENGTH_SHORT).show();
            return;
        }

        long itemId;
        try {
            if (idObject instanceof Number) {
                itemId = ((Number) idObject).longValue();
            } else {
                itemId = Long.parseLong(idObject.toString());
            }
            Log.d("DEBUG", "Parsed ID: " + itemId); // Log parsed ID

            String dishName = (String) item.get("DISH_NAME");
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Ta bort maträtt")
                    .setMessage("Är du säker på att du vill ta bort " + dishName + "?")
                    .setPositiveButton("Ta bort", (dialog, which) -> deleteMenuItem(itemId))
                    .setNegativeButton("Avbryt", null)
                    .show();
        } catch (NumberFormatException e) {
            Log.e("ERROR", "Invalid ID format", e);
            Toast.makeText(this, "Fel: Ogiltigt ID-format", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteMenuItem(long itemId) {
        apiService.deleteMenuItem(itemId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AlacarteMenuManagementActivity.this,
                            "Maträtt borttagen!", Toast.LENGTH_SHORT).show();
                    // Refresh your list of items
                    loadMenuData();
                } else {
                    String errorMsg = "Fel vid borttagning. Kod: " + response.code();
                    Toast.makeText(AlacarteMenuManagementActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String errorMsg = "Nätverksfel: " + t.getMessage();
                Toast.makeText(AlacarteMenuManagementActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK &&
                (requestCode == EDIT_DISH_REQUEST || requestCode == ADD_DISH_REQUEST)) {
            loadMenuData();
        }
    }
}
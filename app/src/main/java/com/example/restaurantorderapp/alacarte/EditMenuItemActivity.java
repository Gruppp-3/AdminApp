package com.example.restaurantorderapp.alacarte;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.restaurantorderapp.R;
import com.example.restaurantorderapp.api.ApiService;
import com.example.restaurantorderapp.api.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditMenuItemActivity extends AppCompatActivity {
    private static final String TAG = "EditMenuItemActivity";

    private EditText nameEditText;
    private EditText descriptionEditText;
    private EditText priceEditText;
    private Spinner categorySpinner;
    private Button updateButton;
    private ProgressBar progressBar;
    private ApiService apiService;

    private Map<String, Object> menuItem;
    private Long menuItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu_item);

        // Set up toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Redigera maträtt");
        }

        // Initialize API service
        apiService = RetrofitClient.getInstance().getApi();

        // Initialize views
        nameEditText = findViewById(R.id.nameInput);
        descriptionEditText = findViewById(R.id.descriptionInput);
        priceEditText = findViewById(R.id.priceInput);
        categorySpinner = findViewById(R.id.categorySpinner);
        updateButton = findViewById(R.id.updateButton);
        progressBar = findViewById(R.id.progressBar);

        // Set up category spinner
        setupCategorySpinner();

        // Get menu item data from intent
        if (getIntent().hasExtra("menuItem")) {
            menuItem = (Map<String, Object>) getIntent().getSerializableExtra("menuItem");
            if (menuItem != null) {
                populateFields(menuItem);
            }
        } else {
            Toast.makeText(this, "Ingen maträtt att redigera", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up update button click listener
        updateButton.setOnClickListener(v -> validateAndUpdate());
    }

    private void setupCategorySpinner() {
        // Display categories in Swedish for UI
        String[] categories = {"Välj kategori", "Förrätt", "Varmrätt", "Efterrätt", "Dryck"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void populateFields(Map<String, Object> item) {
        // Extract ID
        Object idObj = item.get("dish_id");
        if (idObj instanceof Number) {
            menuItemId = ((Number) idObj).longValue();
        }

        // Set name
        String name = (String) item.get("dish_name");
        if (name != null) {
            nameEditText.setText(name);
        }

        // Set description
        String description = (String) item.get("dish_description");
        if (description != null) {
            descriptionEditText.setText(description);
        }

        // Set price
        Object priceObj = item.get("dish_price");
        if (priceObj instanceof Number) {
            double price = ((Number) priceObj).doubleValue();
            priceEditText.setText(String.valueOf(price));
        }

        // Set category
        String category = (String) item.get("dish_type");
        if (category != null) {
            int position = getCategoryPosition(category);
            categorySpinner.setSelection(position);
        }
    }

    private int getCategoryPosition(String apiCategory) {
        switch (apiCategory) {
            case "APPETIZER": return 1;
            case "MAIN": return 2;
            case "DESSERT": return 3;
            case "DRINK": return 4;
            default: return 0;
        }
    }

    private void validateAndUpdate() {
        if (menuItemId == null) {
            Toast.makeText(this, "Kan inte uppdatera: Inget ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get input values
        String name = nameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String priceStr = priceEditText.getText().toString().trim();
        int categoryPosition = categorySpinner.getSelectedItemPosition();

        // Validate inputs
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Ange ett namn");
            nameEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(priceStr)) {
            priceEditText.setError("Ange ett pris");
            priceEditText.requestFocus();
            return;
        }

        if (categoryPosition == 0) {
            Toast.makeText(this, "Välj en kategori", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert category position to API value
        String apiCategory;
        switch (categoryPosition) {
            case 1:
                apiCategory = "APPETIZER";
                break;
            case 2:
                apiCategory = "MAIN";
                break;
            case 3:
                apiCategory = "DESSERT";
                break;
            case 4:
                apiCategory = "DRINK";
                break;
            default:
                apiCategory = "OTHER";
                break;
        }

        // Create updated menu item object
        Map<String, Object> updatedItem = new HashMap<>();
        updatedItem.put("dish_name", name);
        updatedItem.put("dish_description", description);
        updatedItem.put("dish_price", Double.parseDouble(priceStr));
        updatedItem.put("dish_type", apiCategory);

        // Show progress and disable button
        progressBar.setVisibility(View.VISIBLE);
        updateButton.setEnabled(false);

        // Send API request
        apiService.updateMenuItem(menuItemId, updatedItem).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                progressBar.setVisibility(View.GONE);
                updateButton.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(EditMenuItemActivity.this,
                            "Maträtt uppdaterad!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    int errorCode = response.code();
                    String errorMsg = "Fel vid uppdatering av maträtt. Kod: " + errorCode;
                    Log.e(TAG, errorMsg);
                    Toast.makeText(EditMenuItemActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                updateButton.setEnabled(true);

                String errorMsg = "Nätverksfel: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                Toast.makeText(EditMenuItemActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
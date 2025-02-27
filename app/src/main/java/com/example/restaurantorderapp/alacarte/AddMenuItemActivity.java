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

public class AddMenuItemActivity extends AppCompatActivity {
    private static final String TAG = "AddMenuItemActivity";

    private EditText nameEditText;
    private EditText descriptionEditText;
    private EditText priceEditText;
    private Spinner categorySpinner;
    private Button saveButton;
    private ProgressBar progressBar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu_item);

        // Set up toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Lägg till maträtt");
        }

        // Initialize API service
        apiService = RetrofitClient.getInstance().getApi();

        // Initialize views
        nameEditText = findViewById(R.id.nameInput);
        descriptionEditText = findViewById(R.id.descriptionInput);
        priceEditText = findViewById(R.id.priceInput);
        categorySpinner = findViewById(R.id.categorySpinner);
        saveButton = findViewById(R.id.saveButton);
        progressBar = findViewById(R.id.progressBar);

        // Set up category spinner
        setupCategorySpinner();

        // Set up save button click listener
        saveButton.setOnClickListener(v -> validateAndSave());
    }

    private void setupCategorySpinner() {
        // Display categories in Swedish for UI
        String[] categories = {"Välj kategori", "Förrätt", "Varmrätt", "Efterrätt", "Dryck"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void validateAndSave() {
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

        // Create menu item object
        Map<String, Object> menuItem = new HashMap<>();
        menuItem.put("dish_name", name);
        menuItem.put("dish_description", description);
        menuItem.put("dish_price", Double.parseDouble(priceStr));
        menuItem.put("dish_type", apiCategory);

        // Show progress and disable button
        progressBar.setVisibility(View.VISIBLE);
        saveButton.setEnabled(false);

        // Send API request
        apiService.addMenuItem(menuItem).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                saveButton.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(AddMenuItemActivity.this,
                            "Maträtt tillagd!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    int errorCode = response.code();
                    String errorMsg = "Fel vid tillägg av maträtt. Kod: " + errorCode;
                    Log.e(TAG, errorMsg);
                    Toast.makeText(AddMenuItemActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                saveButton.setEnabled(true);

                String errorMsg = "Nätverksfel: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                Toast.makeText(AddMenuItemActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
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
package com.example.restaurantorderapp.lunch;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantorderapp.R;
import com.example.restaurantorderapp.api.ApiService;
import com.example.restaurantorderapp.api.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// EditLunchItemActivity.java
public class EditLunchItemActivity extends AppCompatActivity {
    private EditText nameInput;
    private EditText priceInput;
    private EditText descriptionInput;
    private Button updateButton;
    private Map<String, Object> lunchItem;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_lunch_item);

        apiService = RetrofitClient.getInstance().getApi();
        lunchItem = (Map<String, Object>) getIntent().getSerializableExtra("lunchItem");

        setupViews();
        populateFields();
        setupListeners();
    }

    private void setupViews() {
        nameInput = findViewById(R.id.nameInput);
        priceInput = findViewById(R.id.priceInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        updateButton = findViewById(R.id.updateButton);
    }

    private void setupListeners() {
        updateButton.setOnClickListener(v -> {
            if (validateInput()) {
                updateLunchItem();
            }
        });
    }

    private void populateFields() {
        nameInput.setText((String) lunchItem.get("dish_name"));
        priceInput.setText(String.valueOf(lunchItem.get("dish_price")));
        descriptionInput.setText((String) lunchItem.get("dish_description"));
    }

    private boolean validateInput() {
        boolean isValid = true;

        String name = nameInput.getText().toString().trim();
        String price = priceInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();

        if (name.isEmpty()) {
            nameInput.setError("Namn krävs");
            isValid = false;
        }

        if (price.isEmpty()) {
            priceInput.setError("Pris krävs");
            isValid = false;
        } else {
            try {
                double priceValue = Double.parseDouble(price);
                if (priceValue <= 0) {
                    priceInput.setError("Pris måste vara större än 0");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                priceInput.setError("Ogiltigt prisformat");
                isValid = false;
            }
        }

        if (description.isEmpty()) {
            descriptionInput.setError("Beskrivning krävs");
            isValid = false;
        }

        return isValid;
    }

    private void updateLunchItem() {
        String name = nameInput.getText().toString().trim();
        double price = Double.parseDouble(priceInput.getText().toString().trim());
        String description = descriptionInput.getText().toString().trim();

        Map<String, Object> updatedLunchItem = new HashMap<>();
        updatedLunchItem.put("dish_name", name);
        updatedLunchItem.put("dish_price", price);
        updatedLunchItem.put("dish_description", description);

        // Fix the type casting issue here
        Object rawId = lunchItem.get("id");
        Long itemId;
        if (rawId instanceof Double) {
            itemId = ((Double) rawId).longValue();
        } else if (rawId instanceof Long) {
            itemId = (Long) rawId;
        } else {
            itemId = Long.valueOf(rawId.toString());
        }

        apiService.updateLunchDish(itemId, updatedLunchItem).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditLunchItemActivity.this, "Lunchrätt uppdaterad", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(EditLunchItemActivity.this, "Fel vid uppdatering", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(EditLunchItemActivity.this, "Nätverksfel: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
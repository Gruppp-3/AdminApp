package com.example.restaurantorderapp.lunch;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantorderapp.R;
import com.example.restaurantorderapp.api.ApiService;
import com.example.restaurantorderapp.api.RetrofitClient;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddLunchItemActivity extends AppCompatActivity {
    private EditText nameInput;
    private EditText priceInput;
    private EditText descriptionInput;
    private Button saveButton;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lunch_item);

        apiService = RetrofitClient.getInstance().getApi();
        setupViews();
        setupListeners();
    }

    private void setupViews() {
        nameInput = findViewById(R.id.nameInput);
        priceInput = findViewById(R.id.priceInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        saveButton = findViewById(R.id.saveButton);
    }

    private void setupListeners() {
        saveButton.setOnClickListener(v -> {
            if (validateInput()) {
                saveLunchItem();
            }
        });
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

    private void saveLunchItem() {
        if (!validateInput()) return;

        Map<String, Object> lunchItem = new HashMap<>();
        lunchItem.put("dish_name", nameInput.getText().toString().trim());
        lunchItem.put("dish_price", Double.parseDouble(priceInput.getText().toString().trim()));
        lunchItem.put("dish_description", descriptionInput.getText().toString().trim());
        String lunchType = getIntent().getStringExtra("lunchType");

        if ("WEEKLY".equals(lunchType)) {
            // For weekly dishes, return the dish data locally rather than calling the API.
            android.content.Intent resultIntent = new android.content.Intent();
            resultIntent.putExtra("day", getIntent().getStringExtra("day"));
            resultIntent.putExtra("newDish", (Serializable) lunchItem);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            // For daily dishes, call the API immediately.
            Call<Map<String, Object>> call = apiService.addLunchDish(lunchItem);
            call.enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddLunchItemActivity.this, "Lunch sparad", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        try {
                            String errorBody = response.errorBody().string();
                            Toast.makeText(AddLunchItemActivity.this, "Fel vid sparande: " + errorBody, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Toast.makeText(AddLunchItemActivity.this, "Nätverksfel: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
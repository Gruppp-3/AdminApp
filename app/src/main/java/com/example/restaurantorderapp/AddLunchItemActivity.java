package com.example.restaurantorderapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.restaurantorderapp.api.ApiService;
import com.example.restaurantorderapp.api.RetrofitClient;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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

        // Get the lunch type and the day (if applicable)
        String lunchType = getIntent().getStringExtra("lunchType");
        lunchItem.put("lunchType", lunchType);

        if ("WEEKLY".equalsIgnoreCase(lunchType)) {
            String day = getIntent().getStringExtra("day"); // e.g., "Onsdag"
            int offset = getDayOffset(day);
            String targetDate = getTargetDateString(offset);
            lunchItem.put("date", targetDate);
            lunchItem.put("day", day); // Optional, if you need to send back the day.
        } else {
            // For daily lunches, just use today's date.
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            lunchItem.put("date", sdf.format(new Date()));
        }

        // Now send the lunchItem map via your API call
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

    private String getTargetDateString(int offset) {
        // Get the current date and time.
        Calendar calendar = Calendar.getInstance();

        // Advance to the next Monday.
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DATE, 1);
        }
        // Move to the Monday of the upcoming week (even if today is Monday).
        calendar.add(Calendar.DATE, 7);

        // Add the offset (0 for Monday, 1 for Tuesday, etc.).
        calendar.add(Calendar.DATE, offset);

        // Format the date as "yyyy-MM-dd".
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }


    // Helper method to compute day offset
    private int getDayOffset(String day) {
        switch (day) {
            case "Måndag": return 0;
            case "Tisdag": return 1;
            case "Onsdag": return 2;
            case "Torsdag": return 3;
            case "Fredag": return 4;
            case "Lördag": return 5;
            case "Söndag": return 6;
            default: throw new IllegalArgumentException("Invalid day: " + day);
        }
    }

}
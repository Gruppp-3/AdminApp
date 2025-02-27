package com.example.restaurantorderapp.lunch;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantorderapp.R;
import com.example.restaurantorderapp.api.ApiService;
import com.example.restaurantorderapp.api.RetrofitClient;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        setupSpinner();
    }

    private void setupSpinner() {
        Spinner daySpinner = findViewById(R.id.daySpinner);
        String[] days = {"Måndag", "Tisdag", "Onsdag", "Torsdag", "Fredag", "Lördag", "Söndag"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(spinnerAdapter);
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

        // Use the spinner's selected day to compute the target date.
        Spinner daySpinner = findViewById(R.id.daySpinner);
        String selectedDay = daySpinner.getSelectedItem().toString();
        String targetDate = getTargetDateForDay(selectedDay);

        lunchItem.put("date", targetDate);
        lunchItem.put("day", selectedDay); // Required currently, why?


        // Optionally, include the lunch type if your backend requires it.
        String lunchType = getIntent().getStringExtra("lunchType");
        if(lunchType != null){
            lunchItem.put("lunchType", lunchType);
        }

        // Now send the lunchItem map via the API call.
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


    // Helper method to compute day offset
    private String getTargetDateForDay(String day) {
        int offset;
        switch(day) {
            case "Måndag": offset = 0; break;
            case "Tisdag": offset = 1; break;
            case "Onsdag": offset = 2; break;
            case "Torsdag": offset = 3; break;
            case "Fredag": offset = 4; break;
            case "Lördag": offset = 5; break;
            case "Söndag": offset = 6; break;
            default: throw new IllegalArgumentException("Invalid day: " + day);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.add(Calendar.DATE, offset);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }



}
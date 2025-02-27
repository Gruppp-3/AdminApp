package com.example.restaurantorderapp.lunch;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantorderapp.R;
import com.example.restaurantorderapp.adapter.LunchAdapter;
import com.example.restaurantorderapp.api.ApiService;
import com.example.restaurantorderapp.api.RetrofitClient;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeeklyMenuPlanningActivity extends AppCompatActivity {
    private static final String[] WEEKDAYS = {"Måndag", "Tisdag", "Onsdag", "Torsdag", "Fredag", "Lördag", "Söndag"};
    private static final int ADD_LUNCH_REQUEST = 1;
    private ApiService apiService;
    private Map<String, List<Map<String, Object>>> weeklyMenu;
    private Map<String, LunchAdapter> dayAdapters;
    private Map<String, RecyclerView> dayRecyclerViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_planning);

        apiService = RetrofitClient.getInstance().getApi();
        weeklyMenu = new HashMap<>();
        dayAdapters = new HashMap<>();
        dayRecyclerViews = new HashMap<>();

        // Initialize each day's container
        for (String day : WEEKDAYS) {
            setupDayContainer(day);
        }

        setupSaveButton();
        loadExistingWeeklyMenu();
    }

    private void showError(String message) {
        Log.e(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setupDayContainer(String day) {
        // Get resource IDs dynamically
        String recyclerViewId = day.toLowerCase() + "RecyclerView";
        String buttonId = "add" + day + "DishButton";

        int recyclerViewResId = getResources().getIdentifier(recyclerViewId, "id", getPackageName());
        int buttonResId = getResources().getIdentifier(buttonId, "id", getPackageName());

        RecyclerView recyclerView = findViewById(recyclerViewResId);
        if (recyclerView == null) {
            Log.e(TAG, "Could not find RecyclerView with ID: " + recyclerViewId);
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dayRecyclerViews.put(day, recyclerView);

        List<Map<String, Object>> dishes = new ArrayList<>();
        LunchAdapter adapter = new LunchAdapter(dishes, "WEEKLY", this::onDishClick);
        recyclerView.setAdapter(adapter);
        dayAdapters.put(day, adapter);
        weeklyMenu.put(day, dishes);

        Button addButton = findViewById(buttonResId);
        if (addButton != null) {
            addButton.setOnClickListener(v -> startAddLunchActivity(day));
        } else {
            Log.e(TAG, "Could not find Button with ID: " + buttonId);
        }
    }

    private void startAddLunchActivity(String day) {
        Intent intent = new Intent(this, AddLunchItemActivity.class);
        intent.putExtra("day", day);
        intent.putExtra("lunchType", "WEEKLY");
        startActivityForResult(intent, ADD_LUNCH_REQUEST);
    }

    private void setupSaveButton() {
        Button saveButton = findViewById(R.id.saveWeeklyMenuButton);
        saveButton.setOnClickListener(v -> saveWeeklyMenu());
    }

    private void saveWeeklyMenu() {
        Calendar calendar = Calendar.getInstance();
        // Find the next Monday
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DATE, 1);
        }
        // Ensure we reference the upcoming Monday (next week), even if today is Monday
        calendar.add(Calendar.DATE, 7);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startOfWeek = dateFormat.format(calendar.getTime());

        Map<String, List<Map<String, Object>>> sanitizedMenu = new HashMap<>();
        for (String day : WEEKDAYS) {
            List<Map<String, Object>> dishes = weeklyMenu.get(day);
            if (dishes != null) {
                sanitizedMenu.put(day, dishes); // Keep Swedish day names
            }
        }

        apiService.createWeeklyMenu(sanitizedMenu, startOfWeek).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(WeeklyMenuPlanningActivity.this,
                            "Veckans meny sparad!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    showError("Kunde inte spara veckans meny. Statuskod: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showError("Nätverksfel: " + t.getMessage());
            }
        });
    }


    private void loadExistingWeeklyMenu() {
        apiService.getWeeklyLunch().enqueue(new retrofit2.Callback<Map<String, List<Map<String, Object>>>>() {
            @Override
            public void onResponse(retrofit2.Call<Map<String, List<Map<String, Object>>>> call,
                                   retrofit2.Response<Map<String, List<Map<String, Object>>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    weeklyMenu = response.body();
                    updateAllAdapters();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Map<String, List<Map<String, Object>>>> call, Throwable t) {
                Toast.makeText(WeeklyMenuPlanningActivity.this,
                        "Kunde inte ladda befintlig meny: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAllAdapters() {
        for (String day : WEEKDAYS) {
            List<Map<String, Object>> dishes = weeklyMenu.get(day);
            if (dishes != null && dayAdapters.get(day) != null) {
                dayAdapters.get(day).updateData(dishes);
            }
        }
    }

    private void onDishClick(Map<String, Object> dish) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Hantera maträtt")
                .setItems(new String[]{"Redigera", "Ta bort"}, (dialog, which) -> {
                    if (which == 0) {
                        // Start edit activity
                        Intent intent = new Intent(this, AddLunchItemActivity.class);
                        intent.putExtra("isEdit", true);
                        intent.putExtra("lunchItem", (HashMap<String, Object>) dish);
                        intent.putExtra("lunchType", "WEEKLY");
                        startActivityForResult(intent, ADD_LUNCH_REQUEST);
                    } else {
                        // Show delete confirmation
                        showDeleteConfirmation(dish);
                    }
                })
                .show();
    }

    private void showDeleteConfirmation(Map<String, Object> dish) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Ta bort maträtt")
                .setMessage("Är du säker på att du vill ta bort denna maträtt?")
                .setPositiveButton("Ja", (dialog, which) -> {
                    Object id = dish.get("id");
                    if (id != null) {
                        deleteDish(((Number) id).longValue());
                    }
                })
                .setNegativeButton("Nej", null)
                .show();
    }

    private void deleteDish(Long id) {
        apiService.deleteLunchDish(id).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(WeeklyMenuPlanningActivity.this,
                            "Maträtt borttagen", Toast.LENGTH_SHORT).show();
                    loadExistingWeeklyMenu(); // Reload the menu
                } else {
                    Toast.makeText(WeeklyMenuPlanningActivity.this,
                            "Kunde inte ta bort maträtten", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                Toast.makeText(WeeklyMenuPlanningActivity.this,
                        "Nätverksfel: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_LUNCH_REQUEST && resultCode == RESULT_OK) {
            loadExistingWeeklyMenu(); // Reload the menu after adding/editing
        }
    }
}
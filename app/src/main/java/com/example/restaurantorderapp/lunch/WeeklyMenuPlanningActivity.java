package com.example.restaurantorderapp.lunch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.restaurantorderapp.R;
import com.example.restaurantorderapp.adapter.LunchAdapter;
import com.example.restaurantorderapp.api.ApiService;
import com.example.restaurantorderapp.api.RetrofitClient;
import com.example.restaurantorderapp.lunch.AddLunchItemActivity;
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
    // Temporarily store new dish items grouped by day
    private Map<String, List<Map<String, Object>>> weeklyMenu;
    // Adapters for each day's RecyclerView
    private Map<String, LunchAdapter> dayAdapters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_planning);

        apiService = RetrofitClient.getInstance().getApi();
        weeklyMenu = new HashMap<>();
        dayAdapters = new HashMap<>();

        // Initialize a container for each day
        for (String day : WEEKDAYS) {
            setupDayContainer(day);
        }

        setupSaveButton();
    }

    // Sets up the UI container for each weekday.
    // It creates a RecyclerView (to show the dishes added so far),
    // displays the upcoming date for that day, and sets a button click listener
    // to add new dish items.
    private void setupDayContainer(String day) {
        String resourceDay = getResourceDayName(day);  // e.g. "monday" for "Måndag"
        int recyclerViewResId = getResources().getIdentifier(resourceDay + "RecyclerView", "id", getPackageName());
        int buttonResId = getResources().getIdentifier("add" + capitalize(resourceDay) + "DishButton", "id", getPackageName());
        int dateTextResId = getResources().getIdentifier(resourceDay + "DateTextView", "id", getPackageName());

        RecyclerView recyclerView = findViewById(recyclerViewResId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Display next week's date for this day.
        if (dateTextResId != 0) {
            String upcomingDate = getUpcomingDateForDay(day);
            ((android.widget.TextView) findViewById(dateTextResId)).setText(upcomingDate);
        }

        // Initialize an empty list for new dishes.
        List<Map<String, Object>> dishes = new ArrayList<>();
        LunchAdapter adapter = new LunchAdapter(dishes, "WEEKLY", null);  // No click listener needed
        recyclerView.setAdapter(adapter);
        dayAdapters.put(day, adapter);
        weeklyMenu.put(day, dishes);

        Button addButton = findViewById(buttonResId);
        addButton.setOnClickListener(v -> startAddLunchActivity(day));
    }

    // Helper method to convert Swedish day names to your resource prefix.
    private String getResourceDayName(String day) {
        switch (day) {
            case "Måndag": return "monday";
            case "Tisdag": return "tisdag";
            case "Onsdag": return "onsdag";
            case "Torsdag": return "torsdag";
            case "Fredag": return "fredag";
            case "Lördag": return "lordag";  // Ensure your XML uses "lordag"
            case "Söndag": return "sondag";
            default: return day.toLowerCase();
        }
    }

    // Capitalize first letter for resource name formation.
    private String capitalize(String s) {
        return (s == null || s.isEmpty()) ? s : s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    // Calculates the upcoming date for a given day (based on next week's Monday as the base).
    private String getUpcomingDateForDay(String day) {
        Calendar calendar = Calendar.getInstance();
        // Set calendar to next Monday.
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        int offset = 0;
        switch(day) {
            case "Måndag": offset = 0; break;
            case "Tisdag": offset = 1; break;
            case "Onsdag": offset = 2; break;
            case "Torsdag": offset = 3; break;
            case "Fredag": offset = 4; break;
            case "Lördag": offset = 5; break;
            case "Söndag": offset = 6; break;
            default: break;
        }
        calendar.add(Calendar.DATE, offset);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    // Starts the AddLunchItemActivity, passing the day for which a new dish is being added.
    private void startAddLunchActivity(String day) {
        Intent intent = new Intent(this, AddLunchItemActivity.class);
        intent.putExtra("day", day);
        intent.putExtra("lunchType", "WEEKLY");
        startActivityForResult(intent, ADD_LUNCH_REQUEST);
    }

    // Sets up the save button to send all the new dish items to the backend.
    private void setupSaveButton() {
        Button saveButton = findViewById(R.id.saveWeeklyMenuButton);
        saveButton.setOnClickListener(v -> saveWeeklyMenu());
    }

    // When the save button is pressed, the weeklyMenu map (with all new dishes) is sent to the API.
    private void saveWeeklyMenu() {
        Calendar calendar = Calendar.getInstance();
        // Set calendar to next Monday.
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startOfWeek = dateFormat.format(calendar.getTime());

        apiService.createWeeklyMenu(weeklyMenu, startOfWeek).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(WeeklyMenuPlanningActivity.this, "Veckans meny sparad!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(WeeklyMenuPlanningActivity.this, "Kunde inte spara veckans meny. Statuskod: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(WeeklyMenuPlanningActivity.this, "Nätverksfel: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Receive the new dish details from AddLunchItemActivity.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_LUNCH_REQUEST && resultCode == RESULT_OK && data != null) {
            String day = data.getStringExtra("day");
            Map<String, Object> newDish = (Map<String, Object>) data.getSerializableExtra("newDish");
            if (day != null && newDish != null) {
                List<Map<String, Object>> dishes = weeklyMenu.get(day);
                if (dishes != null) {
                    dishes.add(newDish);
                    // Update the adapter to reflect the newly added dish.
                    dayAdapters.get(day).updateData(dishes);
                }
            }
        }
    }
}

package com.example.restaurantorderapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.restaurantorderapp.adapter.LunchAdapter;
import com.example.restaurantorderapp.api.ApiService;
import com.example.restaurantorderapp.api.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LunchManagementActivity extends AppCompatActivity {
    private static final String TAG = "LunchManagementActivity";
    private static final int ADD_LUNCH_REQUEST = 1;
    private static final int EDIT_LUNCH_REQUEST = 2;

    private RecyclerView recyclerView;
    private LunchAdapter adapter;
    private FloatingActionButton addFab;
    private ApiService apiService;
    private Spinner lunchTypeSpinner;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private String currentType = "DAILY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunch_management);

        apiService = RetrofitClient.getInstance().getApi();
        setupViews();
        setupSwipeRefresh();
        setupSpinner();
        setupListeners();
        loadLunchData();
        checkForWeeklyMenuUpdate();
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.lunchRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addFab = findViewById(R.id.addLunchFab);
        lunchTypeSpinner = findViewById(R.id.lunchTypeSpinner);
        progressBar = findViewById(R.id.progressBar);

        adapter = new LunchAdapter(new ArrayList<>(), currentType, this::showEditDialog);
        recyclerView.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this::loadLunchData);
    }

    private void setupSpinner() {
        List<String> lunchTypes = new ArrayList<>();
        lunchTypes.add("Dagens Lunch");
        lunchTypes.add("Veckans Luncher");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lunchTypes);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lunchTypeSpinner.setAdapter(spinnerAdapter);

        int position = currentType.equals("DAILY") ? 0 : 1;
        lunchTypeSpinner.setSelection(position);

        lunchTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selectedType = pos == 0 ? "DAILY" : "WEEKLY";
                if (!selectedType.equals(currentType)) {
                    currentType = selectedType;
                    loadLunchData();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void setupListeners() {
        addFab.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Välj menytyp")
                    .setItems(new String[]{"Lägg till lunchalternativ", "Planera lunchsedel för kommande vecka"}, (dialog, which) -> {
                        if (which == 0) {
                            Intent intent = new Intent(this, AddLunchItemActivity.class);
                            intent.putExtra("lunchType", "DAILY");
                            startActivityForResult(intent, ADD_LUNCH_REQUEST);
                        } else {
                            Intent intent = new Intent(this, WeeklyMenuPlanningActivity.class);
                            startActivity(intent);
                        }
                    })
                    .show();
        });
    }

    private void checkForWeeklyMenuUpdate() {
        Calendar calendar = Calendar.getInstance();
        // Prompts the user every sunday to plan weekly menu
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Uppdatera veckomenyn")
                    .setMessage("Vill du lägga till nästa veckas lunchmeny?")
                    .setPositiveButton("Ja", (dialog, which) -> {
                        Intent intent = new Intent(this, WeeklyMenuPlanningActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    })
                    .setNegativeButton("Senare", null)
                    .show();
        }
    }

    private void loadLunchData() {
        showLoading(true);
        Log.d(TAG, "Loading lunch data for type: " + currentType);

        if ("DAILY".equals(currentType)) {
            loadDailyLunch();
        } else {
            loadWeeklyLunch();
        }
    }

    private void loadDailyLunch() {
        apiService.getTodayLunch().enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, Object>> items = new ArrayList<>();

                    Map<String, Object> header = new HashMap<>();
                    header.put("isHeader", true);
                    header.put("dayName", "Dagens Lunch");
                    items.add(header);

                    items.addAll(response.body());
                    adapter.updateData(items);
                } else {
                    showError("Kunde inte ladda dagens lunch-meny");
                }
                showLoading(false);
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                showError("Nätverksfel: " + t.getMessage());
                showLoading(false);
            }
        });
    }

    private void loadWeeklyLunch() {
        apiService.getWeeklyLunch().enqueue(new Callback<Map<String, List<Map<String, Object>>>>() {
            @Override
            public void onResponse(Call<Map<String, List<Map<String, Object>>>> call,
                                   Response<Map<String, List<Map<String, Object>>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, List<Map<String, Object>>> weeklyMenu = response.body();
                    updateWeeklyAdapter(weeklyMenu);
                } else {
                    showError("Kunde inte ladda veckans lunch-meny");
                }
                showLoading(false);
            }

            @Override
            public void onFailure(Call<Map<String, List<Map<String, Object>>>> call, Throwable t) {
                showError("Nätverksfel: " + t.getMessage());
                showLoading(false);
            }
        });
    }


    // Flattens the weekly menu map (key: day, value: list of dish maps) into a single list with headers.
    private void updateWeeklyAdapter(Map<String, List<Map<String, Object>>> weeklyMenu) {
        List<Map<String, Object>> flattenedItems = new ArrayList<>();
        // Define the order of days.
        String[] WEEKDAYS = {"Måndag", "Tisdag", "Onsdag", "Torsdag", "Fredag", "Lördag", "Söndag"};
        for (String day : WEEKDAYS) {
            // Create a header for each day.
            Map<String, Object> header = new HashMap<>();
            header.put("isHeader", true);
            header.put("dayName", day);
            flattenedItems.add(header);
            // Add dishes for that day, if any.
            List<Map<String, Object>> dishes = weeklyMenu.get(day);
            if (dishes != null) {
                flattenedItems.addAll(dishes);
            }
        }
        adapter.updateData(flattenedItems);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);

        if (adapter.getItemCount() == 0) {
            findViewById(R.id.emptyStateText).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.emptyStateText).setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        Log.e(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showEditDialog(Map<String, Object> item) {
        if (item.containsKey("isHeader")) {
            return;
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle("Hantera lunch")
                .setItems(new String[]{"Redigera", "Ta bort"}, (dialog, which) -> {
                    if (which == 0) {
                        Intent intent = new Intent(this, EditLunchItemActivity.class);
                        intent.putExtra("lunchItem", (Serializable) item);
                        intent.putExtra("lunchType", currentType);
                        startActivityForResult(intent, EDIT_LUNCH_REQUEST);
                    } else {
                        showDeleteConfirmationDialog(item);
                    }
                })
                .show();
    }

    private void showDeleteConfirmationDialog(Map<String, Object> item) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Ta bort lunch")
                .setMessage("Är du säker på att du vill ta bort denna lunch?")
                .setPositiveButton("Ja", (dialog, which) -> deleteLunchItem(item))
                .setNegativeButton("Nej", null)
                .show();
    }

    private void deleteLunchItem(Map<String, Object> item) {
        Object rawId = item.get("id");
        if (rawId == null) {
            showError("Kunde inte hitta ID för lunch");
            return;
        }
        Long itemId = ((Number) rawId).longValue();
        showLoading(true);
        apiService.deleteLunchDish(itemId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LunchManagementActivity.this, "Lunch borttagen", Toast.LENGTH_SHORT).show();
                    loadLunchData();
                } else {
                    showError("Kunde inte ta bort lunch. Statuskod: " + response.code());
                }
                showLoading(false);
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showError("Nätverksfel: " + t.getMessage());
                showLoading(false);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && (requestCode == EDIT_LUNCH_REQUEST || requestCode == ADD_LUNCH_REQUEST)) {
            loadLunchData();
        }
    }
}

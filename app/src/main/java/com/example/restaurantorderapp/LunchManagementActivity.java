package com.example.restaurantorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.restaurantorderapp.adapter.LunchAdapter;
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

public class LunchManagementActivity extends AppCompatActivity {
    private static final int ADD_LUNCH_REQUEST = 1;
    private static final int EDIT_LUNCH_REQUEST = 2;

    private RecyclerView recyclerView;
    private LunchAdapter adapter;
    private FloatingActionButton addFab;
    private ApiService apiService;
    private Spinner lunchTypeSpinner;
    private String currentType = "DAILY"; // Default value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunch_management);

        apiService = RetrofitClient.getInstance().getApi();
        setupViews();
        setupSpinner();
        setupListeners();
        loadLunchData();
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.lunchRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addFab = findViewById(R.id.addLunchFab);
        lunchTypeSpinner = findViewById(R.id.lunchTypeSpinner);

        adapter = new LunchAdapter(new ArrayList<>(), currentType, this::showEditDialog);
        recyclerView.setAdapter(adapter);
    }

    private void setupSpinner() {
        String[] lunchTypes = {"Dagens Lunch", "Veckans Lunch"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                lunchTypes
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lunchTypeSpinner.setAdapter(spinnerAdapter);

        lunchTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentType = position == 0 ? "DAILY" : "WEEKLY";
                loadLunchData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupListeners() {
        addFab.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddLunchItemActivity.class);
            intent.putExtra("lunchType", currentType);
            startActivityForResult(intent, ADD_LUNCH_REQUEST);
        });
    }

    private void loadLunchData() {
        Log.d("LunchManagement", "Loading lunch data for type: " + currentType);

        if ("DAILY".equals(currentType)) {
            Call<List<Map<String, Object>>> call = apiService.getTodayLunch();
            Log.d("LunchManagement", "Making daily lunch API call");

            call.enqueue(new Callback<List<Map<String, Object>>>() {
                @Override
                public void onResponse(Call<List<Map<String, Object>>> call,
                                       Response<List<Map<String, Object>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("LunchManagement", "Received data: " + response.body().size() + " items");
                        List<Map<String, Object>> newData = response.body();
                        adapter.updateData(newData);
                    } else {
                        Log.e("LunchManagement", "Error loading data: " + response.code());
                        Toast.makeText(LunchManagementActivity.this,
                                "Kunde inte ladda lunch-meny", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                    Log.e("LunchManagement", "Network error", t);
                    Toast.makeText(LunchManagementActivity.this,
                            "Nätverksfel: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showEditDialog(Map<String, Object> item) {
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
                .setTitle("Ta bort lunchrätt")
                .setMessage("Är du säker på att du vill ta bort denna lunchrätt?")
                .setPositiveButton("Ja", (dialog, which) -> deleteLunchItem(item))
                .setNegativeButton("Avbryt", null)
                .show();
    }



    private void deleteLunchItem(Map<String, Object> item) {
        Object rawId = item.get("id");
        if (rawId == null) {
            Toast.makeText(this, "Kunde inte hitta ID för lunch", Toast.LENGTH_SHORT).show();
            return;
        }

        Long itemId = ((Number) rawId).longValue();

        Call<Void> call = apiService.deleteLunchDish(itemId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LunchManagementActivity.this,
                            "Lunch borttagen", Toast.LENGTH_SHORT).show();

                    // Uppdatera RecyclerView direkt
                    List<Map<String, Object>> currentItems = new ArrayList<>(adapter.getLunchItems());
                    currentItems.remove(item);
                    adapter.updateData(currentItems);

                    // Ladda om data från servern för att säkerställa synkronisering
                    loadLunchData();
                } else {
                    Toast.makeText(LunchManagementActivity.this,
                            "Kunde inte ta bort lunch. Statuskod: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(LunchManagementActivity.this,
                        "Nätverksfel: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("LunchManagement", "onActivityResult - requestCode: " + requestCode + ", resultCode: " + resultCode);

        if (resultCode == RESULT_OK) {
            if (requestCode == EDIT_LUNCH_REQUEST) {
                Log.d("LunchManagement", "Edit lunch request successful, reloading data");
                loadLunchData();
            } else if (requestCode == ADD_LUNCH_REQUEST) {
                Log.d("LunchManagement", "Add lunch request successful, reloading data");
                loadLunchData();
            }
        }
    }
}
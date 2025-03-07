package com.example.restaurantorderapp.workshift;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantorderapp.R;
import com.example.restaurantorderapp.adapter.WorkShiftAdapter;
import com.example.restaurantorderapp.api.ApiService;
import com.example.restaurantorderapp.api.RetrofitClient;
import com.example.restaurantorderapp.model.Employee;
import com.example.restaurantorderapp.model.WorkShift;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MonthlyScheduleActivity extends AppCompatActivity implements WorkShiftAdapter.OnShiftActionListener {

    private static final String TAG = "ManageWorkShiftActivity";
    private ApiService apiService;
    private RecyclerView recyclerView;
    private WorkShiftAdapter adapter;
    private List<WorkShift> workShifts = new ArrayList<>();

    @Override
    public void onDeleteShift(WorkShift workShift) {
        new AlertDialog.Builder(this)
                .setTitle("Ta bort arbetspass")
                .setMessage("Är du säker på att du vill ta bort detta arbetspass?")
                .setPositiveButton("Ja", (dialog, which) -> {
                    // Call API to delete the work shift
                    apiService.deleteWorkShift(workShift.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(MonthlyScheduleActivity.this,
                                        "Arbetspass borttaget",
                                        Toast.LENGTH_SHORT).show();
                                // Reload the work shifts after deletion
                                loadWorkShifts();
                            } else {
                                Toast.makeText(MonthlyScheduleActivity.this,
                                        "Kunde inte ta bort arbetspass",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(MonthlyScheduleActivity.this,
                                    "Fel vid borttagning: " + t.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Nej", null)
                .show();
    }

    @Override
    public void onAssignEmployee(WorkShift workShift) {
        // Show dialog to assign employee to shift
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tilldela anställd");

        // Fetch employees from API
        apiService.getAllEmployees().enqueue(new Callback<List<Employee>>() {
            @Override
            public void onResponse(Call<List<Employee>> call, Response<List<Employee>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Employee> employees = response.body();

                    if (employees.isEmpty()) {
                        Toast.makeText(MonthlyScheduleActivity.this,
                                "Inga anställda hittades", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Create array of employee names for the dialog
                    String[] employeeNames = new String[employees.size()];
                    for (int i = 0; i < employees.size(); i++) {
                        Employee emp = employees.get(i);
                        employeeNames[i] = emp.getFirstName() + " " + emp.getLastName();
                    }

                    // Track selected employee
                    final int[] selectedEmployeeIndex = {-1};

                    // Create the single choice items dialog
                    builder.setSingleChoiceItems(employeeNames, -1, (dialog, which) -> {
                        selectedEmployeeIndex[0] = which;
                    });

                    builder.setPositiveButton("Tilldela", (dialog, which) -> {
                        if (selectedEmployeeIndex[0] != -1) {
                            // Get the selected employee
                            Employee selectedEmployee = employees.get(selectedEmployeeIndex[0]);

                            // Update the work shift with the selected employee
                            workShift.setEmployee(selectedEmployee);

                            // Call API to update the work shift
                            apiService.updateWorkShift(workShift.getId(), workShift).enqueue(new Callback<WorkShift>() {
                                @Override
                                public void onResponse(Call<WorkShift> call, Response<WorkShift> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(MonthlyScheduleActivity.this,
                                                "Anställd tilldelad till arbetspass", Toast.LENGTH_SHORT).show();
                                        // Refresh the list
                                        loadWorkShifts();
                                    } else {
                                        Toast.makeText(MonthlyScheduleActivity.this,
                                                "Kunde inte uppdatera arbetspass", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<WorkShift> call, Throwable t) {
                                    Toast.makeText(MonthlyScheduleActivity.this,
                                            "Fel vid uppdatering: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(MonthlyScheduleActivity.this,
                                    "Ingen anställd vald", Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.setNegativeButton("Avbryt", null);
                    builder.show();
                } else {
                    Toast.makeText(MonthlyScheduleActivity.this,
                            "Kunde inte hämta anställda", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Employee>> call, Throwable t) {
                Toast.makeText(MonthlyScheduleActivity.this,
                        "Fel vid hämtning av anställda: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_work_shift);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter
        adapter = new WorkShiftAdapter(workShifts, this, this);
        recyclerView.setAdapter(adapter);

        // Initialize API service via Retrofit
        apiService = RetrofitClient.getInstance().getApi();

        // Set up the Button for adding new shifts
        findViewById(R.id.fabAddShift).setOnClickListener(v -> {
            showAddShiftDialog();
        });

        // Call the backend to load work shifts
        loadWorkShifts();
    }

    private void showAddShiftDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lägg till arbetspass");

        // Inflate a custom layout for the dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_work_shift, null);
        builder.setView(dialogView);

        // Find views in the dialog layout
        DatePicker datePicker = dialogView.findViewById(R.id.date_picker);
        TimePicker startTimePicker = dialogView.findViewById(R.id.start_time_picker);
        TimePicker endTimePicker = dialogView.findViewById(R.id.end_time_picker);
        EditText descriptionEditText = dialogView.findViewById(R.id.edit_description);

        // Configure time pickers to use 24-hour format
        startTimePicker.setIs24HourView(true);
        endTimePicker.setIs24HourView(true);

        // Set up buttons
        builder.setPositiveButton("Spara", (dialog, which) -> {
            // Create a new WorkShift object from the dialog inputs
            WorkShift newShift = new WorkShift();

            // Get date
            int year = datePicker.getYear();
            int month = datePicker.getMonth() + 1; // Month is 0-based in DatePicker
            int day = datePicker.getDayOfMonth();

            // Get times
            int startHour = startTimePicker.getCurrentHour();
            int startMinute = startTimePicker.getCurrentMinute();
            int endHour = endTimePicker.getCurrentHour();
            int endMinute = endTimePicker.getCurrentMinute();

            // Validate time order
            if (startHour > endHour || (startHour == endHour && startMinute >= endMinute)) {
                Toast.makeText(MonthlyScheduleActivity.this,
                        "Starttid måste vara före sluttid", Toast.LENGTH_SHORT).show();
                return;
            }

            // Format date
            String startDateTime = String.format(Locale.getDefault(),
                    "%04d-%02d-%02dT%02d:%02d:00",
                    year, month, day, startHour, startMinute);

            String endDateTime = String.format(Locale.getDefault(),
                    "%04d-%02d-%02dT%02d:%02d:00",
                    year, month, day, endHour, endMinute);

            // Get description
            String description = descriptionEditText.getText().toString().trim();
            if (description.isEmpty()) {
                description = "Arbetspass " + String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
            }

            // Set work shift properties
            newShift.setStartTime(startDateTime);
            newShift.setEndTime(endDateTime);
            newShift.setDescription(description);

            // For debugging
            Log.d(TAG, "Creating work shift with JSON: " +
                    "{startTime:" + startDateTime +
                    ", endTime:" + endDateTime +
                    ", description:" + description + "}");

            // Send the new shift to the API
            apiService.createWorkShift(newShift).enqueue(new Callback<WorkShift>() {
                @Override
                public void onResponse(Call<WorkShift> call, Response<WorkShift> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(MonthlyScheduleActivity.this,
                                "Arbetspass skapat", Toast.LENGTH_SHORT).show();

                        // Reload the shifts
                        loadWorkShifts();
                    } else {
                        Log.e(TAG, "Failed to create work shift. Code: " + response.code());
                        try {
                            if (response.errorBody() != null) {
                                Log.e(TAG, "Error body: " + response.errorBody().string());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }

                        Toast.makeText(MonthlyScheduleActivity.this,
                                "Kunde inte skapa arbetspass (kod: " + response.code() + ")",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<WorkShift> call, Throwable t) {
                    Log.e(TAG, "Error creating work shift", t);
                    Toast.makeText(MonthlyScheduleActivity.this,
                            "Fel vid skapande av arbetspass: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Avbryt", null);
        builder.show();
    }

    private void loadWorkShifts() {
        apiService.getAllWorkShifts().enqueue(new Callback<List<WorkShift>>() {
            @Override
            public void onResponse(Call<List<WorkShift>> call, Response<List<WorkShift>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<WorkShift> shifts = response.body();
                    Log.d(TAG, "Loaded " + shifts.size() + " work shifts");

                    // Clear existing data and add new shifts
                    workShifts.clear();
                    workShifts.addAll(shifts);

                    // Update the adapter
                    adapter.notifyDataSetChanged();

                    if (shifts.isEmpty()) {
                        Toast.makeText(
                                MonthlyScheduleActivity.this,
                                "No shifts found",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                } else {
                    Log.e(TAG, "Failed to load work shifts. Code: " + response.code());
                    Toast.makeText(
                            MonthlyScheduleActivity.this,
                            "Failed to load work shifts",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<List<WorkShift>> call, Throwable t) {
                Log.e(TAG, "Error loading work shifts: " + t.getMessage());
                Toast.makeText(
                        MonthlyScheduleActivity.this,
                        "Error loading work shifts: " + t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}
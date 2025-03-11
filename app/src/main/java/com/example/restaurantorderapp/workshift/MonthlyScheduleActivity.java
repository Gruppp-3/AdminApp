package com.example.restaurantorderapp.workshift;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MonthlyScheduleActivity extends AppCompatActivity implements WorkShiftAdapter.OnShiftActionListener {

    private static final String TAG = "MonthlyScheduleActivity";
    private ApiService apiService;
    private RecyclerView recyclerView;
    private WorkShiftAdapter adapter;
    private TextView tvCurrentPeriod;
    private ImageButton btnPrevious, btnNext;
    private Spinner employeeFilterSpinner;
    private TextView emptyView;
    private View progressBar;
    private RadioGroup viewToggleGroup;
    private RadioButton radioMonth, radioWeek;
    private List<WorkShift> allWorkShifts = new ArrayList<>();
    private List<WorkShift> filteredWorkShifts = new ArrayList<>();
    private List<Employee> allEmployees = new ArrayList<>();

    private Calendar currentCalendar = Calendar.getInstance();
    private Long selectedEmployeeId = null; // null means show all employees
    private boolean showOnlyUnassigned = false; // Flag to show only unassigned shifts
    private boolean isMonthView = true; // Flag to determine if we're in month or week view

    private SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private SimpleDateFormat weekFormat = new SimpleDateFormat("'Vecka' w, yyyy", Locale.getDefault());
    private SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

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
                                loadWorkShiftsFromApi();
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

        // Check if we already have employees loaded
        if (!allEmployees.isEmpty()) {
            showEmployeeSelectionDialog(builder, workShift, allEmployees);
        } else {
            // Fetch employees from API
            showProgressBar(true);
            apiService.getAllEmployees().enqueue(new Callback<List<Employee>>() {
                @Override
                public void onResponse(Call<List<Employee>> call, Response<List<Employee>> response) {
                    showProgressBar(false);
                    if (response.isSuccessful() && response.body() != null) {
                        allEmployees.clear();
                        allEmployees.addAll(response.body());
                        showEmployeeSelectionDialog(builder, workShift, allEmployees);
                    } else {
                        Toast.makeText(MonthlyScheduleActivity.this,
                                "Kunde inte hämta anställda", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Employee>> call, Throwable t) {
                    showProgressBar(false);
                    Toast.makeText(MonthlyScheduleActivity.this,
                            "Fel vid hämtning av anställda: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showEmployeeSelectionDialog(AlertDialog.Builder builder, WorkShift workShift, List<Employee> employees) {
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
                showProgressBar(true);
                apiService.updateWorkShift(workShift.getId(), workShift).enqueue(new Callback<WorkShift>() {
                    @Override
                    public void onResponse(Call<WorkShift> call, Response<WorkShift> response) {
                        showProgressBar(false);
                        if (response.isSuccessful()) {
                            Toast.makeText(MonthlyScheduleActivity.this,
                                    "Anställd tilldelad till arbetspass", Toast.LENGTH_SHORT).show();
                            // Refresh the list
                            loadWorkShiftsFromApi();
                        } else {
                            Toast.makeText(MonthlyScheduleActivity.this,
                                    "Kunde inte uppdatera arbetspass", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<WorkShift> call, Throwable t) {
                        showProgressBar(false);
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_work_shift);

        // Find views
        recyclerView = findViewById(R.id.recyclerView);
        tvCurrentPeriod = findViewById(R.id.tvCurrentPeriod);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        employeeFilterSpinner = findViewById(R.id.employeeFilterSpinner);
        emptyView = findViewById(R.id.emptyView);
        progressBar = findViewById(R.id.progressBar);
        viewToggleGroup = findViewById(R.id.viewToggleGroup);
        radioMonth = findViewById(R.id.radioMonth);
        radioWeek = findViewById(R.id.radioWeek);

        // Initialize RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter
        adapter = new WorkShiftAdapter(filteredWorkShifts, this, this);
        recyclerView.setAdapter(adapter);

        // Initialize API service via Retrofit
        apiService = RetrofitClient.getInstance().getApi();

        // Set up time period navigation
        setupTimeNavigation();

        // Set up view toggle
        setupViewToggle();

        // Set up Employee filter
        setupEmployeeFilter();

        // Set up the Button for adding new shifts
        findViewById(R.id.fabAddShift).setOnClickListener(v -> {
            showAddShiftDialog();
        });

        // Initial data load
        updatePeriodDisplay();
        loadEmployees();
        loadWorkShiftsFromApi();
    }

    private void setupTimeNavigation() {
        btnPrevious.setOnClickListener(v -> {
            if (isMonthView) {
                // Move to previous month
                currentCalendar.add(Calendar.MONTH, -1);
            } else {
                // Move to previous week
                currentCalendar.add(Calendar.WEEK_OF_YEAR, -1);
            }
            updatePeriodDisplay();
            applyFilters();
        });

        btnNext.setOnClickListener(v -> {
            if (isMonthView) {
                // Move to next month
                currentCalendar.add(Calendar.MONTH, 1);
            } else {
                // Move to next week
                currentCalendar.add(Calendar.WEEK_OF_YEAR, 1);
            }
            updatePeriodDisplay();
            applyFilters();
        });
    }

    private void setupViewToggle() {
        viewToggleGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioMonth) {
                isMonthView = true;
                Log.d(TAG, "Switched to Month view");
            } else if (checkedId == R.id.radioWeek) {
                isMonthView = false;
                Log.d(TAG, "Switched to Week view");
            }
            updatePeriodDisplay();
            applyFilters();
        });
    }

    private void updatePeriodDisplay() {
        if (isMonthView) {
            tvCurrentPeriod.setText(monthYearFormat.format(currentCalendar.getTime()));
        } else {
            // Ensure we're at the start of the week for consistency
            currentCalendar.set(Calendar.DAY_OF_WEEK, currentCalendar.getFirstDayOfWeek());
            tvCurrentPeriod.setText(weekFormat.format(currentCalendar.getTime()));
        }
    }

    private void setupEmployeeFilter() {
        // Create a default "All Employees" option
        List<Employee> displayEmployees = new ArrayList<>();

        Employee allEmployeesOption = new Employee();
        allEmployeesOption.setId(-1L); // Special ID for "All Employees"
        allEmployeesOption.setFirstName("Alla");
        allEmployeesOption.setLastName("anställda");
        displayEmployees.add(allEmployeesOption);

        // Add an option for unassigned shifts
        Employee unassignedOption = new Employee();
        unassignedOption.setId(-2L); // Special ID for "Unassigned Shifts"
        unassignedOption.setFirstName("Endast");
        unassignedOption.setLastName("otilldelade pass");
        displayEmployees.add(unassignedOption);

        // Set up adapter for the spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                displayEmployees.stream()
                        .map(emp -> emp.getFirstName() + " " + emp.getLastName())
                        .collect(Collectors.toList())
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        employeeFilterSpinner.setAdapter(spinnerAdapter);

        // Set listener for selection changes
        employeeFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // "All Employees" selected
                    selectedEmployeeId = null;
                    showOnlyUnassigned = false;
                    Log.d(TAG, "Filter: All employees selected");
                } else if (position == 1) {
                    // "Unassigned Shifts" selected
                    selectedEmployeeId = null;
                    showOnlyUnassigned = true;
                    Log.d(TAG, "Filter: Only unassigned shifts selected");
                } else if (position - 2 < allEmployees.size()) {
                    // Specific employee selected (offset by 2 because of "All" and "Unassigned" options)
                    int employeeIndex = position - 2;
                    selectedEmployeeId = allEmployees.get(employeeIndex).getId();
                    showOnlyUnassigned = false;
                    Log.d(TAG, "Filter: Employee selected with ID: " + selectedEmployeeId +
                            " Name: " + allEmployees.get(employeeIndex).getFirstName() +
                            " " + allEmployees.get(employeeIndex).getLastName());
                }
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedEmployeeId = null;
                showOnlyUnassigned = false;
                applyFilters();
            }
        });
    }

    private void loadEmployees() {
        showProgressBar(true);
        apiService.getAllEmployees().enqueue(new Callback<List<Employee>>() {
            @Override
            public void onResponse(Call<List<Employee>> call, Response<List<Employee>> response) {
                showProgressBar(false);
                if (response.isSuccessful() && response.body() != null) {
                    allEmployees.clear();
                    allEmployees.addAll(response.body());

                    // Update spinner with employee data
                    updateEmployeeSpinner();
                } else {
                    Toast.makeText(MonthlyScheduleActivity.this,
                            "Kunde inte hämta anställda", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Employee>> call, Throwable t) {
                showProgressBar(false);
                Toast.makeText(MonthlyScheduleActivity.this,
                        "Fel vid hämtning av anställda: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmployeeSpinner() {
        List<String> employeeNames = new ArrayList<>();
        employeeNames.add("Alla anställda");
        employeeNames.add("Endast otilldelade pass");

        for (Employee emp : allEmployees) {
            employeeNames.add(emp.getFirstName() + " " + emp.getLastName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                employeeNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        employeeFilterSpinner.setAdapter(adapter);
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

        // Set the date picker to current selected period
        if (isMonthView) {
            // Month view - set to current month/year
            datePicker.updateDate(
                    currentCalendar.get(Calendar.YEAR),
                    currentCalendar.get(Calendar.MONTH),
                    1  // First day of month as default
            );
        } else {
            // Week view - set to current week
            Calendar weekCalendar = (Calendar) currentCalendar.clone();
            weekCalendar.set(Calendar.DAY_OF_WEEK, weekCalendar.getFirstDayOfWeek());
            datePicker.updateDate(
                    weekCalendar.get(Calendar.YEAR),
                    weekCalendar.get(Calendar.MONTH),
                    weekCalendar.get(Calendar.DAY_OF_MONTH)
            );
        }

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
            showProgressBar(true);
            apiService.createWorkShift(newShift).enqueue(new Callback<WorkShift>() {
                @Override
                public void onResponse(Call<WorkShift> call, Response<WorkShift> response) {
                    showProgressBar(false);
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(MonthlyScheduleActivity.this,
                                "Arbetspass skapat", Toast.LENGTH_SHORT).show();

                        // Reload the shifts
                        loadWorkShiftsFromApi();
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
                    showProgressBar(false);
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

    private void loadWorkShiftsFromApi() {
        showProgressBar(true);
        apiService.getAllWorkShifts().enqueue(new Callback<List<WorkShift>>() {
            @Override
            public void onResponse(Call<List<WorkShift>> call, Response<List<WorkShift>> response) {
                showProgressBar(false);
                if (response.isSuccessful() && response.body() != null) {
                    allWorkShifts.clear();
                    allWorkShifts.addAll(response.body());
                    Log.d(TAG, "Loaded " + allWorkShifts.size() + " work shifts");

                    // Apply current filters
                    applyFilters();
                } else {
                    Log.e(TAG, "Failed to load work shifts. Code: " + response.code());
                    Toast.makeText(
                            MonthlyScheduleActivity.this,
                            "Failed to load work shifts",
                            Toast.LENGTH_SHORT
                    ).show();
                    showEmptyView(true);
                }
            }

            @Override
            public void onFailure(Call<List<WorkShift>> call, Throwable t) {
                showProgressBar(false);
                Log.e(TAG, "Error loading work shifts: " + t.getMessage());
                Toast.makeText(
                        MonthlyScheduleActivity.this,
                        "Error loading work shifts: " + t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
                showEmptyView(true);
            }
        });
    }

    private void applyFilters() {
        filteredWorkShifts.clear();

        // Get time period for filtering based on current view mode
        int filterYear = currentCalendar.get(Calendar.YEAR);
        int filterMonth = 0;
        int filterWeek = 0;

        if (isMonthView) {
            // Month view - filter by month and year
            filterMonth = currentCalendar.get(Calendar.MONTH) + 1; // 1-based month
            Log.d(TAG, "Applying month filter - Year: " + filterYear + ", Month: " + filterMonth);
        } else {
            // Week view - filter by week of year and year
            filterWeek = currentCalendar.get(Calendar.WEEK_OF_YEAR);
            Log.d(TAG, "Applying week filter - Year: " + filterYear + ", Week: " + filterWeek);
        }

        Log.d(TAG, "Applying filters - SelectedEmployeeId: " + selectedEmployeeId +
                ", ShowOnlyUnassigned: " + showOnlyUnassigned);

        // Debug the total shifts we're filtering
        Log.d(TAG, "Total shifts before filtering: " + allWorkShifts.size());

        for (WorkShift shift : allWorkShifts) {
            try {
                // Parse the date from the shift's start time
                Date shiftDate = apiDateFormat.parse(shift.getStartTime());
                Calendar shiftCal = Calendar.getInstance();
                shiftCal.setTime(shiftDate);

                int shiftYear = shiftCal.get(Calendar.YEAR);
                int shiftMonth = shiftCal.get(Calendar.MONTH) + 1; // 1-based month
                int shiftWeek = shiftCal.get(Calendar.WEEK_OF_YEAR);

                // Check if the shift matches the time period filter
                boolean matchesTimePeriod;

                if (isMonthView) {
                    // Month view - match by month and year
                    matchesTimePeriod = (shiftMonth == filterMonth && shiftYear == filterYear);
                } else {
                    // Week view - match by week of year and year
                    matchesTimePeriod = (shiftWeek == filterWeek && shiftYear == filterYear);
                }

                // Debug information about this shift
                Log.d(TAG, "Shift ID: " + shift.getId() +
                        ", Date: " + shift.getStartTime() +
                        ", Year: " + shiftYear +
                        ", Month: " + shiftMonth +
                        ", Week: " + shiftWeek +
                        ", Matches time period: " + matchesTimePeriod);

                // Check if the shift matches the employee filter or unassigned filter
                boolean matchesEmployee;

                Employee shiftEmployee = shift.getEmployee();
                Long shiftEmployeeId = (shiftEmployee != null) ? shiftEmployee.getId() : null;

                if (showOnlyUnassigned) {
                    // Show only unassigned shifts
                    matchesEmployee = shiftEmployee == null;
                    Log.d(TAG, "Unassigned filter - matches: " + matchesEmployee);
                } else if (selectedEmployeeId == null) {
                    // Show all employees
                    matchesEmployee = true;
                    Log.d(TAG, "All employees filter - matches: true");
                } else {
                    // Show specific employee
                    matchesEmployee = shiftEmployee != null &&
                            shiftEmployeeId != null &&
                            shiftEmployeeId.equals(selectedEmployeeId);
                    Log.d(TAG, "Specific employee filter (" + selectedEmployeeId + ") - matches: " + matchesEmployee);
                }

                if (matchesTimePeriod && matchesEmployee) {
                    filteredWorkShifts.add(shift);
                    Log.d(TAG, "Added shift to filtered list");
                }
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing date: " + shift.getStartTime(), e);
            }
        }

        // Debug the filtered results
        Log.d(TAG, "Total shifts after filtering: " + filteredWorkShifts.size());

        // Update the adapter
        adapter.notifyDataSetChanged();

        // Show empty view if no shifts match filters
        showEmptyView(filteredWorkShifts.isEmpty());
    }

    private void showProgressBar(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showEmptyView(boolean show) {
        emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
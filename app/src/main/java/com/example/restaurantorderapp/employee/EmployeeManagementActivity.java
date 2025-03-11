package com.example.restaurantorderapp.employee;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantorderapp.R;
import com.example.restaurantorderapp.adapter.EmployeeAdapter;
import com.example.restaurantorderapp.api.ApiService;
import com.example.restaurantorderapp.api.RetrofitClient;
import com.example.restaurantorderapp.model.Employee;
import com.example.restaurantorderapp.workshift.MonthlyScheduleActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeeManagementActivity extends AppCompatActivity implements EmployeeAdapter.OnEmployeeActionListener {

    private static final String TAG = "EmployeeManagement";

    private RecyclerView recyclerView;
    private EmployeeAdapter adapter;
    private List<Employee> employeeList = new ArrayList<>();
    private ApiService apiService;
    private View progressBar;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_management);

        // Initialize the API service
        apiService = RetrofitClient.getInstance().getApi();

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);
        FloatingActionButton fabAddEmployee = findViewById(R.id.fabAddEmployee);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EmployeeAdapter(this, employeeList, this);
        recyclerView.setAdapter(adapter);

        // Button to navigate to Monthly Schedule
        findViewById(R.id.btnMonthlySchedule).setOnClickListener(v -> {
            Intent intent = new Intent(EmployeeManagementActivity.this, MonthlyScheduleActivity.class);
            startActivity(intent);
        });

        // FAB for adding new employees
        fabAddEmployee.setOnClickListener(v -> showAddEditEmployeeDialog(null));

        // Load employees
        loadEmployees();
    }

    private void loadEmployees() {
        showProgress(true);
        apiService.getAllEmployees().enqueue(new Callback<List<Employee>>() {
            @Override
            public void onResponse(Call<List<Employee>> call, Response<List<Employee>> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    employeeList.clear();
                    employeeList.addAll(response.body());
                    adapter.updateData(employeeList);
                    updateEmptyView();

                    Log.d(TAG, "Loaded " + employeeList.size() + " employees");
                } else {
                    Toast.makeText(EmployeeManagementActivity.this,
                            "Kunde inte hämta anställda", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error loading employees. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Employee>> call, Throwable t) {
                showProgress(false);
                Toast.makeText(EmployeeManagementActivity.this,
                        "Fel vid hämtning: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Network error loading employees", t);
            }
        });
    }

    private void updateEmptyView() {
        if (employeeList.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEditEmployee(Employee employee) {
        showAddEditEmployeeDialog(employee);
    }

    @Override
    public void onDeleteEmployee(Employee employee) {
        new AlertDialog.Builder(this)
                .setTitle("Ta bort anställd")
                .setMessage("Är du säker på att du vill ta bort " +
                        employee.getFirstName() + " " + employee.getLastName() + "?")
                .setPositiveButton("Ta bort", (dialog, which) -> deleteEmployee(employee))
                .setNegativeButton("Avbryt", null)
                .show();
    }

    private void showAddEditEmployeeDialog(Employee employee) {
        // Set up the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_employee, null);
        builder.setView(dialogView);

        // Get references to views
        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        TextInputEditText etEmployeeId = dialogView.findViewById(R.id.etEmployeeId); // New ID field
        TextInputEditText etFirstName = dialogView.findViewById(R.id.etFirstName);
        TextInputEditText etLastName = dialogView.findViewById(R.id.etLastName);
        TextInputEditText etPhoneNumber = dialogView.findViewById(R.id.etPhoneNumber);
        CheckBox cbIsAdmin = dialogView.findViewById(R.id.cbIsAdmin);

        // Set dialog title based on mode (add or edit)
        boolean isEditMode = employee != null;
        dialogTitle.setText(isEditMode ? "Redigera anställd" : "Lägg till anställd");

        // Pre-fill fields if in edit mode
        if (isEditMode) {
            etEmployeeId.setText(String.valueOf(employee.getId())); // Set the ID
            etEmployeeId.setEnabled(false); // Disable ID editing in edit mode
            etFirstName.setText(employee.getFirstName());
            etLastName.setText(employee.getLastName());
            etPhoneNumber.setText(employee.getPhoneNumber());
            cbIsAdmin.setChecked(employee.getIsAdmin() != null && employee.getIsAdmin());
        }

        // Create and show the dialog
        AlertDialog dialog = builder.setPositiveButton("Spara", null)
                .setNegativeButton("Avbryt", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                // Validate input
                String idStr = etEmployeeId.getText().toString().trim();
                String firstName = etFirstName.getText().toString().trim();
                String lastName = etLastName.getText().toString().trim();
                String phoneNumber = etPhoneNumber.getText().toString().trim();
                boolean isAdmin = cbIsAdmin.isChecked();

                // Validate ID
                if (idStr.isEmpty()) {
                    Toast.makeText(EmployeeManagementActivity.this,
                            "Anställd ID måste fyllas i",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Parse ID
                Long id;
                try {
                    id = Long.parseLong(idStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(EmployeeManagementActivity.this,
                            "Anställd ID måste vara ett nummer",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate other required fields
                if (firstName.isEmpty() || lastName.isEmpty()) {
                    Toast.makeText(EmployeeManagementActivity.this,
                            "Förnamn och efternamn måste fyllas i",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create or update employee object
                Employee updatedEmployee = isEditMode ? employee : new Employee();
                updatedEmployee.setId(id);
                updatedEmployee.setFirstName(firstName);
                updatedEmployee.setLastName(lastName);
                updatedEmployee.setPhoneNumber(phoneNumber);
                updatedEmployee.setIsAdmin(isAdmin);

                // Save to API
                if (isEditMode) {
                    updateEmployee(updatedEmployee, dialog);
                } else {
                    createEmployee(updatedEmployee, dialog);
                }
            });
        });

        dialog.show();
    }



    private void createEmployee(Employee employee, AlertDialog dialog) {
        showProgress(true);
        apiService.createEmployee(employee).enqueue(new Callback<Employee>() {
            @Override
            public void onResponse(Call<Employee> call, Response<Employee> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    // Add new employee to list and update adapter
                    employeeList.add(response.body());
                    adapter.updateData(employeeList);
                    updateEmptyView();

                    dialog.dismiss();
                    Toast.makeText(EmployeeManagementActivity.this,
                            "Anställd skapad", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "API Error: " + response.code() + " - " + errorBody);

                        Toast.makeText(EmployeeManagementActivity.this,
                                "Kunde inte skapa anställd (kod: " + response.code() + ")",
                                Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error response", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Employee> call, Throwable t) {
                showProgress(false);
                Toast.makeText(EmployeeManagementActivity.this,
                        "Nätverksfel: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Network error creating employee", t);
            }
        });
    }

    private void updateEmployee(Employee employee, AlertDialog dialog) {
        showProgress(true);
        apiService.updateEmployee(employee.getId(), employee).enqueue(new Callback<Employee>() {
            @Override
            public void onResponse(Call<Employee> call, Response<Employee> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    // Update employee in list and refresh adapter
                    int position = -1;
                    for (int i = 0; i < employeeList.size(); i++) {
                        if (employeeList.get(i).getId().equals(employee.getId())) {
                            position = i;
                            break;
                        }
                    }

                    if (position != -1) {
                        employeeList.set(position, response.body());
                        adapter.updateData(employeeList);
                    }

                    dialog.dismiss();
                    Toast.makeText(EmployeeManagementActivity.this,
                            "Anställd uppdaterad", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "API Error: " + response.code() + " - " + errorBody);

                        Toast.makeText(EmployeeManagementActivity.this,
                                "Kunde inte uppdatera anställd (kod: " + response.code() + ")",
                                Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error response", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Employee> call, Throwable t) {
                showProgress(false);
                Toast.makeText(EmployeeManagementActivity.this,
                        "Nätverksfel: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Network error updating employee", t);
            }
        });
    }

    private void deleteEmployee(Employee employee) {
        showProgress(true);
        apiService.deleteEmployee(employee.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                showProgress(false);
                if (response.isSuccessful()) {
                    // Remove the employee from the list and update adapter
                    employeeList.removeIf(emp -> emp.getId().equals(employee.getId()));
                    adapter.updateData(employeeList);
                    updateEmptyView();

                    Toast.makeText(EmployeeManagementActivity.this,
                            "Anställd borttagen", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "API Error: " + response.code() + " - " + errorBody);

                        Toast.makeText(EmployeeManagementActivity.this,
                                "Kunde inte ta bort anställd (kod: " + response.code() + ")",
                                Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error response", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showProgress(false);
                Toast.makeText(EmployeeManagementActivity.this,
                        "Nätverksfel: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Network error deleting employee", t);
            }
        });
    }
}
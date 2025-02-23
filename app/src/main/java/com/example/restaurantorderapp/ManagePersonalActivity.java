package com.example.restaurantorderapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class ManagePersonalActivity extends AppCompatActivity {
    private EditText staffNameInput;
    private EditText staffRoleInput;
    private EditText staffDateInput;
    private Button editStaffBtn;
    private Button deleteStaffBtn;
    private Button saveStaffBtn;
    private ListView staffListView;
    private ArrayList<String> staffList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_staff);

        // Initialize views
        staffNameInput = findViewById(R.id.staffNameInput);
        staffRoleInput = findViewById(R.id.staffRoleInput);
        staffDateInput = findViewById(R.id.staffDateInput);
        saveStaffBtn = findViewById(R.id.saveStaffBtn);
        editStaffBtn = findViewById(R.id.editStaffBtn);
        deleteStaffBtn = findViewById(R.id.deleteStaffBtn);
        staffListView = findViewById(R.id.staffListView);

        staffList = new ArrayList<>();

        // Set up DatePickerDialog for staffDateInput
        staffDateInput.setOnClickListener(view -> showDatePicker());

        // Save staff logic
        saveStaffBtn.setOnClickListener(view -> {
            String name = staffNameInput.getText().toString().trim();
            String role = staffRoleInput.getText().toString().trim();
            String date = staffDateInput.getText().toString().trim();

            if (!name.isEmpty() && !role.isEmpty()) {
                staffList.add(name + " - " + role + " - " + date);
                Toast.makeText(this, "Personal har lagts till!", Toast.LENGTH_SHORT).show();
                staffNameInput.setText("");
                staffRoleInput.setText("");
                staffDateInput.setText("");
            } else {
                Toast.makeText(this, "Vänligen ange giltiga uppgifter", Toast.LENGTH_SHORT).show();
            }

        });

        // Edit staff logic
        editStaffBtn.setOnClickListener(view -> {
            Toast.makeText(this, "Redigering kommer snart", Toast.LENGTH_SHORT).show();
        });

        // Delete staff logic
        deleteStaffBtn.setOnClickListener(view -> {
            Toast.makeText(this, "Radering kommer snart", Toast.LENGTH_SHORT).show();
        });

    }

    private void showDatePicker() {
        // Get current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

    // Create DatePickerDialog
    DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, selectedYear, selectedMonth, selectedDay) -> {
                // Format selected date and set it to the input field
                String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                staffDateInput.setText(selectedDate);
            },
            year, month, day);

        datePickerDialog.show();

    }
}



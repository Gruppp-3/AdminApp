package com.example.restaurantorderapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantorderapp.R;
import com.example.restaurantorderapp.model.Employee;

import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private static final String TAG = "EmployeeAdapter";
    private List<Employee> employees;
    private Context context;
    private OnEmployeeActionListener listener;

    public interface OnEmployeeActionListener {
        void onEditEmployee(Employee employee);
        void onDeleteEmployee(Employee employee);
    }

    public EmployeeAdapter(Context context, List<Employee> employees, OnEmployeeActionListener listener) {
        this.context = context;
        this.employees = employees;
        this.listener = listener;
    }

    public void updateData(List<Employee> newEmployees) {
        this.employees = newEmployees;
        logEmployeeDetails(); // Log details before refreshing
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_employee, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee employee = employees.get(position);

        // Set employee name
        String fullName = employee.getFirstName() + " " + employee.getLastName();
        holder.tvEmployeeName.setText(fullName);

        // Set phone number - safely handle null
        String phoneNumber = employee.getPhoneNumber();
        // Explicitly log the phone number for debugging
        Log.d(TAG, "Phone for employee " + employee.getId() + ": '" + phoneNumber + "'");

        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            holder.tvEmployeePhone.setText(phoneNumber);
        } else {
            holder.tvEmployeePhone.setText("Inget telefonnummer");
        }

        // Set admin status
        Boolean isAdmin = employee.getIsAdmin();
        holder.tvEmployeeRole.setText("Admin: " + ((isAdmin != null && isAdmin) ? "Ja" : "Nej"));

        // Set button click listeners
        holder.btnEditEmployee.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditEmployee(employee);
            }
        });

        holder.btnDeleteEmployee.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteEmployee(employee);
            }
        });

        // Make the entire item clickable
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditEmployee(employee);
            }
        });
    }

    @Override
    public int getItemCount() {
        return employees != null ? employees.size() : 0;
    }

    static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmployeeName;
        TextView tvEmployeePhone;
        TextView tvEmployeeRole;
        Button btnEditEmployee;
        Button btnDeleteEmployee;

        EmployeeViewHolder(View itemView) {
            super(itemView);
            tvEmployeeName = itemView.findViewById(R.id.tvEmployeeName);
            tvEmployeePhone = itemView.findViewById(R.id.tvEmployeePhone);
            tvEmployeeRole = itemView.findViewById(R.id.tvEmployeeRole);
            btnEditEmployee = itemView.findViewById(R.id.btnEditEmployee);
            btnDeleteEmployee = itemView.findViewById(R.id.btnDeleteEmployee);
        }
    }
    // Add this to your EmployeeAdapter class
    private void logEmployeeDetails() {
        Log.d(TAG, "Current employees in adapter: " + employees.size());
        for (int i = 0; i < employees.size(); i++) {
            Employee emp = employees.get(i);
            Log.d(TAG, "Employee[" + i + "]: ID=" + emp.getId()
                    + ", Name=" + emp.getFirstName() + " " + emp.getLastName()
                    + ", Phone='" + emp.getPhoneNumber() + "'"
                    + ", Admin=" + emp.getIsAdmin());
        }
    }
}
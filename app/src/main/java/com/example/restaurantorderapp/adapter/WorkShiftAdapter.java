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
import com.example.restaurantorderapp.model.WorkShift;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkShiftAdapter extends RecyclerView.Adapter<WorkShiftAdapter.WorkShiftViewHolder> {

    private List<WorkShift> workShifts;
    private Context context;
    private OnShiftActionListener listener;

    private SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat displayTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private SimpleDateFormat displayDayFormat = new SimpleDateFormat("EEE d MMM", Locale.getDefault());

    public interface OnShiftActionListener {
        void onDeleteShift(WorkShift workShift);
        void onAssignEmployee(WorkShift workShift);
    }

    public WorkShiftAdapter(List<WorkShift> workShifts, Context context, OnShiftActionListener listener) {
        this.workShifts = workShifts;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WorkShiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_work_shift, parent, false);
        return new WorkShiftViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkShiftViewHolder holder, int position) {
        WorkShift workShift = workShifts.get(position);

        try {
            // Parse dates for display
            Date startDate = apiDateFormat.parse(workShift.getStartTime());
            Date endDate = apiDateFormat.parse(workShift.getEndTime());

            if (startDate != null && endDate != null) {
                // Set the date (e.g., "Mon 15 Apr")
                holder.tvShiftDate.setText(displayDayFormat.format(startDate));

                // Set the time (e.g., "08:00 - 16:00")
                String timeRange = displayTimeFormat.format(startDate) +
                        " - " +
                        displayTimeFormat.format(endDate);
                holder.tvShiftTime.setText(timeRange);
            } else {
                // Fallback if parsing fails
                holder.tvShiftDate.setText(workShift.getStartTime().substring(0, 10));
                holder.tvShiftTime.setText("Time not available");
            }
        } catch (ParseException e) {
            // Fallback if parsing fails
            holder.tvShiftDate.setText(workShift.getStartTime().substring(0, 10));
            holder.tvShiftTime.setText("Time not available");
        }

        // Set employee name
        if (workShift.getEmployee() != null &&
                workShift.getEmployee().getFirstName() != null &&
                workShift.getEmployee().getLastName() != null) {

            String employeeName = workShift.getEmployee().getFirstName() + " " +
                    workShift.getEmployee().getLastName();
            holder.tvEmployeeName.setText(employeeName);
            holder.btnAssignEmployee.setText("Ändra"); // Change button text to "Edit"

            // Log the employee name for debugging
            Log.d("WorkShiftAdapter", "Shift ID: " + workShift.getId() +
                    " assigned to employee: " + employeeName +
                    " (ID: " + workShift.getEmployee().getId() + ")");
        } else {
            holder.tvEmployeeName.setText("Ej tilldelad");
            holder.btnAssignEmployee.setText("Tilldela"); // "Assign"

            // Log that this shift is unassigned
            Log.d("WorkShiftAdapter", "Shift ID: " + workShift.getId() + " is unassigned");
        }

        // Set button listeners
        holder.btnDeleteShift.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteShift(workShift);
            }
        });

        holder.btnAssignEmployee.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAssignEmployee(workShift);
            }
        });
    }

    @Override
    public int getItemCount() {
        return workShifts.size();
    }

    static class WorkShiftViewHolder extends RecyclerView.ViewHolder {
        TextView tvShiftDate;
        TextView tvShiftTime;
        TextView tvEmployeeName;
        Button btnAssignEmployee;
        Button btnDeleteShift;

        WorkShiftViewHolder(View itemView) {
            super(itemView);
            tvShiftDate = itemView.findViewById(R.id.tvShiftDate);
            tvShiftTime = itemView.findViewById(R.id.tvShiftTime);
            tvEmployeeName = itemView.findViewById(R.id.tvEmployeeName);
            btnAssignEmployee = itemView.findViewById(R.id.btnAssignEmployee);
            btnDeleteShift = itemView.findViewById(R.id.btnDeleteShift);
        }
    }
}
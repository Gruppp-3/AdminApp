package com.example.restaurantorderapp.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantorderapp.R;
import com.example.restaurantorderapp.model.WorkShift;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkShiftAdapter extends RecyclerView.Adapter<WorkShiftAdapter.WorkShiftViewHolder> {

    private List<WorkShift> workShifts;
    private Context context;
    private OnShiftActionListener listener;

    // Simplified date formatters
    private SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
    private SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat outputTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public interface OnShiftActionListener {
        void onAssignEmployee(WorkShift workShift);
        void onDeleteShift(WorkShift workShift);
    }

    public WorkShiftAdapter(List<WorkShift> workShifts, Context context, OnShiftActionListener listener) {
        this.workShifts = workShifts;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WorkShiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_work_shift, parent, false);
        return new WorkShiftViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull WorkShiftViewHolder holder, int position) {
        WorkShift workShift = workShifts.get(position);

        try {
            // Parse dates
            Date startDate = inputDateFormat.parse(workShift.getStartTime());
            Date endDate = inputDateFormat.parse(workShift.getEndTime());

            // Format and set date and time text
            holder.tvShiftDate.setText(outputDateFormat.format(startDate));

            String timeRange = outputTimeFormat.format(startDate) + " - " +
                    outputTimeFormat.format(endDate);
            holder.tvShiftTime.setText(timeRange);

        } catch (ParseException e) {
            // Fallback if there's a parsing error
            holder.tvShiftDate.setText("Invalid date");
            holder.tvShiftTime.setText(workShift.getStartTime() + " - " + workShift.getEndTime());
        }

        // Set employee name if available, otherwise show "Unassigned"
        if (workShift.getEmployee() != null) {
            holder.tvEmployeeName.setText(workShift.getEmployeeName());
            holder.tvEmployeeName.setTextColor(context.getResources().getColor(android.R.color.black, null));
            holder.tvEmployeeName.setTypeface(null, Typeface.ITALIC);
            holder.btnAssignEmployee.setText("Ändra");
        } else {
            holder.tvEmployeeName.setText("Ej tilldelad");
            holder.tvEmployeeName.setTextColor(context.getResources().getColor(android.R.color.darker_gray, null));
            holder.tvEmployeeName.setTypeface(null, Typeface.ITALIC);
            holder.btnAssignEmployee.setText("Tilldela");
        }

        // Set button click listeners
        holder.btnAssignEmployee.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAssignEmployee(workShift);
            }
        });

        holder.btnDeleteShift.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteShift(workShift);
            }
        });
    }

    @Override
    public int getItemCount() {
        return workShifts.size();
    }

    public void updateData(List<WorkShift> newShifts) {
        this.workShifts = newShifts;
        notifyDataSetChanged();
    }

    public static class WorkShiftViewHolder extends RecyclerView.ViewHolder {
        TextView tvShiftDate;
        TextView tvShiftTime;
        TextView tvEmployeeName;
        Button btnAssignEmployee;
        Button btnDeleteShift;

        public WorkShiftViewHolder(@NonNull View itemView) {
            super(itemView);
            tvShiftDate = itemView.findViewById(R.id.tvShiftDate);
            tvShiftTime = itemView.findViewById(R.id.tvShiftTime);
            tvEmployeeName = itemView.findViewById(R.id.tvEmployeeName);
            btnAssignEmployee = itemView.findViewById(R.id.btnAssignEmployee);
            btnDeleteShift = itemView.findViewById(R.id.btnDeleteShift);
        }
    }
}
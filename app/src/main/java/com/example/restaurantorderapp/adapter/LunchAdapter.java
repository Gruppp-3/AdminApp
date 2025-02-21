package com.example.restaurantorderapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantorderapp.R;

import java.util.List;
import java.util.Map;

public class LunchAdapter extends RecyclerView.Adapter<LunchAdapter.ViewHolder> {
    private List<Map<String, Object>> lunchItems;
    private OnLunchItemClickListener listener;

    public interface OnLunchItemClickListener {
        void onItemClick(Map<String, Object> item);
    }

    public LunchAdapter(List<Map<String, Object>> lunchItems, OnLunchItemClickListener listener) {
        this.lunchItems = lunchItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lunch_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> item = lunchItems.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return lunchItems != null ? lunchItems.size() : 0;
    }

    public void updateData(List<Map<String, Object>> newItems) {
        this.lunchItems = newItems;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView priceTextView;
        private final TextView descriptionTextView;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.lunchDishName);
            priceTextView = itemView.findViewById(R.id.lunchDishPrice);
            descriptionTextView = itemView.findViewById(R.id.lunchDishDescription);
        }

        void bind(Map<String, Object> item, OnLunchItemClickListener listener) {
            nameTextView.setText((String) item.get("dish_name"));
            priceTextView.setText(String.format("%.2f kr", item.get("dish_price")));
            descriptionTextView.setText((String) item.get("dish_description"));

            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}

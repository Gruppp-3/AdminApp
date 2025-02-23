package com.example.restaurantorderapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantorderapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
public class LunchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<Map<String, Object>> lunchItems;
    private String currentType;
    private OnLunchItemClickListener listener;

    public interface OnLunchItemClickListener {
        void onItemClick(Map<String, Object> item);
    }

    public LunchAdapter(List<Map<String, Object>> lunchItems, String type, OnLunchItemClickListener listener) {
        this.lunchItems = lunchItems;
        this.currentType = type;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        Map<String, Object> item = lunchItems.get(position);
        return item.containsKey("isHeader") ? TYPE_HEADER : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.lunch_day_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.lunch_item_layout, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Map<String, Object> item = lunchItems.get(position);

        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind((String) item.get("dayName"));
        } else {
            ((ItemViewHolder) holder).bind(item, listener);
        }
    }

    @Override
    public int getItemCount() {
        return lunchItems != null ? lunchItems.size() : 0;
    }

    public void updateData(List<Map<String, Object>> newItems) {
        this.lunchItems = newItems;
        notifyDataSetChanged();
    }

    public List<Map<String, Object>> getLunchItems() {
        return new ArrayList<>(lunchItems);
    }

    // ViewHolder for day headers
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView dayNameText;

        HeaderViewHolder(View itemView) {
            super(itemView);
            dayNameText = itemView.findViewById(R.id.dayNameText); // This matches the ID in lunch_day_header.xml
        }

        void bind(String dayName) {
            dayNameText.setText(dayName);
        }
    }

    // ViewHolder for lunch items
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView priceTextView;
        private final TextView descriptionTextView;

        ItemViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.lunchDishName);
            priceTextView = itemView.findViewById(R.id.lunchDishPrice);
            descriptionTextView = itemView.findViewById(R.id.lunchDishDescription);
        }

        void bind(Map<String, Object> item, OnLunchItemClickListener listener) {
            // Handle name
            String name = (String) item.get("dish_name");
            nameTextView.setText(name != null ? name : "");

            // Handle price
            Object priceObj = item.get("dish_price");
            if (priceObj != null) {
                double price = ((Number) priceObj).doubleValue();
                priceTextView.setText(String.format(Locale.getDefault(), "%.2f kr", price));
            } else {
                priceTextView.setText("");
            }

            // Handle description
            String description = (String) item.get("dish_description");
            if (description != null && !description.trim().isEmpty()) {
                descriptionTextView.setVisibility(View.VISIBLE);
                descriptionTextView.setText(description);
            } else {
                descriptionTextView.setVisibility(View.GONE);
            }

            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
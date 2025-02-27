package com.example.restaurantorderapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantorderapp.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;
import java.util.Map;

public class AlacarteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private List<Map<String, Object>> menuItems;
    private OnMenuItemClickListener listener;

    public interface OnMenuItemClickListener {
        void onItemClick(Map<String, Object> item);
    }

    public AlacarteAdapter(List<Map<String, Object>> menuItems, OnMenuItemClickListener listener) {
        this.menuItems = menuItems;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        Map<String, Object> item = menuItems.get(position);
        return item.containsKey("isHeader") ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.category_header_layout, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.menu_item_layout, parent, false);
            return new MenuItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Map<String, Object> item = menuItems.get(position);

        if (holder.getItemViewType() == VIEW_TYPE_HEADER) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.bind(item);
        } else {
            MenuItemViewHolder itemHolder = (MenuItemViewHolder) holder;
            itemHolder.bind(item, listener);
        }
    }

    @Override
    public int getItemCount() {
        return menuItems != null ? menuItems.size() : 0;
    }

    public void updateData(List<Map<String, Object>> newItems) {
        this.menuItems = newItems;
        notifyDataSetChanged();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryNameTextView;

        HeaderViewHolder(View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.categoryNameTextView);
        }

        void bind(Map<String, Object> item) {
            String categoryName = (String) item.get("categoryName");
            categoryNameTextView.setText(categoryName);
        }
    }

    static class MenuItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView priceTextView;
        private final TextView descriptionTextView;
        private final MaterialCardView cardView;

        MenuItemViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.dishNameTextView);
            priceTextView = itemView.findViewById(R.id.dishPriceTextView);
            descriptionTextView = itemView.findViewById(R.id.dishDescriptionTextView);
            cardView = itemView.findViewById(R.id.dishCardView);
        }

        void bind(Map<String, Object> item, OnMenuItemClickListener listener) {
            // Changed to use uppercase field names matching the API response
            nameTextView.setText((String) item.get("DISH_NAME"));

            // Format price with Swedish currency
            double price = 0;
            Object priceObj = item.get("DISH_PRICE");
            if (priceObj instanceof Number) {
                price = ((Number) priceObj).doubleValue();
            }
            priceTextView.setText(String.format("%.2f kr", price));

            // Description may be null
            String description = (String) item.get("DISH_DESCRIPTION");
            if (description != null && !description.isEmpty()) {
                descriptionTextView.setText(description);
                descriptionTextView.setVisibility(View.VISIBLE);
            } else {
                descriptionTextView.setVisibility(View.GONE);
            }

            cardView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}
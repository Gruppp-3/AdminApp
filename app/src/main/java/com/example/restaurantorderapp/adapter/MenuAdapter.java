package com.example.restaurantorderapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantorderapp.R;
// Explicitly import our custom MenuItem
import com.example.restaurantorderapp.model.MenuItem;

import java.util.List;


public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    private List<MenuItem> menuItems;

    public MenuAdapter(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuItem item = menuItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return menuItems != null ? menuItems.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView descriptionTextView;
        private final TextView priceTextView;
        private final TextView categoryTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.menuItemName);
            descriptionTextView = itemView.findViewById(R.id.menuItemDescription);
            priceTextView = itemView.findViewById(R.id.menuItemPrice);
            categoryTextView = itemView.findViewById(R.id.menuItemCategory);
        }

        public void bind(MenuItem item) {
            if (item != null) {
                nameTextView.setText(item.getName());
                descriptionTextView.setText(item.getDescription());
                priceTextView.setText(String.format("%.2f kr", item.getPrice()));
                categoryTextView.setText(item.getCategory());
            }
        }
    }
}
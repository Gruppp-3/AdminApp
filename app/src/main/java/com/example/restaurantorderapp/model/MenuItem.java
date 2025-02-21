package com.example.restaurantorderapp.model;

import com.google.gson.annotations.SerializedName;

public class MenuItem {
    @SerializedName("DISH_NAME")
    private String name;

    @SerializedName("DISH_DESCRIPTION")
    private String description;

    @SerializedName("DISH_PRICE")
    private double price;

    @SerializedName("DISH_TYPE_NAME")
    private String category;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
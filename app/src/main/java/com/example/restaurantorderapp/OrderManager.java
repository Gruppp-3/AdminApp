package com.example.restaurantorderapp;

import java.util.ArrayList;
import java.util.List;

public class OrderManager {
    private static OrderManager instance;
    private final List<String> orders;

    private OrderManager(){
        orders = new ArrayList<>();
    }
    public static synchronized OrderManager getInstance(){
        if(instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }
    public void addOrder(String order){
        orders.add(order);
    }
    public List<String> getOrders(){
        return new ArrayList<>(orders);
    }
    public void clearOrders(){
        orders.clear();
    }
}


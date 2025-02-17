package com.example.restaurantorderapp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OrderManager {
    private static OrderManager instance;
    private final List<String> orders;
    private final List<String> readyOrders;

    private OrderManager(){
        orders = new ArrayList<>();
        readyOrders = new ArrayList<>();
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

    public void setOrders(List<String> updatedOrders) {
        orders.clear();
        orders.addAll(updatedOrders);
    }
    public void markOrderReady(String order){
        if(orders.contains(order)){
            orders.remove(order);
            readyOrders.add(order);
        }
    }

    public List<String> getReadyOrders() {
        return new ArrayList<>(readyOrders);
    }

    public void removeOrderByTable(String tableNumber) {
        Iterator<String> iterator = readyOrders.iterator();
        while (iterator.hasNext()){
            String orders = iterator.next();
            if(orders.contains("Beställning för Bord " + tableNumber)){
                iterator.remove();
            }
        }
    }

    public void clearOrders(){
        orders.clear();
    }


}


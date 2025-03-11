package com.example.restaurantorderapp.api;

import com.example.restaurantorderapp.model.Booking;
import com.example.restaurantorderapp.model.Employee;
import com.example.restaurantorderapp.model.WorkShift;

import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;
import java.util.Map;

public interface ApiService {

    // Employee endpoints
    @GET("api/employees")
    Call<List<Employee>> getAllEmployees();
    @GET("api/employees/{id}")
    Call<Employee> getEmployeeById(@Path("id") Long id);
    @Headers("Content-Type: application/json")
    @POST("api/employees")
    Call<Employee> createEmployee(@Body Employee employee);

    @Headers("Content-Type: application/json")
    @PUT("api/employees/{id}")
    Call<Employee> updateEmployee(@Path("id") Long id, @Body Employee employee);

    @DELETE("api/employees/{id}")
    Call<Void> deleteEmployee(@Path("id") Long id);

    // Lunch menu endpoints
    @GET("api/v1/lunch/today")
    Call<List<Map<String, Object>>> getTodayLunch();

    @GET("api/v1/lunch/weekly")
    Call<Map<String, List<Map<String, Object>>>> getWeeklyLunch();

    @POST("api/v1/lunch/weekly")
    Call<Void> createWeeklyMenu(@Body Map<String, List<Map<String, Object>>> weeklyMenu,
                                @Query("startOfWeek") String startOfWeek);
    @POST("api/v1/lunch/today")
    Call<Map<String, Object>> addLunchDish(@Body Map<String, Object> lunchDish);

    @PUT("api/v1/lunch/today/{id}")
    Call<Map<String, Object>> updateLunchDish(@Path("id") Long id, @Body Map<String, Object> lunchDish);

    @DELETE("api/v1/lunch/today/{id}")
    Call<Void> deleteLunchDish(@Path("id") Long id);

    // Alacarte Menu Endpoints
    @GET("api/v1/menu")
    Call<List<Map<String, Object>>> getMenu();

    @GET("api/v1/menu/category/{category}")
    Call<List<Map<String, Object>>> getMenuByCategory(@Path("category") String category);

    @POST("api/v1/menu")
    Call<Void> addMenuItem(@Body Map<String, Object> menuItem);

    @PUT("api/v1/menu/{id}")
    Call<Map<String, Object>> updateMenuItem(@Path("id") Long id, @Body Map<String, Object> menuItem);

    @DELETE("api/v1/menu/{id}")
    Call<Void> deleteMenuItem(@Path("id") Long id);

    // Booking endpoints
    @GET("api/v1/bookings")
    Call<List<Booking>> getBookings();
    @DELETE("api/v1/bookings/{id}")
    Call<Void> deleteBooking(@Path("id") Integer id);

    // WorkShift endpoints
    @GET("api/workshifts")
    Call<List<WorkShift>> getAllWorkShifts();

    @GET("api/workshifts/employee/{employeeId}")
    Call<List<WorkShift>> getWorkShiftsByEmployee(@Path("employeeId") Long employeeId);

    @POST("api/workshifts")
    Call<WorkShift> createWorkShift(@Body WorkShift workShift);

    @PUT("api/workshifts/{id}")
    Call<WorkShift> updateWorkShift(@Path("id") Long id, @Body WorkShift workShift);

    @DELETE("api/workshifts/{id}")
    Call<Void> deleteWorkShift(@Path("id") Long id);


}

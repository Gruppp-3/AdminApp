package com.example.restaurantorderapp.api;

import com.example.restaurantorderapp.model.Booking;

import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;
import java.util.Map;

public interface ApiService {
    // Lunch menu endpoints
    @GET("api/v1/lunch/today")
    Call<List<Map<String, Object>>> getTodayLunch();

    @GET("api/v1/lunch/weekly")
    Call<Map<String, List<Map<String, Object>>>> getWeeklyLunch();

    @POST("api/v1/lunch/weekly")
    Call<Void> createWeeklyMenu(@Body Map<String, List<Map<String, Object>>> weeklyMenu,
                                @Query("startDate") String startDate);

    // Veckoplanering
    @GET("api/v1/lunch/nextWeekly")
    Call<Map<String, List<Map<String, Object>>>> getNextWeeklyLunch();

    @POST("api/v1/lunch/today")
    Call<Map<String, Object>> addLunchDish(@Body Map<String, Object> lunchDish);

    @PUT("api/v1/lunch/today/{id}")
    Call<Map<String, Object>> updateLunchDish(@Path("id") Long id, @Body Map<String, Object> lunchDish);

    @DELETE("api/v1/lunch/today/{id}")
    Call<Void> deleteLunchDish(@Path("id") Long id);

    @GET("api/v1/lunch/validate-date")
    Call<Boolean> validateLunchDate(@Query("date") String date);

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

    @GET("api/v1/bookings/date/{date}")
    Call<List<Map<String, Object>>> getBookingsByDate(@Path("date") String date);

    @POST("api/v1/bookings")
    Call<Map<String, Object>> createBooking(@Body Map<String, Object> booking);

    // Match Booking Class
    @DELETE("api/v1/bookings/{id}")
    Call<Void> deleteBooking(@Path("id") Integer id);

    // Order endpoints
    @GET("api/v1/orders/active")
    Call<List<Map<String, Object>>> getActiveOrders();

    @POST("api/v1/orders")
    Call<Map<String, Object>> createOrder(@Body Map<String, Object> order);

    @PUT("api/v1/orders/{id}/status")
    Call<Void> updateOrderStatus(@Path("id") Long id, @Body Map<String, String> statusUpdate);
}
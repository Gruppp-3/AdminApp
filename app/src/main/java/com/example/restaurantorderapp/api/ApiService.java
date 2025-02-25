package com.example.restaurantorderapp.api;

import com.example.restaurantorderapp.model.Booking;
import com.example.restaurantorderapp.model.MenuItem;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    // Booking endpoints (unchanged)
    @GET("api/v1/bookings")
    Call<List<Booking>> getBookings();

    @GET("api/v1/bookings/{id}")
    Call<Booking> getBooking(@Path("id") int id);

    @POST("api/v1/bookings")
    Call<Booking> createBooking(@Body Booking booking);
    @PUT("api/v1/bookings/{id}")
    Call<Booking> updateBooking(@Path("id") int id, @Body Booking booking);

    @DELETE("api/v1/bookings/{id}")
    Call<Void> deleteBooking(@Path("id") int id);

    // Regular menu endpoints
    @GET("api/v1/menu")
    Call<List<MenuItem>> getMenuItems();

    // Lunch menu endpoints:
    // Get today's lunch dishes.
    @GET("api/v1/lunch/today")
    Call<List<Map<String, Object>>> getTodayLunch();

    // Add a lunch dish (payload must include: dish_name, dish_description, dish_price, date).
    @POST("api/v1/lunch/today")
    Call<Map<String, Object>> addLunchDish(@Body Map<String, Object> lunchDish);

    // Update an existing lunch dish.
    @PUT("api/v1/lunch/today/{id}")
    Call<Map<String, Object>> updateLunchDish(@Path("id") Long id, @Body Map<String, Object> lunchDish);

    // Delete a lunch dish.
    @DELETE("api/v1/lunch/today/{id}")
    Call<Void> deleteLunchDish(@Path("id") Long id);

    // Get the current week’s lunch menu (current week: Monday–Sunday).
    @GET("api/v1/lunch/weekly")
    Call<Map<String, List<Map<String, Object>>>> getWeeklyLunch();

    // (Optional) Get the upcoming week’s lunch menu.
    @GET("api/v1/lunch/nextWeekly")
    Call<Map<String, List<Map<String, Object>>>> getNextWeeklyLunch();

    // Save/update a complete weekly menu.
    @POST("api/v1/lunch/weekly")
    Call<Void> createWeeklyMenu(
            @Body Map<String, List<Map<String, Object>>> weeklyMenu,
            @Query("startOfWeek") String startOfWeek);
}

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
public interface ApiService {
    // Booking endpoints (keep these as they are)
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

    // Lunch menu endpoints - updated to match backend
    @GET("api/v1/lunch/today")
    Call<List<Map<String, Object>>> getTodayLunch();

    @GET("api/v1/lunch/weekly")
    Call<Map<String, List<Map<String, Object>>>> getWeeklyLunch(); // Note: Return type changed

    @POST("api/v1/lunch/today")
    Call<Map<String, Object>> addLunchDish(@Body Map<String, Object> lunchDish);

    @PUT("api/v1/lunch/today/{id}")
    Call<Map<String, Object>> updateLunchDish(@Path("id") Long id, @Body Map<String, Object> lunchDish);

    @DELETE("api/v1/lunch/today/{id}")
    Call<Void> deleteLunchDish(@Path("id") Long id);
}
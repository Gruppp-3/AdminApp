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
    // Booking endpoints
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

    // Menu endpoints
    @GET("api/v1/menu")
    Call<List<MenuItem>> getMenuItems();

    @GET("api/v1/menu/today")
    Call<List<Map<String, Object>>> getTodayLunch();

    @GET("api/v1/menu/weekly")
    Call<Map<String, List<Map<String, Object>>>> getWeeklyLunch();

    @POST("api/v1/menu")
    Call<Map<String, Object>> createMenuItem(@Body Map<String, Object> menuItem);

    @PUT("api/v1/menu/{id}")
    Call<Map<String, Object>> updateMenuItem(@Path("id") Long id, @Body Map<String, Object> menuItem);

    @DELETE("api/v1/menu/{id}")
    Call<Void> deleteMenuItem(@Path("id") Long id);
}
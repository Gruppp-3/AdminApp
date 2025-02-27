package com.example.restaurantorderapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.restaurantorderapp.adapter.BookingAdapter;
import com.example.restaurantorderapp.api.RetrofitClient;
import com.example.restaurantorderapp.model.Booking;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BookingAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Add this code to handle back button click
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.bookingRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BookingAdapter(new BookingAdapter.BookingActionListener() {
            @Override
            public void onEdit(Booking booking) {
                // Handle edit
                Toast.makeText(BookingActivity.this,
                        "Redigera bokning: " + booking.getName(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDelete(Booking booking) {
                deleteBooking(booking);
            }
        });

        recyclerView.setAdapter(adapter);

        // Initialize SwipeRefreshLayout
        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this::loadBookings);

        // Initial load
        loadBookings();
    }

    private void loadBookings() {
        Log.d("BookingActivity", "Starting to load bookings...");  // Add this

        RetrofitClient.getInstance()
                .getApi()
                .getBookings()
                .enqueue(new Callback<List<Booking>>() {
                    @Override
                    public void onResponse(Call<List<Booking>> call,
                                           Response<List<Booking>> response) {
                        swipeRefresh.setRefreshing(false);

                        // Add these log statements
                        Log.d("BookingActivity", "Got response code: " + response.code());
                        Log.d("BookingActivity", "Response message: " + response.message());

                        if (response.isSuccessful() && response.body() != null) {
                            Log.d("BookingActivity", "Success! Number of bookings: "
                                    + response.body().size());
                            Log.d("BookingActivity", "Bookings data: " + response.body().toString());
                            adapter.setBookings(response.body());
                        } else {
                            Log.e("BookingActivity", "Response not successful");
                            Toast.makeText(BookingActivity.this,
                                    "Kunde inte ladda bokningar",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Booking>> call, Throwable t) {
                        swipeRefresh.setRefreshing(false);
                        // Add this log
                        Log.e("BookingActivity", "Network call failed", t);
                        Toast.makeText(BookingActivity.this,
                                "Fel vid laddning av bokningar: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteBooking(Booking booking) {
        new AlertDialog.Builder(this)
                .setTitle("Ta bort bokning")
                .setMessage("Är du säker på att du vill ta bort denna bokning?")
                .setPositiveButton("Ja", (dialog, which) -> {
                    RetrofitClient.getInstance()
                            .getApi()
                            .deleteBooking(booking.getBookingId())
                            .enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call,
                                                       Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        loadBookings();
                                        Toast.makeText(BookingActivity.this,
                                                "Bokning borttagen",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(BookingActivity.this,
                                            "Kunde inte ta bort bokning",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Avbryt", null)
                .show();
    }
}
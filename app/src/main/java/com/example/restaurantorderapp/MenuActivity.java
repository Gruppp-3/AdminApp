package com.example.restaurantorderapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantorderapp.adapter.MenuAdapter;
import com.example.restaurantorderapp.api.RetrofitClient;
import com.example.restaurantorderapp.model.MenuItem;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity {
    private static final String TAG = "MenuActivity";
    private RecyclerView recyclerView;
    private MenuAdapter menuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        recyclerView = findViewById(R.id.menuRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadMenuItems();
    }

    private void loadMenuItems() {
        Call<List<MenuItem>> call = RetrofitClient.getInstance().getApi().getMenuItems();
        call.enqueue(new Callback<List<MenuItem>>() {
            @Override
            public void onResponse(Call<List<MenuItem>> call, Response<List<MenuItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MenuItem> items = response.body();
                    menuAdapter = new MenuAdapter(items);
                    recyclerView.setAdapter(menuAdapter);
                } else {
                    Log.e(TAG, "Error: " + response.code());
                    Toast.makeText(MenuActivity.this,
                            "Error loading menu items: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MenuItem>> call, Throwable t) {
                Log.e(TAG, "Failed to load menu items", t);
                Toast.makeText(MenuActivity.this,
                        "Failed to load menu items: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
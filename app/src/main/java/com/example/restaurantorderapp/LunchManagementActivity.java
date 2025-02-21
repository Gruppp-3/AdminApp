package com.example.restaurantorderapp;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.restaurantorderapp.adapter.LunchAdapter;
import com.example.restaurantorderapp.api.ApiService;
import com.example.restaurantorderapp.api.RetrofitClient;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LunchManagementActivity extends AppCompatActivity {
    private ApiService apiService;
    private RecyclerView recyclerView;
    private LunchAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunch_management);

        // Initialize API service
        apiService = RetrofitClient.getInstance().getApi();

        // Setup RecyclerView
        recyclerView = findViewById(R.id.lunchRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this::loadLunchData);

        // Initial data load
        loadLunchData();
    }

    private void loadLunchData() {
        apiService.getTodayLunch().enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call,
                                   Response<List<Map<String, Object>>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    if (adapter == null) {
                        // Create new adapter with explicit OnLunchItemClickListener
                        adapter = new LunchAdapter(response.body(),
                                new LunchAdapter.OnLunchItemClickListener() {
                                    @Override
                                    public void onItemClick(Map<String, Object> item) {
                                        showEditDialog(item);
                                    }
                                });
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter.updateData(response.body());
                    }
                } else {
                    Toast.makeText(LunchManagementActivity.this,
                            "Failed to load lunch data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(LunchManagementActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditDialog(Map<String, Object> item) {
        // Dialog implementation for editing lunch items
        // We can implement this next if you want
    }
}
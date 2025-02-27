package com.example.restaurantorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AdminLoginActivity extends AppCompatActivity {
    private static final String TAG = "AdminLoginActivity";

    // Declare the views
    private EditText usernameField;
    private EditText passwordField;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        Log.d(TAG, "onCreate called");

        // Initialize the views
        usernameField = findViewById(R.id.username);
        passwordField = findViewById(R.id.password);
        loginButton = findViewById(R.id.btn_login);

        loginButton.setOnClickListener(view -> {
            String username = usernameField.getText().toString();
            String password = passwordField.getText().toString();

            if (username.equals("") && password.equals("")) {
                try {
                    Log.d(TAG, "Login successful, attempting to start AdminActivity");
                    Intent intent = new Intent(AdminLoginActivity.this, MainActivity.class);
                    // Try without flags first
                    startActivity(intent);
                    Log.d(TAG, "Started AdminActivity");
                    finish();
                } catch (Exception e) {
                    Log.e(TAG, "Error starting AdminActivity: " + e.getMessage());
                    e.printStackTrace();
                    Toast.makeText(this, "Ett fel uppstod: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(AdminLoginActivity.this, "Fel användarnamn eller lösenord!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
    }
}
package com.example.restaurantorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText usernameField, passwordField;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        usernameField = findViewById(R.id.username);
        passwordField = findViewById(R.id.password);
        loginButton = findViewById(R.id.btn_login);

        loginButton.setOnClickListener(view -> {
            String username = usernameField.getText().toString();
            String password = passwordField.getText().toString();

            if (username.equals("admin") && password.equals("1234")) {
                Toast.makeText(AdminLoginActivity.this, "Inloggning lyckades!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AdminLoginActivity.this, AdminActivity.class));
                finish();
            } else {
                Toast.makeText(AdminLoginActivity.this, "Fel användarnamn eller lösenord!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

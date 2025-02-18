package com.example.restaurantorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_login);

        EditText usernameEditText = findViewById(R.id.username);
        EditText passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.loginButton);

        // Hårdkodade användare
        final String validUsername = "admin";
        final String validPassword = "password123";

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputUsername = usernameEditText.getText().toString();
                String inputPassword = passwordEditText.getText().toString();

                if (inputUsername.equals(validUsername) && inputPassword.equals(validPassword)) {
                    // Starta MainActivity om inloggningen lyckas
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Stänger LoginActivity så att användaren inte kan gå tillbaka
                } else {
                    Toast.makeText(LoginActivity.this, "Fel användarnamn eller lösenord", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

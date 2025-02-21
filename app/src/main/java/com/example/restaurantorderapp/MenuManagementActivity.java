package com.example.restaurantorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MenuManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_management);

        Button addMenuItemBtn = findViewById(R.id.addMenuItemBtn);
        Button editMenuItemBtn = findViewById(R.id.editMenuItemBtn);
        Button deleteMenuItemBtn = findViewById(R.id.deleteMenuItemBtn);

        addMenuItemBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MenuActivity.class);
            intent.putExtra("mode", "add");
            startActivity(intent);
        });

        editMenuItemBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MenuActivity.class);
            intent.putExtra("mode", "edit");
            startActivity(intent);
        });

        deleteMenuItemBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MenuActivity.class);
            intent.putExtra("mode", "delete");
            startActivity(intent);
        });
    }
}
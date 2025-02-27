package com.example.restaurantorderapp.lunch;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantorderapp.R;

public class ManageMenuActivity extends AppCompatActivity {

    private Button addMenuItemBtn, editMenuItemBtn, deleteMenuItemBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_management);
        Log.d("ManageMenuActivity", "Activity created");

        addMenuItemBtn = findViewById(R.id.addMenuItemBtn);
        editMenuItemBtn = findViewById(R.id.editMenuItemBtn);
        deleteMenuItemBtn = findViewById(R.id.deleteMenuItemBtn);

        // Lägg till funktionalitet för att lägga till menyobjekt
        addMenuItemBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ManageMenuActivity.this, LunchManagementActivity.class);
            intent.putExtra("action", "add");
            startActivity(intent);
        });
        editMenuItemBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ManageMenuActivity.this, LunchManagementActivity.class);
            intent.putExtra("action", "edit");
            startActivity(intent);
        });
        // Lägg till funktionalitet för att ta bort menyobjekt
        deleteMenuItemBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ManageMenuActivity.this, LunchManagementActivity.class);
            intent.putExtra("action", "delete");
            startActivity(intent);
        });
    }
}

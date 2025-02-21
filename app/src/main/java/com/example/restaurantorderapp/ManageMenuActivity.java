package com.example.restaurantorderapp;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ManageMenuActivity extends AppCompatActivity {

    private Button addMenuItemBtn, editMenuItemBtn, deleteMenuItemBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_management);

        addMenuItemBtn = findViewById(R.id.addMenuItemBtn);
        editMenuItemBtn = findViewById(R.id.editMenuItemBtn);
        deleteMenuItemBtn = findViewById(R.id.deleteMenuItemBtn);

        addMenuItemBtn.setOnClickListener(view -> {
            // Lägg till funktionalitet för att lägga till menyobjekt
        });

        editMenuItemBtn.setOnClickListener(view -> {
            // Lägg till funktionalitet för att redigera menyobjekt
        });

        deleteMenuItemBtn.setOnClickListener(view -> {
            // Lägg till funktionalitet för att ta bort menyobjekt
        });
    }
}

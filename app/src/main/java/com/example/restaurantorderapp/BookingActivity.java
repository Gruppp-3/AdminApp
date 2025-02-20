package com.example.restaurantorderapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Arrays;

public class BookingActivity extends AppCompatActivity {

    private ListView availableTablesList, bookedTablesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Knyt listorna till UI
        availableTablesList = findViewById(R.id.availableTablesList);
        bookedTablesList = findViewById(R.id.bookedTablesList);

        // Exempeldata: Tillgängliga och bokade bord
        ArrayList<String> availableTables = new ArrayList<>(Arrays.asList("Bord 2", "Bord 4", "Bord 5"));
        ArrayList<String> bookedTables = new ArrayList<>(Arrays.asList("Bord 1", "Bord 3", "Bord 6"));

        // Skapa adapter för att visa listorna
        ArrayAdapter<String> availableAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, availableTables);
        ArrayAdapter<String> bookedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bookedTables);

        // Koppla adaptern till listorna
        availableTablesList.setAdapter(availableAdapter);
        bookedTablesList.setAdapter(bookedAdapter);
    }
}

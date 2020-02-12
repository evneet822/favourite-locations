package com.example.a759831andrioidassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button addPlace;
    ListView locationListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addPlace = findViewById(R.id.btn_add_place);
        locationListView = findViewById(R.id.location_list_view);

        addPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LocationActivity.class);
                startActivity(intent);
            }
        });

        final String[] days = {"monday", "tues", "wed", "thrusday"};

//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, days);
//        locationListView.setAdapter(adapter);

        LocationAdaptor locationAdaptor = new LocationAdaptor(this,R.layout.list_layout,Locations.savedLocations);
        locationListView.setAdapter(locationAdaptor);

        locationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,LocationActivity.class);
                intent.putExtra("listPosition" ,position);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        LocationAdaptor locationAdaptor = new LocationAdaptor(this,R.layout.list_layout,Locations.savedLocations);
        locationListView.setAdapter(locationAdaptor);
    }
}

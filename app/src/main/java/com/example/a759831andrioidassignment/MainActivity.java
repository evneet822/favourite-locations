package com.example.a759831andrioidassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button addPlace;
//    ListView locationListView;
    SwipeMenuListView locationListView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addPlace = findViewById(R.id.btn_add_place);

        locationListView =  findViewById(R.id.location_list_view);
//        locationListView = findViewById(R.id.location_list_view);

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

        final LocationAdaptor locationAdaptor = new LocationAdaptor(this,R.layout.list_layout,Locations.savedLocations);
        locationListView.setAdapter(locationAdaptor);

        locationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,LocationActivity.class);
                intent.putExtra("listPosition" ,position);
                startActivity(intent);
            }
        });

        SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteitem = new SwipeMenuItem(getApplicationContext());
                deleteitem.setBackground(new ColorDrawable(Color.RED));
                deleteitem.setTitle("Delete");
                deleteitem.setTitleColor(Color.WHITE);
                deleteitem.setWidth(170);

                menu.addMenuItem(deleteitem);

                SwipeMenuItem updateitem = new SwipeMenuItem(getApplicationContext());
                updateitem.setBackground(new ColorDrawable(Color.GRAY));
                updateitem.setWidth(170);
                updateitem.setTitle("Update");
                updateitem.setTitleColor(Color.WHITE);

                menu.addMenuItem(updateitem);
            }
        };
        locationListView.setMenuCreator(swipeMenuCreator);

        locationListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index){
                    case 0:
                        Locations.savedLocations.remove(position);
                        LocationAdaptor locationAdaptor = new LocationAdaptor(MainActivity.this,R.layout.list_layout,Locations.savedLocations);
                        locationListView.setAdapter(locationAdaptor);

                        break;
                    case 1:
                        Toast.makeText(MainActivity.this, "updated", Toast.LENGTH_SHORT).show();
                        break;
                }

                return false;
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

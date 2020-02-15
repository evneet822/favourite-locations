package com.example.a759831andrioidassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
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
    SwipeMenuListView locationListView;
    DataBaseHelper dataBaseHelper;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addPlace = findViewById(R.id.btn_add_place);

        locationListView =  findViewById(R.id.location_list_view);

        dataBaseHelper = new DataBaseHelper(this);



        addPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LocationActivity.class);
                startActivity(intent);
            }
        });


        final LocationAdaptor locationAdaptor = new LocationAdaptor(this,R.layout.list_layout,Locations.savedLocations,dataBaseHelper);
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
                deleteitem.setTitleSize(15);
                deleteitem.setTitleColor(Color.WHITE);
                deleteitem.setWidth(170);

                menu.addMenuItem(deleteitem);

                SwipeMenuItem updateitem = new SwipeMenuItem(getApplicationContext());
                updateitem.setBackground(new ColorDrawable(Color.GRAY));
                updateitem.setWidth(170);
                updateitem.setTitle("Update");
                updateitem.setTitleSize(15);
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

                        if(dataBaseHelper.deleteLocation(Locations.savedLocations.get(position).getId())){
                            loadLocations();
                            LocationAdaptor locationAdaptor = new LocationAdaptor(MainActivity.this,R.layout.list_layout,Locations.savedLocations,dataBaseHelper);
                            locationListView.setAdapter(locationAdaptor);
                        }
//                        Locations.savedLocations.remove(position);
//                        LocationAdaptor locationAdaptor = new LocationAdaptor(MainActivity.this,R.layout.list_layout,Locations.savedLocations,dataBaseHelper);
//                        locationListView.setAdapter(locationAdaptor);


                        break;
                    case 1:
                        Intent intent = new Intent(MainActivity.this,LocationActivity.class);
                        intent.putExtra("update",true);
                        intent.putExtra("listPosition",position);
                        startActivity(intent);
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

         loadLocations();

        LocationAdaptor locationAdaptor = new LocationAdaptor(this,R.layout.list_layout,Locations.savedLocations,dataBaseHelper);
        locationListView.setAdapter(locationAdaptor);
    }

    private void loadLocations(){
        /*
        String sql = "select * from employees";
        Cursor cursor = mDatabase.rawQuery(sql,null);

         */

        Cursor cursor  =  dataBaseHelper.getallLocations();
        Locations.savedLocations.clear();

        if(cursor.moveToFirst()){
            do{
//                employeeList.add(new Employee(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getDouble(4)));
                Locations.savedLocations.add(new Locations(cursor.getInt(0),cursor.getDouble(1),cursor.getDouble(2),cursor.getString(3),cursor.getString(4),cursor.getInt(5)));
                System.out.println(cursor.getInt(0));
                System.out.println(cursor.getDouble(1));
                System.out.println(cursor.getDouble(2));
                System.out.println(cursor.getString(3));
                System.out.println(cursor.getString(4));
                System.out.println(cursor.getString(5));

            }while (cursor.moveToNext());
            cursor.close();

            //show items in list view
            //we use a custom adaptor to show employee

            LocationAdaptor locationAdaptor = new LocationAdaptor(this,R.layout.list_layout,Locations.savedLocations,dataBaseHelper);
            locationListView.setAdapter(locationAdaptor);


//            EmployeeAdaptor employeeAdaptor = new EmployeeAdaptor(this,R.layout.list_layout_employee,employeeList,databaseHelper);
//            listView.setAdapter(employeeAdaptor);


        }
    }
}

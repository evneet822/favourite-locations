package com.example.a759831andrioidassignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    private int REQUEST_CODE = 1;

    private int position;
    private String selectedadress;
    private boolean alreadyAdded = false;

    public final int RADIUS = 1500;
    double latitude,longitude;
    double destinationLatitude, destinationLongitude;

    public static boolean requestedDirection,locationSelected;
    public static String distance,duration;
    private static int locationVisited = 0;

    Button addFavourite;
    CheckBox checkBoxVisited;

    DataBaseHelper dataBaseHelper;


    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        System.out.println("on create");
        initMap();
        getUserLocation();

        addFavourite = findViewById(R.id.btn_add_favourite);
        checkBoxVisited = findViewById(R.id.checkbox_visited);


        if(!checkPermission())
            requestPermission();
        else
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());

        final Intent intent = getIntent();
        position = intent.getIntExtra("listPosition",-1);
        locationSelected = intent.getBooleanExtra("update",false);


        if(position >= 0){
            addFavourite.setEnabled(false);
            checkBoxVisited.setEnabled(true);
        }

        dataBaseHelper = new DataBaseHelper(this);

        addFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                final String savedDate1 = sdf.format(calendar.getTime());

                final Location nearbyLocation = new Location("");
                nearbyLocation.setLatitude(destinationLatitude);
                nearbyLocation.setLongitude(destinationLongitude);

                final String addd = getAddress(nearbyLocation);


//                final Locations favLocation1 = new Locations(destinationLatitude,destinationLongitude,getAddress(nearbyLocation),savedDate1,locationVisited);
//                final Locations favlocation = new Locations(1,destinationLatitude,destinationLongitude,getAddress(nearbyLocation),savedDate1,locationVisited);

                System.out.println("---------------------------------------------------");
                System.out.println("before alert");

                AlertDialog.Builder builder1 = new AlertDialog.Builder(LocationActivity.this);
                builder1.setTitle("Do you want to save location?");
                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        for (int i = 0 ; i < Locations.savedLocations.size(); i++){
                            double lat = Locations.savedLocations.get(i).getUserLat();
                            if(destinationLatitude == lat){
                                alreadyAdded = true;
                                break;
                            }
                        }
                        if(alreadyAdded){
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(LocationActivity.this);
                            builder2.setTitle("Location already saved!!!");
                            builder2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            AlertDialog dialog2 = builder2.create();
                            dialog2.show();
                        }else {
//                            Locations.savedLocations.add(favlocation);
                            System.out.println("----crosscheck------");

                            System.out.println(destinationLatitude+destinationLongitude+addd+savedDate1+locationVisited);

                            if(dataBaseHelper.addLocation(destinationLatitude,destinationLongitude,addd,savedDate1,locationVisited)){
                                System.out.println("added");
                            }else {
                                System.out.println("not added");
                            }

//                            if(databaseHelper.addEmployee(name,dept,joiningDate,Double.parseDouble(salary)))
//                                Toast.makeText(this, "employee added", Toast.LENGTH_SHORT).show();
//                            else
//                                Toast.makeText(this, "employee not added", Toast.LENGTH_SHORT).show();

                            Intent intent2 = new Intent(LocationActivity.this,MainActivity.class);
                            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent2);


                        }



                    }
                });

                builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog1 = builder1.create();
                alertDialog1.show();

                System.out.println("---------------------------------------------------");
                System.out.println("after alert");
            }
        });

        checkBoxVisited.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    locationVisited = 1;
                }



                destinationLatitude = Locations.savedLocations.get(position).getUserLat();
                destinationLongitude = Locations.savedLocations.get(position).getUserLong();

//                Location location = new Location("");
//                location.setLatitude(destinationLatitude);
//                location.setLongitude(destinationLongitude);

                String location = Locations.savedLocations.get(position).getAddress();
                System.out.println("--------------------");
                System.out.println(location);


                String date = Locations.savedLocations.get(position).getDate();


//                Locations locations1 = new Locations(destinationLatitude,destinationLongitude,getAddress(location),date,locationVisited);

                if(dataBaseHelper.updateLocation(Locations.savedLocations.get(position).getId(),destinationLatitude,destinationLongitude,location,date,locationVisited)){
                    Toast.makeText(LocationActivity.this, "updated", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(LocationActivity.this, "not updated", Toast.LENGTH_SHORT).show();
                }


//                Locations locations = new Locations(1,destinationLatitude,destinationLongitude,getAddress(location),date,locationVisited);
//                Locations.savedLocations.remove(position);
//                Locations.savedLocations.add(position,locations);

                locationVisited = 0;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.action_bar_menu,menu);
        return true;

    }



    private void initMap() {
        System.out.println("inside init");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("on map ready");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMarkerDragListener(this);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                Location location = new Location("location saved");
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);

                destinationLatitude = latLng.latitude;
                destinationLongitude = latLng.longitude;

                MarkerOptions options = new MarkerOptions().position(latLng).title(getAddress(location));
                mMap.addMarker(options);

//                Calendar calendar = Calendar.getInstance();
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
//                String savedDate = sdf.format(calendar.getTime());
//
//                System.out.println("---------------------------------------------------");
//                System.out.println("latitude " + destinationLatitude + "longitude " + destinationLongitude);
//
//                setMarker(location);
//
//                final Locations favLocation = new Locations(destinationLatitude,destinationLongitude,getAddress(location),savedDate,locationVisited);
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this);
//                builder.setTitle("Do you want to save location?");
//                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//
//                        Locations.savedLocations.add(favLocation);
//                    }
//                });
//
//                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//
//                for (int i = 0 ; i < Locations.savedLocations.size(); i++){
//                    System.out.println(Locations.savedLocations.get(i).getUserLat());
//                    System.out.println(Locations.savedLocations.get(i).getAddress());
//                }



            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {

                AlertDialog.Builder builder2 = new AlertDialog.Builder(LocationActivity.this);
                builder2.setTitle("Select source or destination");
                builder2.setPositiveButton("Source", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        latitude = marker.getPosition().latitude;
                        longitude = marker.getPosition().longitude;

                    }
                });

                builder2.setNegativeButton("Destination", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        destinationLatitude = marker.getPosition().latitude;
                        destinationLongitude = marker.getPosition().longitude;

                    }
                });
                AlertDialog alertDialog1 = builder2.create();
                alertDialog1.show();




                        return true;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Object[] dataTransfer;
        String url;
        float color;
        switch (item.getItemId()){
            case R.id.action_restaurants:
                 url = getUrl(latitude,longitude,"restaurant");
                 color = BitmapDescriptorFactory.HUE_MAGENTA;
                dataTransfer = new Object[3];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = color;

                NearbyPlaceData nearbyPlaceData = new NearbyPlaceData();
                nearbyPlaceData.execute(dataTransfer);
                return true;

            case R.id.action_cafe:
                url = getUrl(latitude,longitude,"cafe");
                color = BitmapDescriptorFactory.HUE_YELLOW;
                dataTransfer = new Object[3];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = color;

                NearbyPlaceData nearbyPlaceData1 = new NearbyPlaceData();
                nearbyPlaceData1.execute(dataTransfer);
                return  true;

            case R.id.action_museum:
                url = getUrl(latitude,longitude,"museum");
                color = BitmapDescriptorFactory.HUE_ORANGE;
                dataTransfer = new Object[3];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = color;

                NearbyPlaceData nearbyPlaceData2 = new  NearbyPlaceData();
                nearbyPlaceData2.execute(dataTransfer);
                Toast.makeText(this, "museum", Toast.LENGTH_SHORT).show();
                return true;


            case R.id.action_hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;

            case R.id.action_terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;

            case R.id.action_satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;

            case R.id.action_distance:
            case R.id.action_direction:
                dataTransfer = new Object[3];
                url = getDirectionUrl();
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = new LatLng(destinationLatitude,destinationLongitude);

                GetDirectionsData getDirectionsData = new GetDirectionsData();
                getDirectionsData.execute(dataTransfer);

                if(item.getItemId() == R.id.action_distance) {
                    Toast.makeText(this, "Distance: " + distance + "Duration: " + duration, Toast.LENGTH_SHORT).show();
                    requestedDirection = false;
                }
                else
                    requestedDirection = true;

                return true;
                default:
                    return super.onOptionsItemSelected(item);

        }
    }

    private void requestPermission() {
        System.out.println("request permission");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private boolean checkPermission() {
        System.out.println("check permission");
        int permissionStatus = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionStatus == PackageManager.PERMISSION_GRANTED;
    }

    private void getUserLocation(){
        System.out.println("get user location");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);
        setHomeMarker();


    }

    private void setHomeMarker(){
        System.out.println("set home marker");
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                System.out.println("on location result");
                for (Location location : locationResult.getLocations()) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    latitude = userLocation.latitude;
                    longitude = userLocation.longitude;

                   mMap.clear();

                    CameraPosition cameraPosition = CameraPosition.builder()
                            .target(new LatLng(userLocation.latitude, userLocation.longitude))
                            .zoom(15)
                            .bearing(0)
                            .tilt(45)
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).draggable(false));



                }

                if(position >= 0){

                    System.out.println("inside postion if");
                    Location selectedLocation = new Location("hii");
                    selectedLocation.setLatitude(Locations.savedLocations.get(position).getUserLat());
                    selectedLocation.setLongitude(Locations.savedLocations.get(position).getUserLong());
                    selectedadress = Locations.savedLocations.get(position).getAddress();

                    System.out.println("---------------------------------------------------");
                    System.out.println(selectedadress);

                    LatLng ulatLng = new LatLng(selectedLocation.getLatitude(),selectedLocation.getLongitude());
                    destinationLatitude = ulatLng.latitude;
                    destinationLongitude = ulatLng.longitude;

                    System.out.println("---------------------------------------------------");
                    System.out.println("latitude " + ulatLng.latitude + "longitude " + ulatLng.longitude);

                    if(locationSelected == true){
                        MarkerOptions options = new MarkerOptions().position(ulatLng).title(selectedadress)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)).draggable(true);
                        mMap.addMarker(options);
                    }else {
                        MarkerOptions options = new MarkerOptions().position(ulatLng).title(selectedadress)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)).draggable(false);
                        mMap.addMarker(options);
                    }



                }
            }
        };

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        System.out.println("on request permission result");
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setHomeMarker();
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            }
        }
    }

    private void setMarker(Location location){

            LatLng userlatLng = new LatLng(location.getLatitude(),location.getLongitude());
            MarkerOptions options = new MarkerOptions().position(userlatLng).title("your location").draggable(false);

//            markerLmMap.addMarker(options));
            mMap.addMarker(options);

    }

    private String getAddress(Location location){

        String address = "";

        Geocoder geocoder = new Geocoder(this,Locale.getDefault());
        try {
            List<Address> locationAddress = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            if(locationAddress != null && locationAddress.size() > 0){

                if(locationAddress.get(0).getSubThoroughfare() != null)
                    address += locationAddress.get(0).getSubThoroughfare() + " ";
                if(locationAddress.get(0).getThoroughfare() != null)
                    address += locationAddress.get(0).getThoroughfare() + " ";
                if(locationAddress.get(0).getLocality() != null)
                    address += locationAddress.get(0).getLocality();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }

        return address;

    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=" + latitude + "," + longitude);
        googlePlaceUrl.append("&radius=" + RADIUS);
        googlePlaceUrl.append("&type=" + nearbyPlace);
        googlePlaceUrl.append("&key=" + getString(R.string.api_key_places));
        Log.d("", "getUrl: "+googlePlaceUrl);
        return googlePlaceUrl.toString();

    }

    private String getDirectionUrl() {
        StringBuilder googleDirectionUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionUrl.append("origin="+latitude+","+longitude);
        googleDirectionUrl.append("&destination="+destinationLatitude+","+destinationLongitude);
        googleDirectionUrl.append("&key="+getString(R.string.api_key_places));
        Log.d("", "getDirectionUrl: "+googleDirectionUrl);
        return googleDirectionUrl.toString();
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        System.out.println("inside marker end");

        destinationLongitude = marker.getPosition().latitude;
        destinationLongitude = marker.getPosition().longitude;

        Location location1 = new Location("");
        location1.setLatitude(destinationLatitude);
        location1.setLongitude(destinationLongitude);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String savedDate1 = sdf.format(calendar.getTime());

//        Locations locations1 = new Locations(1,destinationLatitude,destinationLongitude,getAddress(location),savedDate1,locationVisited);
//
//        Locations.savedLocations.remove(position);
//
//        Locations.savedLocations.add(position,locations1);
        System.out.println("--------------------------------------------------------------------");
        System.out.println(getAddress(location1));

        if( dataBaseHelper.updateLocation(Locations.savedLocations.get(position).getId(),destinationLatitude,destinationLongitude,getAddress(location1),savedDate1,locationVisited)){
            Toast.makeText(this, "updated", Toast.LENGTH_SHORT).show();
            System.out.println("--------------------------------------------------------------------");
            System.out.println(" updated");
        }else {
            Toast.makeText(this, "not updated", Toast.LENGTH_SHORT).show();
            System.out.println("--------------------------------------------------------------------");
            System.out.println("not updated");
        }



//        dataBaseHelper.updateLocation(Locations.savedLocations.get(position).getId(),destinationLatitude,destinationLongitude,(getAddress(location)),locationVisited);


    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }
}

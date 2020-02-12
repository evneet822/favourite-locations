package com.example.a759831andrioidassignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

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
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int REQUEST_CODE = 1;

    private int position;
    private String selectedadress;




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


        if(!checkPermission())
            requestPermission();
        else
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());

        Intent intent = getIntent();
        position = intent.getIntExtra("listPosition",-1);
        System.out.println(position);

//        if(position >= 0){
//
//            System.out.println("inside postion if");
//            Location selectedLocation = new Location("hii");
//            selectedLocation.setLatitude(Locations.savedLocations.get(position).getUserLat());
//            selectedLocation.setLongitude(Locations.savedLocations.get(position).getUserLong());
////
//            selectedadress = Locations.savedLocations.get(position).getAddress();
////
//            LatLng latLng = new LatLng(selectedLocation.getLatitude(),selectedLocation.getLongitude());
//            MarkerOptions options = new MarkerOptions().position(latLng).title(selectedadress)
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//            mMap.addMarker(options);
//        }
////
//        if(position >= 0){
//
//            Location selectedLocations = new Location("");
//            selectedLocations.setLatitude(Locations.savedLocations.get(position).getUserLat());
//            selectedLocations.setLongitude(Locations.savedLocations.get(position).getUserLong());
//            selectedadress = Locations.savedLocations.get(position).getAddress();
//
//            setMarker(selectedLocations);
//
//        }




    }

    private void initMap() {
        System.out.println("inside init");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("on map ready");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                Location location = new Location("your destination");
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);

                setMarker(location);

                Locations favLocation = new Locations(location.getLatitude(),location.getLongitude(),getAddress(location));

                Locations.savedLocations.add(favLocation);


                for (int i = 0 ; i < Locations.savedLocations.size(); i++){
                    System.out.println(Locations.savedLocations.get(i).getUserLat());
                    System.out.println(Locations.savedLocations.get(i).getAddress());
                }




            }
        });

        if(position >= 0){

            System.out.println("inside postion if");
            Location selectedLocation = new Location("hii");
            selectedLocation.setLatitude(Locations.savedLocations.get(position).getUserLat());
            selectedLocation.setLongitude(Locations.savedLocations.get(position).getUserLong());
//
            selectedadress = Locations.savedLocations.get(position).getAddress();
//
            LatLng ulatLng = new LatLng(selectedLocation.getLatitude(),selectedLocation.getLongitude());
            MarkerOptions options = new MarkerOptions().position(ulatLng).title(selectedadress)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.addMarker(options);
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

//                    mMap.clear(); //clear the old markers

                    CameraPosition cameraPosition = CameraPosition.builder()
                            .target(new LatLng(userLocation.latitude, userLocation.longitude))
                            .zoom(15)
                            .bearing(0)
                            .tilt(45)
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

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

//        if(position >= 0){
//            LatLng userlatLng = new LatLng(location.getLatitude(),location.getLongitude());
//            MarkerOptions options = new MarkerOptions().position(userlatLng).title(selectedadress);
//            mMap.addMarker(options);
//        }else{
            LatLng userlatLng = new LatLng(location.getLatitude(),location.getLongitude());
            MarkerOptions options = new MarkerOptions().position(userlatLng).title("your location");
            mMap.addMarker(options);
//        }


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
}

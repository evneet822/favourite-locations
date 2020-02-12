package com.example.a759831andrioidassignment;

import android.location.Location;

import java.util.ArrayList;

public class Locations {

    private Double userLat;
    private Double userLong;
    private String address;
    private Location favLocation;




    public static ArrayList<Locations> savedLocations = new ArrayList<>();

    public Locations(double userLat, double userLong, String address) {
        this.userLat = userLat;
        this.userLong = userLong;
        this.address = address;
    }

    public Locations(String address, Location favLocation) {
        this.address = address;
        this.favLocation = favLocation;
    }

    public Double getUserLat() {
        return userLat;
    }

    public Double getUserLong() {
        return userLong;
    }

    public String getAddress() {
        return address;
    }

    public Location getFavLocation() {
        return favLocation;
    }
}

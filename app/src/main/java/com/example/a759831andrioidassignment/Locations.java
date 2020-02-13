package com.example.a759831andrioidassignment;

import android.location.Location;

import java.util.ArrayList;

public class Locations {

    private Double userLat;
    private Double userLong;
    private String address, date;




    public static ArrayList<Locations> savedLocations = new ArrayList<>();

    public Locations(Double userLat, Double userLong, String address, String date) {
        this.userLat = userLat;
        this.userLong = userLong;
        this.address = address;
        this.date = date;
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

    public String getDate() {
        return date;
    }
}

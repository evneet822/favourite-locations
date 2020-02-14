package com.example.a759831andrioidassignment;

import android.location.Location;

import java.util.ArrayList;

public class Locations {


    private int id;
    private Double userLat;
    private Double userLong;
    private String address, date;
    private int isVisited;





    public static ArrayList<Locations> savedLocations = new ArrayList<>();

    public Locations(int id, Double userLat, Double userLong, String address, String date, int isVisited) {
        this.id = id;
        this.userLat = userLat;
        this.userLong = userLong;
        this.address = address;
        this.date = date;
        this.isVisited = isVisited;
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

    public int isVisited() {
        return isVisited;
    }

    public int getId() {
        return id;
    }
}

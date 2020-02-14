package com.example.a759831andrioidassignment;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetDirectionsData extends AsyncTask<Object, String, String> {

    String googleDirectionsData;
    GoogleMap mMap;
    String url;

    String distance;
    String duration;

    LatLng latLng;

    public static List<Polyline> polylineList = new ArrayList<>();


    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        latLng = (LatLng) objects[2];

        FetchURL fetchURL = new FetchURL();
        try {
            googleDirectionsData = fetchURL.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googleDirectionsData;
    }

    @Override
    protected void onPostExecute(String s) {

        HashMap<String, String> distances = null;
        DataParser distancesParser = new DataParser();
        distances = distancesParser.parseDistance(s);

        distance = distances.get("distance");
        duration = distances.get("duration");





//        LocationActivity.marker.remove();
//        polyline.remove();


        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("Duration : " + duration)
                .snippet("Distance : " + distance);
        mMap.addMarker(markerOptions);
        LocationActivity.distance = distance;
        LocationActivity.duration = duration;


        if (LocationActivity.requestedDirection) {
            String[] directionsList;
            DataParser parser = new DataParser();
            directionsList = parser.parseDirections(s);
            Log.d("", "onPostExecute: " + directionsList);
            displayDirections(directionsList);
        }
    }

    private void displayDirections(String[] directionsList) {

        for (int i = 0; i < polylineList.size(); i++){
            polylineList.get(i).remove();
        }


        int count = directionsList.length;
        for (int i = 0; i < count; i++) {
            PolylineOptions options = new PolylineOptions()
                    .color(Color.RED)
                    .width(10)
                    .addAll(PolyUtil.decode(directionsList[i]));
            polylineList.add(mMap.addPolyline(options));
        }
    }

}

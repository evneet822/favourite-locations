package com.example.a759831andrioidassignment;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.HashMap;

public class GetDirectionsData extends AsyncTask<Object, String, String> {

    String googleDirectionsData;
    GoogleMap mMap;
    String url;

    String distance;
    String duration;

    LatLng latLng;

    private Polyline polyline;

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

        if(LocationActivity.marker != null)
            LocationActivity.marker.remove();

        if(polyline != null)
            polyline.remove();

//        LocationActivity.marker.remove();
//        polyline.remove();


        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("Duration : " + duration)
                .snippet("Distance : " + distance);
        mMap.addMarker(markerOptions);


        if (LocationActivity.requestedDirection) {
            String[] directionsList;
            DataParser parser = new DataParser();
            directionsList = parser.parseDirections(s);
            Log.d("", "onPostExecute: " + directionsList);
            displayDirections(directionsList);
        }
    }

    private void displayDirections(String[] directionsList) {
        int count = directionsList.length;
        for (int i = 0; i < count; i++) {
            PolylineOptions options = new PolylineOptions()
                    .color(Color.RED)
                    .width(10)
                    .addAll(PolyUtil.decode(directionsList[i]));
            polyline = mMap.addPolyline(options);
            mMap.addPolyline(options);
        }
    }

}

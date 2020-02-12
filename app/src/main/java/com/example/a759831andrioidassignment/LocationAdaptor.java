package com.example.a759831andrioidassignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class LocationAdaptor extends ArrayAdapter {

    Context mcontext;
    private List<Locations> locations;
    private final int layoutresource;



    public LocationAdaptor(@NonNull Context context, int resource, List<Locations> locations) {
        super(context, resource, locations);
        this.mcontext = context;
        this.locations = locations;
        this.layoutresource = resource;
    }

    @Override
    public int getCount() {
        return locations.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mcontext);
        View v = layoutInflater.inflate(layoutresource,null);

        TextView adrss = v.findViewById(R.id.address_text_view);

        final Locations locations1 = locations.get(position);

        System.out.println("adaptor view "  + locations1.getAddress());

        adrss.setText(locations1.getAddress());

        return v;
    }
}

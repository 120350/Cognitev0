package com.example.mohamedashour.cognitev;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Mohamed Ashour on 14/09/2017.
 */
public class nearbyPlacesAdapter extends ArrayAdapter<foursquareElements> {

    ArrayList<foursquareElements> places;
    Context context;
    public nearbyPlacesAdapter(Context context, int textViewResourceId, ArrayList<foursquareElements> objects) {
        super(context, textViewResourceId, objects);
        this.places = objects;
        this.context = context;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.row_layout, null,true);
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView address = (TextView) view.findViewById(R.id.address);
        final ImageView image = (ImageView) view.findViewById(R.id.image);

        name.setText(places.get(position).getName());
        address.setText(places.get(position).getAddress());
        Picasso.with(context)
                .load(Uri.parse(places.get(position).getCategoryIcon()))
                .error(R.mipmap.ic_launcher)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        // Try again online if cache failed
                        try {
                            Picasso.with(context)
                                    .load(Uri.parse(places.get(position).getCategoryIcon()))
                                    .error(R.mipmap.ic_launcher)
                                    .into(image);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        return view;
    }



}
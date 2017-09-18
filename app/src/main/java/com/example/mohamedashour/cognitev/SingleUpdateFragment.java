package com.example.mohamedashour.cognitev;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class SingleUpdateFragment extends Fragment {
    private GoogleMap googleMap;

    public SingleUpdateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_single_update, container, false);


        return view;
    }

}

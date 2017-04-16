package com.example.shyam.mydash;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import static android.widget.Toast.makeText;


public class RestaurantSearchFragment extends Fragment {

    public RestaurantSearchFragment() {
    }

    public static RestaurantSearchFragment newInstance(String param1, String param2) {
        RestaurantSearchFragment fragment = new RestaurantSearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static Address geoCodeZipCode(String zipCode, Activity a) {
        final Geocoder geocoder = new Geocoder(a);
        try {
            List<Address> addresses = geocoder.getFromLocationName(zipCode, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                // Use the address as needed
                String message = String.format("Latitude: %f, Longitude: %f",
                        address.getLatitude(), address.getLongitude());
                //makeText(a, message, Toast.LENGTH_LONG).show();
                return address;
            } else {
                makeText(a, "Unable to geocode zipcode, please enter valid zip code", Toast.LENGTH_LONG).show();
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant_search, container, false);

        final EditText et = (EditText) view.findViewById(R.id.searchzipcode);
        final Button b = (Button) view.findViewById(R.id.searchclick);
        b.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO: validate the zipcode value before passing it in
                String zipcode = et.getText().toString();
                if (!zipcode.isEmpty()) {
                    if (geoCodeZipCode(zipcode, getActivity()) != null) {
                        Intent intent = new Intent(getActivity(), RestaurantSearchListActivity.class);
                        intent.putExtra("zipcode", zipcode.toString());
                        startActivity(intent);
                    }
                }
            }
        });

        return view;

    }



}

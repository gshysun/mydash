package com.example.shyam.mydash;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.shyam.objectmodels.RestaurantList;

public class RestaurantSearchListActivity extends AppCompatActivity {
    private RestaurantListFragment mRestaurantListFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_search_list);
        mRestaurantListFragment = new RestaurantListFragment(getIntent().getStringExtra("zipcode"));
        setTitle("Search results for " + getIntent().getStringExtra("zipcode"));

        //mRestaurantListFragment.SetRestaurantList(RestaurantList.createDummyRestaurantList());
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragcontent, mRestaurantListFragment).commit();
    }

}

package com.example.shyam.mydash;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import com.example.shyam.objectmodels.RestaurantList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

// I have used Volley for the networked requests/response
// and the following for a bottom/action bar
// https://github.com/roughike/BottomBar

public class StartingActivity extends FragmentActivity {

    private enum Panel {
        SEARCH, FAVS
    }
    private static String TAG = "StartingActivity";
    private Panel sCurrPanel = Panel.SEARCH;
    private RestaurantListFragment sRestaurantListFragment = null;
    private RestaurantSearchFragment sRestaurantSearchFragment = null;
    private static ArrayList<RestaurantList> sFavorites = null;

    public static void addToFavorites(RestaurantList r) {
        for(int i = 0; i< sFavorites.size(); i++) {
            // a cursory check before adding it into existing favs list
            if (sFavorites.get(i).placeName.equals(r.placeName) &&
                    sFavorites.get(i).placeAddress.equals(r.placeAddress)) {
                return;
            }
        }
        sFavorites.add(r);
    }

    public void setupBottomBarHandlers(final FragmentActivity fa, int bottomBarId) {
        BottomBar bottombar = (BottomBar) fa.findViewById(bottomBarId);
        bottombar.setOnTabSelectListener(new OnTabSelectListener() {
               @Override
               public void onTabSelected(@IdRes int tabId) {
                   if (tabId == R.id.favs && sCurrPanel != Panel.FAVS) {
                       Log.d(TAG, "Favs clicked");
                       Gson gson = new Gson();
                       String value = gson.toJson(sFavorites);

                       sCurrPanel = Panel.FAVS;
                       if (sRestaurantListFragment!=null)
                            sRestaurantListFragment = null;
                       sRestaurantListFragment = new RestaurantListFragment(sFavorites);

                       FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                       t.replace(R.id.maincontentpanel, sRestaurantListFragment);
                       t.commit();

                   } else if (tabId == R.id.search && sCurrPanel != Panel.SEARCH) {
                       Log.d(TAG, "Search clicked");
                       sCurrPanel = Panel.SEARCH;
                       if (sRestaurantSearchFragment == null)
                           sRestaurantSearchFragment = new RestaurantSearchFragment();

                       FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                       t.replace(R.id.maincontentpanel, sRestaurantSearchFragment);
                       t.commit();
                   }
               }
           }
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        sFavorites = new ArrayList<RestaurantList>();
        SharedPreferences prefs = this.getSharedPreferences("settings", Context.MODE_PRIVATE);
        String value = prefs.getString("favorites", null);
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();
        sFavorites = gson.fromJson(value,  new TypeToken<List<RestaurantList>>(){}.getType());

        sRestaurantSearchFragment = new RestaurantSearchFragment();
        getSupportFragmentManager().beginTransaction().
                add(R.id.maincontentpanel, sRestaurantSearchFragment).commit();

        setupBottomBarHandlers(this, R.id.bottomBar);

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Ideally it should just be saved to "user" profile on the cloud, but faking it for now
        SharedPreferences prefs = this.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();

        String value = gson.toJson(sFavorites);
        prefsEditor.putString("favorites", value);
        prefsEditor.commit();
    }

}

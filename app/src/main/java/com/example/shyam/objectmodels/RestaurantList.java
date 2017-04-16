package com.example.shyam.objectmodels;

import java.util.ArrayList;

/**
 * Created by sgopalakrishnan on 4/15/17.
 */

public class RestaurantList {
    // place name, photo, address, type of establishment, rating, price level

    public String placeName;
    public String placeAddress;
    public int priceLevel;
    public float rating;
    public String photosRef;
    public String categories;

    public RestaurantList(String pn, String pa, int pl, float r, String pr, String c) {
        placeName = pn;
        placeAddress = pa;
        priceLevel = pl;
        rating = r;
        photosRef = pr;
        categories = c;
    }

    public static ArrayList<RestaurantList> createDummyRestaurantList() {
        ArrayList<RestaurantList> al = new ArrayList<>();
        for(int i=0; i<100; i++) {
            al.add(new RestaurantList("Bengali Sweets (which closed now)",
                    "701 First Ave, Sunnyvale, CA - 94087", 2, (float) 4.5, "notavailable",
                    "fast food, indian, restaurant"));
        }
        return al;
    }
}

package com.example.shyam.mydash;

import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.shyam.objectmodels.RestaurantList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.widget.Toast.makeText;

public class RestaurantListFragment extends Fragment {
    private ArrayList<RestaurantList> mRestaurantList = null;
    private RecyclerView mRestaurantListRecyclerView = null;
    private String mZipcode = "";
    private boolean mIsNetworked = false;

    public void SetZipCode(String s) {
        mZipcode = s;
    }
    public void SetRestaurantList(ArrayList<RestaurantList> rl) {
        mRestaurantList = rl;
    }

    public RestaurantListFragment(String s) {
        mZipcode = s;
        mRestaurantList = new ArrayList<RestaurantList>();
        // kick off request to get the list of places
        mIsNetworked = true;
    }

    public RestaurantListFragment(ArrayList<RestaurantList> rl) {
        mRestaurantList = rl;
        mIsNetworked = false;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // search for the places
    // https://maps.googleapis.com/maps/api/place/nearbysearch/json?
    // location=-33.8670522,151.1957362
    // &radius=500
    // &type=restaurant
    // &keyword=indian
    // &key=YOUR_API_KEY

    // retrieving the photo ref
    // https://maps.googleapis.com/maps/api/place/photo?
    // maxwidth=400
    // &photoreference=PHOTO_REF
    // &key=YOUR_API_KEY
    private static String MYGOOGLEAPIKEY = "AIzaSyCFLljdZtmqRjOv0diWvfmQ0K7Z-oO2Uw8";
    private static String GOOGLEAPIBASEURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/";
    private static String TAG = "RestaurantListFragment";
    private int previousTotal = 0;
    private boolean loading = true;
    private String next_page_token = "";
    private int visibleThreshold = 5;
    RequestQueue requestQueue;
    RestaurantListAdapter adapter;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private void fetchResultsForApi(String url) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONArray jsonArray = null;
                    Log.d(TAG, "got response!!");
                    try {
                        jsonArray = response.getJSONArray("results");
                        Log.d(TAG, "results - " + jsonArray.length());
                        for(int i=0; i<jsonArray.length(); i++){
                            // Some times the next page token responses are 0, so reset only
                            // after you get a valid set of responses
                            next_page_token = "";
                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                            //JSONObject photos = jsonObject.getJSONObject("photos");
                            String types_string = "Tags: ";
                            if (jsonObject.has("types")) {
                                JSONArray types = jsonObject.getJSONArray("types");
                                for (int j=0; j< types.length(); j++) {
                                    types_string += types.getString(j);
                                    if (j+1>=2)
                                        break ; // two tags is enough
                                    else
                                        types_string+= ", ";

                                }
                            }
                            String name = "";
                            if (jsonObject.has("name"))
                                name = jsonObject.getString("name");
                            String vicinity = "";
                            if (jsonObject.has("vicinity")) {
                                vicinity = jsonObject.getString("vicinity");
                                if (vicinity.length() > 35) {
                                    vicinity = vicinity.substring(0,Math.min(vicinity.length(), 30));
                                    vicinity += "...";
                                }
                            }
                            int price_level = 0;
                            if (jsonObject.has("price_level"))
                                price_level = (int) jsonObject.getInt("price_level");
                            float rating = 0;
                            if (jsonObject.has("rating"))
                                rating = (float) jsonObject.getDouble("rating");

                            RestaurantList item = new RestaurantList(
                                    name,
                                    vicinity,
                                    price_level,
                                    rating,
                                    "emtpy",//photos.getString("photo_reference"),
                                    types_string.toString()
                            );
                            Log.d(TAG, "Adding item for = " + item.placeName);
                            mRestaurantList.add(item);
                            adapter.notifyDataSetChanged();
                        }
                        if (response.has("next_page_token")) {
                            next_page_token = response.getString("next_page_token");
                            Log.d(TAG, "next_page_token = " + next_page_token);
                        } else {
                            Log.d(TAG, "no next page to be recieved");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }                       }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
            }
        });
        requestQueue.add(req);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant_list, container, false);

        mRestaurantListRecyclerView = (RecyclerView) view.findViewById(R.id.rvRestaurantList);
        adapter = new RestaurantListAdapter(getContext(), mRestaurantList);
        mRestaurantListRecyclerView.setAdapter(adapter);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRestaurantListRecyclerView.setLayoutManager(llm);

        if (!mIsNetworked) return view;


        // the rest of the processing is only for the networked requests

        requestQueue = Volley.newRequestQueue(getContext());
        Address address = RestaurantSearchFragment.geoCodeZipCode(mZipcode, getActivity());

        String placesearch = GOOGLEAPIBASEURL + "json?location=" + address.getLatitude()
                                + "," + address.getLongitude()
                                + "&radius=5000" // so that it would trigger
                                                 // refetch for endless scrolling
                                + "&type=restaurant" + "&key="
                                + MYGOOGLEAPIKEY;

        Log.d(TAG, "API - " + placesearch);
        fetchResultsForApi(placesearch);
        mRestaurantListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public final void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!recyclerView.canScrollVertically(-1)) {
                } else if (!recyclerView.canScrollVertically(1)) {
                    onScrolledToBottom();
                } else if (dy < 0) {
                } else if (dy > 0) {
                }
            }

            public void onScrolledToBottom() {
                //Do pagination.. i.e. fetch new data
                if (!next_page_token.isEmpty()) {
                    String url = GOOGLEAPIBASEURL + "json?pagetoken=" + next_page_token
                            + "&key=" + MYGOOGLEAPIKEY;
                    Log.d(TAG, "Paginating - " + url);
                    fetchResultsForApi(url);
                    makeText(getContext(), "Updating list...", Toast.LENGTH_SHORT).show();

                }
            }

        });
        return view;
    }



}

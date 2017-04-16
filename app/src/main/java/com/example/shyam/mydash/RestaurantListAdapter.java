package com.example.shyam.mydash;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shyam.objectmodels.RestaurantList;

import java.util.ArrayList;

import static android.widget.Toast.makeText;

/**
 * Created by sgopalakrishnan on 4/15/17.
 */

public class RestaurantListAdapter extends RecyclerView.Adapter <RestaurantListAdapter.ViewHolder> implements View.OnClickListener, View.OnLongClickListener{
    private static String TAG = "RestaurantListAdapter";
    private ArrayList<RestaurantList> mList;
    private static Context sContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView heroImage;
        private TextView nameOfRestaurant;
        private TextView tags;
        private TextView address;
        private TextView priceLevel;
        private TextView rating;
        private TextView timeToArrive;
        private TextView price; // out of 3 $
        private TextView hoursOfOperation;
        private int itemPosition;
        private boolean isExpanded;

        public ViewHolder(View itemView) {
            super(itemView);
            nameOfRestaurant = (TextView) itemView.findViewById(R.id.restaurant_title);
            address = (TextView) itemView.findViewById(R.id.restaurant_address);
            tags = (TextView) itemView.findViewById(R.id.restaurant_tags);
            priceLevel = (TextView) itemView.findViewById(R.id.restaurant_price_level);
            rating = (TextView) itemView.findViewById(R.id.restaurant_rating);
        }


    }

    public RestaurantListAdapter(Context c, ArrayList<RestaurantList> rl) {
        sContext = c;
        mList = rl;
    }

    public static Context getContext() {
        return sContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View rlView = inflater.inflate(R.layout.item_restaurant, parent, false);
        final ViewHolder viewHolder = new ViewHolder(rlView);
        viewHolder.itemView.setOnClickListener(RestaurantListAdapter.this);
        viewHolder.itemView.setOnLongClickListener(RestaurantListAdapter.this);
        viewHolder.itemView.setTag(viewHolder);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RestaurantListAdapter.ViewHolder holder, int position) {
        RestaurantList item = mList.get(position);
        TextView tv = holder.nameOfRestaurant;
        tv.setText((CharSequence) item.placeName);
        tv = holder.address;
        tv.setText(item.placeAddress);
        tv = holder.tags;
        tv.setText(item.categories);
        tv = holder.priceLevel;
        String pl = "Price: ";
        if (item.priceLevel == 0)
            pl += "(N/A)";
        else
            for(int i=0; i<item.priceLevel; i++)
                pl += "$";

        tv.setText(pl);
        tv = holder.rating;
        tv.setText("Rating:" + item.rating);

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View view) {
        ViewHolder holder = (ViewHolder) view.getTag();
        // add to favorites list or go to individual menu
    }

    @Override
    public boolean onLongClick(View v) {
        ViewHolder holder = (ViewHolder) v.getTag();
        StartingActivity.addToFavorites(mList.get(holder.getAdapterPosition()));
        // Oh then what about removing from the favorites list?
        // Sorry, love is forever, cant undo (atleast not now :P)
        makeText(getContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();

        return false;
    }


}

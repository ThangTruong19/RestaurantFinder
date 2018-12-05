package com.thangtruong19.restaurantfinder.Menu;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.thangtruong19.restaurantfinder.R;

import java.util.List;

/**
 * Created by User on 05/12/2018.
 */

public class MenuAdapter extends ArrayAdapter<RestaurantMenu>{
    public MenuAdapter(@NonNull Context context, int resource, @NonNull List<RestaurantMenu> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView= ((Activity)getContext()).getLayoutInflater().inflate(R.layout.item_menu,parent,false);
        }
        TextView nameTextview=convertView.findViewById(R.id.name);
        TextView priceTextview=convertView.findViewById(R.id.price);

        RestaurantMenu currentMenuItem=getItem(position);

        nameTextview.setText(currentMenuItem.getName());
        priceTextview.setText(Integer.toString(currentMenuItem.getPrice()));

        return convertView;
    }
}

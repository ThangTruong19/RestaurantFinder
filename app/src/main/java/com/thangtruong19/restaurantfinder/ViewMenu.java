package com.thangtruong19.restaurantfinder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thangtruong19.restaurantfinder.Menu.MenuAdapter;
import com.thangtruong19.restaurantfinder.Menu.RestaurantMenu;

import java.util.ArrayList;
import java.util.List;

public class ViewMenu extends AppCompatActivity {

    private String currentRestaurant;
    private String referenceString;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;
    private ListView menuListview;
    private MenuAdapter menuAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_menu);

        currentRestaurant=Common.currentResult.getName();
        switch (currentRestaurant){
            case "ABC Bakery & Café":
                referenceString="ABC";
                break;
            case "Deja Vu Coffee":
                referenceString="dejavu";
                break;
            case "Starbucks New World":
                referenceString="starbuck";
                break;
            case "The Coffee Bean & Tea Leaf":
                referenceString="coffeebean";
                break;
            case "Highland Coffee":
                referenceString="highland";
                break;
                default:
                    referenceString="noMenu";
        }

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference().child(referenceString);

        menuListview=findViewById(R.id.menu_list_view);

        menuListview.setEmptyView(findViewById(R.id.empty_view));

        List<RestaurantMenu> restaurantMenus=new ArrayList<>();
        menuAdapter=new MenuAdapter(this,R.layout.item_menu,restaurantMenus);
        menuListview.setAdapter(menuAdapter);

        if(childEventListener==null){
            childEventListener=new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    RestaurantMenu restaurantMenu=dataSnapshot.getValue(RestaurantMenu.class);
                    menuAdapter.add(restaurantMenu);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            databaseReference.addChildEventListener(childEventListener);
        }
    }
}

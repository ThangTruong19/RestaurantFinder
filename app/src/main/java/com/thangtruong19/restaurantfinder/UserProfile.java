package com.thangtruong19.restaurantfinder;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.thangtruong19.restaurantfinder.Model.User;
import com.thangtruong19.restaurantfinder.Model.UserAccountSettings;
import com.thangtruong19.restaurantfinder.utils.UniversalImageLoader;

import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserProfile extends AppCompatActivity {

    private static final String TAG = "ProfileFragment";

    private static final int ACTIVITY_NUM = 4;

    //firebase
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private Context mContext;
    private String userID;
    private TextView tv_email, tv_phone,tv_location,tv_description,tv_username;
    private CircleImageView avatar;
    private Button btn_edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_description = (TextView) findViewById(R.id.tv_description);
        mContext = UserProfile.this;
        avatar = (CircleImageView) findViewById(R.id.avatar);
        btn_edit = (Button) findViewById(R.id.btn_edit);
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        userID = auth.getCurrentUser().getUid();
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, EditProfile.class);
                intent.putExtra(getString(R.string.calling_activity), getString(R.string.profile_activity));
                startActivity(intent);
            }
        });
        myRef = mFirebaseDatabase.getReference().child("users").child(userID);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String userEmail = user.getEmail();
                String userAddress = user.getAddress();
                String userPhone = String.valueOf(user.getPhone_number());
                String userName =user.getUsername();
                tv_username.setText(userName);
                tv_email.setText(userEmail);
                tv_location.setText(userAddress);
                tv_phone.setText(userPhone);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        myRef.addListenerForSingleValueEvent(valueEventListener);
    }
}


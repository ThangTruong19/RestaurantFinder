package com.thangtruong19.restaurantfinder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.thangtruong19.restaurantfinder.Model.User;
import com.thangtruong19.restaurantfinder.Model.UserAccountSettings;
import com.thangtruong19.restaurantfinder.utils.StringManipulation;

public class RegisterActivity extends AppCompatActivity {
    private String email, username, password,address;
    private long phone;
    private MaterialEditText re_email,re_username,re_password,re_phone,re_location;
    private Button btn_back_rg,sign_up_button;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userID;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private String append = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initWidgets();
        setupFirebaseAuth();
        init();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisterActivity.this,SignInMethod.class));
        finish();
        super.onBackPressed();
    }

    private void initWidgets() {
        re_email = (MaterialEditText) findViewById(R.id.re_email);
        re_username = (MaterialEditText) findViewById(R.id.re_username);
        re_password = (MaterialEditText) findViewById(R.id.re_password);
        re_phone=(MaterialEditText)findViewById(R.id.re_phone);
        re_location=(MaterialEditText) findViewById(R.id.re_location);
        sign_up_button=(Button)findViewById(R.id.sign_up_button);
        btn_back_rg=(Button)findViewById(R.id.btn_back_rg);
        btn_back_rg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,SignInMethod.class));
                finish();
            }
        });
    }

    private void init() {
        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=re_email.getText().toString();
                username=re_username.getText().toString();
                password=re_password.getText().toString();
                phone=Long.parseLong(re_phone.getText().toString());
                address=re_location.getText().toString();
                password=re_password.getText().toString();
                if(checkInputs(email,username,password,phone,address)){
                    registerNewEmail(email,password,username);
                }
            }
        });
    }
    public void registerNewEmail(final String email, String password, final String username){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();

                        }
                        else if(task.isSuccessful()){
                            //send verificaton email
                            sendVerificationEmail();

                            userID = auth.getCurrentUser().getUid();
                            startActivity(new Intent(RegisterActivity.this,SignInMethod.class));
                            finish();
                        }

                    }
                });
    }

    public void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                            }else{
                                Toast.makeText(RegisterActivity.this, "couldn't send verification email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    private boolean checkInputs(String email, String username, String password,long phone,String address){
        if(email.equals("") || username.equals("") || password.equals("")||re_phone.equals("")||address.equals("")){
            Toast.makeText(RegisterActivity.this, "All fields must be filled out.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private boolean isStringNull(String string){

        if(string.equals("")){
            return true;
        }
        else{
            return false;
        }
    }

     /*
    ------------------------------------ Firebase ---------------------------------------------
     */


    private void checkIfUsernameExists(final String username) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    if (singleSnapshot.exists()){
                        append = myRef.push().getKey().substring(3,10);
                    }
                }

                String mUsername = "";
                mUsername = username + append;

                //add new user to the database
                addNewUser(email, mUsername, "", "",phone,address,password);

                Toast.makeText(RegisterActivity.this, "Signup successful. Sending verification email.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void addNewUser(String email, String username, String description, String profile_photo,long phone,String address,String password){

        User user = new User( userID,  phone,  email,  StringManipulation.condenseUsername(username) ,address);

        myRef.child(RegisterActivity.this.getString(R.string.dbname_users))
                .child(userID)
                .setValue(user);


        UserAccountSettings settings = new UserAccountSettings(
                description,
                username,
                profile_photo,
                StringManipulation.condenseUsername(username),password);

        myRef.child(RegisterActivity.this.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .setValue(settings);

    }
    private void setupFirebaseAuth(){

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in

                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            checkIfUsernameExists(username);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    finish();

                } else {
                    // User is signed out

                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            auth.removeAuthStateListener(mAuthListener);
        }
    }
}

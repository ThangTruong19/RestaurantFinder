package com.thangtruong19.restaurantfinder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SectionActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    TextView emailtxt,status;
    Button signout;
    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section);
        auth=FirebaseAuth.getInstance();
        emailtxt=(TextView)findViewById(R.id.emailtxt);
        status=(TextView)findViewById(R.id.status);
        user=auth.getCurrentUser();
        emailtxt.setText(user.getEmail());
        if(user.isEmailVerified()){
            status.setText("Email Verified");
        }
        signout=(Button)findViewById(R.id.signout);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }
    public void signOut(){
        auth.signOut();
        finish();
        Intent i=new Intent(this,SignInMethod.class);
        startActivity(i);
    }
}

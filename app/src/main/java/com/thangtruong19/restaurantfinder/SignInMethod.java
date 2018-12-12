package com.thangtruong19.restaurantfinder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thangtruong19.restaurantfinder.Model.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignInMethod extends AppCompatActivity {
    Button btn_su, btn_login, btn_reset;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/SVN_Helvetica Neue Condensed Regular.ttf").setFontAttrId(R.attr.fontPath).build());
        setContentView(R.layout.activity_sign_in_method);
        //Init Firebase
        setupFirebaseAuth();
        btn_su = (Button) findViewById(R.id.btn_su);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_reset = (Button) findViewById(R.id.btn_reset);
        if (auth.getCurrentUser() != null) {
            //startActivity(new Intent(SignInMethod.this, SectionActivity.class));
            startActivity(new Intent(SignInMethod.this, MapsActivity.class));
            finish();
        }
        btn_su.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(SignInMethod.this,RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder AlertDialog = new AlertDialog.Builder(SignInMethod.this);
                AlertDialog.setTitle("Reset Password");
                AlertDialog.setMessage("Please use your email to reset your account");
                LayoutInflater inflater = LayoutInflater.from(SignInMethod.this);
                View forgot = inflater.inflate(R.layout.layout_forgotpwd, null);
                final MaterialEditText editrsEmail = (MaterialEditText) forgot.findViewById(R.id.editrsEmail);
                AlertDialog.setView(forgot);
                AlertDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = editrsEmail.getText().toString();
                        if (TextUtils.isEmpty(email)) {
                            editrsEmail.setError("Input your Email ID");
                            return;
                        }


                        final SpotsDialog waitingDialog = new SpotsDialog(SignInMethod.this);
                        waitingDialog.show();
                        auth.sendPasswordResetEmail(email)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            waitingDialog.dismiss();
                                            Toast.makeText(SignInMethod.this, "We have sent you an email to reset your password!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            waitingDialog.dismiss();
                                            Toast.makeText(SignInMethod.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                    }
                });
                AlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog.show();
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialEditText email=(MaterialEditText) findViewById(R.id.email);
                final MaterialEditText pass=(MaterialEditText)findViewById(R.id.pass);
                String Email = email.getText().toString();
                final String Password = pass.getText().toString();

                if (TextUtils.isEmpty(Email)&&TextUtils.isEmpty(Password)) {
                    email.setError("Enter email address");
                    pass.setError("Enter password");
                    return;
                }else if(TextUtils.isEmpty(Email)){
                    email.setError("Enter email address");
                    return;
                } else if(TextUtils.isEmpty(Password)){
                    pass.setError("Enter password");
                    return;
                }

                final SpotsDialog waitingDialog = new SpotsDialog(SignInMethod.this);
                waitingDialog.show();

                //authenticate user
                auth.signInWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(SignInMethod.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                waitingDialog.dismiss();
                                if (!task.isSuccessful()) {
                                    // there was an error

                                    if (Password.length() < 6) {
                                        pass.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(SignInMethod.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    btn_login.setEnabled(false);
                                    Toast.makeText(SignInMethod.this, "Login Succeed", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignInMethod.this, MapsActivity.class));
                                    finish();
                                }
                            }
                        });
            }
        });
    }
    private boolean isStringNull(String string){

        if(string.equals("")){
            return true;
        }
        else{
            return false;
        }
    }
    private void setupFirebaseAuth() {
        auth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
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

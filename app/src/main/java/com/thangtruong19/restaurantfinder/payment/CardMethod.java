package com.thangtruong19.restaurantfinder.payment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.braintreepayments.cardform.view.CardForm;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.thangtruong19.restaurantfinder.MapsActivity;
import com.thangtruong19.restaurantfinder.R;
import com.thangtruong19.restaurantfinder.ViewMenu;

import java.io.IOException;

public class CardMethod extends AppCompatActivity {
    Button bSubmitPaymentInfo;
    CardForm card_form;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_method);
        card_form=(CardForm)findViewById(R.id.card_form);
        bSubmitPaymentInfo=(Button)findViewById(R.id.bSubmitPaymentInfo);
        card_form.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(true)
                .mobileNumberRequired(true)
                .setup(CardMethod.this);
        card_form.getCvvEditText().setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        bSubmitPaymentInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(card_form.isValid()){
                    AlertDialog.Builder alertBuilder=new AlertDialog.Builder(CardMethod.this);
                    alertBuilder.setTitle("Confirm before purchase");
                    alertBuilder.setMessage("Card number: "+card_form.getCardNumber()+"\n"+
                    "Phone number: "+ card_form.getMobileNumber());
                    alertBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            Toast.makeText(CardMethod.this,"Succeed",Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(CardMethod.this,ViewMenu.class);
                            startActivity(i);
                        }
                    });
                    alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog=alertBuilder.create();
                    alertDialog.show();
                }else{
                    Toast.makeText(CardMethod.this,"Please complete the form",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
package com.thangtruong19.restaurantfinder.payment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.thangtruong19.restaurantfinder.MapsActivity;
import com.thangtruong19.restaurantfinder.R;
import com.thangtruong19.restaurantfinder.ViewMenu;

public class PaymentMethod extends AppCompatActivity {
    RadioButton first,second;
    Button bSubmitPaymentMethod;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);
        first=(RadioButton)findViewById(R.id.first);
        second=(RadioButton)findViewById(R.id.second);
        bSubmitPaymentMethod =(Button)findViewById(R.id.bSubmitPaymentMethod);
        bSubmitPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(first.isChecked()){
                    Intent i=new Intent(PaymentMethod.this,CardMethod.class);
                    startActivity(i);
                    finish();
                }else if(second.isChecked()){
                    finish();
                    Toast.makeText(PaymentMethod.this,"Succeed",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PaymentMethod.this,ViewMenu.class));
                }
            }
        });
    }
}

package com.thangtruong19.restaurantfinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.thangtruong19.restaurantfinder.payment.PaymentMethod;

import java.text.NumberFormat;

public class OrderSummary extends AppCompatActivity {
    String mname,msubprice,mplace,mdate,mtime,mtax,mtotalprice;
    TextView product_name,tvSubTotal,tvTax,tvDeliveryDate,tvDeliveryTime,tvAddress,tvOrderTotal;
    Button btn_submit_order;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);
        product_name=(TextView)findViewById(R.id.product_name);
        tvSubTotal=(TextView)findViewById(R.id.tvSubTotal);
        tvTax=(TextView)findViewById(R.id.tvTax);
        tvDeliveryDate=(TextView)findViewById(R.id.tvDeliveryDate);
        tvDeliveryTime=(TextView)findViewById(R.id.tvDeliveryTime);
        tvAddress=(TextView)findViewById(R.id.tvAddress);
        tvOrderTotal=(TextView)findViewById(R.id.tvOrderTotal);
        btn_submit_order=(Button)findViewById(R.id.btn_submit_order);
        Intent givenData = getIntent();
        Bundle bundle = givenData.getExtras();
        try {
            mname = bundle.getString("name");
            msubprice = bundle.getString("subprice");
            mplace = bundle.getString("place");
            mdate = bundle.getString("date");
            mtime = bundle.getString("time");
            mtax=bundle.getString("tax");
            mtotalprice=bundle.getString("totalprice");
        } catch (NullPointerException e) {
            mname = "";
            msubprice = "";
            mplace = "";
            mdate = "";
            mtime = "";
            mtax="";
            mtotalprice="";
        }
        product_name.setText(mname);
        tvSubTotal.setText(msubprice);
        tvDeliveryDate.setText(mdate);
        tvDeliveryTime.setText(mtime);
        tvAddress.setText(mplace);
        tvTax.setText(mtax);
        tvOrderTotal.setText(mtotalprice);
        btn_submit_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderSummary.this,PaymentMethod.class));
            }
        });
    }
}

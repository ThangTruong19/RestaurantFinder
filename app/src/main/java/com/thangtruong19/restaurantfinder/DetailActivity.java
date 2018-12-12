package com.thangtruong19.restaurantfinder;

import android.content.ContentResolver;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.thangtruong19.restaurantfinder.Menu.RestaurantMenu;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import java.text.NumberFormat;
import java.util.Calendar;

public class DetailActivity extends AppCompatActivity {
    ContentResolver mContentResolver;
    TextView tv_name,summary;
    String name,price,date,time,mtaxx;
    int mtax;
    int mtotalprice;
    private int subPrice;
    int sprice;
    public String mplace;
    private int mQuantity = 1;
    Button Order,btn_pickdate,btn_picktime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        PlaceAutocompleteFragment autocompleteFragment =
                (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(
                        R.id.place_autocomplete_fragment);
        autocompleteFragment.setFilter(new AutocompleteFilter.Builder().setCountry("VN").build());
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mplace=place.getName().toString();
            }

            @Override
            public void onError(Status status) {

            }
        });
        tv_name=(TextView)findViewById(R.id.tv_name);
        summary=(TextView)findViewById(R.id.summary_text_view);
        Order=(Button)findViewById(R.id.btn_order);
        btn_pickdate=(Button)findViewById(R.id.btn_pickdate);
        btn_picktime=(Button)findViewById(R.id.btn_picktime);
        btn_picktime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cur_calender = Calendar.getInstance();
                com.wdullaer.materialdatetimepicker.time.TimePickerDialog timePicker = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                        (btn_picktime).setText(hourOfDay + " : " + minute);
                        time=hourOfDay + " : " + minute;
                    }
                }, cur_calender.get(Calendar.HOUR_OF_DAY), cur_calender.get(Calendar.MINUTE), true);
                //set dark light false
                timePicker.setThemeDark(true);
                timePicker.vibrate(true);
                timePicker.setAccentColor(getResources().getColor(R.color.colorAccent));
                timePicker.show(getFragmentManager(), "Timepickerdialog");
            }
        });
        btn_pickdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cur_calender = Calendar.getInstance();
                DatePickerDialog datePicker = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, monthOfYear);
                                int month=monthOfYear+1;
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                long date_ship_millis = calendar.getTimeInMillis();
                                (btn_pickdate).setText(dayOfMonth+"/"+month+"/"+year);
                                date=dayOfMonth+"/"+month+"/"+year;
                            }
                        },
                        cur_calender.get(Calendar.YEAR),
                        cur_calender.get(Calendar.MONTH),
                        cur_calender.get(Calendar.DAY_OF_MONTH)
                );
                //set dark theme
                datePicker.setThemeDark(true);
                datePicker.setAccentColor(getResources().getColor(R.color.colorAccent));
                datePicker.setMinDate(cur_calender);
                datePicker.show(getFragmentManager(), "Datepickerdialog");
            }
        });
        mContentResolver = this.getContentResolver();
        Intent givenData=getIntent();
        Bundle bundle=givenData.getExtras();
        try{
            name=bundle.getString("name");
            price=bundle.getString("price");
        }catch(NullPointerException e){
            name="";
            price="";
        }
        tv_name.setText(name);
    }
    public void increment(View view){
        sprice=Integer.parseInt(price);
        mQuantity = mQuantity + 1;
        displayQuantity(mQuantity);
        subPrice = mQuantity * sprice;
        displayCost(subPrice);
    }

    public void decrement(View view){
        if (mQuantity > 1){

            mQuantity = mQuantity - 1;
            displayQuantity(mQuantity);
            subPrice = mQuantity * sprice;
            displayCost(subPrice);

        }
    }
    private void displayQuantity(int numberOfItems) {
        TextView quantityTextView = (TextView) findViewById(R.id.quantity_text_view);
        quantityTextView.setText(String.valueOf(numberOfItems));
    }
    public void displayCost(int totalPrice) {
        String convertPrice = NumberFormat.getCurrencyInstance().format(totalPrice);
        summary.setText(convertPrice);
    }
    public void submitOrder(View view){
        Intent intent=new Intent(DetailActivity.this,OrderSummary.class);
        String key1="name";
        String value1=name;
        String convertPrice = NumberFormat.getCurrencyInstance().format(subPrice);
        String key2="subprice";
        String value2=convertPrice;
        String key3="place";
        String value3=mplace;
        String key4="date";
        String value4=date;
        String key5="time";
        String value5=time;
        mtax=subPrice/10;
        mtaxx=NumberFormat.getCurrencyInstance().format(mtax);
        String key6="tax";
        String value6=mtaxx;
        mtotalprice=mtax+subPrice;
        String convertTotalPrice = NumberFormat.getCurrencyInstance().format(mtotalprice);
        String key7="totalprice";
        String value7=convertTotalPrice;
        Bundle bundle=new Bundle();
        bundle.putString(key1,value1);
        bundle.putString(key2,value2);
        bundle.putString(key3,value3);
        bundle.putString(key4,value4);
        bundle.putString(key5,value5);
        bundle.putString(key6,value6);
        bundle.putString(key7,value7);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}

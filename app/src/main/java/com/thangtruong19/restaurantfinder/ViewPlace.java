package com.thangtruong19.restaurantfinder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;


import com.squareup.picasso.Picasso;
import com.thangtruong19.restaurantfinder.PlaceModel.PlaceDetail;
import com.thangtruong19.restaurantfinder.Remote.IGoogleApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewPlace extends AppCompatActivity {

    ImageView photo;
    RatingBar ratingBar;
    TextView opening_hours,place_address,place_name;
    Button btnViewOnMap;
    Button btnViewMenu;

    IGoogleApiService mService;

    PlaceDetail mPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_place);

        photo=findViewById(R.id.photo);
        ratingBar=findViewById(R.id.ratingBar);
        opening_hours=findViewById(R.id.open_hours);
        place_address=findViewById(R.id.place_address);
        place_name=findViewById(R.id.place_name);
        btnViewOnMap=findViewById(R.id.btnShowMap);
        btnViewMenu=findViewById(R.id.btnViewMenu);

        btnViewOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(mPlace.getResult().getUrl()));
                startActivity(intent);
            }
        });

        btnViewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ViewPlace.this,ViewMenu.class);
                startActivity(intent);
            }
        });

        if(Common.currentResult.getPhotos() !=null && Common.currentResult.getPhotos().length>0) {
            Picasso.get()
                    .load(getPhotoOfPlace(Common.currentResult.getPhotos()[0].getPhoto_reference(),1000))
                    .placeholder(R.drawable.ic_image)
                    .error(R.drawable.ic_error)
                    .into(photo);
        }

        if(Common.currentResult.getRating()!=null && !TextUtils.isEmpty(Common.currentResult.getRating())){
            ratingBar.setRating(Float.parseFloat(Common.currentResult.getRating()));
        }else{
            ratingBar.setVisibility(View.GONE);
        }

        if(Common.currentResult.getName()!=null && !TextUtils.isEmpty(Common.currentResult.getName())){
            place_name.setText(Common.currentResult.getName());
        }else{
            place_name.setVisibility(View.GONE);
        }

        if(Common.currentResult.getOpening_hours()!=null){
            if(Common.currentResult.getOpening_hours().getOpen_now().equals("true")){
                opening_hours.setText("Open now");
            }else{
                opening_hours.setText("Closed");
            }
        }else{
            opening_hours.setVisibility(View.GONE);
        }

        mService= Common.getGoogleAPIService();

        mService.getDetailPlace(getPlaceDetailUrl(Common.currentResult.getPlace_id()))
                .enqueue(new Callback<PlaceDetail>() {
                    @Override
                    public void onResponse(Call<PlaceDetail> call, Response<PlaceDetail> response) {
                        mPlace=response.body();
                        place_address.setText(mPlace.getResult().getFormatted_address());
                    }

                    @Override
                    public void onFailure(Call<PlaceDetail> call, Throwable t) {

                    }
                });
    }

    private String getPlaceDetailUrl(String place_id) {
        StringBuilder url=new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        url.append("placeid="+place_id);
        url.append("&key="+"AIzaSyBqa-GmqP0BruW9ghJnfrG-WyNKsKS9UTM");
        return url.toString();
    }

    private String getPhotoOfPlace(String photos_reference,int maxWidth) {
        StringBuilder url=new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?");
        url.append("maxwidth="+maxWidth);
        url.append("&photoreference="+photos_reference);
        url.append("&key="+"AIzaSyBqa-GmqP0BruW9ghJnfrG-WyNKsKS9UTM");
        return url.toString();
    }
}

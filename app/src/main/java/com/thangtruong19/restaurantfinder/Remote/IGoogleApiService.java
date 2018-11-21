package com.thangtruong19.restaurantfinder.Remote;

import com.thangtruong19.restaurantfinder.Model.MyPlaces;
import com.thangtruong19.restaurantfinder.PlaceModel.PlaceDetail;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by User on 19/11/2018.
 */

public interface IGoogleApiService {
    @GET
    Call<MyPlaces> getNearByPlaces(@Url String url);

    @GET
    Call<PlaceDetail> getDetailPlace(@Url String url);
}

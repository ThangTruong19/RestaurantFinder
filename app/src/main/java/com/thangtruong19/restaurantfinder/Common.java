package com.thangtruong19.restaurantfinder;

import com.thangtruong19.restaurantfinder.Model.Results;
import com.thangtruong19.restaurantfinder.Remote.IGoogleApiService;
import com.thangtruong19.restaurantfinder.Remote.RetrofitClient;

/**
 * Created by User on 19/11/2018.
 */

public class Common {

    public static Results currentResult;

    private static final String GOOGLE_API_CLIENT="https://maps.googleapis.com/";

    public static IGoogleApiService getGoogleAPIService(){
        return RetrofitClient.getClient(GOOGLE_API_CLIENT).create(IGoogleApiService.class);
    }
}

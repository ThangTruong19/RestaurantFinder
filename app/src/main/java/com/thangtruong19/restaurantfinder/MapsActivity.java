package com.thangtruong19.restaurantfinder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.thangtruong19.restaurantfinder.Model.MyPlaces;
import com.thangtruong19.restaurantfinder.Model.Results;
import com.thangtruong19.restaurantfinder.Remote.IGoogleApiService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
        ,GoogleApiClient.ConnectionCallbacks
        ,GoogleApiClient.OnConnectionFailedListener
        ,LocationListener {

    private static final int MY_REQUEST_CODE = 1000 ;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    //private double latitude,longtitude;
    private Location mLastLocation;
    private Marker mMarker;
    private LocationRequest mLocationRequest;
    private MyPlaces currentPlace;
    private AutoCompleteTextView searchText;
    private ImageView mGps;
    private PlaceAutoCompleteAdapter placeAutoCompleteAdapter;
    private static final LatLngBounds LAT_LNG_BOUNDS=new LatLngBounds(new LatLng(-40,-168),new LatLng(71,136));
    protected GeoDataClient mGeoDataClient;
    private static final String TAG="MapActivity";

    IGoogleApiService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M){
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mService= Common.getGoogleAPIService();

        buildGoogleApiClient();

        searchText=findViewById(R.id.input_search);
        searchText.setOnItemClickListener(mAutoCompleteClickListener);

        mGps=findViewById(R.id.ic_gps);
        mGeoDataClient= Places.getGeoDataClient(this,null);
        initSearchText();
    }

    private void initSearchText(){
        Log.d(TAG,"InitSearchText: init");

        placeAutoCompleteAdapter=new PlaceAutoCompleteAdapter(MapsActivity.this,mGeoDataClient,LAT_LNG_BOUNDS,null);
        searchText.setAdapter(placeAutoCompleteAdapter);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH
                        ||actionId==EditorInfo.IME_ACTION_DONE
                        ||event.getAction()==KeyEvent.ACTION_DOWN
                        ||event.getAction()==KeyEvent.KEYCODE_ENTER){
                    geoLocate();
                }
                return false;
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick: click gps icon");
                mMap.clear();
                mLocationRequest= new LocationRequest();

                mLocationRequest.setInterval(1000);
                mLocationRequest.setFastestInterval(1000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

                if(ContextCompat.checkSelfPermission(MapsActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MapsActivity.this);
                }
            }
        });

    }
    private void geoLocate(){
        Log.d(TAG,"geoLocate: geolocate");

        String searchString=searchText.getText().toString();

        Geocoder geocoder=new Geocoder(MapsActivity.this);
        List<Address> list =new ArrayList<>();
        try{
            list=geocoder.getFromLocationName(searchString,1);
        }catch(IOException e){
            e.printStackTrace();
        }

        if(list.size()>0){
            Address address=list.get(0);
            Log.d(TAG,"found a location: "+address.toString());

            LatLng latLng=new LatLng(address.getLatitude(),address.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,20));

            mMap.clear();
            nearByPlace(address.getLatitude(),address.getLongitude());

            MarkerOptions options=new MarkerOptions()
                    .position(latLng)
                    .title(address.getAddressLine(0));

            mMap.addMarker(options);

        }
    }

    private AdapterView.OnItemClickListener mAutoCompleteClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final AutocompletePrediction item=placeAutoCompleteAdapter.getItem(position);
            final String placeId=item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult=Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient,placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback=new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.d(TAG,"onResult: Place query did not complete successfully"+places.getStatus().toString());
                places.release();
                return;
            }
            final Place place=places.get(0);

            moveCamera(place.getLatLng(),20,place);
            places.release();

        }
    };
    private void moveCamera(LatLng latLng,int zoom,Place place){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        mMap.clear();
        nearByPlace(latLng.latitude,latLng.longitude);

        if(place!=null){
            try{

                MarkerOptions options=new MarkerOptions()
                        .position(latLng)
                        .title(place.getName().toString());

                mMarker=mMap.addMarker(options);
            }catch (NullPointerException e){
                Log.e(TAG,"moveCamera: NullPointerException: "+e.getMessage());
            }
        }else {
            mMap.addMarker(new MarkerOptions().position(latLng));
        }
    }

    private void nearByPlace(double latitude,double longtitude) {
        //mMap.clear();
        String url=getUrl(latitude,longtitude);

        mService.getNearByPlaces(url)
                .enqueue(new Callback<MyPlaces>() {
                    @Override
                    public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {

                        currentPlace=response.body();

                        if(response.isSuccessful()){
                            for(int i=0;i<response.body().getResults().length;i++){
                                MarkerOptions markerOptions=new MarkerOptions();
                                Results googlePlace=response.body().getResults()[i];

                                double lat=Double.parseDouble(googlePlace.getGeometry().getLocation().getLat());
                                double lng=Double.parseDouble(googlePlace.getGeometry().getLocation().getLng());
                                String placeName=googlePlace.getName();
                                String vicinity=googlePlace.getVicinity();
                                LatLng latLng=new LatLng(lat,lng);

                                markerOptions.position(latLng);
                                markerOptions.title(placeName);

                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant));

                                markerOptions.snippet(String.valueOf(i));//Assign index for marker
                                clickMarker();
                                mMap.addMarker(markerOptions);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyPlaces> call, Throwable t) {

                    }
                });
    }

    private String getUrl(double latitude, double longtitude) {
        StringBuilder googlePlacesUrl=new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location="+latitude+","+longtitude);
        googlePlacesUrl.append("&radius="+10000);
        googlePlacesUrl.append("&type="+"cafe");
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key="+"AIzaSyBqa-GmqP0BruW9ghJnfrG-WyNKsKS9UTM");
        Log.d("MapActivity","getUrl: "+googlePlacesUrl.toString());
        return googlePlacesUrl.toString();
    }

    private boolean checkLocationPermission() {
        Log.d("MapActivity","checkLocationPermission is called");

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_REQUEST_CODE);
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("MapActivity","onRequestPermissionsResult is called");

        switch (requestCode){
            case MY_REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        if(mGoogleApiClient==null){
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }else{
                    Toast.makeText(this,"Permission Denied!",Toast.LENGTH_LONG).show();
                }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest= new LocationRequest();

        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        mLastLocation=location;

        if(mMarker!=null){
            mMarker.remove();
        }

        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());

        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        mMarker=mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        nearByPlace(location.getLatitude(),location.getLongitude());

        if(mGoogleApiClient!=null){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        }
    }

    private void clickMarker(){
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Common.currentResult=currentPlace.getResults()[Integer.parseInt(marker.getSnippet())];
                Intent intent=new Intent(MapsActivity.this,ViewPlace.class);
                startActivity(intent);
                return false;
            }
        });
    }
}

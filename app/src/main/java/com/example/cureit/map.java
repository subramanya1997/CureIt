package com.example.cureit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class map extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;

    private Location location;
    private LocationCallback locationCallback;

    Double latitude,longitude;

    private MaterialSearchBar materialSearchBar;
    private View mapView;

    private final float DEFAULT_ZOOM = 15;
    private final int RADIUS_IN_METER = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_map );

        materialSearchBar = (MaterialSearchBar) findViewById( R.id.placeSearchBar );

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.maps );

        supportMapFragment.getMapAsync( this );

        mapView = supportMapFragment.getView();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( map.this );
        Places.initialize( map.this, "AIzaSyCFvgFMc-HzeLOryxfbinrhFyzYy--N_tI" );
        placesClient = Places.createClient( this );
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();


        materialSearchBar.setOnSearchActionListener( new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch( text.toString(), true, null, true );
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        } );

        materialSearchBar.addTextChangeListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                        .setCountry( "in" )
                        .setTypeFilter( TypeFilter.ADDRESS )
                        .setSessionToken( token )
                        .setQuery( s.toString() )
                        .build();

                placesClient.findAutocompletePredictions( predictionsRequest ).addOnCompleteListener( new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                        if (task.isSuccessful()){
                            FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                            if(predictionsResponse != null ){
                                predictionList = predictionsResponse.getAutocompletePredictions();
                                List<String> suggestionList = new ArrayList<>(  );
                                for (int i =0 ; i<predictionList.size();i++){
                                    AutocompletePrediction prediction = predictionList.get( i );
                                    suggestionList.add( prediction.getFullText( null ).toString() );
                                }
                                materialSearchBar.updateLastSuggestions( suggestionList );
                                if(!materialSearchBar.isSuggestionsVisible()){
                                    materialSearchBar.showSuggestionsList();
                                }
                            }

                        }
                    }
                } );

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );

        materialSearchBar.setSuggstionsClickListener( new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                if(position >= predictionList.size()){
                    return;
                }
                AutocompletePrediction prediction = predictionList.get( position );
                String suggestion = materialSearchBar.getLastSuggestions().get( position ).toString();
                materialSearchBar.setText( suggestion );

                new Handler(  ).postDelayed( new Runnable() {
                    @Override
                    public void run() {
                        materialSearchBar.clearSuggestions();
                    }
                } ,1000);

                InputMethodManager imm = (InputMethodManager) getSystemService( INPUT_METHOD_SERVICE );
                if( imm!= null){
                    imm.hideSoftInputFromWindow( materialSearchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY );
                }
                String placeID = prediction.getPlaceId();
                List<Place.Field> placeField = Arrays.asList( Place.Field.LAT_LNG );

                final FetchPlaceRequest  fetchPlaceRequest = FetchPlaceRequest.builder(placeID,placeField).build();
                placesClient.fetchPlace( fetchPlaceRequest ).addOnSuccessListener( new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place = fetchPlaceResponse.getPlace();

                        LatLng latLng = place.getLatLng();
                        if (latLng != null){
                            mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( latLng,DEFAULT_ZOOM ) );

                            latitude = place.getLatLng().latitude;
                            longitude = place.getLatLng().longitude;

                            String hospital = "hospital";
                            String url = getUrl(latitude,longitude, hospital);
                            Object dataTransfer[] = new Object[2];
                            dataTransfer[0] = mMap;
                            dataTransfer[1] = url;

                            GetNearByPlacesData getNearByPlacesData = new GetNearByPlacesData();
                            getNearByPlacesData.execute( dataTransfer );
                        }
                    }
                } );
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        } );


    }

    private String getUrl(double latitude, double longitude, String place ){
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append( "location="+latitude+","+longitude );
        googlePlaceUrl.append( "&radius="+RADIUS_IN_METER );
        googlePlaceUrl.append( "&type="+place );
        googlePlaceUrl.append( "&sensor=true" );
        googlePlaceUrl.append( "&key="+"AIzaSyCFvgFMc-HzeLOryxfbinrhFyzYy--N_tI" );

        return googlePlaceUrl.toString();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled( true );
        mMap.getUiSettings().setMyLocationButtonEnabled( true );

        if(mapView != null && mapView.findViewById( Integer.parseInt( "1" )) != null ){
            View locationButton = ((View) mapView.findViewById( Integer.parseInt( "1" ) ).getParent())
                    .findViewById( Integer.parseInt( "2" ) );

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule( RelativeLayout.ALIGN_PARENT_TOP , 0);
            layoutParams.addRule( RelativeLayout.ALIGN_PARENT_BOTTOM , RelativeLayout.TRUE);
            layoutParams.setMargins( 0,0,40,180 );

        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval( 10000 );
        locationRequest.setFastestInterval( 5000 );
        locationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest( locationRequest );

        SettingsClient settingsClient = LocationServices.getSettingsClient( map.this );
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings( builder.build() );

        task.addOnSuccessListener( map.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();

            }
        } );

        task.addOnFailureListener( map.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof ResolvableApiException){
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                    try {
                        resolvableApiException.startResolutionForResult( map.this , 51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        } );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (resultCode == 51){
            if (resultCode == RESULT_OK){
                getDeviceLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener( new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if( task.isSuccessful()){
                            location = task.getResult();


                            if (location!=null){

                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( new LatLng( location.getLatitude(),location.getLongitude() ), DEFAULT_ZOOM ) );

                                String hospital = "hospital";
                                String url = getUrl(latitude,longitude, hospital);
                                Object dataTransfer[] = new Object[2];
                                dataTransfer[0] = mMap;
                                dataTransfer[1] = url;

                                GetNearByPlacesData getNearByPlacesData = new GetNearByPlacesData();
                                getNearByPlacesData.execute( dataTransfer );

                            }else {

                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval( 10000 );
                                locationRequest.setFastestInterval( 5000 );
                                locationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );

                                locationCallback = new LocationCallback(){
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult( locationResult );

                                        if(locationResult == null){
                                            return;
                                        }
                                        location  = locationResult.getLastLocation();
                                        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( new LatLng( location.getLatitude(),location.getLongitude() ), DEFAULT_ZOOM ) );
                                        mFusedLocationProviderClient.removeLocationUpdates( locationCallback );
                                        latitude = location.getLatitude();
                                        longitude = location.getLongitude();

                                        String hospital = "hospital";
                                        String url = getUrl(latitude,longitude, hospital);
                                        Object dataTransfer[] = new Object[2];
                                        dataTransfer[0] = mMap;
                                        dataTransfer[1] = url;

                                        GetNearByPlacesData getNearByPlacesData = new GetNearByPlacesData();
                                        getNearByPlacesData.execute( dataTransfer );
                                    }
                                };
                                mFusedLocationProviderClient.requestLocationUpdates( locationRequest, locationCallback, null);

                            }
                        }
                    }
                } );
    }
}

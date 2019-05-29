package com.example.cureit;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetNearByPlacesData extends AsyncTask<Object,String,String> {

    String googlePlacesData;
    GoogleMap mMap;
    String url;

    @Override
    protected String doInBackground(Object... objects) {

        mMap = (GoogleMap) objects[0];
        url = (String)objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlacesData = downloadUrl.readUrl( url );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String,String>> nearByPlaceList = null;
        DataParser parser = new DataParser();
        nearByPlaceList = parser.parse( s );
        showNearByplaces( nearByPlaceList );
    }

    private void showNearByplaces(List<HashMap<String, String>> nearByPlaces){
        for(int i = 0; i < nearByPlaces.size(); i++){
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearByPlaces.get( i );

            String placeName = googlePlace.get( "place_name" );
            String vicinity = googlePlace.get( "vicinity" );
            Double lat = Double.parseDouble( googlePlace.get( "lat" ));
            Double lng = Double.parseDouble( googlePlace.get( "lng" ));

            LatLng latLng = new LatLng( lat,lng );
            markerOptions.position( latLng );
            markerOptions.title( placeName );
            markerOptions.icon( BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN) );

            mMap.addMarker( markerOptions );

        }
    }
}

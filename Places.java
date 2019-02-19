package com.example.locationmap;

import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/*
    getting the results on map
 */
public class Places extends AsyncTask<Object,String,String> {
    private String placesData;
    private GoogleMap mMap;
    private String url;
    private MapsActivity mainActivity;

    public void setMainActivity(MapsActivity activity){
        mainActivity = activity;
    }

    @Override
    protected String doInBackground(Object... objects) {
        /* retourner le resultats des parkings */
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        UrlReader urlR = new UrlReader();
        try{
            placesData = urlR.read(url);
        }catch(IOException e){
            e.printStackTrace();
        }
        return placesData;
    }


    @Override
    protected void onPostExecute(String s){
        List<HashMap<String,String>> parkingList = null;
        /*retourne la liste des parkings trouvés*/
        DataParser parser = new DataParser();
        parkingList = parser.getListParking(s);
        if( parkingList != null)
            showParkings(parkingList);
        else {
            Toast.makeText(mainActivity , "Parkings not found!",Toast.LENGTH_SHORT).show();
        }
    }

    /*afficher les parkings sur map*/
    private void showParkings(List<HashMap<String, String>> parkings){
        for(int i =0; i<parkings.size(); i++){
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String,String> gooleParking = parkings.get(i);
            String parkingName = gooleParking.get("paking_Name");
            String vicinity = gooleParking.get("vicinity");
            String ref = gooleParking.get("reference");
            Double lat = Double.parseDouble(gooleParking.get("lat"));
            Double lng = Double.parseDouble(gooleParking.get("lng"));
            LatLng latLng = new LatLng(lat,lng);
            markerOptions.title(parkingName+":"+vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

        }
    }




}

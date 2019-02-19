package com.example.locationmap;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, LocationSource {
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationSource.OnLocationChangedListener locationChangedListener;
    private Button findButton;
    private EditText search;
    private double lat = 0,lng = 0;
    private  static final int RADIUS = 50000;
    private static final String KEY ="AIzaSyAH3963QemJitncPPnUDG42JUc0Yq6tXPo";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        findButton = (Button) findViewById(R.id.btnFind);
        search = (EditText) findViewById(R.id.placeText);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        if (locationManager != null) {
            boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (gpsIsEnabled) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION
                            , Manifest.permission.ACCESS_COARSE_LOCATION};
                    ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10F, this);

            } else {
                buildAlertMessageNoGps();
            }
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String parkingName =  search.getText().toString();
                if (parkingName.equals("")){
                    mMap.clear();
                    String url = getUrl(lat,lng);
                    Object dataTransfer [] = new Object[2];
                    dataTransfer[0] = mMap;
                    dataTransfer[1] = url;
                    Places places = new Places();
                    places.setMainActivity(MapsActivity.this);
                    places.execute(dataTransfer);
                    Toast.makeText(MapsActivity.this , "Showing parking ...",Toast.LENGTH_LONG).show();


                }else{
                    /*si l'user a saisie une adresse on l'affiche sur map si trouvée*/
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    List<Address> addressList=null;
                    try {
                        addressList = geocoder.getFromLocationName(parkingName,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (addressList.size() > 0){
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        mMap.addMarker(markerOptions);

                    }else {
                        Toast.makeText(MapsActivity.this, "Not found!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }

    private String getUrl(double lat,double lng){
        StringBuilder url = new StringBuilder();
        url.append("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        url.append("location=" + lat + "," +lng +"&radius="+ RADIUS);
        url.append( "&type=parking&key="+KEY);
        return  url.toString();
    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (locationChangedListener != null) {
            locationChangedListener.onLocationChanged(location);
            lat = location.getLatitude();
            lng = location.getLongitude();

            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "provider disabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        locationChangedListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        locationChangedListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        Boolean isAnswered = false;

        while(!isAnswered){

            if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            else{
                isAnswered = true;

            }
        }
        mMap.setMyLocationEnabled(true);
        mMap.setLocationSource(this);
    }
}
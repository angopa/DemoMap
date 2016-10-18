package demos.android.com.craneo.mymaps;


import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final float ZOOM_VALUE = 15;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 101;
    GoogleMap mMap;
    private static final int ERROR_DIALOG_REQUEST = 901;
    private static final double
            SEATTLE_LAT = 47.60621,
            SEATTLE_LNG = -122.33207,
            SYDNEY_LAT = -33.867487,
            SYDNEY_LNG = 150.20699,
            NEWYORK_LAT = 40.714354,
            NEWYORK_LNG = -74.005973;

    private GoogleApiClient mLocationClient;
    private LocationListener mLocationListener;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (servicesOk()) {
            setContentView(R.layout.activity_map);
            initMap();
        } else {
            setContentView(R.layout.activity_main);
            Toast.makeText(this, "Map not connected!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.mapTypeNone:
                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.mapTypeNormal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeSatellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeHybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.mapTypeTerrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void initMap() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    public boolean servicesOk() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int isAvailable = googleAPI.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleAPI.isUserResolvableError(isAvailable)) {
            googleAPI.getErrorDialog(this, isAvailable, ERROR_DIALOG_REQUEST).show();
        } else {
            Toast.makeText(this, "Can't connect to mapping service", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        gotoLocation(SEATTLE_LAT, SEATTLE_LNG, ZOOM_VALUE);

        mLocationClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mLocationClient.connect();
    }

    private void gotoLocation(double latitud, double longitud, float zoom) {
        LatLng latLng = new LatLng(latitud, longitud);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.moveCamera(update);
    }

    private void hideSoftKeyboard(View v) {
        InputMethodManager imm =
                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void geoLocate(View view) throws IOException {
        hideSoftKeyboard(view);
        TextView tv = (TextView) findViewById(R.id.editText1);
        String searchString = tv.getText().toString();
        Geocoder geocoder = new Geocoder(this);
        List<Address> list = geocoder.getFromLocationName(searchString, 1);

        if (list.size() > 0) {
            Address add = list.get(0);
            String locality = add.getLocality();
            Toast.makeText(this, "Found: " + locality, Toast.LENGTH_SHORT).show();

            double lat = add.getLatitude();
            double lng = add.getLongitude();

            gotoLocation(lat, lng, ZOOM_VALUE);

            if(marker != null){
                marker.remove();
            }
            MarkerOptions options = new MarkerOptions()
                    .title(locality)
                    .position(new LatLng(lat, lng))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker));
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));


            marker = mMap.addMarker(options);

        }
    }

    public void showCurrentLocation(MenuItem item) {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
        }
        Location currentLocation = LocationServices.FusedLocationApi
                .getLastLocation(mLocationClient);

        if (currentLocation == null){
            Toast.makeText(this, "Coudn't connect!", Toast.LENGTH_SHORT).show();
        }else{
            LatLng latLng = new LatLng(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_VALUE);
            mMap.animateCamera(update);

        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this, "Ready to map!", Toast.LENGTH_SHORT).show();

//        mLocationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
////                Toast.makeText(MainActivity.this,
////                        "Location Changed: "+location.getLongitude()+", "+location.getLatitude()
////                        , Toast.LENGTH_SHORT).show();
//                gotoLocation(location.getLongitude(), location.getLatitude(), ZOOM_VALUE);
//            }
//        };
//        LocationRequest request = LocationRequest.create();
//        request.setPriority(LocationRequest.PRIORITY_LOW_POWER);
//        request.setInterval(10000);
//        request.setFastestInterval(1000);
//        if (ContextCompat.checkSelfPermission(this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions();
//        }
//        LocationServices.FusedLocationApi.requestLocationUpdates(
//                mLocationClient,request, mLocationListener);
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_FINE_LOCATION);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission fine location granteed!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission fine location denied!", Toast.LENGTH_SHORT).show();
                }
                return;
            }


        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        LocationServices.FusedLocationApi.removeLocationUpdates(mLocationClient, mLocationListener);
    }
}

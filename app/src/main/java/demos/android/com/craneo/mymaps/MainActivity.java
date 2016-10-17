package demos.android.com.craneo.mymaps;

import android.graphics.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    private static final float ZOOM_VALUE = 15;
    GoogleMap mMap;
    private static final int ERROR_DIALOG_REQUEST = 901;
    private static final double
                        SEATTLE_LAT = 47.60621,
                        SEATTLE_LNG = -122.33207,
                        SYDNEY_LAT = -33.867487,
                        SYDNEY_LNG = 150.20699,
                        NEWYORK_LAT = 40.714354,
                        NEWYORK_LNG = -74.005973;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(servicesOk()){
            setContentView(R.layout.activity_map);
            initMap();
            Toast.makeText(this, "Ready to map!", Toast.LENGTH_SHORT).show();
        }else{
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initMap() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    public boolean servicesOk(){
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int isAvailable = googleAPI.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS){
            return true;
        }else if (googleAPI.isUserResolvableError(isAvailable)){
            googleAPI.getErrorDialog(this, isAvailable,  ERROR_DIALOG_REQUEST).show();
        }else{
            Toast.makeText(this, "Can't connect to mapping service", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        gotoLocation(SEATTLE_LAT, SEATTLE_LNG, ZOOM_VALUE) ;
    }

    private void gotoLocation(double latitud, double longitud, float zoom){
        LatLng latLng = new LatLng(latitud, longitud);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.moveCamera(update);
    }
}

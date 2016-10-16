package demos.android.com.craneo.mymaps;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;

public class MainActivity extends AppCompatActivity {

    GoogleMap mMap;
    private static final int ERROR_DIALOG_REQUEST = 901;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(servicesOk()){
            Toast.makeText(this, "Ready to map!", Toast.LENGTH_SHORT).show();
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
}

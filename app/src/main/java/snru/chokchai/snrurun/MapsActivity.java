package snru.chokchai.snrurun;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.util.Log;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
     // Explicit
    private GoogleMap mMap;
    private double snruLatADouble=17.191968,
            snruLngADouble = 104.092300;
    private LocationManager locationManager;
    private Criteria criteria;
    private double myLatADouble,myLagADouble;
    private boolean gpsABoolean, networkABoolean;
    private String[] userString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_design);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Setup Location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);

        userString = getIntent().getStringArrayExtra("User");

    }// Main method

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.removeUpdates((android.location.LocationListener) locationListener);
        myLatADouble = snruLatADouble;
        myLagADouble = snruLngADouble;

        Location networkLocation = myFindLocation(LocationManager.NETWORK_PROVIDER, "ไม่ได้ต่อเน็ต");
        if (networkLocation != null) {
            myLatADouble = networkLocation.getLatitude();
            myLagADouble = networkLocation.getLongitude();

        }
        Location gpsLocation = myFindLocation(LocationManager.GPS_PROVIDER, "ไม่มี GPS");
        if (gpsLocation != null) {
            myLatADouble = gpsLocation.getLatitude();
            myLagADouble = gpsLocation.getLongitude();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        locationManager.removeUpdates( locationListener);
    }

    public Location myFindLocation(String strProvider, String strError) {

        Location location = null;
        if (locationManager.isProviderEnabled(strProvider)) {

            locationManager.requestLocationUpdates(strProvider, 1000, 10, locationListener);
            location = locationManager.getLastKnownLocation(strProvider);

        } else {
            Log.d("test", "my Error==" + strError);
        }

        return location;
    }

    // Create Inner class
    public android.location.LocationListener locationListener = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            myLatADouble = location.getLatitude();
            myLagADouble = location.getLongitude();

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

       // Setup for สกล
        LatLng snruLatLng = new LatLng(snruLatADouble, snruLngADouble);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(snruLatLng,15));

        // My Loop
        myLoop();

    } // On maps

    private void myLoop() {
        Log.d("18May16", "mylat=" + myLatADouble);
        Log.d("18May16", "mylng=" + myLagADouble);

         updateLocation();


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myLoop();

            }
        },3000);

    } // My loop

    private void updateLocation() {

        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormEncodingBuilder()
                .add("isAdd", "true")
                .add("id", userString[0])
                .add("Lat", Double.toString(myLatADouble))
                .add("Lng", Double.toString(myLagADouble))
                .build();

        Request.Builder builder = new Request.Builder();
        Request request = builder.url("http://swiftcodingthai.com/snru/edit_location.php").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {

            }
        });

    } // update location
}// Main Class

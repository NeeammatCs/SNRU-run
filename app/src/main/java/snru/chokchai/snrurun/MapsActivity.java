package snru.chokchai.snrurun;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

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
    private double[] buildLatDoubles = {17.1939512, 17.19157333, 17.18640751, 17.18970791};
    private double[] buildLngDoubles = {104.0908885, 104.09533024, 104.09294844, 104.08822775};
    private int[] buildInts = {R.drawable.build1, R.drawable.build2, R.drawable.build3, R.drawable.build4};


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
    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1.609344 * 1000;


        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    // Inner class
    public class SynLocation extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {

            try {

                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url("http://swiftcodingthai.com/snru/get_user.php").build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();


            } catch (Exception e) {
                return null;
            }

            //return null;
        }// doInBack

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            mMap.clear();

            for (int i=0;i<buildLatDoubles.length;i++) {
                LatLng latLng = new LatLng(buildLatDoubles[i], buildLngDoubles[i]);
                mMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(buildInts[i])));
            }

            try {

                JSONArray jsonArray = new JSONArray(s);

                String[] nameStrings = new String[jsonArray.length()];
                String[] latString = new String[jsonArray.length()];
                String[] lngString = new String[jsonArray.length()];
                String[] avataString = new String[jsonArray.length()];

                for (int i =0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    nameStrings[i] = jsonObject.getString("Name");
                    latString[i] = jsonObject.getString("Lat");
                    lngString[i] = jsonObject.getString("Lng");
                    avataString[i] = jsonObject.getString("Avata");

                    LatLng latLng = new LatLng(Double.parseDouble(latString[i]),
                            Double.parseDouble(lngString[i]));
                    mMap.addMarker(new MarkerOptions().position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(findIcon(avataString[i])))
                            .title(nameStrings[i]));



                } //for

            } catch (Exception e) {
                e.printStackTrace();
            }

        } //on post

        private int findIcon(String avataString) {
            int intIcon = R.drawable.doremon48;;
            switch (Integer.parseInt(avataString)) {
                case 0:
                    intIcon = R.drawable.doremon48;
                    break;
                case 1:
                    intIcon=R.drawable.bird48;
                    break;
                case 2:
                    intIcon=R.drawable.kon48;
                    break;
                case 3:
                    intIcon=R.drawable.nobita48;
                    break;
                case 4:
                    intIcon=R.drawable.rat48;
                    break;
            }
            return intIcon;
        }
    } // SynLocation




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

        createAllMarker();

         updateLocation();


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myLoop();

            }
        },3000);

    } // My loop

    private void createAllMarker() {
        SynLocation synLocation = new SynLocation();
        synLocation.execute();
    }// createMarker

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

        // check Distance
        double myDistance = distance(myLatADouble, myLagADouble,
                buildLatDoubles[0], buildLngDoubles[0]);
        Log.d("19May16", "myDistance==>" + myDistance);
        if (myDistance<20) {
            showAlert();
        }



    } // update location

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.doremon48);
        builder.setTitle("ด่านที่1");
        builder.setMessage("คุณึงด่านที่ 1 แล้ว");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent = new Intent(MapsActivity.this,Exercise.class);
                intent.putExtra("User", userString);
                startActivity(intent);

            }
        });
        builder.show();
    }
}// Main Class

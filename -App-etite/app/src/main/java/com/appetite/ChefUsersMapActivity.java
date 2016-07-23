package com.appetite;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ChefUsersMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    Firebase locRef;
    String chef_name;
    Double mLatitude, mLongitude;
    GoogleApiClient mGoogleApiClient;
    String userLocString;
    String cheflatlng;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_users_map);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .enableAutoManage(this, this)
                    .build();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Bundle bundle = getIntent().getExtras();
        chef_name = bundle.getString("chef_name");
    }

    public void sendRequest(){

        String stringUrl = "https://maps.googleapis.com/maps/api/directions/json?origin="+""+userLocString+"&destination="+""+cheflatlng+"&key=AIzaSyCZOwTuuqtZCpy0SS_Y_KERUnZtwXl_MuE";
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            new RequestHandler(this).execute(stringUrl);
        } else {
            // display error
            Log.d("no connection", "No network connnection available");
        }

    }

    public void requestCompletedCallBack(String result) {
        try {
            JSONObject jObject = new JSONObject(result);
            JSONObject route = jObject.getJSONArray("routes").getJSONObject(0);
            JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
            String line = overviewPolyline.getString("points");
            List<LatLng> decodedPath = PolyUtil.decode(line);

            mMap.addPolyline(new PolylineOptions().addAll(decodedPath)
                    .width(5)
                    .color(Color.BLUE));

            JSONObject bounds = route.getJSONObject("bounds");
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            //Adding the bounds from the JSON to the buider
            LatLng latLngNorthEast = new LatLng(bounds.getJSONObject("northeast").getDouble("lat"), bounds.getJSONObject("northeast").getDouble("lng"));
            builder.include(latLngNorthEast);
            LatLng latLngSouthWest = new LatLng(bounds.getJSONObject("southwest").getDouble("lat"), bounds.getJSONObject("southwest").getDouble("lng"));
            builder.include(latLngSouthWest);
            LatLngBounds bound = builder.build();

            //To move the camera to the bouds built
            int padding = 40;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bound, padding);
            mMap.moveCamera(cu);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();

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
    }
    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitude = mLastLocation.getLatitude();
            mLongitude = mLastLocation.getLongitude();
            LatLng newPos = new LatLng(mLatitude, mLongitude);
            mMap.addMarker(new MarkerOptions().position(newPos).title(Login.userName));

            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLatitude, mLongitude)));

            userLocString = String.valueOf(mLatitude) + "," + String.valueOf(mLongitude);

            locRef = new Firebase("https://app-etite.firebaseio.com/userInfo/chef");
            locRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String location = dataSnapshot.child(chef_name).child("address").getValue().toString();
                    String latitude = dataSnapshot.child(chef_name).child("latitude").getValue().toString();
                    String longitude = dataSnapshot.child(chef_name).child("longitude").getValue().toString();
                    LatLng chefPos = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    mMap.addMarker(new MarkerOptions().position(chefPos).title(chef_name));
                    cheflatlng = latitude + "," + longitude;
                    sendRequest();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){

    }

}
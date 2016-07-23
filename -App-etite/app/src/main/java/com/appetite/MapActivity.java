package com.appetite;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private double mLatitude;
    private double mLongitude;
    ArrayList<LatLng> pinLocationList;
    ArrayList<String> chefsList;
    Firebase mRef,picRef;
    String chefs_enrolled,chef_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Firebase.setAndroidContext(this);

        if(mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .enableAutoManage(this, this)
                    .build();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);
        mapFragment.getMapAsync(this);

        Bundle bundle = getIntent().getExtras();
        chef_menu = bundle.getString("menu_name");

        //Kavitha's part chefs' enrolled
        mRef = new Firebase("https://app-etite.firebaseio.com/chefsEnrolled/");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chefsList = new ArrayList<String>();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    if (postSnapshot.hasChild(chef_menu)) {
                        chefs_enrolled = postSnapshot.getKey();
                        chefsList.add(chefs_enrolled);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        picRef = new Firebase("https://app-etite.firebaseio.com/userInfo/chef");
        picRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator iterator = chefsList.iterator();
                while(iterator.hasNext()) {
                    String key=(String)iterator.next();
                    String chef = dataSnapshot.child(key).child("username").getValue().toString();
                    String address = dataSnapshot.child(key).child("address").getValue().toString();
                    String latitude = dataSnapshot.child(key).child("latitude").getValue().toString();
                    String longitude = dataSnapshot.child(key).child("longitude").getValue().toString();

                    LatLng newPos = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    mMap.addMarker(new MarkerOptions().position(newPos).title(chef));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(newPos));
                }

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
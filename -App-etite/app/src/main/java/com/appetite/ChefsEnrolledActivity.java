package com.appetite;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.util.Collections.sort;

public class ChefsEnrolledActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Firebase mRef, picRef;
    TextView itemName;
    TextView topLabel;
    Boolean displayList;
    String chef_menu,chefs_enrolled,chef_ingredients,quantity_avbl;
    List<ChefsEnrolledList> chefsEnrolledDetails;
    Map<String,ChefMenuDetails>  chefMenuDetailsMap = new HashMap<String, ChefMenuDetails>();
    ListView chefsEnrolledlistView;
    ChefsEnrolledListAdapter chefsEnrolledListAdapter;
    String key;
    Double mLatitude = 37.3492;
    Double mLongitude = -121.9381;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chefs_enrolled);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .enableAutoManage(this, this)
                    .build();
        }

        chefsEnrolledDetails = new ArrayList<ChefsEnrolledList>();
        Bundle bundle = getIntent().getExtras();
        chef_menu = bundle.getString("menuName");

        topLabel = (TextView)findViewById(R.id.chefsEnrolledLabel);
        topLabel.setText("Your chefs' for " + chef_menu + "!");

        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://app-etite.firebaseio.com/chefsEnrolled/");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                chefMenuDetailsMap.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    if (postSnapshot.hasChild(chef_menu)) {
                        chefs_enrolled = postSnapshot.getKey();
                            if(chefs_enrolled!=null && Integer.parseInt(dataSnapshot.child(chefs_enrolled).child(chef_menu).child("quantity").getValue().toString())>0){

                                chef_ingredients = dataSnapshot.child(chefs_enrolled).child(chef_menu).child("ingredients").getValue().toString();
                                quantity_avbl = dataSnapshot.child(chefs_enrolled).child(chef_menu).child("quantity").getValue().toString();
                                String food_image;
                                if(dataSnapshot.child(chefs_enrolled).child(chef_menu).child("foodImg").getValue() != null) {
                                    food_image = dataSnapshot.child(chefs_enrolled).child(chef_menu).child("foodImg").getValue().toString();
                                } else {
                                    food_image = "";
                                }

                                ChefMenuDetails chefMenuDetails = new ChefMenuDetails(chef_ingredients, quantity_avbl, food_image);
                                chefMenuDetailsMap.put(chefs_enrolled, chefMenuDetails);
                        }
                    }
                }

                chefsList();

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    public void chefsList(){
        picRef = new Firebase("https://app-etite.firebaseio.com/userInfo/chef");
        picRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                chefsEnrolledDetails.clear();

                Iterator myVeryOwnIterator = chefMenuDetailsMap.keySet().iterator();
                while(myVeryOwnIterator.hasNext()) {
                    key=(String)myVeryOwnIterator.next();

                    String chefpicUrl = dataSnapshot.child(key).child("pic").getValue().toString();
                    String latitude = dataSnapshot.child(key).child("latitude").getValue().toString();
                    String longitude = dataSnapshot.child(key).child("longitude").getValue().toString();
                    ChefMenuDetails value=chefMenuDetailsMap.get(key);

                    chefsEnrolledDetails.add(new ChefsEnrolledList(key, chefpicUrl, value.getIngredients(), value.getQuantity(), value.getFoodImg(), latitude, longitude));

                    chefsEnrolledList();
                }

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
    public void chefsEnrolledList(){

        chefsEnrolledlistView = (ListView) findViewById(R.id.chefsEnrolledlistView);

        chefsEnrolledListAdapter = new ChefsEnrolledListAdapter(this, R.layout.activity_chefs_enrolled_list_adapter, chefsEnrolledDetails, mLatitude, mLongitude);
        sort(chefsEnrolledDetails, new ChefsEnrolledList.DistanceComparator(mLatitude, mLongitude));
        chefsEnrolledlistView.setAdapter(chefsEnrolledListAdapter);

        chefsEnrolledlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChefsEnrolledList chefsEnrolledList = chefsEnrolledDetails.get(position);

                Intent chefDishInfo = new Intent(ChefsEnrolledActivity.this, DishDetailsActivity.class);
                chefDishInfo.putExtra("chefName", chefsEnrolledList.getChefName());
                chefDishInfo.putExtra("chefDish", chef_menu);
                chefDishInfo.putExtra("chefIngredients", chefsEnrolledList.getIngredients());
                chefDishInfo.putExtra("chefQuantity", chefsEnrolledList.getQuantity());
                chefDishInfo.putExtra("foodImage", chefsEnrolledList.getFoodImage());
                startActivity(chefDishInfo);
            }
        });


    }
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_map) {
            Intent chefsLocator = new Intent(ChefsEnrolledActivity.this,MapActivity.class);
            chefsLocator.putExtra("menu_name",chef_menu);
            startActivity(chefsLocator);
        }

        return super.onOptionsItemSelected(item);
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
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}

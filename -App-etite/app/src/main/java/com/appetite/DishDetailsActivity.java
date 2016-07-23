package com.appetite;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.HashMap;
import java.util.Map;

public class DishDetailsActivity extends AppCompatActivity {

    ImageView chefDishPic;
    TextView chefIngredients,chefQuantity;
    EditText quantityInput;
    static String quantityOrdered,chefName,chefDishName,chefDishIngredients,chefDishQuantity, chefFoodImage;
    Button orderButton,reviewBtn;
    Firebase mRef;
    public static int PRICE = 5;
    int remainingQuantity = 0;
    public GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_details);
        Firebase.setAndroidContext(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Dish Info"));
        tabLayout.addTab(tabLayout.newTab().setText("Reviews"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Bundle nameBundle = getIntent().getExtras();
        chefName = nameBundle.getString("chefName");

        Bundle dishNameBundle = getIntent().getExtras();
        chefDishName = dishNameBundle.getString("chefDish");

        Bundle bundle = getIntent().getExtras();
        chefDishIngredients = bundle.getString("chefIngredients");

        Bundle bdle = getIntent().getExtras();
        chefDishQuantity = bdle.getString("chefQuantity");

        chefFoodImage = bundle.getString("foodImage");

    }

    //alert dialog box
    public void showAlertDialog(DishDetailsActivity dishDetailsActivity) {
        int quantity = Integer.parseInt(quantityOrdered);
        int totalPrice = PRICE * quantity;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(dishDetailsActivity);
        alertDialog.setTitle("Order Confirmation");
        alertDialog.setMessage("Price of your order is $" + totalPrice + ".");
        //positive response
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SmsManager smsMngr = SmsManager.getDefault();
                smsMngr.sendTextMessage("4089212442", null, "You have received and order from " + Login.userName + " for" + quantityInput + " " + chefDishName + ".", null, null);
                Toast.makeText(getApplicationContext(), "Order placed!", Toast.LENGTH_LONG).show();
                chefQuantity.setText("");

            }
        });
        //negative response
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.create().show();



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
            Intent chefUserMap = new Intent(DishDetailsActivity.this,ChefUsersMapActivity.class);
            chefUserMap.putExtra("chef_name",chefName);
            startActivity(chefUserMap);
        }
        else if (id==R.id.action_logOut){
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Intent returnToLogin = new Intent(getApplicationContext(), MainActivity.class);
                            returnToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(returnToLogin);
                            finish();
                            MainActivity.cookModule = false;
                            MainActivity.eatModule = false;


                        }
                    });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }
}

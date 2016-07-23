package com.appetite;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kavi on 2/13/16.
 */
public class MenuList extends AppCompatActivity {

    TextView welcometxt;
    TextView item_name, item_price;
    Firebase mRef,menuRef,mRef1,mRef2;
    ImageView image;
    String user,status;
    ListView menulistView;
    List<Menu> menuList;
    ImageButton searchBtn;
    String searchDish;
    EditText searchWord;
    MenulistAdapter menuListAdapter;
    String chef,dish,quan,cust;
    static boolean orderPage=false;
    Login signOut;
    public GoogleApiClient mGoogleApiClient;
    String itemName,itemPrice,itemImage,itemCuisine;
    int count=0;
    int counts=0;
    Menu menuItems;
    int dataReading = 0;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.menulist_layout);
        Firebase.setAndroidContext(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        searchBtn =(ImageButton) (findViewById(R.id.searchB));
        searchWord = (EditText)(findViewById(R.id.searchW));
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyWord = searchWord.getText().toString();
                searchContent(keyWord);
                Log.d("QWERTY", keyWord);
            }
        });


        mRef = new Firebase("https://app-etite.firebaseio.com/menulist");
        menuList = new ArrayList<Menu>();
        mRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                menuList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {


                    if (postSnapshot.child("item_name").getValue() != null && postSnapshot.child("imageEncoded") != null && postSnapshot.child("cuisine") != null) {
                        itemName = postSnapshot.child("item_name").getValue().toString();
                        itemImage = postSnapshot.child("imageEncoded").getValue().toString();
                        itemCuisine = postSnapshot.child("cuisine").getValue().toString();
                        menuList.add(new Menu(itemName, itemImage, itemCuisine));
                    }

                }
                menuList();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("The read failed: ", firebaseError.getMessage());

            }
        });
        mRef1 = new Firebase("https://app-etite.firebaseio.com/orders");
        mRef1.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                chef = dataSnapshot.child("chef").getValue().toString();
                cust = dataSnapshot.child("customer").getValue().toString();
                dish = dataSnapshot.child("dish").getValue().toString();
                quan = dataSnapshot.child("quantity").getValue().toString();
                count++;
                Log.d("COUNT++:",Login.userName+",count = "+count);
                if (Login.userName.equals(chef)&count>1 ) {
                    notifyChef();
                    orderPage=true;
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("The read failed: ", firebaseError.getMessage());

            }
        });
        mRef2 = new Firebase("https://app-etite.firebaseio.com/notifyUsers/");
        mRef2.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                user = dataSnapshot.child("user").getValue().toString();
                status = dataSnapshot.child("orderStatus").getValue().toString();
                counts++;
                Log.d("COUNTS++:", Login.userName + ",count = " + counts);
                if (Login.userName.equals(user) & counts > 1) {
                    notifyUser();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }


        });
    }
    public void notifyChef(){
        int requestCode = 0;
        int flags = 0;
        Intent i = new Intent(this,ChefMenuInfo.class);

        i.putExtra("menuname",dish);
        i.putExtra("user", Login.userName);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, requestCode, i, PendingIntent.FLAG_UPDATE_CURRENT);
        int id = 12345;
        Notification notification = new Notification.Builder(this)
                .setContentTitle("Order Received")
                .setContentText("You have a new order")
                .setSmallIcon(R.drawable.appicon)
                .setContentIntent(pendingIntent)
                .setColor(0xffAD3232)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setPriority(Notification.PRIORITY_MAX)
                .setVibrate(new long[0])
                .build();
        notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(
                Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
    }
    public void menuList(){

        menulistView = (ListView) findViewById(R.id.listView);

        menuListAdapter = new MenulistAdapter(this, R.layout.menulist_rowlayout, menuList);
        menulistView.setAdapter(menuListAdapter);

        menulistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menuItems = menuList.get(position);
                if (MainActivity.cookModule) {
                    menuRef = new Firebase("https://app-etite.firebaseio.com/chefsEnrolled/" + Login.userName);
                    menuRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            dataReading++;

                            if (dataSnapshot.hasChild(menuItems.getItemName())) {
                                if(dataReading<=2) {
                                    Intent chefMenuDetails = new Intent(MenuList.this, ChefMenuInfo.class);
                                    chefMenuDetails.putExtra("menu_name", menuItems.getItemName());
                                    startActivity(chefMenuDetails);
                                }
                            } else {
                                Intent chefMenuDetails = new Intent(MenuList.this, ChefMenuItemActivity.class);
                                chefMenuDetails.putExtra("item_name", menuItems.getItemName());
                                startActivity(chefMenuDetails);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                } else if (MainActivity.eatModule) {
                    Intent chefsEnrolled = new Intent(MenuList.this, ChefsEnrolledActivity.class);
                    chefsEnrolled.putExtra("menuName", menuItems.getItemName());
                    startActivity(chefsEnrolled);
                }
            }
        });
    }

    public void notifyUser(){
        int requestCode = 0;
        Intent i = new Intent(this,UserNotifiedActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, requestCode, i, PendingIntent.FLAG_UPDATE_CURRENT);
        int id = 12345;
        Notification notification = new Notification.Builder(this)
                .setContentTitle("Order Status!")
                .setContentText("You have new update.")
                .setColor(0xffAD3232)
                .setSmallIcon(R.drawable.appicon)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setPriority(Notification.PRIORITY_MAX)
                .setVibrate(new long[0])
                .build();
        notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(
                Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
    }
    public void searchContent(String word){
        searchDish=word;
        menuList.clear();
        mRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot.child("item_name").getValue() != null && postSnapshot.child("imageEncoded") != null && postSnapshot.child("cuisine") != null) {
                        String itemName = postSnapshot.child("item_name").getValue().toString();
                        String itemImage = postSnapshot.child("imageEncoded").getValue().toString();
                        String itemCuisine = postSnapshot.child("cuisine").getValue().toString();
                        if (itemName.equalsIgnoreCase(searchDish) || itemCuisine.equalsIgnoreCase(searchDish)) {
                            menuList.add(new Menu(itemName, itemImage, itemCuisine));
                        }
                    }
                    menuList();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("The read failed: ", firebaseError.getMessage());
            }
        });
    }
    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        // Display an hint to the user about what he should say.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hungry for?!");
        // Given an hint to the recognizer about what the user is going to say
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Specify how many results you want to receive. The results will be sorted
        // where the first result is the one with higher confidence.
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            searchWord.setText(matches.get(0));
            String voiceInput = searchWord.getText().toString();
            if(voiceInput!=null ){
                searchContent(voiceInput);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public boolean onCreateOptionsMenu(android.view.Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        if (MainActivity.cookModule){
        getMenuInflater().inflate(R.menu.menu_cook, menu);}
        else{
            getMenuInflater().inflate(R.menu.menu_eat, menu);
        }
        getMenuInflater().inflate(R.menu.menu_mic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home) {
            return true;
        }

        else if(id == R.id.action_reviews){
            String chefName = Login.userName;
            Intent reviewsIntent = new Intent(this,ReviewsCookModuleActivity.class);
                reviewsIntent.putExtra("chef", chefName);
                startActivity(reviewsIntent);
            return true;
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
        }else if(id==R.id.action_mic){
            startVoiceRecognitionActivity();
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
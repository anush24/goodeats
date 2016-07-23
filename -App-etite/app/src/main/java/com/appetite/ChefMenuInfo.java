package com.appetite;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChefMenuInfo extends AppCompatActivity {

    TextView menuName,menuIngredients,menuQuantity,menuQuantityOrdered;
    TextView dishName,dishIngre,dishQuan,quanOrdered;
    ImageView cheffMenuImg,foodImg;
    Firebase mRef,mRef2;
    String customer,status;
    Button doneBtn;
    EditText takeMsg;
    String qOrdered,chefMenus,quantity,ingredients,chef_menu,chefmenu;
    public GoogleApiClient mGoogleApiClient;
    private DropboxAPI<AndroidAuthSession> mDBApi;
    final static private String APP_KEY = "ya6xzn1c4rjsjdu";
    final static private String APP_SECRET = "tka1puxynswkeek";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(MenuList.orderPage){
        setContentView(R.layout.order_page);
            chefmenu=(String)this.getIntent().getStringExtra("menuname");
            customer=(String)this.getIntent().getStringExtra("user");
        }
                else{
            setContentView(R.layout.activity_chef_menu_info);
            }

        Firebase.setAndroidContext(this);

        AppKeyPair keyPair = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session =
                new AndroidAuthSession(keyPair,
                        "bu-o0D7UlrAAAAAAAAAACqbhdtpfpEG8J1_KrFJWnCLfyxlnJ8q_44LSiXkk1yig");
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        String chefName = Login.userName;
        if(MenuList.orderPage){
            chef_menu=chefmenu;
            dishName= (TextView) findViewById(R.id.dish);
            dishName.setText(chefmenu);
            doneBtn=(Button)findViewById(R.id.doneButton);
            doneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    quanOrdered.setText("0");
                    MenuList.orderPage = false;
                    Toast.makeText(getBaseContext(), "Customer is notified", Toast.LENGTH_LONG).show();
                    updateStatus();
                }
            });
        }
        else{
            Bundle bundle = getIntent().getExtras();
            chef_menu = bundle.getString("menu_name");
            menuName = (TextView) findViewById(R.id.chef_menu);
            menuName.setText(chef_menu);
        }

        mRef = new Firebase("https://app-etite.firebaseio.com/chefsEnrolled/" + chefName + "/" + chef_menu+"/");

        mRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("quantity").getValue()!=null & dataSnapshot.child("ingredients").getValue()!=null) {
                    quantity = dataSnapshot.child("quantity").getValue().toString();
                    ingredients = dataSnapshot.child("ingredients").getValue().toString();
                    if (dataSnapshot.child("quantityOrdered").getValue() == null) {
                        qOrdered = "0";
                    } else {
                        qOrdered = dataSnapshot.child("quantityOrdered").getValue().toString();
                    }
                    if(MenuList.orderPage){
                    dishIngre = (TextView) findViewById(R.id.ingre);
                    dishIngre.setText("Pravallika Yanamadala");

                    quanOrdered = (TextView) findViewById(R.id.ordered);
                    quanOrdered.setText(qOrdered);

                    foodImg = (ImageView)findViewById(R.id.chef_foodImg);}
                    else{
                        menuIngredients = (TextView) findViewById(R.id.view_ingredients);
                        menuIngredients.setText(ingredients);

                        menuQuantity = (TextView) findViewById(R.id.view_quantity);
                        menuQuantity.setText(quantity);

                        menuQuantityOrdered = (TextView) findViewById(R.id.view_quantityOrdered);
                        menuQuantityOrdered.setText(qOrdered);

                        cheffMenuImg = (ImageView)findViewById(R.id.chef_menuImg);
                    }

                    AsyncTask<String,Void, ByteArrayOutputStream> downloadHandler = new AsyncTask<String,Void,ByteArrayOutputStream>() {

                        @Override
                        protected ByteArrayOutputStream doInBackground(String... params) {

                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            try {
                                if( dataSnapshot.child("foodImg").getValue().toString() != null ) {
                                    DropboxAPI.DropboxFileInfo info = mDBApi.getFile(dataSnapshot.child("foodImg").getValue().toString(), null, byteArrayOutputStream, null);
                                }
                            } catch (DropboxException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    byteArrayOutputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            return byteArrayOutputStream;
                        }

                        @Override
                        protected void onPostExecute(ByteArrayOutputStream outputStream) {
                            byte[] outputArray= outputStream.toByteArray();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(outputArray, 0, outputArray.length);
                            if(MenuList.orderPage){
                                foodImg.setImageBitmap(bitmap);
                            }else
                            {
                            cheffMenuImg.setImageBitmap(bitmap);}
                        }
                    };

                    downloadHandler.execute();

                }
            };

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("The read failed: ", firebaseError.getMessage());
            }
        });




    }
    public boolean onCreateOptionsMenu(android.view.Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        if (MainActivity.cookModule){
            getMenuInflater().inflate(R.menu.menu_cook, menu);}
        else{
            getMenuInflater().inflate(R.menu.menu_eat, menu);
        }
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
            finish();
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
    protected void updateStatus(){
        mRef2 = new Firebase("https://app-etite.firebaseio.com/notifyUsers/");
        Map<String, Object> orders = new HashMap<String, Object>();
        orders.put("orderStatus","ready");
        takeMsg = (EditText)findViewById(R.id.msgFrmChef);
        orders.put("message",takeMsg.getText().toString() );
        mRef2.updateChildren(orders);

    }

}

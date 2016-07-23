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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class OrderConfirmation extends AppCompatActivity {

    String imgPath, chef_name,chef_menu ,user_quan, user_price;
    final static private String APP_KEY = "ya6xzn1c4rjsjdu";
    final static private String APP_SECRET = "tka1puxynswkeek";
    private DropboxAPI<AndroidAuthSession> mDBApi;
    ImageView chefDishPic;
    public GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppKeyPair keyPair = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session =
                new AndroidAuthSession(keyPair,
                        "bu-o0D7UlrAAAAAAAAAACqbhdtpfpEG8J1_KrFJWnCLfyxlnJ8q_44LSiXkk1yig");
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        setContentView(R.layout.activity_order_confirmation);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        chefDishPic = (ImageView)findViewById(R.id.chefImg);

        imgPath = DishDetailsActivity.chefFoodImage;
        if (imgPath.equals("")) {
            chefDishPic.setImageResource(R.drawable.caesarsalad);
        } else {
            AsyncTask<String, Void, ByteArrayOutputStream> downloadHandler = new AsyncTask<String, Void, ByteArrayOutputStream>() {

                @Override
                protected ByteArrayOutputStream doInBackground(String... params) {

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    try {

                        DropboxAPI.DropboxFileInfo info = mDBApi.getFile(imgPath, null, byteArrayOutputStream, null);
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
                    byte[] outputArray = outputStream.toByteArray();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(outputArray, 0, outputArray.length);
                    chefDishPic.setImageBitmap(bitmap);
                }
            };

            downloadHandler.execute();
        }
        Bundle namebundle = getIntent().getExtras();
        chef_name = namebundle.getString("chefName");
        TextView chef = (TextView)findViewById(R.id.chefName);
        chef.setText(chef_name);

        Bundle menuBundle = getIntent().getExtras();
        chef_menu = menuBundle.getString("chefDish");
        TextView dish = (TextView)findViewById(R.id.chefMenu);
        dish.setText(chef_menu);

        Bundle quanBundle = getIntent().getExtras();
        user_quan = menuBundle.getString("quantity");
        TextView u_quan = (TextView)findViewById(R.id.userQuan);
        u_quan.setText(user_quan);

        Bundle priceBundle = getIntent().getExtras();
        user_price = priceBundle.getString("price");
        TextView u_price = (TextView)findViewById(R.id.price);
        u_price.setText("$"+user_price);

        TextView orderConfirmed = (TextView)findViewById(R.id.tv_orderConfirmation);
        orderConfirmed.setText("You will be notified about the pick up time shortly.");
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
            Intent chefUserMap = new Intent(OrderConfirmation.this,ChefUsersMapActivity.class);
            chefUserMap.putExtra("chef_name",chef_name);
            startActivity(chefUserMap);
        }
        else if (id==R.id.action_logOut){
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            //mStatusTextView.setText("");
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

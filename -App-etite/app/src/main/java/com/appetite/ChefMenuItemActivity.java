package com.appetite;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static java.util.UUID.randomUUID;

public class ChefMenuItemActivity extends AppCompatActivity {

    String itemName,chefName,location,ingredients,foodImagePath,quantityInput;
    EditText et_quantity;
    EditText et_ingredients;
    int count = 0;
    Button bt_post,bt_increase,bt_decrease;
    Firebase mRef, orderRef;
    public GoogleApiClient mGoogleApiClient;
    private static final int SELECT_PHOTO = 100;
    final static private String APP_KEY = "ya6xzn1c4rjsjdu";
    final static private String APP_SECRET = "tka1puxynswkeek";
    private DropboxAPI<AndroidAuthSession> mDBApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_menu_item);

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


        //view item name
        TextView caption = (TextView) findViewById(R.id.view_itemName);
        caption.setText(getIntent().getExtras().getString("item_name"));
        et_ingredients = (EditText) findViewById(R.id.et_ingredients);
        bt_post = (Button) findViewById(R.id.postButton);

        quantityIncDec();

        postChefDetails();
    }
    public void quantityIncDec(){
        et_quantity = (EditText) findViewById(R.id.et_quantityLabel);
        et_quantity.setText(String.valueOf(count));

        bt_increase = (Button)findViewById(R.id.bt_increment);
        bt_increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                et_quantity.setText(String.valueOf(count));
                quantityInput = et_quantity.getText().toString();
            }
        });
        bt_decrease = (Button)findViewById(R.id.bt_decrement);
        bt_decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count--;
                et_quantity.setText(String.valueOf(count));
                quantityInput = et_quantity.getText().toString();


            }
        });

    }

    public void postChefDetails(){

        bt_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingredients = et_ingredients.getText().toString();
                itemName = getIntent().getExtras().getString("item_name");
                chefName = Login.userName;
                mRef = new Firebase("https://app-etite.firebaseio.com/chefsEnrolled/" + chefName);
                mRef.child(itemName).setValue(new ChefMenuItem(ingredients, quantityInput, foodImagePath));
                finish();
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
        getMenuInflater().inflate(R.menu.menu_pic, menu);
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
        }else if(id==R.id.action_camera){

            Intent photoPickIntent = new Intent(Intent.ACTION_PICK);
            photoPickIntent.setType("image/*");
            startActivityForResult(photoPickIntent, SELECT_PHOTO);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturned){
        super.onActivityResult(requestCode, resultCode, imageReturned);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK) {
                    try {
                        InputStream imageInputStream = this.getContentResolver().openInputStream(imageReturned.getData());
                        AsyncTask<InputStream, Void, String> StorageHandler = new AsyncTask<InputStream, Void, String>() {

                            @Override
                            protected String doInBackground(InputStream... inputStream) {
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                                int fileSize = 0;
                                byte[] inputByteBuffer = new byte[1024];

                                if(inputStream[0] != null) {
                                    while(true) {
                                        try {
                                            int readBytes = inputStream[0].read(inputByteBuffer);
                                            if (readBytes == -1) {
                                                break;
                                            }
                                            byteArrayOutputStream.write(inputByteBuffer, 0, readBytes);
                                            fileSize = fileSize + readBytes;
                                        } catch (IOException e) {
                                            Log.e("ioexception", "in reading the image");
                                        }
                                    }
                                }

                                byte[] inputArray = byteArrayOutputStream.toByteArray();
                                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(inputArray);
                                try {
                                    String name = randomUUID().toString();
                                    foodImagePath = "/"+"chef_image_"+name;
                                    DropboxAPI.Entry response = mDBApi.putFile("/"+"chef_image_"+name, byteArrayInputStream,
                                            fileSize, null, null);
                                    Log.i("DbExampleLog", "The uploaded file's rev is: " + response.rev);
                                } catch (DropboxException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }
                        };
                        StorageHandler.execute(imageInputStream);
                    } catch (FileNotFoundException e) {
                        Log.d("notfound", "File not found");
                    }

                }
        }
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

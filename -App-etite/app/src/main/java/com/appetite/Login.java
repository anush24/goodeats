package com.appetite;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.InputStream;


public class Login extends AppCompatActivity implements View.OnClickListener {

    public GoogleApiClient mGoogleApiClient;
    public static int RC_SIGN_IN = 1;
    TextView mStatusTextView;
    Button bLogin;
    EditText etUserName, etPassword;
    TextView tvRegisterLink;
    String name,address,username,password;
    int id,phone_no;
    String userPwd;
    String email;
    Uri photoUrl;
    public static String userName;
    Firebase chefRef,customerRef;
    String uname;
    SignInButton signInButton;
    public static boolean googleSignIn = false;
    int count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Firebase.setAndroidContext(this);


        etUserName = (EditText)findViewById(R.id.etUsername);
        etPassword = (EditText)findViewById(R.id.etPassword);
        bLogin = (Button)findViewById(R.id.bLogin);
        tvRegisterLink = (TextView)findViewById(R.id.tvRegisterLink);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setColorScheme(SignInButton.COLOR_AUTO);
        signInButton.setScopes(gso.getScopeArray());

        bLogin.setOnClickListener(this);

        tvRegisterLink.setOnClickListener(this);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signIn();
            }
        });


    }


    @Override
    public void onClick(View v) {


        switch (v.getId()){

                case R.id.bLogin:
                    if(MainActivity.cookModule == true) {
                        chefRef = new Firebase("https://app-etite.firebaseio.com/userInfo/chef");
                        userName = etUserName.getText().toString();
                        userPwd = etPassword.getText().toString();
                        chefRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                    uname = postSnapshot.child("username").getValue().toString();

                                    String pwd = postSnapshot.child("password").getValue().toString();
                                    if (uname.equals(userName) & pwd.equals(userPwd)) {
                                        displayMenu();
                                        etUserName.setText("");
                                        etPassword.setText("");

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Username and password do not match", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }else if(MainActivity.eatModule == true){

                        customerRef = new Firebase("https://app-etite.firebaseio.com/userInfo/customer");
                        userName = etUserName.getText().toString();
                        userPwd = etPassword.getText().toString();
                        customerRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {

                                    uname = data.child("username").getValue().toString();

                                    String pwd = data.child("password").getValue().toString();
                                    if (uname.equals(userName) & pwd.equals(userPwd)) {
                                        displayMenu();
                                        etUserName.setText("");
                                        etPassword.setText("");

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Username and password do not match", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }

                break;
            case R.id.tvRegisterLink:

                if(MainActivity.cookModule == true){

                    startActivity(new Intent(this, ChefRegister.class));

                }else if(MainActivity.eatModule == true) {

                    startActivity(new Intent(this, CustomerRegister.class));
                }

                break;

        }
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("SIGN IN", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            googleSignIn = true;
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            userName = acct.getDisplayName();
            email = acct.getEmail();
            photoUrl = acct.getPhotoUrl();
            if(MainActivity.cookModule) {
                chefRef = new Firebase("https://app-etite.firebaseio.com/userInfo/chef");

                chefRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                       if( (!dataSnapshot.child(userName).hasChild("phone")) && (!dataSnapshot.child(userName).hasChild("location"))){
                           chefProfile(userName, email, photoUrl);
                       } else{
                           displayMenu();
                       }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

            }else if(MainActivity.eatModule){
                customerRef = new Firebase("https://app-etite.firebaseio.com/userInfo/customer");
                customerRef.child(userName).setValue(new User(userName) );
                displayMenu();
            }

        } else {
                Toast.makeText(getApplicationContext(),"Invalid Login",Toast.LENGTH_SHORT).show();
        }
    }

    public void chefProfile(String gname,String gmail, Uri picUrl){
        Intent chefProfile = new Intent(this, ChefProfile.class);
        chefProfile.putExtra("username",gname);
        chefProfile.putExtra("email",gmail);
        chefProfile.putExtra("picUrl",picUrl.toString());
        startActivity(chefProfile);

    }
    public void displayMenu(){
        Intent menuList = new Intent(this, MenuList.class);
        startActivity(menuList);
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
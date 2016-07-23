package com.appetite;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.firebase.client.Firebase;


public class ChefRegister extends AppCompatActivity implements View.OnClickListener {

    Button bRegister;
    EditText etPhone,etAddress,etUserName,etPassword;
    String name,phone,address,username,pwd;
    Firebase mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Firebase.setAndroidContext(this);

        etPhone = (EditText)findViewById(R.id.etPhone);
        etAddress = (EditText)findViewById(R.id.etAddress);
        etUserName = (EditText)findViewById(R.id.etUsername);
        etPassword = (EditText)findViewById(R.id.etPassword);

        bRegister = (Button)findViewById(R.id.bRegister);

        bRegister.setOnClickListener(this);

    }
    public void insert() {

        phone = etPhone.getText().toString();
        address = etAddress.getText().toString();
        username = etUserName.getText().toString();
        pwd = etPassword.getText().toString();
        mRef = new Firebase("https://app-etite.firebaseio.com/userInfo/chef");
        mRef.child(username).setValue(new User(username, pwd, phone, address));

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bRegister:
                insert();
                startActivity(new Intent(this,Login.class));
                break;
        }

    }
}

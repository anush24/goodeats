package com.appetite;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;


public class CustomerRegister extends AppCompatActivity implements View.OnClickListener {

    Button bRegister;
    EditText etUserName,etPassword;
    String name,username,pwd;
    Firebase mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register);
        Firebase.setAndroidContext(this);

        etUserName = (EditText)findViewById(R.id.etUsername);
        etPassword = (EditText)findViewById(R.id.etPassword);

        bRegister = (Button)findViewById(R.id.bRegister);

        bRegister.setOnClickListener(this);

    }
    public void insert() {

        username = etUserName.getText().toString();
        pwd = etPassword.getText().toString();
        mRef = new Firebase("https://app-etite.firebaseio.com/userInfo/customer");
        mRef.child(username).setValue(new User(username, pwd));

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

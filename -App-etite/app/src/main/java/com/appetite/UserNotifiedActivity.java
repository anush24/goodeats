package com.appetite;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class UserNotifiedActivity extends AppCompatActivity {
     String message;
     TextView displayMessage;
     Firebase mRef2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_notified);
        displayMessage = (TextView)findViewById(R.id.message1);
        Firebase.setAndroidContext(this);
        mRef2 = new Firebase("https://app-etite.firebaseio.com/notifyUsers");
        mRef2.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                message = dataSnapshot.child("message").getValue().toString();
                displayMessage.setText(message);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("The read failed: ", firebaseError.getMessage());

            }
        });

    }

}

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
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AddReviewActivity extends AppCompatActivity {
    EditText rev;
    String ratingValue;
    String user;
    String chef;
    ContentValues newValues;
    Firebase ref;
    Reviews r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        Bundle bdl = getIntent().getExtras();
        chef = bdl.getString("chefName");
        user=Login.userName;
        rev=(EditText) findViewById(R.id.review);
        ref = new Firebase("https://app-etite.firebaseio.com/ChefReview/"+chef+"/");
        rev=(EditText) findViewById(R.id.review);
        RatingBar rat=(RatingBar)findViewById(R.id.ratingBar);
        rat.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingValue = String.valueOf(rating);
            }
        });
        Button save = (Button) findViewById(R.id.button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String review = rev.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date = sdf.format(new Date());
                r = new Reviews(user, review, ratingValue, date);
                ref.child(user).setValue(r);
                Intent i = new Intent();
                setResult(RESULT_OK, i);
                finish();

            }
        });
    }

}

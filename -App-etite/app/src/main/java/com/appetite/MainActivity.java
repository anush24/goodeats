package com.appetite;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    ImageView bEat;
    ImageView bCook;
    TextView appTitle;
    public static boolean cookModule = false;
    public static boolean eatModule = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        bEat = (ImageView) findViewById(R.id.bEat);

        bCook = (ImageView) findViewById(R.id.bCook);
        bEat.setOnClickListener(this);
        bCook.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.bEat:
                eatModule = true;
                startActivity(new Intent(this, Login.class));

                break;
            case R.id.bCook:
                cookModule = true;
                startActivity(new Intent(this,Login.class));

                break;
        }

    }
}
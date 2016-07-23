package com.appetite;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReviewsCookModuleActivity extends AppCompatActivity {

    List<Reviews> cap;
    CustomAdapter ca;
    Reviews r;
    public static final int REQUEST=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews_cook_module);
        Bundle bundle = getIntent().getExtras();
        String chef = bundle.getString("chef");
        Firebase.setAndroidContext(this);
        Firebase ref = new Firebase("https://app-etite.firebaseio.com/ChefReview/"+chef+"/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cap = new ArrayList();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String rev = postSnapshot.child("review").getValue().toString();
                    String rat = postSnapshot.child("rating").getValue().toString();
                    String dat = postSnapshot.child("date").getValue().toString();
                    String usr = postSnapshot.child("user").getValue().toString();
                    cap.add(new Reviews(usr, rev, rat, dat));
                }
                viewList();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST && resultCode == RESULT_OK) {
            ca.notifyDataSetChanged();

        }
    }
    public void viewList(){
        ListView lview = (ListView) findViewById(R.id.listView);
        ca = new CustomAdapter(this, R.layout.custom_row, cap);
        lview.setAdapter(ca);
        lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }


}

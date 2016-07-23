package com.appetite;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReviiewList extends AppCompatActivity {
    List<Reviews> cap;
    CustomAdapter ca;
    Reviews r;
    String chefName;
    public static final int REQUEST=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);
        Bundle bdl = getIntent().getExtras();
        chefName=bdl.getString("chefName");
        Firebase.setAndroidContext(this);
        Firebase ref = new Firebase("https://app-etite.firebaseio.com/ChefReview/"+chefName+"/");
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
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ReviiewList.this, AddReviewActivity.class);
                i.putExtra("chefName",chefName);
                startActivityForResult(i, REQUEST);
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

    }
}
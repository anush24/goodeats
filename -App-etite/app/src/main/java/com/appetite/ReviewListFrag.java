package com.appetite;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pravallika on 3/9/16.
 */
public class ReviewListFrag extends Fragment {
    List<Reviews> cap;
    CustomAdapter ca;
    Reviews r;
    View v;
    public static final int REQUEST=1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v =inflater.inflate(R.layout.activity_review_list, container, false);
        Firebase.setAndroidContext(getContext());
        Firebase ref = new Firebase("https://app-etite.firebaseio.com/ChefReview/"+DishDetailsActivity.chefName+"/");
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
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AddReviewActivity.class);
                i.putExtra("chefName",DishDetailsActivity.chefName);
                startActivityForResult(i, REQUEST);
            }
        });

        return v;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST && resultCode == 0) {
            ca.notifyDataSetChanged();
        }
    }

    public void viewList(){
        ListView lview = (ListView) v.findViewById(R.id.listView);
        ca = new CustomAdapter(getContext(), R.layout.custom_row, cap);
        lview.setAdapter(ca);
    }

}

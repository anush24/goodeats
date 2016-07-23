package com.appetite;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Kavi on 3/9/16.
 */
public class MapFrag extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
   View v = inflater.inflate(R.layout.activity_chef_users_map,container,false);
    return v;}
}
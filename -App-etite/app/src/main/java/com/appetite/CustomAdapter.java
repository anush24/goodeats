package com.appetite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by pravallika on 2/15/16.
 */
public class CustomAdapter extends ArrayAdapter {
    private List<Reviews> review;
    public CustomAdapter(Context context, int resource,List<Reviews> review) {
        super(context, resource,review);
        this.review= review;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        Reviews rev= review.get(position);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.custom_row, null);

        TextView textView = (TextView) row.findViewById(R.id.userRow);
        textView.setText(rev.getUser());

        TextView date = (TextView)row.findViewById(R.id.dateRow);
        date.setText(rev.getDate());

        RatingBar rBar=(RatingBar) row.findViewById(R.id.ratingRow);
        rBar.setRating(Float.parseFloat(rev.getRating()));

        TextView reviewRow1 = (TextView)row.findViewById(R.id.reviewRow);
        reviewRow1.setText(rev.getReview());

        return row;
    }
}
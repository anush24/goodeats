package com.appetite;

/**
 * Created by pravallika on 2/15/16.
 */
public class Reviews {
    private String chef;
    private String user;
    private String review;
    private String rating;
    private String date;
    public Reviews(String user,String review,String rating,String date){
        this.user=user;
        this.review=review;
        this.rating=rating;
        this.date=date;
    }

    public String getUser(){
        return user;
    }
    public String getReview(){
        return review;
    }
    public String getRating(){
        return rating;
    }
    public String getDate(){
        return date;
    }

}

package com.appetite;

import android.util.Log;

import java.util.Comparator;

/**
 * Created by Kavi on 3/7/16.
 */
public class ChefsEnrolledList {

    String chefName;
    String chefPic;
    String quantity;
    String ingredients;
    String foodImg;
    Double latitude;
    Double longitude;

    public ChefsEnrolledList(String chefName,String chefPic, String ingredients, String quantity ,String foodImg, String latitude, String longitude) {
        this.chefName = chefName;
        this.chefPic = chefPic;
        this.ingredients = ingredients;
        this.quantity = quantity;
        this.foodImg = foodImg;
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
    }

    public void setLatitude(String latitude) {
        this.latitude = Double.parseDouble(latitude);
    }

    public void setLongitude(String longitude) {
        this.longitude = Double.parseDouble(longitude);
    }

    public Double getLatitude() {

        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getFoodImage() { return foodImg; }

    public void setFoodImage(String foodImg) { this.foodImg = foodImg; }

    public String getChefName() {
        return chefName;
    }

    public void setChefName(String chefName) {
        this.chefName = chefName;
    }

    public String getChefPic() {
        return chefPic;
    }

    public void setChefPic(String chefPic) {
        this.chefPic = chefPic;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    static public class DistanceComparator implements Comparator<ChefsEnrolledList> {
        private double mLat;
        private double mLong;

        public DistanceComparator(double lat, double longitude) {
            mLat  = lat;
            mLong = longitude;
        }
        @Override
        public int compare(ChefsEnrolledList lhs, ChefsEnrolledList rhs) {
            double distance1 = Math.sqrt((Math.pow(mLat - lhs.getLatitude(), 2) + (Math.pow(mLong - lhs.getLongitude(), 2))));
            double distance2 = Math.sqrt((Math.pow(mLat - rhs.getLatitude(), 2) + (Math.pow(mLong - rhs.getLongitude(), 2))));

            Log.d("distance1", String.valueOf(distance1 * 69));
            Log.d("distance2", String.valueOf(distance2 * 69));

            if(distance1 < distance2) {
                return -1;
            } else if(distance1 > distance2) {
                return 1;
            } else {
                return 0;
            }
        }

    }
}

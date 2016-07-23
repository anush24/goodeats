package com.appetite;

/**
 * Created by Kavi on 3/7/16.
 */
public class ChefMenuDetails {

    String chefName;
    String ingredients;
    String quantity;
    String foodImg;

    public ChefMenuDetails(String ingredients, String quantity, String foodImg) {
        this.ingredients = ingredients;
        this.quantity = quantity;
        this.foodImg = foodImg;
    }

    public String getFoodImg() {
        return foodImg;
    }

    public void setFoodImg(String foodImg) {
        this.foodImg = foodImg;
    }

    public String getChefName() {
        return chefName;
    }

    public void setChefName(String chefName) {
        this.chefName = chefName;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}

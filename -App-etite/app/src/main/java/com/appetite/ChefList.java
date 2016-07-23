package com.appetite;

/**
 * Created by Kavi on 3/3/16.
 */
public class ChefList {

    String menuName;
    String quantity;
    String menuImg;
    String ingredients;
    String qOrdered;

    public ChefList(){}

    public ChefList(String menuName,String quantity,String ingredients,String qOrdered){

        this.menuName = menuName;
        this.quantity = quantity;
        this.ingredients = ingredients;
        this.qOrdered = qOrdered;

    }

    public String getqOrdered() {
        return qOrdered;
    }

    public void setqOrdered(String qOrdered) {
        this.qOrdered = qOrdered;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }


    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getMenuImg() {
        return menuImg;
    }

    public void setMenuImg(String menuImg) {
        this.menuImg = menuImg;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }
}

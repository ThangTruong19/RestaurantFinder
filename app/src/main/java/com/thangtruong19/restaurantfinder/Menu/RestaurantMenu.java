package com.thangtruong19.restaurantfinder.Menu;

/**
 * Created by User on 05/12/2018.
 */

public class RestaurantMenu {
    private String name;
    private int price;

    public RestaurantMenu(){
    }

    public RestaurantMenu(String name,int price){
        this.name=name;
        this.price=price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}

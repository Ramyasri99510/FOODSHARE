package com.foodshare.foodshare.models;

import android.net.Uri;

public class FoodDataHolder {
    private static FoodDataHolder instance;
    
    public String foodName;
    public String category;
    public String quantity;
    public String unit;
    public Uri photoUri;
    public long expiryTime;
    public String location;
    public String city;

    private FoodDataHolder() {}

    public static FoodDataHolder getInstance() {
        if (instance == null) {
            instance = new FoodDataHolder();
        }
        return instance;
    }

    public void clear() {
        foodName = null;
        category = null;
        quantity = null;
        unit = null;
        photoUri = null;
        expiryTime = 0;
        location = null;
        city = null;
    }
}
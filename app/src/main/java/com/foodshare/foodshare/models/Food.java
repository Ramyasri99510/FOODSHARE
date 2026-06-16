package com.foodshare.foodshare.models;

public class Food {
    public String id;
    public String donorId;
    public String donorName;
    public String foodName;
    public String category;
    public String quantity;
    public String unit;
    public long expiryTime;
    public String location;
    public String city;
    public String photoUrl;
    public String status; // available, claimed, expired
    public long createdAt;

    public Food() {}

    public Food(String id, String donorId, String donorName, String foodName, String category, String quantity, String unit, long expiryTime, String location, String city, String photoUrl, String status, long createdAt) {
        this.id = id;
        this.donorId = donorId;
        this.donorName = donorName;
        this.foodName = foodName;
        this.category = category;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryTime = expiryTime;
        this.location = location;
        this.city = city;
        this.photoUrl = photoUrl;
        this.status = status;
        this.createdAt = createdAt;
    }
}
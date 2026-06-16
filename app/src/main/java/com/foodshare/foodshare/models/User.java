package com.foodshare.foodshare.models;

public class User {
    public String uid;
    public String name;
    public String email;
    public String phone;
    public String role;
    public String city;
    public String language;
    public long createdAt;

    public User() {} // Required for Firebase

    public User(String uid, String name, String email, String phone, String role, String city, String language, long createdAt) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.city = city;
        this.language = language;
        this.createdAt = createdAt;
    }
}
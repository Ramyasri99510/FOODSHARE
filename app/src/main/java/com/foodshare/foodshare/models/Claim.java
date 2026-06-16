package com.foodshare.foodshare.models;

public class Claim {
    public String id;
    public String foodId;
    public String receiverId;
    public String status; // active, completed, cancelled
    public long claimedAt;

    public Claim() {}

    public Claim(String id, String foodId, String receiverId, String status, long claimedAt) {
        this.id = id;
        this.foodId = foodId;
        this.receiverId = receiverId;
        this.status = status;
        this.claimedAt = claimedAt;
    }
}
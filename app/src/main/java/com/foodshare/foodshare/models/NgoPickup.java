package com.foodshare.foodshare.models;

public class NgoPickup {
    public String id;
    public String foodId;
    public String ngoId;
    public long scheduledTime;
    public String status; // scheduled, completed, cancelled

    public NgoPickup() {}

    public NgoPickup(String id, String foodId, String ngoId, long scheduledTime, String status) {
        this.id = id;
        this.foodId = foodId;
        this.ngoId = ngoId;
        this.scheduledTime = scheduledTime;
        this.status = status;
    }
}
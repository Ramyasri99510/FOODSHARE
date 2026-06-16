package com.foodshare.foodshare;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.concurrent.TimeUnit;

public class ExpiryAlertManager {

    private static final String TAG = "ExpiryAlertManager";

    public static void checkAndNotify(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        long now = System.currentTimeMillis();
        long twoHoursFromNow = now + TimeUnit.HOURS.toMillis(2);

        db.collection("foods")
                .whereEqualTo("status", "available")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Long expiryTime = doc.getLong("expiryTime");
                        String foodName = doc.getString("foodName");

                        if (expiryTime != null && expiryTime > now && expiryTime <= twoHoursFromNow) {
                            sendPushNotification(foodName);
                        }
                    }
                });
    }

    private static void sendPushNotification(String foodName) {
        // Logic to trigger FCM via Cloud Functions or Server
        // For client-side demo, we log the alert
        Log.d(TAG, "ALERT: " + foodName + " is expiring in 2 hours! Notifying nearby receivers...");
    }
}
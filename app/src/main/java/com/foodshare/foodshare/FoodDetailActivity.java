package com.foodshare.foodshare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FoodDetailActivity extends BaseActivity {

    private FirebaseFirestore db;
    private String foodId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        db = FirebaseFirestore.getInstance();

        TextView textFoodName = findViewById(R.id.textFoodName);
        TextView textPickupAddress = findViewById(R.id.textPickupAddress);
        Button btnClaim = findViewById(R.id.btnClaim);

        foodId = getIntent().getStringExtra("food_id");
        String foodName = getIntent().getStringExtra("food_name");
        String address = getIntent().getStringExtra("address");

        if (foodName != null) textFoodName.setText(foodName);
        if (address != null) textPickupAddress.setText(address);

        btnClaim.setOnClickListener(v -> claimFood());
    }

    private void claimFood() {
        if (foodId == null) return;
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) return;

        Map<String, Object> claimData = new HashMap<>();
        claimData.put("id", UUID.randomUUID().toString());
        claimData.put("foodId", foodId);
        claimData.put("receiverId", userId);
        claimData.put("status", "active");
        claimData.put("claimedAt", System.currentTimeMillis());

        db.collection("claims").add(claimData)
                .addOnSuccessListener(documentReference -> {
                    // Update food status to claimed
                    db.collection("foods").document(foodId).update("status", "claimed")
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Food Claimed Successfully!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, ClaimConfirmActivity.class));
                                finish();
                            });
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Claim failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
package com.foodshare.foodshare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.foodshare.foodshare.models.Food;
import com.foodshare.foodshare.models.FoodDataHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class PostFoodStep3Activity extends BaseActivity {

    private EditText etNotes;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_food_step3);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();

        etNotes = findViewById(R.id.etNotes);
        Button btnPost = findViewById(R.id.btnPost);

        btnPost.setOnClickListener(v -> postDonation());
    }

    private void postDonation() {
        FoodDataHolder holder = FoodDataHolder.getInstance();
        if (mAuth.getCurrentUser() == null) return;

        String foodId = UUID.randomUUID().toString();
        String donorId = mAuth.getCurrentUser().getUid();
        
        StorageReference ref = storage.getReference().child("food_photos/" + foodId);
        
        Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show();

        ref.putFile(holder.photoUri).addOnSuccessListener(taskSnapshot -> {
            ref.getDownloadUrl().addOnSuccessListener(uri -> {
                String photoUrl = uri.toString();
                
                Food newFood = new Food(
                    foodId,
                    donorId,
                    "Anonymous Donor", // Simplified for demo
                    holder.foodName,
                    holder.category,
                    holder.quantity,
                    holder.unit,
                    holder.expiryTime,
                    holder.location,
                    holder.city,
                    photoUrl,
                    "available",
                    System.currentTimeMillis()
                );

                db.collection("foods").document(foodId).set(newFood)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Donation Posted!", Toast.LENGTH_SHORT).show();
                            holder.clear();
                            startActivity(new Intent(this, PostSuccessActivity.class));
                            finishAffinity();
                        });
            });
        }).addOnFailureListener(e -> Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
package com.foodshare.foodshare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends BaseActivity {

    private TextView textName, textBadge, tvStatPosts, tvStatMeals;
    private ImageView imageProfile;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private final ActivityResultLauncher<String> selectImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    uploadProfilePhoto(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        textName = findViewById(R.id.textName);
        textBadge = findViewById(R.id.textBadge);
        imageProfile = findViewById(R.id.imageProfile);
        tvStatPosts = findViewById(R.id.tvStatPosts);
        tvStatMeals = findViewById(R.id.tvStatMeals);
        
        TextView menuSettings = findViewById(R.id.menuSettings);
        TextView menuLogout = findViewById(R.id.menuLogout);

        loadUserProfile();
        loadStats();

        imageProfile.setOnClickListener(v -> selectImageLauncher.launch("image/*"));

        menuSettings.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });

        menuLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadUserProfile() {
        String uid = mAuth.getUid();
        if (uid == null) return;

        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        textName.setText(documentSnapshot.getString("name"));
                        textBadge.setText(documentSnapshot.getString("role"));
                        String photoUrl = documentSnapshot.getString("photoUrl");
                        if (photoUrl != null && !photoUrl.isEmpty()) {
                            Glide.with(this).load(photoUrl).placeholder(R.drawable.ic_foodshare_logo).into(imageProfile);
                        }
                    }
                });
    }

    private void loadStats() {
        String uid = mAuth.getUid();
        if (uid == null) return;

        db.collection("foods").whereEqualTo("donorId", uid).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int count = queryDocumentSnapshots.size();
                    tvStatPosts.setText(String.valueOf(count));
                    
                    double totalKg = 0;
                    for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            String qty = doc.getString("quantity");
                            if (qty != null) totalKg += Double.parseDouble(qty);
                        } catch (Exception ignored) {}
                    }
                    tvStatMeals.setText(String.valueOf((int)(totalKg / 0.4)));
                });
    }

    private void uploadProfilePhoto(Uri uri) {
        String uid = mAuth.getUid();
        if (uid == null) return;

        Toast.makeText(this, "Updating Profile Photo...", Toast.LENGTH_SHORT).show();
        StorageReference ref = storage.getReference().child("profile_photos/" + uid);

        ref.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            ref.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                db.collection("users").document(uid).update("photoUrl", downloadUri.toString())
                        .addOnSuccessListener(aVoid -> {
                            Glide.with(this).load(downloadUri).into(imageProfile);
                            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
                        });
            });
        }).addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
package com.foodshare.foodshare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PostSuccessActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_success);

        Button btnViewListings = findViewById(R.id.btnViewListings);
        Button btnGoHome = findViewById(R.id.btnGoHome);

        btnViewListings.setOnClickListener(v -> {
            startActivity(new Intent(this, MyListingsActivity.class));
            finish();
        });

        btnGoHome.setOnClickListener(v -> {
            startActivity(new Intent(this, DonorHomeActivity.class));
            finish();
        });
    }
}
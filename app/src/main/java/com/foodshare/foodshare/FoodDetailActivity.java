package com.foodshare.foodshare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FoodDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        TextView textFoodName = findViewById(R.id.textFoodName);
        TextView textPickupAddress = findViewById(R.id.textPickupAddress);
        Button btnClaim = findViewById(R.id.btnClaim);

        String foodName = getIntent().getStringExtra("food_name");
        String address = getIntent().getStringExtra("address");

        if (foodName != null) textFoodName.setText(foodName);
        if (address != null) textPickupAddress.setText(address);

        btnClaim.setOnClickListener(v -> {
            startActivity(new Intent(this, ClaimConfirmActivity.class));
        });
    }
}
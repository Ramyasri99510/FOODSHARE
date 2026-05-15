package com.foodshare.foodshare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PostFoodStep3Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_food_step3);

        Button btnPost = findViewById(R.id.btnPost);

        btnPost.setOnClickListener(v -> {
            startActivity(new Intent(this, PostSuccessActivity.class));
            finishAffinity(); // Clear stack for this demo
        });
    }
}
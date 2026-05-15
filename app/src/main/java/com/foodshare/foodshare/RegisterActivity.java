package com.foodshare.foodshare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button btnCreateAccount = findViewById(R.id.btnCreateAccount);

        btnCreateAccount.setOnClickListener(v -> {
            // In a real app, we'd check the role and navigate accordingly
            // For this demo, we'll go to DonorHomeActivity
            Intent intent = new Intent(RegisterActivity.this, DonorHomeActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
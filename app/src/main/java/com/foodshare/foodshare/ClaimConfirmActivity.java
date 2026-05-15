package com.foodshare.foodshare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ClaimConfirmActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_confirm);

        Button btnViewClaims = findViewById(R.id.btnViewClaims);

        btnViewClaims.setOnClickListener(v -> {
            startActivity(new Intent(this, MyClaimsActivity.class));
            finish();
        });
    }
}
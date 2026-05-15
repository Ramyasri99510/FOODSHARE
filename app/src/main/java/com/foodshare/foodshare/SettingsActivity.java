package com.foodshare.foodshare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView menuAboutAi = findViewById(R.id.menuAboutAi);
        menuAboutAi.setOnClickListener(v -> {
            startActivity(new Intent(this, AboutAiActivity.class));
        });
    }
}
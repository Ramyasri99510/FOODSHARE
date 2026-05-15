package com.foodshare.foodshare;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class NGODashboardActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ngo_dashboard);

        ExtendedFloatingActionButton fabBrowse = findViewById(R.id.fabBrowse);
        fabBrowse.setOnClickListener(v -> {
            startActivity(new Intent(this, NGOBrowseActivity.class));
        });
    }
}
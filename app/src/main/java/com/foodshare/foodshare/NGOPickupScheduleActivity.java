package com.foodshare.foodshare;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class NGOPickupScheduleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ngo_pickup_schedule);

        EditText etDate = findViewById(R.id.etDate);
        AutoCompleteTextView actVehicle = findViewById(R.id.actVehicle);
        Button btnConfirm = findViewById(R.id.btnConfirm);

        etDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                etDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        String[] vehicles = {"Mini Truck", "Van", "Auto Rickshaw", "Two Wheeler"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, vehicles);
        actVehicle.setAdapter(adapter);

        btnConfirm.setOnClickListener(v -> {
            Toast.makeText(this, "Pickup Scheduled Successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
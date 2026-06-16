package com.foodshare.foodshare;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.foodshare.foodshare.models.FoodDataHolder;

import java.util.Calendar;

public class PostFoodStep2Activity extends BaseActivity {

    private EditText etExpiryDate, etExpiryTime, etLocation;
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_food_step2);

        etExpiryDate = findViewById(R.id.etExpiryDate);
        etExpiryTime = findViewById(R.id.etExpiryTime);
        etLocation = findViewById(R.id.etLocation);
        Button btnNext = findViewById(R.id.btnNext);

        etExpiryDate.setOnClickListener(v -> showDatePicker());
        etExpiryTime.setOnClickListener(v -> showTimePicker());

        btnNext.setOnClickListener(v -> {
            if (saveData()) {
                startActivity(new Intent(this, PostFoodStep3Activity.class));
            }
        });
    }

    private void showDatePicker() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            etExpiryDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            etExpiryTime.setText(hourOfDay + ":" + minute);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private boolean saveData() {
        String date = etExpiryDate.getText().toString().trim();
        String time = etExpiryTime.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        if (date.isEmpty() || time.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        FoodDataHolder holder = FoodDataHolder.getInstance();
        holder.expiryTime = calendar.getTimeInMillis();
        holder.location = location;
        // In a real app, we'd get city from location or a separate field
        holder.city = "Chennai"; 
        
        return true;
    }
}
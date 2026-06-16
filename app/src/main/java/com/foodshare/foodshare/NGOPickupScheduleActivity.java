package com.foodshare.foodshare;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.foodshare.foodshare.models.NgoPickup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.UUID;

public class NGOPickupScheduleActivity extends BaseActivity {

    private EditText etDate;
    private AutoCompleteTextView actVehicle;
    private String foodId;
    private Calendar calendar = Calendar.getInstance();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ngo_pickup_schedule);

        db = FirebaseFirestore.getInstance();
        foodId = getIntent().getStringExtra("food_id");

        etDate = findViewById(R.id.etDate);
        actVehicle = findViewById(R.id.actVehicle);
        Button btnConfirm = findViewById(R.id.btnConfirm);

        etDate.setOnClickListener(v -> showDatePicker());

        String[] vehicles = {"Mini Truck", "Van", "Auto Rickshaw", "Two Wheeler"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, vehicles);
        actVehicle.setAdapter(adapter);

        btnConfirm.setOnClickListener(v -> schedulePickup());
    }

    private void showDatePicker() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            etDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void schedulePickup() {
        if (foodId == null) {
            Toast.makeText(this, "No food item selected", Toast.LENGTH_SHORT).show();
            return;
        }

        String pickupId = UUID.randomUUID().toString();
        String ngoId = FirebaseAuth.getInstance().getUid();

        NgoPickup pickup = new NgoPickup(
                pickupId,
                foodId,
                ngoId,
                calendar.getTimeInMillis(),
                "scheduled"
        );

        db.collection("ngopickups").document(pickupId).set(pickup)
                .addOnSuccessListener(aVoid -> {
                    // Update food status to claimed
                    db.collection("foods").document(foodId).update("status", "claimed");
                    Toast.makeText(this, "Pickup Scheduled Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
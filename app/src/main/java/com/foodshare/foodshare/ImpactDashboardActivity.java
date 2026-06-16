package com.foodshare.foodshare;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ImpactDashboardActivity extends BaseActivity {

    private TextView tvTotalKg, tvTotalMeals, tvCo2Saved, tvDonationsCount;
    private FirebaseFirestore db;
    private String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impact_dashboard);

        db = FirebaseFirestore.getInstance();
        currentUid = FirebaseAuth.getInstance().getUid();

        tvTotalKg = findViewById(R.id.tvTotalKg);
        tvTotalMeals = findViewById(R.id.tvTotalMeals);
        tvCo2Saved = findViewById(R.id.tvCo2Saved);
        tvDonationsCount = findViewById(R.id.tvDonationsCount);

        if (currentUid != null) {
            calculateImpact();
        }
    }

    private void calculateImpact() {
        db.collection("foods")
                .whereEqualTo("donorId", currentUid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    double totalKg = 0;
                    int count = queryDocumentSnapshots.size();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String qtyStr = doc.getString("quantity");
                        try {
                            if (qtyStr != null) totalKg += Double.parseDouble(qtyStr);
                        } catch (NumberFormatException ignored) {}
                    }

                    updateUI(totalKg, count);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading stats", Toast.LENGTH_SHORT).show());
    }

    private void updateUI(double kg, int count) {
        double meals = kg / 0.4;
        double co2 = kg * 2.0;

        tvTotalKg.setText(String.format("%.1f kg", kg));
        tvTotalMeals.setText(String.valueOf((int) meals));
        tvCo2Saved.setText(String.format("%.1f kg", co2));
        tvDonationsCount.setText(String.valueOf(count));
    }
}
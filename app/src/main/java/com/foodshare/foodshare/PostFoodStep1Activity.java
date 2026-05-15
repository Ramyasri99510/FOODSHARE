package com.foodshare.foodshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

import java.util.HashMap;
import java.util.Map;

public class PostFoodStep1Activity extends BaseActivity {

    private AutoCompleteTextView actCategory, actUnit;
    private MaterialCardView cardAiSuggestion;
    private TextView textAiSuggestion, labelPoweredBy;
    private Button btnAcceptAi;

    private final Map<String, Integer> expiryHoursMap = new HashMap<String, Integer>() {{
        put("Cooked Rice or Dal", 4);
        put("Cooked Curry or Sambar", 6);
        put("Bread or Bakery items", 12);
        put("Raw Vegetables", 48);
        put("Raw Fruits", 72);
        put("Packaged food", 168);
        put("Beverages", 24);
        put("Biryani or Pulao", 5);
        put("Idli or Dosa", 3);
        put("Sweets or Desserts", 8);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_food_step1);

        actCategory = findViewById(R.id.actCategory);
        actUnit = findViewById(R.id.actUnit);
        cardAiSuggestion = findViewById(R.id.cardAiSuggestion);
        textAiSuggestion = findViewById(R.id.textAiSuggestion);
        labelPoweredBy = findViewById(R.id.labelPoweredBy);
        btnAcceptAi = findViewById(R.id.btnAcceptAi);
        Button btnNext = findViewById(R.id.btnNext);

        String[] categories = expiryHoursMap.keySet().toArray(new String[0]);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        actCategory.setAdapter(categoryAdapter);

        // Standardized units
        String[] units = {"kg", "Liters", "Packets", "Portions", "Items"};
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, units);
        actUnit.setAdapter(unitAdapter);

        actCategory.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCategory = (String) parent.getItemAtPosition(position);
            showAiSuggestion(selectedCategory);
        });

        btnAcceptAi.setOnClickListener(v -> {
            Toast.makeText(this, "AI Suggestion Applied!", Toast.LENGTH_SHORT).show();
            cardAiSuggestion.setVisibility(View.GONE);
            labelPoweredBy.setVisibility(View.GONE);
        });

        btnNext.setOnClickListener(v -> {
            startActivity(new Intent(this, PostFoodStep2Activity.class));
        });
    }

    private void showAiSuggestion(String category) {
        Integer hours = expiryHoursMap.get(category);
        if (hours != null) {
            cardAiSuggestion.setVisibility(View.VISIBLE);
            labelPoweredBy.setVisibility(View.VISIBLE);
            
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.HOUR_OF_DAY, hours);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("hh:mm a, dd MMM", java.util.Locale.getDefault());
            String suggestedTime = sdf.format(cal.getTime());

            String message = "🤖 AI Prediction: Best within " + hours + " hours.\nSuggested Expiry: " + suggestedTime;
            textAiSuggestion.setText(message);
        } else {
            cardAiSuggestion.setVisibility(View.GONE);
            labelPoweredBy.setVisibility(View.GONE);
        }
    }
}
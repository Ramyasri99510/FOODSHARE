package com.foodshare.foodshare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.foodshare.foodshare.models.FoodDataHolder;
import com.google.android.material.card.MaterialCardView;

import java.util.Map;
import java.util.TreeMap;

public class PostFoodStep1Activity extends BaseActivity {

    private AutoCompleteTextView actCategory, actUnit;
    private EditText etFoodName, etQuantity;
    private MaterialCardView cardAiSuggestion;
    private TextView textAiSuggestion, labelPoweredBy;
    private Button btnAcceptAi;
    private ImageView imageSelectedFood;
    private LinearLayout layoutPhotoPlaceholder;
    private View progressUpload;
    private Uri selectedPhotoUri;

    private final ActivityResultLauncher<String> selectImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedPhotoUri = uri;
                    imageSelectedFood.setImageURI(uri);
                    imageSelectedFood.setVisibility(View.VISIBLE);
                    layoutPhotoPlaceholder.setVisibility(View.GONE);
                    Toast.makeText(this, "Photo Ready!", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private final Map<String, Integer> expiryHoursMap = new TreeMap<String, Integer>() {{
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

        etFoodName = findViewById(R.id.etFoodName);
        actCategory = findViewById(R.id.actCategory);
        actUnit = findViewById(R.id.actUnit);
        etQuantity = findViewById(R.id.etQuantity);
        cardAiSuggestion = findViewById(R.id.cardAiSuggestion);
        textAiSuggestion = findViewById(R.id.textAiSuggestion);
        labelPoweredBy = findViewById(R.id.labelPoweredBy);
        btnAcceptAi = findViewById(R.id.btnAcceptAi);
        progressUpload = findViewById(R.id.progressUpload);
        Button btnNext = findViewById(R.id.btnNext);
        
        MaterialCardView cardPhoto = findViewById(R.id.cardPhoto);
        imageSelectedFood = findViewById(R.id.imageSelectedFood);
        layoutPhotoPlaceholder = findViewById(R.id.layoutPhotoPlaceholder);

        cardPhoto.setOnClickListener(v -> selectImageLauncher.launch("image/*"));

        String[] categories = expiryHoursMap.keySet().toArray(new String[0]);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        actCategory.setAdapter(categoryAdapter);

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
            if (saveData()) {
                startActivity(new Intent(this, PostFoodStep2Activity.class));
            }
        });
    }

    private boolean saveData() {
        String name = etFoodName.getText().toString().trim();
        String category = actCategory.getText().toString().trim();
        String quantity = etQuantity.getText().toString().trim();
        String unit = actUnit.getText().toString().trim();

        if (name.isEmpty() || category.isEmpty() || quantity.isEmpty() || unit.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedPhotoUri == null) {
            Toast.makeText(this, "Please tap the camera icon to upload a food photo!", Toast.LENGTH_LONG).show();
            return false;
        }

        FoodDataHolder holder = FoodDataHolder.getInstance();
        holder.foodName = name;
        holder.category = category;
        holder.quantity = quantity;
        holder.unit = unit;
        holder.photoUri = selectedPhotoUri;
        
        return true;
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

            String suggestionHeader = getString(R.string.ai_suggestion);
            String message = suggestionHeader + ": Best within " + hours + " hours.\nSuggested Expiry: " + suggestedTime;
            textAiSuggestion.setText(message);
        } else {
            cardAiSuggestion.setVisibility(View.GONE);
            labelPoweredBy.setVisibility(View.GONE);
        }
    }
}
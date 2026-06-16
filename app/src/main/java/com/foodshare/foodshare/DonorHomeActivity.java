package com.foodshare.foodshare;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodshare.foodshare.models.Food;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.search.SearchView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DonorHomeActivity extends BaseActivity {

    private RecyclerView rvFoodList;
    private FoodAdapter adapter;
    private final List<Food> foodItems = new ArrayList<>();
    private final List<Food> fullList = new ArrayList<>();
    private FirebaseFirestore db;
    private SearchView searchView;
    private TextView tvCommunityImpact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_home);

        db = FirebaseFirestore.getInstance();
        rvFoodList = findViewById(R.id.rvFoodList);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        searchView = findViewById(R.id.searchView);
        tvCommunityImpact = findViewById(R.id.tvCommunityImpact);

        adapter = new FoodAdapter(foodItems);
        rvFoodList.setLayoutManager(new LinearLayoutManager(this));
        rvFoodList.setAdapter(adapter);

        findViewById(R.id.fabChat).setOnClickListener(v -> {
            startActivity(new Intent(this, ChatAssistantActivity.class));
        });

        setupBottomNavigation(bottomNav);
        setupSearch();
        loadFoodItems();
        updateCommunityImpact();
    }

    private void updateCommunityImpact() {
        // Sample happy stat for demo
        tvCommunityImpact.setText("🌟 Great news! Together we shared 45kg of food in your city today!");
    }

    private void setupSearch() {
        searchView.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            String query = searchView.getText().toString();
            filterList(query);
            searchView.hide();
            return false;
        });
    }

    private void filterList(String text) {
        foodItems.clear();
        if (text.isEmpty()) {
            foodItems.addAll(fullList);
        } else {
            String query = text.toLowerCase();
            for (Food item : fullList) {
                if (item.foodName.toLowerCase().contains(query) || 
                    item.city.toLowerCase().contains(query)) {
                    foodItems.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void setupBottomNavigation(BottomNavigationView bottomNav) {
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_post) {
                startActivity(new Intent(this, PostFoodStep1Activity.class));
            } else if (id == R.id.nav_listings) {
                startActivity(new Intent(this, MyListingsActivity.class));
            } else if (id == R.id.nav_impact) {
                startActivity(new Intent(this, ImpactDashboardActivity.class));
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            }
            return true;
        });
    }

    private void loadFoodItems() {
        db.collection("foods")
                .whereEqualTo("status", "available")
                .addSnapshotListener((value, error) -> {
                    fullList.clear();
                    if (value != null && !value.isEmpty()) {
                        fullList.addAll(value.toObjects(Food.class));
                    }
                    
                    if (fullList.isEmpty()) {
                        addWelcomeSamples();
                    }
                    
                    filterList(""); 
                });
    }

    private void addWelcomeSamples() {
        if (fullList.size() < 2) {
            fullList.add(new Food("sample1", "S1", "Fresh Mart", "Assorted Fruits", "Fruits", "10", "kg", 
                    System.currentTimeMillis() + 86400000, "Anna Nagar", "Chennai", "", "available", System.currentTimeMillis()));
            fullList.add(new Food("sample2", "S2", "NGO Partner", "Rice & Dal Packets", "Cooked Food", "25", "Packets", 
                    System.currentTimeMillis() + 3600000, "Indiranagar", "Bangalore", "", "available", System.currentTimeMillis()));
            fullList.add(new Food("sample3", "S3", "Healthy Bites", "Vegetable Salad", "Salad", "5", "Portions", 
                    System.currentTimeMillis() + 7200000, "Banjara Hills", "Hyderabad", "", "available", System.currentTimeMillis()));
        }
    }

    private class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {
        private final List<Food> items;
        FoodAdapter(List<Food> items) { this.items = items; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_listing, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Food item = items.get(position);
            holder.textName.setText(item.foodName);
            
            String emoji = "🍲";
            if (item.category != null) {
                if (item.category.contains("Rice")) emoji = "🍚";
                else if (item.category.contains("Bread") || item.category.contains("Bakery")) emoji = "🍞";
                else if (item.category.contains("Curry")) emoji = "🥘";
                else if (item.category.contains("Fruit")) emoji = "🍎";
                else if (item.category.contains("Salad")) emoji = "🥗";
            }
            holder.textEmoji.setText(emoji);
            holder.textDonor.setText("Donor: " + item.donorName + " | " + item.city);
            holder.textQty.setText("Qty: " + item.quantity + " " + item.unit);
            
            long hoursLeft = (item.expiryTime - System.currentTimeMillis()) / (1000 * 60 * 60);
            String expiryText = "Smart Alert: " + Math.max(1, hoursLeft) + "h left";
            holder.textExpiry.setText(expiryText);

            // Dynamic color logic for "Urgency Intelligence"
            if (hoursLeft <= 2) {
                holder.textExpiry.setBackgroundColor(Color.parseColor("#FEE2E2"));
                holder.textExpiry.setTextColor(Color.parseColor("#B91C1C"));
            } else if (hoursLeft <= 6) {
                holder.textExpiry.setBackgroundColor(Color.parseColor("#FFF3E0"));
                holder.textExpiry.setTextColor(Color.parseColor("#E65100"));
            } else {
                holder.textExpiry.setBackgroundColor(Color.parseColor("#F0FDF4"));
                holder.textExpiry.setTextColor(Color.parseColor("#166534"));
            }
            
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(DonorHomeActivity.this, FoodDetailActivity.class);
                intent.putExtra("food_id", item.id);
                intent.putExtra("food_name", item.foodName);
                intent.putExtra("address", item.location);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() { return items.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textName, textEmoji, textDonor, textQty, textExpiry;
            ViewHolder(View itemView) {
                super(itemView);
                textName = itemView.findViewById(R.id.textFoodName);
                textEmoji = itemView.findViewById(R.id.textFoodEmoji);
                textDonor = itemView.findViewById(R.id.textDonorInfo);
                textQty = itemView.findViewById(R.id.textQuantity);
                textExpiry = itemView.findViewById(R.id.textExpiry);
            }
        }
    }
}
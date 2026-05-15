package com.foodshare.foodshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class DonorHomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_home);

        RecyclerView rvFoodList = findViewById(R.id.rvFoodList);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        List<FoodListing> foodItems = new ArrayList<>();
        foodItems.add(new FoodListing("Excess Biryani", "🍚", "Rahul", "T Nagar", "5 kg", "1.2 km", "2h"));
        foodItems.add(new FoodListing("Fresh Bread", "🍞", "Annapurna", "Adyar", "10 Loaves", "3.5 km", "5h"));
        foodItems.add(new FoodListing("Vegetable Curry", "🥘", "Green Kitchen", "Velachery", "2 Liters", "2.1 km", "3h"));
        foodItems.add(new FoodListing("Apples", "🍎", "Fresh Mart", "Anna Nagar", "3 kg", "0.5 km", "12h"));
        foodItems.add(new FoodListing("Fruit Salad", "🥗", "Diet Hub", "Mylapore", "1.5 kg", "4.0 km", "1h"));

        FoodAdapter adapter = new FoodAdapter(foodItems);
        rvFoodList.setLayoutManager(new LinearLayoutManager(this));
        rvFoodList.setAdapter(adapter);

        findViewById(R.id.fabChat).setOnClickListener(v -> {
            startActivity(new Intent(this, ChatAssistantActivity.class));
        });

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

    private static class FoodListing {
        String name, emoji, donor, area, qty, distance, expiry;
        FoodListing(String name, String emoji, String donor, String area, String qty, String distance, String expiry) {
            this.name = name;
            this.emoji = emoji;
            this.donor = donor;
            this.area = area;
            this.qty = qty;
            this.distance = distance;
            this.expiry = expiry;
        }
    }

    private class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {
        private final List<FoodListing> items;

        FoodAdapter(List<FoodListing> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_listing, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            FoodListing item = items.get(position);
            holder.textName.setText(item.name);
            holder.textEmoji.setText(item.emoji);
            holder.textDonor.setText("Donor: " + item.donor + " | " + item.area);
            holder.textQty.setText("Qty: " + item.qty);
            holder.textDist.setText(item.distance);
            holder.textExpiry.setText("Expires in " + item.expiry);
            
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(DonorHomeActivity.this, FoodDetailActivity.class);
                intent.putExtra("food_name", item.name);
                intent.putExtra("donor_info", "Donor: " + item.donor + " | " + item.area);
                intent.putExtra("address", "123, Street Name, " + item.area + ", Chennai");
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textName, textEmoji, textDonor, textQty, textDist, textExpiry;

            ViewHolder(View itemView) {
                super(itemView);
                textName = itemView.findViewById(R.id.textFoodName);
                textEmoji = itemView.findViewById(R.id.textFoodEmoji);
                textDonor = itemView.findViewById(R.id.textDonorInfo);
                textQty = itemView.findViewById(R.id.textQuantity);
                textDist = itemView.findViewById(R.id.textDistance);
                textExpiry = itemView.findViewById(R.id.textExpiry);
            }
        }
    }
}
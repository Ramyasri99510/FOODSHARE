package com.foodshare.foodshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodshare.foodshare.models.Food;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ReceiverBrowseActivity extends BaseActivity {

    private RecyclerView rvBrowse;
    private BrowseAdapter adapter;
    private List<Food> foodItems = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_browse);

        db = FirebaseFirestore.getInstance();
        rvBrowse = findViewById(R.id.rvBrowse);

        adapter = new BrowseAdapter(foodItems);
        rvBrowse.setLayoutManager(new LinearLayoutManager(this));
        rvBrowse.setAdapter(adapter);

        findViewById(R.id.fabChat).setOnClickListener(v -> {
            startActivity(new Intent(this, ChatAssistantActivity.class));
        });

        loadFoodItems();
    }

    private void loadFoodItems() {
        db.collection("foods")
                .whereEqualTo("status", "available")
                .addSnapshotListener((value, error) -> {
                    foodItems.clear();
                    if (value != null && !value.isEmpty()) {
                        foodItems.addAll(value.toObjects(Food.class));
                    }
                    
                    // Add sample items if list is empty for demo
                    if (foodItems.isEmpty()) {
                        addSampleItems();
                    }
                    
                    adapter.notifyDataSetChanged();
                });
    }

    private void addSampleItems() {
        foodItems.add(new Food("sample1", "S1", "Rahul", "Excess Biryani", "Cooked Rice", "5", "kg", 
                System.currentTimeMillis() + 14400000, "T Nagar, Chennai", "Chennai", "", "available", System.currentTimeMillis()));
        foodItems.add(new Food("sample2", "S2", "Annapurna", "Fresh Bread", "Bakery", "10", "Loaves", 
                System.currentTimeMillis() + 43200000, "Adyar, Chennai", "Chennai", "", "available", System.currentTimeMillis()));
        foodItems.add(new Food("sample3", "S3", "Healthy Bites", "Vegetable Salad", "Salad", "5", "Portions", 
                System.currentTimeMillis() + 7200000, "Banjara Hills", "Hyderabad", "", "available", System.currentTimeMillis()));
    }

    private class BrowseAdapter extends RecyclerView.Adapter<BrowseAdapter.ViewHolder> {
        private final List<Food> items;

        BrowseAdapter(List<Food> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receiver_food, parent, false);
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
            
            long hoursLeft = (item.expiryTime - System.currentTimeMillis()) / (1000 * 60 * 60);
            holder.textExpiry.setText("Expires in " + Math.max(1, hoursLeft) + "h");

            holder.btnClaim.setOnClickListener(v -> {
                Intent intent = new Intent(ReceiverBrowseActivity.this, FoodDetailActivity.class);
                intent.putExtra("food_id", item.id);
                intent.putExtra("food_name", item.foodName);
                intent.putExtra("address", item.location);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textName, textEmoji, textExpiry;
            Button btnClaim;

            ViewHolder(View itemView) {
                super(itemView);
                textName = itemView.findViewById(R.id.textFoodName);
                textEmoji = itemView.findViewById(R.id.textFoodEmoji);
                textExpiry = itemView.findViewById(R.id.textExpiry);
                btnClaim = itemView.findViewById(R.id.btnClaim);
            }
        }
    }
}
package com.foodshare.foodshare;

import android.content.Intent;
import android.graphics.Color;
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

public class NGOBrowseActivity extends BaseActivity {

    private RecyclerView rvNgoBrowse;
    private NgoBrowseAdapter adapter;
    private List<Food> bulkItems = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ngo_browse);

        db = FirebaseFirestore.getInstance();
        rvNgoBrowse = findViewById(R.id.rvNgoBrowse);

        adapter = new NgoBrowseAdapter(bulkItems);
        rvNgoBrowse.setLayoutManager(new LinearLayoutManager(this));
        rvNgoBrowse.setAdapter(adapter);

        loadBulkDonations();
    }

    private void loadBulkDonations() {
        db.collection("foods")
                .whereEqualTo("status", "available")
                .addSnapshotListener((value, error) -> {
                    bulkItems.clear();
                    if (value != null && !value.isEmpty()) {
                        bulkItems.addAll(value.toObjects(Food.class));
                    }
                    
                    if (bulkItems.isEmpty()) {
                        addNgoSamples();
                    }
                    
                    adapter.notifyDataSetChanged();
                });
    }

    private void addNgoSamples() {
        bulkItems.add(new Food("bulk1", "B1", "Hotel Grand", "Dinner Buffet Surplus", "Cooked Food", "45", "kg", 
                System.currentTimeMillis() + 10800000, "Mylapore", "Chennai", "", "available", System.currentTimeMillis()));
        bulkItems.add(new Food("bulk2", "B2", "Royal Bakery", "Large Bread Batch", "Bakery", "20", "kg", 
                System.currentTimeMillis() + 43200000, "Whitefield", "Bangalore", "", "available", System.currentTimeMillis()));
    }

    private class NgoBrowseAdapter extends RecyclerView.Adapter<NgoBrowseAdapter.ViewHolder> {
        private final List<Food> items;
        NgoBrowseAdapter(List<Food> items) { this.items = items; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ngo_browse, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Food item = items.get(position);
            
            // UI Enhancement: Highlight High-Quantity items as "BULK"
            double qty = 0;
            try { qty = Double.parseDouble(item.quantity); } catch (Exception ignored) {}
            
            if (qty >= 20) {
                holder.textName.setText("📦 BULK: " + item.foodName);
                holder.textName.setTextColor(Color.parseColor("#1A6B3A"));
            } else {
                holder.textName.setText(item.foodName);
                holder.textName.setTextColor(Color.parseColor("#1A1A1A"));
            }

            holder.textInfo.setText("Location: " + item.city + " | Qty: " + item.quantity + " " + item.unit);
            
            holder.btnSchedule.setOnClickListener(v -> {
                Intent intent = new Intent(NGOBrowseActivity.this, NGOPickupScheduleActivity.class);
                intent.putExtra("food_id", item.id);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() { return items.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textName, textInfo;
            Button btnSchedule;
            ViewHolder(View itemView) {
                super(itemView);
                textName = itemView.findViewById(R.id.textFoodName);
                textInfo = itemView.findViewById(R.id.textNgoInfo);
                btnSchedule = itemView.findViewById(R.id.btnSchedule);
            }
        }
    }
}
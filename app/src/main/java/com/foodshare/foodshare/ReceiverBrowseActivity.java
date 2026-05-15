package com.foodshare.foodshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ReceiverBrowseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_browse);

        RecyclerView rvBrowse = findViewById(R.id.rvBrowse);

        List<String> items = new ArrayList<>();
        items.add("Fresh Chapatis - 20 pcs");
        items.add("Milk - 2 Liters");

        BrowseAdapter adapter = new BrowseAdapter(items);
        rvBrowse.setLayoutManager(new LinearLayoutManager(this));
        rvBrowse.setAdapter(adapter);

        findViewById(R.id.fabChat).setOnClickListener(v -> {
            startActivity(new Intent(this, ChatAssistantActivity.class));
        });
    }

    private class BrowseAdapter extends RecyclerView.Adapter<BrowseAdapter.ViewHolder> {
        private final List<String> items;

        BrowseAdapter(List<String> items) {
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
            holder.textName.setText(items.get(position));
            holder.btnClaim.setOnClickListener(v -> {
                startActivity(new Intent(ReceiverBrowseActivity.this, FoodDetailActivity.class));
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textName;
            Button btnClaim;

            ViewHolder(View itemView) {
                super(itemView);
                textName = itemView.findViewById(R.id.textFoodName);
                btnClaim = itemView.findViewById(R.id.btnClaim);
            }
        }
    }
}
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MyListingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_listings);

        RecyclerView rvMyListings = findViewById(R.id.rvMyListings);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        List<String> myItems = new ArrayList<>();
        myItems.add("Paneer Butter Masala - 2kg");
        myItems.add("Mixed Fruit Basket");

        ListingAdapter adapter = new ListingAdapter(myItems);
        rvMyListings.setLayoutManager(new LinearLayoutManager(this));
        rvMyListings.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, PostFoodStep1Activity.class));
        });
    }

    private class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ViewHolder> {
        private final List<String> items;

        ListingAdapter(List<String> items) {
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
            holder.textName.setText(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textName;

            ViewHolder(View itemView) {
                super(itemView);
                textName = itemView.findViewById(R.id.textFoodName);
            }
        }
    }
}
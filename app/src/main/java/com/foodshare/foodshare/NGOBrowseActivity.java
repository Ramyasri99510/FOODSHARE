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

public class NGOBrowseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ngo_browse);

        RecyclerView rvNgoBrowse = findViewById(R.id.rvNgoBrowse);

        List<String> bulkItems = new ArrayList<>();
        bulkItems.add("Restaurant Surplus - 50kg");
        bulkItems.add("Wedding Feast - 100kg");

        NgoBrowseAdapter adapter = new NgoBrowseAdapter(bulkItems);
        rvNgoBrowse.setLayoutManager(new LinearLayoutManager(this));
        rvNgoBrowse.setAdapter(adapter);
    }

    private class NgoBrowseAdapter extends RecyclerView.Adapter<NgoBrowseAdapter.ViewHolder> {
        private final List<String> items;

        NgoBrowseAdapter(List<String> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ngo_browse, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.textName.setText(items.get(position));
            holder.btnSchedule.setOnClickListener(v -> {
                startActivity(new Intent(NGOBrowseActivity.this, NGOPickupScheduleActivity.class));
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textName;
            Button btnSchedule;

            ViewHolder(View itemView) {
                super(itemView);
                textName = itemView.findViewById(R.id.textFoodName);
                btnSchedule = itemView.findViewById(R.id.btnSchedule);
            }
        }
    }
}
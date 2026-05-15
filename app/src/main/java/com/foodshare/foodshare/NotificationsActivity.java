package com.foodshare.foodshare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        RecyclerView rvNotifications = findViewById(R.id.rvNotifications);

        List<NotificationItem> notifications = new ArrayList<>();
        notifications.add(new NotificationItem("Urgent", "Your Paneer Masala is expiring in 30 mins!", "Red"));
        notifications.add(new NotificationItem("New Claim", "Rahul has claimed your Biryani donation.", "Green"));
        notifications.add(new NotificationItem("Update", "NGO Pickup scheduled for tomorrow 10 AM.", "Blue"));

        NotificationAdapter adapter = new NotificationAdapter(notifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(adapter);
    }

    private static class NotificationItem {
        String title, message, color;
        NotificationItem(String title, String message, String color) {
            this.title = title;
            this.message = message;
            this.color = color;
        }
    }

    private class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
        private final List<NotificationItem> items;

        NotificationAdapter(List<NotificationItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            NotificationItem item = items.get(position);
            holder.textTitle.setText(item.title);
            holder.textMessage.setText(item.message);
            
            int color = 0;
            if (item.color.equals("Red")) color = getResources().getColor(R.color.danger);
            else if (item.color.equals("Green")) color = getResources().getColor(R.color.primary);
            else color = getResources().getColor(R.color.secondary);
            
            holder.border.setBackgroundColor(color);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textTitle, textMessage;
            View border;

            ViewHolder(View itemView) {
                super(itemView);
                textTitle = itemView.findViewById(R.id.textNotifTitle);
                textMessage = itemView.findViewById(R.id.textNotifMessage);
                border = itemView.findViewById(R.id.viewBorder);
            }
        }
    }
}
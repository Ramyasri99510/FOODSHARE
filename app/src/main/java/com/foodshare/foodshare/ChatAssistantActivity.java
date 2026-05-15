package com.foodshare.foodshare;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatAssistantActivity extends BaseActivity {

    private RecyclerView rvChat;
    private ChatAdapter adapter;
    private final List<ChatMessage> messages = new ArrayList<>();
    private EditText etMessage;
    private ChipGroup cgSuggestions;
    private GenerativeModelFutures model;
    
    // Set your API key here for Gemini to work. 
    // If left as "YOUR_API_KEY", the app will use built-in smart responses for demonstration.
    private static final String GEMINI_API_KEY = "YOUR_API_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_assistant);

        if (!GEMINI_API_KEY.equals("YOUR_API_KEY")) {
            GenerativeModel gm = new GenerativeModel("gemini-pro", GEMINI_API_KEY);
            model = GenerativeModelFutures.from(gm);
        }

        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        FloatingActionButton btnSend = findViewById(R.id.btnSend);
        cgSuggestions = findViewById(R.id.cgSuggestions);

        adapter = new ChatAdapter(messages);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);

        findViewById(R.id.toolbar).setOnClickListener(v -> finish());

        btnSend.setOnClickListener(v -> {
            String msg = etMessage.getText().toString().trim();
            if (!msg.isEmpty()) {
                sendMessage(msg);
            }
        });

        setupSuggestions();

        addMessage(new ChatMessage("Hello! I am FoodShare Assistant. How can I help you today?", false));
    }

    private void setupSuggestions() {
        for (int i = 0; i < cgSuggestions.getChildCount(); i++) {
            Chip chip = (Chip) cgSuggestions.getChildAt(i);
            chip.setOnClickListener(v -> sendMessage(chip.getText().toString()));
        }
    }

    private void sendMessage(String text) {
        addMessage(new ChatMessage(text, true));
        etMessage.setText("");

        final ChatMessage typingMsg = new ChatMessage("Thinking...", false);
        addMessage(typingMsg);

        if (model != null) {
            // Real Gemini Logic
            Content content = new Content.Builder()
                    .addText("You are FoodShare Assistant for the FoodShare app in India. " +
                            "User Question: " + text + "\n" +
                            "Respond helpfully and briefly. Respond in the same language as the user.")
                    .build();

            Executor executor = Executors.newSingleThreadExecutor();
            ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    runOnUiThread(() -> {
                        messages.remove(typingMsg);
                        addMessage(new ChatMessage(result.getText(), false));
                    });
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    runOnUiThread(() -> {
                        messages.remove(typingMsg);
                        addMessage(new ChatMessage("Error connecting to Gemini. Using local help instead: " + getSmartResponse(text), false));
                    });
                }
            }, executor);
        } else {
            // Smart Local Logic for Demo
            rvChat.postDelayed(() -> {
                messages.remove(typingMsg);
                addMessage(new ChatMessage(getSmartResponse(text), false));
            }, 1000);
        }
    }

    private String getSmartResponse(String input) {
        String query = input.toLowerCase();
        if (query.contains("donate")) return "To donate, tap 'Post Food' in your menu. Upload a photo, select the type (Rice, Curry, etc.), and follow the AI expiry suggestion. It's that easy!";
        if (query.contains("expiry") || query.contains("hours")) return "Our AI model suggests expiry times based on food science. For example, cooked food stays fresh for 4-6 hours, while fruits can last 72 hours.";
        if (query.contains("claim")) return "Receivers can browse the 'Home' screen to find nearby food. Just tap 'Claim' to see the pickup location and donor details.";
        if (query.contains("ngo")) return "NGOs can register during signup. They get access to bulk donations from restaurants and wedding halls!";
        if (query.contains("safety")) return "Always ensure food is stored in clean containers. Our AI expiry predictor helps you stay within safe consumption windows.";
        return "I can help you with donations, claiming food, or understanding our AI features. Just ask!";
    }

    private void addMessage(ChatMessage msg) {
        messages.add(msg);
        adapter.notifyDataSetChanged();
        rvChat.scrollToPosition(messages.size() - 1);
    }

    private static class ChatMessage {
        String text;
        boolean isUser;
        ChatMessage(String text, boolean isUser) {
            this.text = text;
            this.isUser = isUser;
        }
    }

    private class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
        private final List<ChatMessage> items;
        ChatAdapter(List<ChatMessage> items) { this.items = items; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ChatMessage msg = items.get(position);
            holder.textMessage.setText(msg.text);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.card.getLayoutParams();
            if (msg.isUser) {
                params.gravity = Gravity.END;
                holder.card.setCardBackgroundColor(getResources().getColor(R.color.primary, null));
                holder.textMessage.setTextColor(getResources().getColor(R.color.white, null));
            } else {
                params.gravity = Gravity.START;
                holder.card.setCardBackgroundColor(getResources().getColor(R.color.gray_light, null));
                holder.textMessage.setTextColor(getResources().getColor(R.color.text_primary, null));
            }
            holder.card.setLayoutParams(params);
        }

        @Override
        public int getItemCount() { return items.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textMessage;
            MaterialCardView card;
            ViewHolder(View itemView) {
                super(itemView);
                textMessage = itemView.findViewById(R.id.textMessage);
                card = itemView.findViewById(R.id.cardMessage);
            }
        }
    }
}
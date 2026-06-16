package com.foodshare.foodshare;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatAssistantActivity extends BaseActivity {

    private RecyclerView rvChat;
    private ChatAdapter adapter;
    private final List<ChatMessage> messages = new ArrayList<>();
    private EditText etMessage;
    private ChipGroup cgSuggestions;
    private GenerativeModelFutures model;
    private TextToSpeech tts;
    
    private static final String GEMINI_API_KEY = "YOUR_API_KEY";

    private final ActivityResultLauncher<Intent> voiceLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    ArrayList<String> voiceResults = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (voiceResults != null && !voiceResults.isEmpty()) {
                        sendMessage(voiceResults.get(0));
                    }
                }
            }
    );

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
        FloatingActionButton btnMic = findViewById(R.id.btnMic);
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

        btnMic.setOnClickListener(v -> startVoiceInput());

        setupSuggestions();

        tts = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.getDefault());
            }
        });

        addMessage(new ChatMessage("Hello! I am your FoodShare AI Assistant. Unlike foreign apps, we are built specifically for India and are 100% free. How can I help you share surplus food today?", false));
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your question...");
        try {
            voiceLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Voice recognition not supported on this device.", Toast.LENGTH_SHORT).show();
        }
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
            Content content = new Content.Builder()
                    .addText("You are FoodShare Assistant, a specialized AI for a free food donation app in India. " +
                            "IMPORTANT: Emphasize that we have a Smart Expiry engine that foreign apps like OLIO don't have. " +
                            "Mention that we support NGOs with bulk pickup tools. " +
                            "Always be friendly, brief, and professional. Respond in the same language as the user. " +
                            "User Question: " + text)
                    .build();

            Executor executor = Executors.newSingleThreadExecutor();
            ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    runOnUiThread(() -> {
                        messages.remove(typingMsg);
                        String aiReply = result.getText();
                        addMessage(new ChatMessage(aiReply, false));
                        speak(aiReply);
                    });
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    runOnUiThread(() -> {
                        messages.remove(typingMsg);
                        String fallback = getSmartResponse(text);
                        addMessage(new ChatMessage(fallback, false));
                        speak(fallback);
                    });
                }
            }, executor);
        } else {
            rvChat.postDelayed(() -> {
                messages.remove(typingMsg);
                String localResponse = getSmartResponse(text);
                addMessage(new ChatMessage(localResponse, false));
                speak(localResponse);
            }, 1000);
        }
    }

    private String getSmartResponse(String input) {
        String query = input.toLowerCase();
        if (query.contains("donate")) return "To donate, tap '+' in the menu. Pick the food type, and our AI will suggest an expiry time to ensure it reaches someone before spoiling!";
        if (query.contains("ngo")) return "NGOs get a special dashboard to manage bulk pickups from hotels and events. We are the first app in India to offer this!";
        if (query.contains("free")) return "FoodShare is 100% free for everyone. There are no fees or hidden costs, unlike paid surplus apps.";
        if (query.contains("expiry")) return "Our AI Smart Expiry engine notifies receivers exactly 2 hours before food expires, making us much faster and safer than other apps.";
        return "I can help with donations, NGO support, or explain our Smart Expiry system. What's on your mind?";
    }

    private void speak(String text) {
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void addMessage(ChatMessage msg) {
        messages.add(msg);
        adapter.notifyItemInserted(messages.size() - 1);
        rvChat.scrollToPosition(messages.size() - 1);
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
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
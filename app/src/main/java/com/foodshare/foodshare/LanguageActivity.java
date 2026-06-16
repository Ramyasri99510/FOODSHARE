package com.foodshare.foodshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class LanguageActivity extends BaseActivity {

    private int selectedPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        RecyclerView rvLanguages = findViewById(R.id.rvLanguages);
        Button btnContinue = findViewById(R.id.btnContinue);

        List<LanguageModel> languages = new ArrayList<>();
        languages.add(new LanguageModel("English", "English", "en"));
        languages.add(new LanguageModel("हिंदी", "Hindi", "hi"));
        languages.add(new LanguageModel("தமிழ்", "Tamil", "ta"));
        languages.add(new LanguageModel("తెలుగు", "Telugu", "te"));
        languages.add(new LanguageModel("ಕನ್ನಡ", "Kannada", "kn"));
        languages.add(new LanguageModel("മലയാളം", "Malayalam", "ml"));

        LanguageAdapter adapter = new LanguageAdapter(languages);
        rvLanguages.setLayoutManager(new GridLayoutManager(this, 2));
        rvLanguages.setAdapter(adapter);

        btnContinue.setOnClickListener(v -> {
            LanguageModel selected = languages.get(selectedPosition);
            LocaleHelper.setLocale(this, selected.isoCode);
            startActivity(new Intent(LanguageActivity.this, LocationActivity.class));
            finish();
        });
    }

    private static class LanguageModel {
        String nativeName, englishName, isoCode;
        LanguageModel(String nativeName, String englishName, String isoCode) {
            this.nativeName = nativeName;
            this.englishName = englishName;
            this.isoCode = isoCode;
        }
    }

    private class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder> {
        private final List<LanguageModel> languages;

        LanguageAdapter(List<LanguageModel> languages) {
            this.languages = languages;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            LanguageModel language = languages.get(position);
            holder.textNative.setText(language.nativeName);
            holder.textEnglish.setText(language.englishName);

            if (position == selectedPosition) {
                holder.card.setStrokeColor(getResources().getColor(R.color.primary, null));
                holder.card.setStrokeWidth(4);
            } else {
                holder.card.setStrokeColor(getResources().getColor(R.color.gray_light, null));
                holder.card.setStrokeWidth(2);
            }

            holder.itemView.setOnClickListener(v -> {
                int previousSelected = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);
            });
        }

        @Override
        public int getItemCount() {
            return languages.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textNative, textEnglish;
            MaterialCardView card;

            ViewHolder(View itemView) {
                super(itemView);
                textNative = itemView.findViewById(R.id.textNative);
                textEnglish = itemView.findViewById(R.id.textEnglish);
                card = itemView.findViewById(R.id.cardLanguage);
            }
        }
    }
}
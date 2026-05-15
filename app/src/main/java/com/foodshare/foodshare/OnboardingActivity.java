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
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

public class OnboardingActivity extends BaseActivity {

    private ViewPager2 viewPager;
    private Button btnNext, btnSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        btnNext = findViewById(R.id.btnNext);
        btnSkip = findViewById(R.id.btnSkip);

        OnboardingAdapter adapter = new OnboardingAdapter();
        viewPager.setAdapter(adapter);

        btnNext.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < 2) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                navigateToLanguage();
            }
        });

        btnSkip.setOnClickListener(v -> navigateToLanguage());

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    btnNext.setText("Get Started");
                } else {
                    btnNext.setText("Next");
                }
            }
        });
    }

    private void navigateToLanguage() {
        Intent intent = new Intent(OnboardingActivity.this, LanguageActivity.class);
        startActivity(intent);
        finish();
    }

    private class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.ViewHolder> {

        private final String[] titles = {"Reduce Food Waste", "Easy Donation", "Track Impact"};
        private final String[] descriptions = {
                "Connect with nearby receivers and NGOs to ensure surplus food reaches those in need.",
                "Post your surplus food in just a few steps and get notified when it's claimed.",
                "See the real-world impact of your donations in terms of meals shared and CO2 saved."
        };

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_onboarding, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.title.setText(titles[position]);
            holder.description.setText(descriptions[position]);
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView title, description;

            ViewHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.textTitle);
                description = itemView.findViewById(R.id.textDescription);
            }
        }
    }
}
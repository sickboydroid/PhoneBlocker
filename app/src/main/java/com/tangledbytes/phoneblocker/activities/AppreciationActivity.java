package com.tangledbytes.phoneblocker.activities;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.tangledbytes.phoneblocker.R;
import com.tangledbytes.phoneblocker.utils.BlockerSession;

import java.util.Random;

public class AppreciationActivity extends AppCompatActivity {
    public static final String TAG = "AppreciationActivity";
    private InterstitialAd mInterstitialAd;
    private TextView tvProgressTitle;
    private Button btnFinish;
    private LinearProgressIndicator pbLoading;
    private final FullScreenContentCallback adCallback = new FullScreenContentCallback() {
        @Override
        public void onAdClicked() {
            // Called when a click is recorded for an ad.
            Log.d(TAG, "Ad was clicked.");
        }

        @Override
        public void onAdDismissedFullScreenContent() {
            // Called when ad is dismissed.
            Log.d(TAG, "Ad dismissed fullscreen content.");
            mInterstitialAd = null;
            finish();
        }

        @Override
        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
            // Called when ad fails to show.
            Log.e(TAG, "Ad failed to show fullscreen content.");
            Log.e(TAG, adError.toString());
            mInterstitialAd = null;
            finish();
        }

        @Override
        public void onAdImpression() {
            // Called when an impression is recorded for an ad.
            Log.d(TAG, "Ad recorded an impression.");
        }

        @Override
        public void onAdShowedFullScreenContent() {
            // Called when ad is shown.
            Log.d(TAG, "Ad showed fullscreen content.");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appreciation);
        setUpAds();
        setupViews();
        startLoading();
        new BlockerSession(this).setHasShownAppreciationActivity(true);
    }

    private void startLoading() {
        Handler handler = new Handler();
        Thread loader = new Thread(() -> {
            int progress = 0;
            while (progress <= 100) {
                try {
                    Thread.sleep(new Random().nextInt(150));
                    if (mInterstitialAd != null) progress += 10;
                    else progress += 2;
                    int finalProgress = progress;
                    handler.post(() -> pbLoading.setProgressCompat(finalProgress, true));
                    if (progress == 30)
                        updateProgressTitle(R.string.finishing_up, mInterstitialAd != null);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            updateProgressTitle(R.string.done, mInterstitialAd != null);
            btnFinish.post(() -> btnFinish.setVisibility(View.VISIBLE));
        });
        loader.start();
    }

    private void setUpAds() {
        AdRequest adRequest = new AdRequest.Builder().build();
        //TODO: Change unit ID in release version
        // Real ad unit ID: ca-app-pub-3970034979547566/8171811204
        // Test ad unit ID: ca-app-pub-3940256099942544/1033173712
        InterstitialAd.load(this, "ca-app-pub-3970034979547566/8171811204", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                interstitialAd.setFullScreenContentCallback(adCallback);
                mInterstitialAd = interstitialAd;
                Log.i(TAG, "Ad has been loaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.d(TAG, "Ad could not be loaded");
                Log.d(TAG, loadAdError.toString());
                mInterstitialAd = null;
            }
        });
    }

    private void setupViews() {
        pbLoading = findViewById(R.id.pb_loading);
        tvProgressTitle = findViewById(R.id.tv_progress_title);
        btnFinish = findViewById(R.id.btn_finish);
        btnFinish.setOnClickListener((View view) -> showAd());
    }

    private void showAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(AppreciationActivity.this);
            Log.d(TAG, "Ad was shown successfully");
            return;
        }
        finish();
    }

    private void updateProgressTitle(int resId, boolean fastAnimation) {
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        int animDuration = 600;
        if (fastAnimation) animDuration = 300;
        fadeOut.setDuration(animDuration);
        fadeIn.setDuration(animDuration);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tvProgressTitle.startAnimation(fadeIn);
                tvProgressTitle.setText(resId);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        tvProgressTitle.startAnimation(fadeOut);
    }

    @Override
    public void onBackPressed() {
    }
}
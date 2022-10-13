package com.sickboydroid.phoneblocker.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sickboydroid.phoneblocker.R;
import com.sickboydroid.phoneblocker.utils.BlockerSession;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appreciation);
        new BlockerSession(this).setHasShownAppreciationActivity(true);
    }

    private void setupViews() {

    }
}
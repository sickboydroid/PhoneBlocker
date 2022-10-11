package com.sickboydroid.phoneblocker.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.sickboydroid.phoneblocker.utils.AppPreferences;
import com.sickboydroid.phoneblocker.utils.BlockerSession;
import com.sickboydroid.phoneblocker.utils.Constants;
import com.sickboydroid.phoneblocker.R;
import com.sickboydroid.phoneblocker.services.BlockerService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private SwitchMaterial switchPreventPowerOff;
    private SwitchMaterial switchBlockNotifications;
    private SwitchMaterial switchBlockCalls;
    private Button btnLockDevice;
    private Button btnIncrementHours;
    private Button btnIncrementMinutes;
    private Button btnIncrementSeconds;
    private Button btnDecrementHours;
    private Button btnDecrementMinutes;
    private Button btnDecrementSeconds;
    private TextView tvDurationHours;
    private TextView tvDurationMinutes;
    private TextView tvDurationSeconds;
    private int durationHours = 1;
    private int durationMinutes = 30;
    private int durationSeconds = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpAds();
        notifyAboutAdminPermission();
        setUpViews();
    }

    private void setUpAds() {
        MobileAds.initialize(this, null);
        AdView bannerAd = findViewById(R.id.adview_banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        bannerAd.loadAd(adRequest);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpViews() {
        switchPreventPowerOff = findViewById(R.id.switch_prevent_power_off);
        switchBlockNotifications = findViewById(R.id.switch_block_notifications);
        switchBlockCalls = findViewById(R.id.switch_block_calls);
        btnIncrementHours = findViewById(R.id.btn_increment_hours);
        btnDecrementHours = findViewById(R.id.btn_decrement_hours);
        btnIncrementMinutes = findViewById(R.id.btn_increment_minutes);
        btnDecrementMinutes = findViewById(R.id.btn_decrement_minutes);
        btnIncrementSeconds = findViewById(R.id.btn_increment_seconds);
        btnDecrementSeconds = findViewById(R.id.btn_decrement_seconds);
        tvDurationHours = findViewById(R.id.tv_duration_hours);
        tvDurationMinutes = findViewById(R.id.tv_duration_minutes);
        tvDurationSeconds = findViewById(R.id.tv_duration_seconds);

//        btnIncrementMinutes.setOnTouchListener((View.OnTouchListener) (view, motionEvent) ->  {
//            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
//                case MotionEvent.ACTION_DOWN:
//
//                    break;
//                case MotionEvent.ACTION_UP:
//                    break;
//            }
//            return true;
//        });
        btnLockDevice = findViewById(R.id.btn_lock_device);
        btnIncrementHours.setOnClickListener(this);
        btnDecrementHours.setOnClickListener(this);
        btnIncrementMinutes.setOnClickListener(this);
        btnDecrementMinutes.setOnClickListener(this);
        btnIncrementSeconds.setOnClickListener(this);
        btnDecrementSeconds.setOnClickListener(this);
        btnLockDevice.setOnClickListener(view -> startBlocking());
        updateDurationTextViews();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_increment_hours) {
            durationHours++;
        } else if (id == R.id.btn_decrement_hours) {
            if (durationHours != 0) durationHours--;
        } else if (id == R.id.btn_increment_minutes) {
            durationMinutes += 5;
            if (durationMinutes == 60) {
                durationHours++;
                durationMinutes = 0;
            }
        } else if (id == R.id.btn_decrement_minutes) {
            if (durationMinutes != 0) durationMinutes -= 5;
        } else if (id == R.id.btn_increment_seconds) {
            durationSeconds += 10;
            if (durationSeconds == 60) durationSeconds = 0;
        } else if (id == R.id.btn_decrement_seconds) {
            if (durationSeconds != 0) durationSeconds -= 10;
        }
        updateDurationTextViews();
    }

    private void updateDurationTextViews() {
        tvDurationHours.setText(String.format(getString(R.string.x_hrs), durationHours));
        tvDurationMinutes.setText(String.format(getString(R.string.x_min), durationMinutes));
        tvDurationSeconds.setText(String.format(getString(R.string.x_secs), durationSeconds));
        btnLockDevice.setText(String.format(getString(R.string.lock_device_button_text), durationHours, durationMinutes, durationSeconds));
    }

    private void startBlocking() {
        // Convert duration to millis
        long durationMillis = durationHours * 60 * 60 * 1000L;
        durationMillis += durationMinutes * 60 * 1000L;
        durationMillis += durationSeconds * 1000L;

        Intent intentBlockerService = new Intent(this, BlockerService.class);
        BlockerSession session = new BlockerSession(this);
        session.createSession(durationMillis, switchPreventPowerOff.isChecked(), switchBlockCalls.isChecked(), switchBlockNotifications.isChecked());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(intentBlockerService);
        else startService(intentBlockerService);
    }

    private void notifyAboutAdminPermission() {
        if (!new AppPreferences(this).getBoolean(Constants.PREF_HAS_ADMIN_PERMISSION))
            Toast.makeText(this, "Device ADMIN Permission is denied", Toast.LENGTH_SHORT).show();
    }
}
package com.tangledbytes.phoneblocker.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tangledbytes.phoneblocker.R;
import com.tangledbytes.phoneblocker.dialogs.AboutDialog;
import com.tangledbytes.phoneblocker.services.BlockerService;
import com.tangledbytes.phoneblocker.utils.AppPreferences;
import com.tangledbytes.phoneblocker.utils.BlockerSession;
import com.tangledbytes.phoneblocker.utils.Constants;
import com.tangledbytes.phoneblocker.utils.Utils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private SwitchCompat switchPreventPowerOff;
    private SwitchCompat switchBlockNotifications;
    private SwitchCompat switchBlockCalls;
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
    //TODO: set time to hrs = 1, mins = 30, seconds = 0
    private int durationHours = 0;
    private int durationMinutes = 0;
    private int durationSeconds = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (shouldShowIntro()) {
            Intent intentAppIntro = new Intent(this, AppIntroActivity.class);
            startActivity(intentAppIntro);
            finish();
        } else if (!Utils.hasBootPermission(this) || !Utils.hasDeviceAdminPermission(this)) {
            Intent intentAppIntro = new Intent(this, AppIntroActivity.class);
            intentAppIntro.putExtra(Constants.EXTRA_REQUEST_ONLY_PERMISSIONS, true);
            startActivity(intentAppIntro);
            finish();
        }
        setContentView(R.layout.activity_main);
        setUpToolbar();
        setUpAds();
        setUpViews();
    }

    private boolean shouldShowIntro() {
        return !new AppPreferences(this).getBoolean(Constants.PREF_HAS_SEEN_APP_INTRO, false);
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.activity_toolbar);
        toolbar.setSubtitle(R.string.app_name);
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setOnMenuItemClickListener((MenuItem item) -> {
            if (item.getItemId() == R.id.menu_about)
                showAboutDialog();
            return true;
        });
    }

    private void setUpAds() {
        AdView bannerAd = findViewById(R.id.adview_banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        bannerAd.loadAd(adRequest);
    }

    private void showAboutDialog() {
        startActivity(new Intent(this, AboutDialog.class));
    }

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


        // TODO: Add touch listener to increase duration on holding down the button
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
            if (durationHours == 16)
                Toast.makeText(this, R.string.hour_limit_exceeded, Toast.LENGTH_SHORT).show();
            durationHours++;
        } else if (id == R.id.btn_decrement_hours) {
            if (durationHours != 0) durationHours--;
        } else if (id == R.id.btn_increment_minutes) {
            durationMinutes += 5;
            if (durationMinutes == 60)
                durationMinutes = 0;
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
}
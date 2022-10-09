package com.sickboydroid.phoneblocker;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.sickboydroid.phoneblocker.services.BlockerService;

public class MainActivity extends AppCompatActivity {
    private SwitchMaterial switchPreventPowerOff;
    private SwitchMaterial switchBlockNotifications;
    private SwitchMaterial switchBlockCalls;
    private Button btnLockDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notifyAboutAdminPermission();
        setUpViews();
    }

    private void setUpViews() {
        switchPreventPowerOff = findViewById(R.id.switch_prevent_power_off);
        switchBlockNotifications = findViewById(R.id.switch_block_notifications);
        switchBlockCalls = findViewById(R.id.switch_block_calls);
        btnLockDevice = findViewById(R.id.btn_lock_device);
        btnLockDevice.setOnClickListener(view -> lockDevice());
    }

    private void lockDevice() {
        Intent intentBlockerService = new Intent(this, BlockerService.class);
        intentBlockerService.putExtra(Constants.EXTRA_PREVENT_POWER_OFF, switchPreventPowerOff.isChecked());
        intentBlockerService.putExtra(Constants.EXTRA_BLOCK_CALLS, switchBlockCalls.isChecked());
        intentBlockerService.putExtra(Constants.EXTRA_BLOCK_NOTIFICATIONS, switchBlockCalls.isChecked());
        //TODO: Pass proper value
        intentBlockerService.putExtra(Constants.EXTRA_COUNTDOWN_DURATION, 50_000L);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(intentBlockerService);
        else
            startService(intentBlockerService);
    }

    private void notifyAboutAdminPermission() {
        if (!new AppPreferences(this).getBoolean(Constants.PREF_HAS_ADMIN_PERMISSION))
            Toast.makeText(this, "Device ADMIN Permission is denied", Toast.LENGTH_SHORT).show();
    }
}
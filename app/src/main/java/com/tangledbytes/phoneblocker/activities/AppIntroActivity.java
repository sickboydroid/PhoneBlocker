package com.tangledbytes.phoneblocker.activities;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroCustomLayoutFragment;
import com.github.appintro.AppIntroFragment;
import com.tangledbytes.phoneblocker.R;
import com.tangledbytes.phoneblocker.receivers.AdminReceiver;
import com.tangledbytes.phoneblocker.utils.AppPreferences;
import com.tangledbytes.phoneblocker.utils.Constants;
import com.tangledbytes.phoneblocker.utils.Utils;

public class AppIntroActivity extends AppIntro {
    private static final String TAG = "AppIntroActivity";
    AppIntroCustomLayoutFragment permissionFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpActivity();
        if (!getIntent().getBooleanExtra(Constants.EXTRA_REQUEST_ONLY_PERMISSIONS, false))
            addIntroductorySlides();
        else
            addPermissionRequestSlide();
    }

    public void setUpActivity() {
        setButtonsEnabled(true);
        showStatusBar(true);
        setSystemBackButtonLocked(true);
        setWizardMode(true);
    }

    private void addPermissionRequestSlide() {
        addSlide(AppIntroFragment.createInstance("Permissions revoked",
                "Press next to take the necessary action",
                R.drawable.lock,
                R.color.materialDark));
        permissionFragment = AppIntroCustomLayoutFragment.newInstance(R.layout.layout_permission_request);
        addSlide(permissionFragment);
    }

    private void addIntroductorySlides() {
        addSlide(AppIntroFragment.createInstance("Welcome to Phone Blocker",
                "Conquer your phone addiction!",
                R.drawable.thumbs_up,
                R.color.materialDark));
        addSlide(AppIntroFragment.createInstance(
                "How does this app help?",
                "With a single click, this app lets you to lock your device for hours so that you can spend your time in doing meaningful work",
                R.drawable.man,
                R.color.materialDark
        ));
        permissionFragment = AppIntroCustomLayoutFragment.newInstance(R.layout.layout_permission_request);
        addSlide(permissionFragment);
    }

    @Override
    protected void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        updatePermissionButtons();
    }

    private void updatePermissionButtons() {
        Button btnGrantAdminPermission = findViewById(R.id.btn_grant_admin_permission);
        Button btnGrantBootPermission = findViewById(R.id.btn_grant_boot_permission);
        if(btnGrantAdminPermission == null || btnGrantBootPermission == null)
            // Current slide might not be the slide containing these buttons
            return;
        btnGrantAdminPermission.setOnClickListener((View view) -> {
            ComponentName componentDeviceAdmin = new ComponentName(this, AdminReceiver.class);
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentDeviceAdmin);
            startActivity(intent);
        });
        btnGrantBootPermission.setOnClickListener((View view) -> {
        });
        if (Utils.hasDeviceAdminPermission(this) && btnGrantAdminPermission.isEnabled()) {
            btnGrantAdminPermission.setEnabled(false);
            btnGrantAdminPermission.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green_80)));
            btnGrantAdminPermission.setText(R.string.granted);
        } else if (!Utils.hasDeviceAdminPermission(this) && !btnGrantAdminPermission.isEnabled()) {
            btnGrantAdminPermission.setEnabled(true);
            btnGrantAdminPermission.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red_80)));
            btnGrantAdminPermission.setText(R.string.grant);
        }

        if (Utils.hasBootPermission(this) && btnGrantBootPermission.isEnabled()) {
            btnGrantBootPermission.setEnabled(false);
            btnGrantBootPermission.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green_80)));
            btnGrantBootPermission.setText(R.string.granted);
        } else if (!Utils.hasBootPermission(this) && !btnGrantBootPermission.isEnabled()) {
            btnGrantBootPermission.setEnabled(true);
            btnGrantBootPermission.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red_80)));
            btnGrantBootPermission.setText(R.string.grant);
        }

        if (Utils.hasBootPermission(this) && Utils.hasDeviceAdminPermission(this))
            setButtonsEnabled(true);
        else
            setButtonsEnabled(false);
    }

    @Override
    protected void onDonePressed(@Nullable Fragment currentFragment) {
        new AppPreferences(this).changePreference(Constants.PREF_HAS_SEEN_APP_INTRO, true);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePermissionButtons();
    }
}
package com.sickboydroid.phoneblocker.admin;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;

import androidx.annotation.NonNull;

import com.sickboydroid.phoneblocker.AppPreferences;
import com.sickboydroid.phoneblocker.Constants;

public class AdminReceiver extends DeviceAdminReceiver {
    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        new AppPreferences(context).changePreference(Constants.PREF_HAS_ADMIN_PERMISSION, true);
    }

    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
        new AppPreferences(context).changePreference(Constants.PREF_HAS_ADMIN_PERMISSION, false);
    }
}

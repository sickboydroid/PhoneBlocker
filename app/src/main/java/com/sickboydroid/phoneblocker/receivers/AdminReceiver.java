package com.sickboydroid.phoneblocker.receivers;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.sickboydroid.phoneblocker.utils.AppPreferences;
import com.sickboydroid.phoneblocker.utils.Constants;

import java.io.File;
import java.io.IOException;

public class AdminReceiver extends DeviceAdminReceiver {
    private static final String TAG = "AdminReceiver";

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        showToast(context, intent.getAction());
        showToast(context, "Broadcast receiver");
    }

    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        new AppPreferences(context).changePreference(Constants.PREF_HAS_ADMIN_PERMISSION, true);
    }

    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
        new AppPreferences(context).changePreference(Constants.PREF_HAS_ADMIN_PERMISSION, false);
    }

    @Override
    public void onUserStarted(@NonNull Context context, @NonNull Intent intent, @NonNull UserHandle startedUser) {
        showToast(context, "User Started");
    }

    private void showToast(Context context, String msg) {
        Log.d(TAG, msg);
        File file = new File(context.getFilesDir(), msg + System.currentTimeMillis());
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPasswordFailed(@NonNull Context context, @NonNull Intent intent, @NonNull UserHandle user) {
        showToast(context, "password failed");
    }

    @Override
    public void onPasswordSucceeded(@NonNull Context context, @NonNull Intent intent, @NonNull UserHandle user) {
        showToast(context, "password succeeded");
    }

    @Override
    public void onLockTaskModeEntering(@NonNull Context context, @NonNull Intent intent, @NonNull String pkg) {
        showToast(context, "LOck Task mode entering");
    }
}

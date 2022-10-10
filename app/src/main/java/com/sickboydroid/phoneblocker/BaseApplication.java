package com.sickboydroid.phoneblocker;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
//        if(shouldShowInitialScreens()) {
//
//            return;
//        }
//
//        if(hasAdminPermission()) {
//            return;
//        }
//
//        if(hasOverlayPermission()) {
//            return;
//        }
//        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel(Constants.DEFAULT_NOTIFICATION_CHANNEL, getString(R.string.default_notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private boolean hasOverlayPermission() {
        return false;
    }

    private boolean hasAdminPermission() {
        return false;
    }

    private boolean shouldShowInitialScreens() {
        return false;
    }
}

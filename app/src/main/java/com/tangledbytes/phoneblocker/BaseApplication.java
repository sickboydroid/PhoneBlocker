package com.tangledbytes.phoneblocker;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.ads.MobileAds;
import com.tangledbytes.libtb.log.XLog;
import com.tangledbytes.phoneblocker.utils.Constants;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        XLog.initialize(getFilesDir());
        createNotificationChannel();
        MobileAds.initialize(this);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel(Constants.DEFAULT_NOTIFICATION_CHANNEL, getString(R.string.default_notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}

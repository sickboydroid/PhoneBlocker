package com.sickboydroid.phoneblocker.services;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.sickboydroid.phoneblocker.Constants;
import com.sickboydroid.phoneblocker.Countdown;
import com.sickboydroid.phoneblocker.R;

public class BlockerService extends Service {
    private final String TAG = "BlockerService";
    private final int NOTIFICATION_ID = 23;
    private boolean isCountdownComplete = false;
    private boolean isBlockerThreadRunning = false;
    private boolean preventPowerOff;
    private boolean blockNotifications;
    private boolean blockCalls;

    private final Runnable countdownCompletionRunnable = () -> {
        Log.d(TAG, "Countdown completed. Device can be unlocked!!");
        isCountdownComplete = true;
        stopSelf();
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        preventPowerOff = intent.getBooleanExtra(Constants.EXTRA_PREVENT_POWER_OFF, true);
        blockNotifications = intent.getBooleanExtra(Constants.EXTRA_BLOCK_NOTIFICATIONS, false);
        blockCalls = intent.getBooleanExtra(Constants.EXTRA_BLOCK_CALLS, false);
        long countdownDuration = intent.getLongExtra(Constants.EXTRA_COUNTDOWN_DURATION, 60_000L);
        showNotification();
        blockCallsAndNotifications();
        Countdown.startCountdown(new Handler(), countdownCompletionRunnable, countdownDuration);
        startBlockerThread();
        return START_REDELIVER_INTENT;
    }

    private void blockCallsAndNotifications() {
        if (blockCalls && blockNotifications)
            Log.d(TAG, "You forgot to implement call blocker and notifications");
    }

    public void startBlockerThread() {
        if (isBlockerThreadRunning) return;
        Blocker blocker = new Blocker(new Handler());
        blocker.start();
    }

    private void showNotification() {
        if (Build.VERSION.SDK_INT >= 26) {
            Notification build = new Notification.Builder(getApplicationContext(), Constants.NOTIFICATION_CHANNEL_NAME).setTicker(getString(R.string.admin_service_notif_ticker)).setSmallIcon(R.mipmap.ic_launcher_round).setContentTitle(getString(R.string.admin_service_notif_content_title)).setContentText(getString(R.string.admin_service_notif_content)).build();
            NotificationChannel notificationChannel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_NAME, getString(R.string.default_notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(NOTIFICATION_ID, build);
            startForeground(NOTIFICATION_ID, build);
        } else {
            startForeground(NOTIFICATION_ID, new Notification.Builder(getApplicationContext()).setTicker(getString(R.string.admin_service_notif_ticker)).setSmallIcon(R.mipmap.ic_launcher_round).setContentTitle(getString(R.string.admin_service_notif_content_title)).setContentText(getString(R.string.admin_service_notif_content)).setPriority(Notification.PRIORITY_HIGH).build());
        }
    }

    private class Blocker extends Thread {
        private PowerManager powerManager;
        private KeyguardManager keyguardManager;
        private DevicePolicyManager dpm;
        private Handler handler;

        public Blocker(Handler handler) {
            powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            this.handler = handler;
        }

        @Override
        public void run() {
            Log.i(TAG, "Blocker thread has started");
            isBlockerThreadRunning = true;
            while (!isCountdownComplete) {
                sleepThread(200);
                // Screen is off, just pass for now
                if (!powerManager.isInteractive()) {
                    sleepThread(500);
                    continue;
                }
                // Screen is on, prevent any system dialog (for preventing power off of phone)
                if (preventPowerOff)
                    handler.post(() -> sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)));
                // Screen is on and also device is unlocked so lock it
                if (!keyguardManager.isKeyguardLocked())
                    dpm.lockNow();
            }
            isBlockerThreadRunning = false;
            Log.i(TAG, "Blocker thread has ended");
        }

        private void sleepThread(long delayMillis) {
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

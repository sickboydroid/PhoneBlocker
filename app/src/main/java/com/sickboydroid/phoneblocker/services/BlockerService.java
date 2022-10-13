package com.sickboydroid.phoneblocker.services;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.sickboydroid.phoneblocker.R;
import com.sickboydroid.phoneblocker.activities.AppreciationActivity;
import com.sickboydroid.phoneblocker.utils.BlockerSession;
import com.sickboydroid.phoneblocker.utils.Constants;

public class BlockerService extends Service {
    private final String TAG = "BlockerService";
    private final int FOREGROUND_NOTIFICATION_ID = 23;
    private final int COMPLETION_NOTIFICATION_ID = 24;
    private CountdownManager countdownManager;
    private NotificationCompat.Builder notificationBuilder;
    private boolean isBlockerThreadRunning = false;
    private boolean preventPowerOff;
    private boolean blockNotifications;
    private boolean blockCalls;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        BlockerSession blockerSession = new BlockerSession(this);
        preventPowerOff = blockerSession.preventFromPowerOff();
        blockNotifications = blockerSession.blockNotifications();
        blockCalls = blockerSession.blockCalls();
        long countdownDuration = blockerSession.getDuration();
        showNotification();
        blockCallsAndNotifications();
        countdownManager = CountdownManager.startCountdown(countdownDuration);
        startBlockerThread();
        return START_REDELIVER_INTENT;
    }

    private void onCountdownCompletes() {
        Log.d(TAG, "Countdown completed. Device can be unlocked!!");
        Notification notification = new NotificationCompat.Builder(this, Constants.DEFAULT_NOTIFICATION_CHANNEL).setContentText("Timer has completed. You can now use your phone normally").setContentTitle("Timer Completed").setTicker("Timer Completed Ticker").setSmallIcon(R.mipmap.ic_launcher_round).setPriority(NotificationCompat.PRIORITY_MAX).build();
        NotificationManagerCompat.from(this).notify(COMPLETION_NOTIFICATION_ID, notification);
        Intent intentAppreciationActivity = new Intent(this, AppreciationActivity.class);
        startActivity(intentAppreciationActivity);
        stopSelf();
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
        notificationBuilder = new NotificationCompat.Builder(this, Constants.DEFAULT_NOTIFICATION_CHANNEL).setSmallIcon(R.mipmap.ic_launcher_round).setContentTitle(getString(R.string.blocker_service_notification_content_title)).setOnlyAlertOnce(true).setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Notification notification = notificationBuilder.build();
        NotificationManagerCompat.from(this).notify(FOREGROUND_NOTIFICATION_ID, notification);
        startForeground(FOREGROUND_NOTIFICATION_ID, notification);
    }

    private void updateNotification() {
        long remainingMillis = countdownManager.getRemainingMillis();
        int remainingSeconds = (int) (remainingMillis / 1000) % 60;
        int remainingMinutes = (int) ((remainingMillis / (1000 * 60)) % 60);
        int remainingHours = (int) (remainingMillis / (1000 * 60 * 60));
        notificationBuilder.setContentText("Remaining: " + remainingHours + " hours " + remainingMinutes + " minutes " + remainingSeconds + " seconds");
        NotificationManagerCompat.from(this).notify(FOREGROUND_NOTIFICATION_ID, notificationBuilder.build());
    }

    private static class CountdownManager {
        long countdownEnd;

        private CountdownManager(long durationMillis) {
            countdownEnd = System.currentTimeMillis() + durationMillis;
        }

        public static CountdownManager startCountdown(long durationMillis) {
            return new CountdownManager(durationMillis);
        }

        public boolean isCountdownComplete() {
            return System.currentTimeMillis() > countdownEnd;
        }

        public long getRemainingMillis() {
            if (isCountdownComplete()) return -1;
            return countdownEnd - System.currentTimeMillis();
        }
    }

    private class Blocker extends Thread {
        private final PowerManager powerManager;
        private final KeyguardManager keyguardManager;
        private final DevicePolicyManager dpm;
        private final Handler handler;

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
            while (!countdownManager.isCountdownComplete()) {
                Log.d(TAG, "Remaining Time: " + countdownManager.getRemainingMillis());
                updateNotification();
                sleepThread(250);
                // Screen is off, just pass for now
                if (!powerManager.isInteractive()) {
                    sleepThread(500);
                    continue;
                }
                // Screen is on, prevent any system dialog (for preventing power off of phone)
                if (preventPowerOff)
                    handler.post(() -> sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)));
                // Screen is on and also device is unlocked so lock it
                if (!keyguardManager.isKeyguardLocked()) dpm.lockNow();
            }
            isBlockerThreadRunning = false;
            onCountdownCompletes();
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

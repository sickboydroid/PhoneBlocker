package com.sickboydroid.phoneblocker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import java.util.Calendar;

public class Countdown {
    public static void startCountdown(Handler handler, Runnable run, long delayMillis) {
        handler.postDelayed(run, delayMillis);
    }
}

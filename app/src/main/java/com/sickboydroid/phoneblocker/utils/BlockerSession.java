package com.sickboydroid.phoneblocker.utils;

import android.content.Context;

public class BlockerSession {
    private final Context context;
    private final AppPreferences prefs;

    public BlockerSession(Context context) {
        this.context = context;
        prefs = new AppPreferences(context);
    }

    public boolean isSessionPending() {
        return getEndTime() >= System.currentTimeMillis();
    }

    public long getStartTime() {
        return prefs.getLong(Constants.PREF_BLOCK_SESSION_START_TIME);
    }

    public long getEndTime() {
        return prefs.getLong(Constants.PREF_BLOCK_SESSION_END_TIME);
    }

    public long getDuration() {
        return getEndTime() - getStartTime();
    }

    public boolean hasShownAppreciationActivity() {
        return prefs.getBoolean(Constants.PREF_BLOCK_SESSION_HAS_SHOWN_APPRECIATION_ACTIVITY);
    }

    public boolean preventFromPowerOff() {
        return prefs.getBoolean(Constants.PREF_BLOCK_SESSION_PREVENT_POWER_OFF);
    }

    public boolean blockNotifications() {
        return prefs.getBoolean(Constants.PREF_BLOCK_SESSION_BLOCK_NOTIFICATIONS);
    }

    public boolean blockCalls() {
        return prefs.getBoolean(Constants.PREF_BLOCK_SESSION_BLOCK_CALLS);
    }

    public void createSession(long duration, boolean preventPowerOff, boolean blockCalls, boolean blockNotifications) {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + duration;
        prefs.changePreference(Constants.PREF_BLOCK_SESSION_START_TIME, startTime);
        prefs.changePreference(Constants.PREF_BLOCK_SESSION_END_TIME, endTime);
        prefs.changePreference(Constants.PREF_BLOCK_SESSION_PREVENT_POWER_OFF, preventPowerOff);
        prefs.changePreference(Constants.PREF_BLOCK_SESSION_BLOCK_CALLS, blockCalls);
        prefs.changePreference(Constants.PREF_BLOCK_SESSION_BLOCK_NOTIFICATIONS, blockNotifications);
    }

    public void setHasShownAppreciationActivity(boolean shown) {
        prefs.changePreference(Constants.PREF_BLOCK_SESSION_HAS_SHOWN_APPRECIATION_ACTIVITY, shown);
    }
}

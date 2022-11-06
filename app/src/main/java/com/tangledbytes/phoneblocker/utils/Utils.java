package com.tangledbytes.phoneblocker.utils;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.tangledbytes.phoneblocker.receivers.AdminReceiver;

public class Utils {
    public static void sleepThread(long delayMillis) {
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasDeviceAdminPermission(Context context) {
        DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName mAdminName = new ComponentName(context, AdminReceiver.class);
        return mDPM != null && mDPM.isAdminActive(mAdminName);
    }

    //TODO: Implement this method for the devices like MI. These devices do not give boot permission without enabling
    public static boolean hasBootPermission(Context context) {
        return true;
    }

    public static void disable(ViewGroup layout) {
        layout.setEnabled(false);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup)
                disable((ViewGroup) child);
            else
                child.setEnabled(false);
        }
    }
}
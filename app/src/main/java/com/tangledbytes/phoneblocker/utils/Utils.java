package com.tangledbytes.phoneblocker.utils;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;

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
}
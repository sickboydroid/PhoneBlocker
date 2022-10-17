
package com.tangledbytes.phoneblocker.utils;

        import android.app.admin.DevicePolicyManager;
        import android.content.ComponentName;
        import android.content.Context;
        import android.widget.Toast;

        import com.tangledbytes.phoneblocker.activities.AppIntroActivity;
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

    public static boolean hasBootPermission(Context context) {
        return true;
    }
}
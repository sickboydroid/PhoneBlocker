package com.sickboydroid.phoneblocker.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

aimport com.sickboydroid.phoneblocker.activities.AppreciationActivity;
import com.sickboydroid.phoneblocker.activities.MainActivity;
import com.sickboydroid.phoneblocker.services.BlockerService;
import com.sickboydroid.phoneblocker.utils.BlockerSession;

public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String TAG = BootCompletedReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Boot Completed");
        BlockerSession blockerSession = new BlockerSession(context);
        if (blockerSession.isSessionPending()) {
            Intent intentBlockerService = new Intent(context, BlockerService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.startForegroundService(intentBlockerService);
            else
                context.startService(intentBlockerService);
        } else if(!blockerSession.hasShownAppreciationActivity()) {
            Intent intentAppreciationActivity = new Intent(context, AppreciationActivity.class);
            context.startActivity(intentAppreciationActivity);
        }
    }
}

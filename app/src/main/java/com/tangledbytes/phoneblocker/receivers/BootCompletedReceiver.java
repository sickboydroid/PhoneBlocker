package com.tangledbytes.phoneblocker.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.tangledbytes.libtb.log.XLog;
import com.tangledbytes.phoneblocker.activities.AppreciationActivity;
import com.tangledbytes.phoneblocker.services.BlockerService;
import com.tangledbytes.phoneblocker.utils.BlockerSession;

public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        XLog.d(TAG, "AppBlocker auto started via intent=" + intent.getAction());
        handleCallingActivity();
        BlockerSession blockerSession = new BlockerSession(context);
        if (blockerSession.isSessionPending()) {
            XLog.d(TAG, "We have a pending session, restarting it");
            Intent intentBlockerService = new Intent(context, BlockerService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.startForegroundService(intentBlockerService);
            else
                context.startService(intentBlockerService);
        } else if (!blockerSession.hasShownAppreciationActivity()) {
            XLog.d(TAG, "We haven't shown the AppreciationActivity, so lets show it");
            Intent intentAppreciationActivity = new Intent(context, AppreciationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentAppreciationActivity);
        } else {
            XLog.d(TAG, "There is nothing to be done");
        }
    }

    private void handleCallingActivity() {
    }
}
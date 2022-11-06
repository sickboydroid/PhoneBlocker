package com.tangledbytes.phoneblocker.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.tangledbytes.libtb.log.XLog;
import com.tangledbytes.phoneblocker.services.BlockerService;
import com.tangledbytes.phoneblocker.utils.BlockerSession;

public class AutoStartActivity extends AppCompatActivity {
    private static final String TAG = "AutoStartActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XLog.d(TAG, "AppBlocker auto started");
        BlockerSession blockerSession = new BlockerSession(this);
        if (blockerSession.isSessionPending()) {
            XLog.d(TAG, "We have a pending session, restarting it");
            Intent intentBlockerService = new Intent(this, BlockerService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startForegroundService(intentBlockerService);
            else
                startService(intentBlockerService);
        } else if (!blockerSession.hasShownAppreciationActivity()) {
            XLog.d(TAG, "We haven't shown the AppreciationActivity, so lets show it");
            Intent intentAppreciationActivity = new Intent(this, AppreciationActivity.class);
            startActivity(intentAppreciationActivity);
        } else {
            XLog.d(TAG, "There is nothing to be done");
        }
        finish();
    }
}
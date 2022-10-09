package com.sickboydroid.phoneblocker;

import android.app.Application;
import android.content.Intent;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        if(shouldShowInitialScreens()) {
//
//            return;
//        }
//
//        if(hasAdminPermission()) {
//            return;
//        }
//
//        if(hasOverlayPermission()) {
//            return;
//        }
//        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    private boolean hasOverlayPermission() {
        return false;
    }

    private boolean hasAdminPermission() {
        return false;
    }

    private boolean shouldShowInitialScreens() {
        return false;
    }
}

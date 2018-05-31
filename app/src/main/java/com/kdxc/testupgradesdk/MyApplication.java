package com.kdxc.testupgradesdk;

import android.app.Application;

import com.kdxc.app_upgrade_monitor.sdk.UpgradeMonitor;

/**
 * Created by phe on 2018/5/28.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        UpgradeMonitor.getInstance().initUpgradeMonitor(getApplicationContext());
    }
}

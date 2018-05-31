package com.kdxc.testupgradesdk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kdxc.app_upgrade_monitor.sdk.UpgradeMonitor;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UpgradeMonitor.getInstance().startUpgradeMonitor(getApplicationContext());
    }
}

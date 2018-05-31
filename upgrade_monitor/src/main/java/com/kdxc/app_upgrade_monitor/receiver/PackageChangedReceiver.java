package com.kdxc.app_upgrade_monitor.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.kdxc.app_upgrade_monitor.Utils.ReportUpgradeInfoUtils;
import com.kdxc.app_upgrade_monitor.Utils.SharedPreferencesHelper;
import com.kdxc.app_upgrade_monitor.app.AppApiContact;
import com.kdxc.app_upgrade_monitor.app.AppSpContact;

import java.io.File;


/**
 * Created by phe on 2018/5/23.
 */

public class PackageChangedReceiver extends BroadcastReceiver {

    private static final String TAG = AppApiContact.TAG;
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferencesHelper.getInstance().Builder(context);
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
            Log.i(TAG, "Intent.ACTION_PACKAGE_ADDED");
        } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)){
            Log.i(TAG, "Intent.ACTION_PACKAGE_REPLACED");
            if (!SharedPreferencesHelper.getInstance().
                    getString(AppSpContact.UPGRADE_VER_NUM_KEY).equals("")) {
                startNewApp(context, context.getPackageName());
                //安装成功了 将需要报告更新成功状态置为true
                SharedPreferencesHelper.getInstance().putBoolean(
                        AppSpContact.IS_NEEDED_REPORT_UPGRADE_STATUS, true);
                ReportUpgradeInfoUtils.reportUpgradeInfo(context, "");
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        removeFile();
                    }
                }.start();
            } else {
                Log.i(TAG, "第一次安装应用,不需要报告更新状态");
            }
        }
    }

    private void removeFile() {
        String filePath = SharedPreferencesHelper.getInstance().getString(AppSpContact.DOWNLOADED_FILE_PATH);
        Log.i(TAG, "filePath to delete: " + filePath);
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
            Log.i(TAG, "成功删除文件!");
        } else {
            Log.i(TAG, "不需删除文件!");
        }
    }

    private static void startNewApp(Context context, String packageName) {
        Log.i(TAG, "==  开始启动新APP ==");
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(launchIntent);
    }

    private static void reStartApp(Context context) {
        Log.i(TAG, "==  开始重启APP ==");
        ActivityManager manager = (ActivityManager)context.getSystemService("activity");
        manager.restartPackage(context.getPackageName());
    }
}

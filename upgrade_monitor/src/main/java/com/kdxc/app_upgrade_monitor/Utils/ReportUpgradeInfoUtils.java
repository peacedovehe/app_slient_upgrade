package com.kdxc.app_upgrade_monitor.Utils;

import android.content.Context;
import android.util.Log;

import com.kdxc.app_upgrade_monitor.app.AppApiContact;
import com.kdxc.app_upgrade_monitor.app.AppSpContact;
import com.kdxc.app_upgrade_monitor.net.HttpApiMethods;
import com.kdxc.app_upgrade_monitor.net.bean.ReportUpdateInfo;
import com.kdxc.app_upgrade_monitor.net.params.ReportUpdateInfoParams;
import com.kdxc.app_upgrade_monitor.sdk.UpgradeMonitor;

import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by phe on 2018/5/24.
 */

public class ReportUpgradeInfoUtils {

    private static final String TAG = AppApiContact.TAG;
    private static CompositeSubscription mSubscriptions;
    private static HttpApiMethods httpApiMethods;

    public static void reportUpgradeInfo(Context context, String failedReason) {
        Log.i(TAG, "开始调用 报告更新状态接口");
        if (mSubscriptions == null) {
            mSubscriptions = new CompositeSubscription();
        }

        if (httpApiMethods == null) {
            httpApiMethods = UpgradeMonitor.getInstance().getHttpApiMethods();
        }

        mSubscriptions.clear();
        Subscriber<ReportUpdateInfo> subscriber = new Subscriber<ReportUpdateInfo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "请求 报告更新状态接口失败!");
                //更新失败 将状态置为下次还是需要报告升级状态
                SharedPreferencesHelper.getInstance().putBoolean(
                        AppSpContact.IS_NEEDED_REPORT_UPGRADE_STATUS, true);
            }

            @Override
            public void onNext(ReportUpdateInfo reportUpdateInfo) {
                Log.i(TAG, "请求 报告更新状态接口成功!");
                //更新成功 将状态置为不需要报告
                SharedPreferencesHelper.getInstance().putBoolean(
                        AppSpContact.IS_NEEDED_REPORT_UPGRADE_STATUS, false);
            }
        };
        SharedPreferencesHelper.getInstance().setToken(AppApiContact.INIT_TOKEN);
        int dcId = SharedPreferencesHelper.getInstance().getInt(AppSpContact.UPGRADE_DCID_KEY);
        String upgradeSuccessVersion = SharedPreferencesHelper.getInstance().
                getString(AppSpContact.UPGRADE_VER_NUM_KEY);
        Log.i(TAG, "upgradeSuccessVersion: " + upgradeSuccessVersion);
        ReportUpdateInfoParams params = new ReportUpdateInfoParams();
        //当前应用的版本号
        params.setSoftwareVersion(InstallAppUtils.getVersion(context));
        params.setUpdateFinishTime(System.currentTimeMillis());
        if (InstallAppUtils.getVersion(context).equals(upgradeSuccessVersion)) {
            Log.i(TAG, "此次更新任务成功,开始调用报告更新状态接口!");
            params.setUpdateStatus(2);
        } else {
            Log.i(TAG, "此次更新任务失败,开始调用报告更新状态接口!--->" + failedReason);
            params.setUpdateStatus(3);
            params.setFailReason(failedReason);
        }
        if (!SharedPreferencesHelper.getInstance().
                getString(AppSpContact.UPGRADE_VER_NUM_KEY).equals("")) {//如果是第一次安装 这里不应该开启
            httpApiMethods.reportUpdateInfo(subscriber, dcId,
                    SignatureHelper.getMacAddress(), params);
            mSubscriptions.add(subscriber);
        }
    }
}

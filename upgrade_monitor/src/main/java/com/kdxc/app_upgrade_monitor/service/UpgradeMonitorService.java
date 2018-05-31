package com.kdxc.app_upgrade_monitor.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadTask;
import com.kdxc.app_upgrade_monitor.Utils.DateHelper;
import com.kdxc.app_upgrade_monitor.Utils.DeviceInfoManager;
import com.kdxc.app_upgrade_monitor.Utils.InstallAppUtils;
import com.kdxc.app_upgrade_monitor.Utils.MergeApkFile;
import com.kdxc.app_upgrade_monitor.Utils.ReportUpgradeInfoUtils;
import com.kdxc.app_upgrade_monitor.Utils.SharedPreferencesHelper;
import com.kdxc.app_upgrade_monitor.Utils.SignatureHelper;
import com.kdxc.app_upgrade_monitor.Utils.SilentInstall;
import com.kdxc.app_upgrade_monitor.app.AppApiContact;
import com.kdxc.app_upgrade_monitor.app.AppSpContact;
import com.kdxc.app_upgrade_monitor.net.HttpApiMethods;
import com.kdxc.app_upgrade_monitor.net.bean.TimeBean;
import com.kdxc.app_upgrade_monitor.net.bean.UpgradeInfoBean;
import com.kdxc.app_upgrade_monitor.net.params.UpgradeParams;
import com.kdxc.app_upgrade_monitor.sdk.UpgradeMonitor;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by phe on 2018/5/21.
 */

public class UpgradeMonitorService extends Service {

    private static final String TAG = AppApiContact.TAG;
    public static final int GRAY_SERVICE_ID = 1001;
    protected CompositeSubscription mSubscriptions;
    HttpApiMethods httpApiMethods;
    private Timer timer = new Timer();
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            recordMirrorInfo();
        }
    };;
    private long period = 300000;//第一次间隔5分钟

    private String downloadUrl;

    private UpgradeInfoBean upgradeInfoBean;
    private String fileName = "";
    private String filePath = "";

    private boolean isDownloadCompleted = false;
    private boolean isDownloading = false;//是否正在下载中

    //private BroadcastReceiver myBroadCast

    public UpgradeMonitorService() {

    }

    @Override
    public void onCreate() {
        Log.i(TAG, "UpgradeMonitorService onCreate");
        Log.i(TAG, "service pid= "  + android.os.Process.myPid());
        Aria.download(this).register();
        isDownloadCompleted = false;
        isDownloading = false;
        boolean isNeedReport = SharedPreferencesHelper.getInstance().getBoolean(
                AppSpContact.IS_NEEDED_REPORT_UPGRADE_STATUS);
        if (isNeedReport) {
            //如果需要再次报告更新状态 则在这里报告
            Log.i(TAG, "再次报告更新成功状态");
            ReportUpgradeInfoUtils.reportUpgradeInfo(this, "");
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "onStartCommand!!!");

        if (mSubscriptions == null) {
            mSubscriptions = new CompositeSubscription();
        }

        if (httpApiMethods == null) {
            httpApiMethods = UpgradeMonitor.getInstance().getHttpApiMethods();
        }

        getSystemTime();

        if (Build.VERSION.SDK_INT < 18) {
            startForeground(GRAY_SERVICE_ID, new Notification());//API < 18 ，此方法能有效隐藏Notification上的图标
        } else {
            Intent innerIntent = new Intent(this, GrayInnerService.class);
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID, new Notification());
        }

        return START_STICKY;
    }

    //获取系统时间
    private void getSystemTime() {
        mSubscriptions.clear();
        Subscriber<TimeBean> subscriber = new Subscriber<TimeBean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                //ToastHelper.showToastMessage(e.getMessage());
                Log.e(TAG, e.toString());
            }

            @Override
            public void onNext(TimeBean timeBean) {
                Log.i(TAG, timeBean.toJson());
                //保存系统时间与当前时间的差值
                SharedPreferencesHelper.getInstance().putLong(AppSpContact.TIME_DIFFERENCE,
                        System.currentTimeMillis() - timeBean.getSysTime());
                //开始进行定时任务,开始间隔10秒防止一个接一个的增量更新任务无法成功更新的问题
                if (timer != null) {
                    timer.cancel();
                }
                timer = new Timer();
                task = new TimerTask() {
                    @Override
                    public void run() {
                        recordMirrorInfo();
                    }
                };
                timer.schedule(task, 10000, period);
            }
        };
        httpApiMethods.getSysTime(subscriber);
        mSubscriptions.add(subscriber);
    }

    private void recordMirrorInfo() {
        ActivityManager activityManager = DeviceInfoManager.getActivityManager(this);
        UpgradeParams params = new UpgradeParams();
        params.setSoftwareVersion(InstallAppUtils.getVersion(this));
        float curCpuRate = DeviceInfoManager.getCurProcessCpuRate();
        params.setMainProgramCpu(curCpuRate);
        float totalCpuRate = DeviceInfoManager.getTotalCpuRate();
        params.setCpu(totalCpuRate);
        float curRam = DeviceInfoManager.getCurrentAppMemUsedPercent();
        params.setMainProgramRam(curRam);
        float totalMemUsedPersent = DeviceInfoManager.getUsedPercentValue(this);
        params.setRam(totalMemUsedPersent);
        Log.i(TAG, "~当前应用Cpu使用用率: " + curCpuRate + "\n" +
            "~总的Cpu使用率: " + totalCpuRate + "\n" +
            "~当前应用内存使用率: " + curRam + "\n" +
            "~当前内存总使用率: " + totalMemUsedPersent);
        SharedPreferencesHelper.getInstance().setToken(AppApiContact.INIT_TOKEN);

        mSubscriptions.clear();
        Subscriber<UpgradeInfoBean> subscriber = new Subscriber<UpgradeInfoBean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                //ToastHelper.showToastMessage(e.getMessage());
                Log.e(TAG, "recordMirrorInfo error: " + e.toString());
            }

            @Override
            public void onNext(UpgradeInfoBean upgradeInfo) {
                Log.i(TAG, upgradeInfo.toJson());
                upgradeInfoBean = upgradeInfo;
                if (upgradeInfo.needUpdate) {
                    Log.e(TAG, "upgradeInfo.needUpdate");
                    downloadFile(upgradeInfo);
                } else {
                    if (period > 1800000) {//如果间隔时间大于半小时则等于半小时
                        period = 1800000;
                    }
                    timer.cancel();
                    timer = new Timer();
                    task = new TimerTask() {
                        @Override
                        public void run() {
                            recordMirrorInfo();
                        }
                    };
                    timer.schedule(task, period, period);
                    period += 300000;
                }
                Log.i(TAG, "currentTime-->" + System.currentTimeMillis()
                        + "period--->" + period);
            }
        };

        httpApiMethods.recordMirrorInfo(subscriber,
                SignatureHelper.getMacAddress(), params);
        mSubscriptions.add(subscriber);
    }

    //在这里处理任务执行中的状态，如进度进度条的刷新
    @Download.onTaskRunning protected void running(DownloadTask task) {
        Log.i(TAG, "task.getDownloadPath--->" + task.getDownloadPath() + "\n"
            + "task.getDownloadUrl--->" + task.getDownloadUrl() + "\n"
            + "task.getKey()-->" + task.getKey());
        if(task.getKey().equals(downloadUrl)){
            //....
            //可以通过url判断是否是指定任务的回调
            Log.i(TAG, "是指定的下载任务!");
        } else {
            Log.i(TAG, "不是指定的下载任务!");
        }
        int p = task.getPercent();	//任务进度百分比
        String speed = task.getConvertSpeed();	//转换单位后的下载速度，单位转换需要在配置文件中打开
        long speed1 = task.getSpeed(); //原始byte长度速度
        Log.i(TAG, "下载任务百分比: " + p +"\n" + "下载速度: " + speed + "\n"
        + "原始下载速度(byte): " + speed1);
    }

    @Download.onTaskComplete public void taskComplete(DownloadTask task) {
        //在这里处理任务完成的状态
        Log.i(TAG, "onTaskComplete");

        if (Aria.download(this).taskExists(downloadUrl)){
            Log.i(TAG, "完成后 任务还是存在");
        } else {
            Log.i(TAG, "完成后 任务就不存在了");
        }
        Aria.download(this).removeAllTask(false);
        isDownloading = false;
        isDownloadCompleted = true;
        //保存需要更新的版本
        SharedPreferencesHelper.getInstance().putString(AppSpContact.UPGRADE_VER_NUM_KEY,
                upgradeInfoBean.softwareObj.swVersion);
        //保存更新任务的编号
        SharedPreferencesHelper.getInstance().putInt(AppSpContact.UPGRADE_DCID_KEY,
                Integer.parseInt(upgradeInfoBean.softwareObj.dcId));
        //保存已经下载好的文件名称,便于安装成功后删除文件
        SharedPreferencesHelper.getInstance().putString(AppSpContact.DOWNLOADED_FILE_PATH,
                filePath);
        //开始安装
        installApp(upgradeInfoBean.softwareObj);

    }

    @Download.onTaskFail public void taskFail(DownloadTask task) {
        Log.i(TAG, "onTaskFail");
    }

    @Download.onTaskCancel public void taskCancel(DownloadTask task) {
        Log.i(TAG, "onTaskCancel");
    }

    @Download.onTaskStop public void taskStop(DownloadTask task) {
        Log.i(TAG, "onTaskStop");
    }

    @Download.onTaskResume public void taskResume(DownloadTask task) {
        Log.i(TAG, "onTaskResume");
    }

    /**
     * {@code @Download.onNoSupportBreakPoint}注解，如果该任务不支持断点，Aria会调用该方法
     */
    @Download.onNoSupportBreakPoint public void noSupportBreakPoint(DownloadTask task) {
        Log.i(TAG, "onNoSupportBreakPoint");
    }

    private void downloadFile(UpgradeInfoBean upgradeInfo) {
        if (upgradeInfo == null) {
            return;
        }
        UpgradeInfoBean.SoftwareObj softwareObj = upgradeInfo.softwareObj;

        if ("1".equals(softwareObj.updateWay)) {//增量更新
            Log.i(TAG, "增量更新");
            downloadUrl = softwareObj.swDiffUrl;
            //fileName = System.currentTimeMillis() + ".patch";
            fileName = "tmp_" + softwareObj.dcId + "_" + softwareObj.swVersion + ".patch";
        } else if ("2".equals(softwareObj.updateWay)) {//全量更新
            Log.i(TAG, "全量更新");
            downloadUrl = softwareObj.swFileUrl;
            //fileName = System.currentTimeMillis() + ".apk";
            fileName = "tmp_" + softwareObj.dcId + "_" + softwareObj.swVersion + ".apk";;
        }

        filePath =  Environment.getExternalStorageDirectory().getPath() + "/" + fileName;
        File tmpFile = new File(filePath);
        if (tmpFile.exists()) {
            Log.i(TAG, "file size--->" + tmpFile.length());
            int savedDcId = SharedPreferencesHelper.getInstance().getInt(AppSpContact.UPGRADE_DCID_KEY);
            long fileSize = tmpFile.length();
            //需要下载更新的DcId和需要更新的文件大小一致就不需要重复下载了
            if ("1".equals(softwareObj.updateWay)) {
                if (fileSize == Long.parseLong(softwareObj.swDiffSize) &&
                        Integer.parseInt(softwareObj.dcId) == savedDcId) {
                    isDownloadCompleted = true;
                } else {
                    isDownloadCompleted = false;
                }
            } else if ("2".equals(softwareObj.updateWay)){
                if (fileSize == Long.parseLong(softwareObj.swFileSize) &&
                        Integer.parseInt(softwareObj.dcId) == savedDcId) {
                    isDownloadCompleted = true;
                } else {
                    isDownloadCompleted = false;
                }
            }
        }

        Log.i(TAG, "downloadUrl-->" + downloadUrl);
        //重置一下下载路径
        //Aria.download(this).removeAllTask(false);
        if (/*!Aria.download(this).taskExists(downloadUrl) ||*/!isDownloading && !isDownloadCompleted) {
            isDownloading = true;
            Log.i(TAG, "开始进行下载!!!");
            Aria.download(this)
                    .load(downloadUrl)
        /*.setDownloadPath(Environment.getExternalStorageDirectory().getPath() + "test.patch")*/
                    .setFilePath(filePath)
                    /*.useServerFileName(true)*/
                    .start();
        } else {
            Log.i(TAG, "正在下载中或者已经下载完成!!!");
            if (isDownloading) {
                Log.w(TAG, "上次下载任务还在进行中,不进行下载操作");
            }

            if (isDownloadCompleted) {
                Log.i(TAG, "下载任务已经完成,无需重复下载");
                isDownloading = false;
                //开始进行安装
                installApp(upgradeInfoBean.softwareObj);
            }
        }
    }

    private void installApp(UpgradeInfoBean.SoftwareObj softwareObj) {
        if ("1".equals(softwareObj.installWay)) {//立即安装
            Log.i(TAG, "立即安装");
            //
            installAPkFile(filePath);
        } else if ("2".equals(softwareObj.installWay)) {//定时安装
            Log.i(TAG, "定时安装");
            //
            String[] startHoursAndMins = softwareObj.installTimeStart.split(":");
            String[] endHoursAndMins = softwareObj.installTimeEnd .split(":");

            final Calendar startInstallCalendar = Calendar.getInstance();
            startInstallCalendar.set(Calendar.HOUR_OF_DAY,
                    Integer.parseInt(startHoursAndMins[0]));
            startInstallCalendar.set(Calendar.MINUTE,
                    Integer.parseInt(startHoursAndMins[1]));
            final Calendar endInstallCalendar = Calendar.getInstance();
            endInstallCalendar.set(Calendar.HOUR_OF_DAY,
                    Integer.parseInt(endHoursAndMins[0]));
            endInstallCalendar.set(Calendar.MINUTE,
                    Integer.parseInt(endHoursAndMins[1]));
            Calendar currentCalendar = Calendar.getInstance();
            if (endInstallCalendar.before(currentCalendar)) {
                startInstallCalendar.add(Calendar.HOUR_OF_DAY, 24);
                endInstallCalendar.add(Calendar.HOUR_OF_DAY, 24);
            } else {
                if (startInstallCalendar.before(currentCalendar)) {
                    startInstallCalendar.setTime(currentCalendar.getTime());
                }
            }

            Date startInstallDate = startInstallCalendar.getTime();
            Log.i(TAG, "开始安装日期:" + DateHelper.
                    millisToStringDate(startInstallDate.getTime(),
                            "yyyy-MM-dd HH:mm:ss"));
            final Timer installTimer = new Timer();
            TimerTask installTask = new TimerTask() {
                @Override
                public void run() {
                    Calendar rightNow = Calendar.getInstance();
                    if (rightNow.before(endInstallCalendar) &&
                            rightNow.after(startInstallCalendar)) {
                        //在安装结束时间之前 进行安装任务
                        Log.i(TAG, "在更新时间内进行更新!");
                        installAPkFile(filePath);
                    } else {
                        Log.i(TAG, "不在更新时间内不进行更新!");
                    }
                }
            };
            //指定日期进行安装
            installTimer.schedule(installTask, startInstallDate);
        }
    }

    private void installAPkFile(String filePath) {

        if ("2".equals(upgradeInfoBean.softwareObj.updateWay)) {
            Log.i(TAG, "filePath--->" + filePath);
            //InstallAppUtils.install("", filePath);
            //String result = InstallAppUtils.exe(filePath);
            SilentInstall.install(filePath);
        } else {
            //这里是增量更新 需要先组装成App才能进行更新
            String sd = Environment.getExternalStorageDirectory().getPath();
            String source= getApplicationContext().getPackageResourcePath();//老的安装包的路径
            Log.i(TAG, "老的安装包路径source --->" + source);
            try {
                MergeApkFile.mergeApkFile(source, filePath, sd + "/new_base.apk", upgradeInfoBean.softwareObj.swFileMd5);
            } catch (Exception e) {
                Log.e(TAG, "合并文件失败!!!");
                e.printStackTrace();
            }
        }
    }

    public class MyBinder extends Binder {
        private UpgradeMonitorService service;

        public MyBinder() {
            service = UpgradeMonitorService.this;
        }

//        public void disconnect() {
//            service.disconnect();
//        }
    }

    /**
     * 给 API >= 18 的平台上用的灰色保活手段
     */
    public static class GrayInnerService extends Service {

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(GRAY_SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Services onDestory");
        super.onDestroy();
        timer.cancel();
        Aria.download(this).unRegister();
        isDownloading = false;
        isDownloadCompleted = false;
    }
}

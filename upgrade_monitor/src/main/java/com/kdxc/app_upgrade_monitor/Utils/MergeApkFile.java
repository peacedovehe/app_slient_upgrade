package com.kdxc.app_upgrade_monitor.Utils;

import android.os.AsyncTask;
import android.util.Log;

import com.kdxc.app_upgrade_monitor.app.AppApiContact;
import com.kdxc.app_upgrade_monitor.sdk.UpgradeMonitor;
import com.nothome.delta.GDiffPatcher;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.concurrent.ExecutionException;

/**
 * Created by phe on 2018/5/23.
 */

public class MergeApkFile {

    private static MergeApkFileAsyncTask mergeApkFileAsyncTask;

    private static File mergeFile(final String source, final String patch,
                                  String target) throws Exception {
        GDiffPatcher patcher = new GDiffPatcher();
        File deffFile = new File(patch);
        File updatedFile = new File(target);
        patcher.patch(new File(source), deffFile, updatedFile);
        return updatedFile;
    }

    private static File mergeApk(final String source, final String patch,
                                final String target, String newApkMd5) {
        File updateFile = null;
        try {
            updateFile = mergeFile(source, patch, target);
        } catch (Exception e) {
            e.printStackTrace();
            ReportUpgradeInfoUtils.reportUpgradeInfo(UpgradeMonitor.getInstance().getmContext(),
                    "合并文件的时候出现错误!");
            return null;
        }
        String ufpMd5 = getMD5(updateFile);
        Log.i(AppApiContact.TAG, "服务端下发的md5:" + newApkMd5 + ",新合并后的apk MD5:" + ufpMd5);
        if (ufpMd5 == null || !newApkMd5.equalsIgnoreCase(ufpMd5)) {
            ReportUpgradeInfoUtils.reportUpgradeInfo(UpgradeMonitor.getInstance().getmContext(),
                    "MD5校验失败!");
            if (updateFile != null && updateFile.exists()) {
                updateFile.delete();
            }
        } else {
            Log.i(AppApiContact.TAG, "开始安装合并后的apk文件---->" + updateFile.getAbsolutePath());
            //这里不能再使用AsyncTask 因为现在已经是异步任务里面了~~
            SilentInstall.exe(updateFile.getAbsolutePath());
        }

        return updateFile;
    }

    public static Boolean mergeApkFile(String source, String patch,
                                       String target, String newApkMd5) {
        if (mergeApkFileAsyncTask == null) {
            mergeApkFileAsyncTask = new MergeApkFileAsyncTask();
        }

        mergeApkFileAsyncTask.execute(new String[]{source, patch, target, newApkMd5});
        File result = null;

        try {
            result = (File) mergeApkFileAsyncTask.get();
            if(!result.exists()) {
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String getMD5(File file){

        int len;
        MessageDigest digest = null;
        InputStream is = null;
        byte buffer[] = new byte[1024];
        try {
            digest = MessageDigest.getInstance("MD5");

            is = new BufferedInputStream(new FileInputStream((File)file));

            while (-1!=(len = is.read(buffer,0,1024))) {

                digest.update(buffer,0,len);
            }

            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        BigInteger bigInteger = new BigInteger(1, digest.digest());
        return bigInteger.toString(16);
    }

    static class MergeApkFileAsyncTask extends AsyncTask<String, String, File> {
        MergeApkFileAsyncTask() {

        }

        @Override
        protected File doInBackground(String... strings) {
            return mergeApk(strings[0], strings[1], strings[2], strings[3]);
        }
    }
}

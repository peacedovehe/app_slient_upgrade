package com.kdxc.app_upgrade_monitor.Utils;

import android.os.AsyncTask;
import android.util.Log;

import com.kdxc.app_upgrade_monitor.sdk.UpgradeMonitor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

/**
 * Created by phe on 2018/5/23.
 */

public class SilentInstall {
    private static InstallAsyncTask mInstallAsyncTask;

    public SilentInstall() {
    }

    public static String exe(String apkAbsolutePath) {
        String[] args = new String[]{"pm", "install", "-r", apkAbsolutePath};
        String result = "";
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;
        Log.e("kdxc", "111111111");
        try {
            ByteArrayOutputStream e = new ByteArrayOutputStream();
            boolean read = true;
            process = processBuilder.start();
            errIs = process.getErrorStream();

            int read1;
            while((read1 = errIs.read()) != -1) {
                e.write(read1);
            }

            e.write(157);
            inIs = process.getInputStream();

            while((read1 = inIs.read()) != -1) {
                e.write(read1);
            }

            byte[] data = e.toByteArray();
            result = new String(data);
            Log.e("kdxc", "222222222");
        } catch (IOException var20) {
            var20.printStackTrace();
            Log.e("kdxc", "333333333");
            ReportUpgradeInfoUtils.reportUpgradeInfo(UpgradeMonitor.getInstance().getmContext(), "打开新的Apk文件错误!");
        } catch (Exception var21) {
            Log.e("kdxc", "444444444");
            var21.printStackTrace();
            ReportUpgradeInfoUtils.reportUpgradeInfo(UpgradeMonitor.getInstance().getmContext(), "静默安装过程中出现未知错误!");
        } finally {
            try {
                if(errIs != null) {
                    errIs.close();
                }

                if(inIs != null) {
                    inIs.close();
                }
            } catch (IOException var19) {
                var19.printStackTrace();
                ReportUpgradeInfoUtils.reportUpgradeInfo(UpgradeMonitor.getInstance().getmContext(),
                        "文件读写错误!");
            }

            if(process != null) {
                process.destroy();
            }

        }
        return result;
    }

    public static Boolean install(String apkAbsolutePath) {
        if(mInstallAsyncTask == null) {
            mInstallAsyncTask = new SilentInstall.InstallAsyncTask();
        }

        mInstallAsyncTask.execute(new String[]{apkAbsolutePath});
        String result = null;

        try {
            result = (String)mInstallAsyncTask.get();
        } catch (InterruptedException var3) {
            var3.printStackTrace();
        } catch (ExecutionException var4) {
            var4.printStackTrace();
        }
        //正常安装这里是不会执行到的
        ReportUpgradeInfoUtils.reportUpgradeInfo(
                UpgradeMonitor.getInstance().getmContext(), "App未使用系统签名,无法进行安装!");
        Log.e("kdxc", "  ==  静默安装2  ==  " + result.toString());
        return Boolean.valueOf(true);
    }

    static class InstallAsyncTask extends AsyncTask<String, String, String> {
        InstallAsyncTask() {
        }

        protected String doInBackground(String... params) {
            return SilentInstall.exe(params[0]);
        }
    }
}

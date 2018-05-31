package com.kdxc.app_upgrade_monitor.Utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by phe on 2018/5/22.
 */

public class InstallAppUtils {

    private static final String TAG = "InstallAppUtils";

    //可以使用
    public static String exe(String apkAbsolutePath) {
        String[] args = new String[]{"pm", "install", "-r", apkAbsolutePath};
        String result = "";
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;

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
        } catch (IOException var20) {
            var20.printStackTrace();
        } catch (Exception var21) {
            var21.printStackTrace();
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
            }

            if(process != null) {
                process.destroy();
            }

        }

        return result;
    }

    public static void install(final String packageName, final String filePath) {

        File file = new File(filePath);
        if (filePath == null || filePath.length() == 0 || file == null) {

            return;
        }
        String[] args = { "pm", "install", "-r", filePath };
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();
        try {
            process = processBuilder.start();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;

            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        Log.i(TAG, "------>" + successMsg.toString() + "\n"
                + "------>" + errorMsg.toString());
        if (successMsg.toString().contains("Success") || successMsg.toString().contains("success")) {
            Log.i(TAG, "安装成功!");
        } else {
            Log.e(TAG, "安装失败!");
        }
    }

    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "1.0";
        }
    }
}

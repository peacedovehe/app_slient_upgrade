package com.kdxc.app_upgrade_monitor.Utils;

import com.kdxc.app_upgrade_monitor.app.AppSpContact;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Author:    Xiao_Tian
 * Version    V1.0
 * Date:      16/10/24 16:31
 * Description: 签名的帮助类
 * Why & What is modified:
 */
public class SignatureHelper {


    public static String getSignature(String uri, long timestamp) {
        StringBuilder dataBuffer = new StringBuilder();
        dataBuffer.append(uri);//uri=“/api/login”
        // dataBuffer.append(queryData);//queryData=“username=13000000000&pwd=123456”
        dataBuffer.append(timestamp);//System.currentTimeMillis()
        byte[] data = new byte[0];
        Mac mac = null;
        SecretKey secretKey;
        try {
            data = dataBuffer.toString().getBytes("utf-8");
            byte[] key = "F227D8B161932936".getBytes();
            secretKey = new SecretKeySpec(key, "HmacSHA256");
            mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
        } catch (Exception e) {
            e.printStackTrace();
        }


        byte[] digest = mac != null ? mac.doFinal(data) : new byte[0];
        return StringHelper.bytes2HexString(digest);// 转为十六进制的字符串,这里得到的值就是
    }


    /**
     * 获取设备的Mac地址
     *
     * @return 返回的是大写去掉':'的十六进制数
     */
    public static String getMacAddress() {
        String macSerial = "";
        String defaultMac = "60:B7:CA:BD:16";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            String line;
            while ((line = input.readLine()) != null) {
                macSerial += line.trim();
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //去掉':',转大写
        //return StringHelper.notEmpty(macSerial) ? macSerial.replaceAll(":", "").toUpperCase() : defaultMac;
        return StringHelper.notEmpty(macSerial) ? macSerial.toUpperCase() : defaultMac;
    }

    /**
     * 利用保存的差值得到时间戳
     */
    public static long getTime() {
        long localtime = System.currentTimeMillis();
        long timeDifference = SharedPreferencesHelper.getInstance().getLong(AppSpContact.TIME_DIFFERENCE);
        return localtime - timeDifference;
    }


}

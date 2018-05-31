package com.kdxc.app_upgrade_monitor.net;

/**
 * Author:    Xiao_Tian
 * Version    V1.0
 * Date:      16/10/9 9:08
 * Description: 自定义的网络异常类
 * Why & What is modified:
 */
public class ApiException extends RuntimeException {
    private int errorCode;//错误码

    public ApiException(String detailMessage) {
        super(detailMessage);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}

package com.kdxc.app_upgrade_monitor.app;

public class AppApiContact {

    public static final String HTTP_INPUT_TYPE = "application/json; charset=utf-8";
    public static final int DEFAULT_CURRENT_PAGE = 1;//默认请求第一页
    public static final String STATUS_NET_SUCCESS = "0";//网络请求成功状态
    public static final String STATUS_TOKEN_RENEW = "1004";//重新登录
    public static final String STATUS_TOKEN_OVERTIME = "1005";//Token 已过期
    public static final String STATUS_USER_UNBIND = "1104"; //第三方账号未绑定
    public static final String STATUS_PHONE_REGISTERED = "1103"; //手机已注册
    public static final String STATUS_CODE_ERROR = "1105";//验证码错误

    //用户第一次登录的初始token
    public static final String INIT_TOKEN = "09A5B407241A329A8E4C980BA0E78040";

    //APP端deviceClass的标识
    public static final String DEVICE_CLASS = "Mirror";//"APP-Android";

    public static final String VERSION_NAME = "1.3";

    public static final String TAG = "kdxc";
}

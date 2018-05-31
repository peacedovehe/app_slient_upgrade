package com.kdxc.app_upgrade_monitor.net.bean.base;

import com.kdxc.app_upgrade_monitor.Utils.StringHelper;
import com.kdxc.app_upgrade_monitor.app.AppApiContact;


public class BaseMetaV1 extends BaseBean {
    public String returnCode = "-1";

    public String errorMsg;

    public boolean isSuccess() {
        return  AppApiContact.STATUS_NET_SUCCESS.equals(returnCode);
    }

    public boolean isRenew(){
        return AppApiContact.STATUS_TOKEN_RENEW.equals(returnCode);
    }

    public boolean isOverTime(){
        return AppApiContact.STATUS_NET_SUCCESS.equals(returnCode);
    }

    public boolean isOverdueToken(){
        return AppApiContact.STATUS_TOKEN_OVERTIME.equals(returnCode);
    }

    public String getMsg() {
        return (StringHelper.isEmpty(errorMsg)) ? "" : errorMsg;
    }

}

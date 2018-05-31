package com.kdxc.app_upgrade_monitor.net.bean.base;

import com.google.gson.Gson;

public class BaseBean {
    public String toJson() {
        return new Gson().toJson(this);
    }
}

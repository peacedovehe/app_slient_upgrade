package com.kdxc.app_upgrade_monitor.net;


import com.kdxc.app_upgrade_monitor.net.bean.base.BaseMetaV1;

/*
 * Created by xt on 16/10/8.
 **/
public class HttpResultV1<T> extends BaseMetaV1 {
    public T datas;

    public T getDatas() {
        return datas;
    }
}

package com.kdxc.app_upgrade_monitor.net.bean;


import com.kdxc.app_upgrade_monitor.net.bean.base.BaseBean;

/**
 * Created by phe on 2018/5/17.
 */

public class UpgradeInfoBean extends BaseBean {

    public boolean needUpdate; //true：需要更新 false：没有需要更新的版本，此字段为false时，softwareObj对象为空

    public SoftwareObj softwareObj;

    public class SoftwareObj {
        public String swVersion;
        public String swName;
        public String swUpdateComment;
        public String swFileUrl;
        public String swFileSize;
        public String swFileMd5;
        public String swDiffUrl;
        public String swDiffSize;
        public String swPublishTime; //格式：yyyy-MM-dd
        public String updateWay;//	1：增量 2：全量
        public String installWay;// 1：立即安装 2：定时安装
        public String installTimeStart;// 格式：02:00
        public String installTimeEnd;// 格式：04:00
        public String dcId;// 在报告更新状态的时候，把这个值一并传给云端

    }
}

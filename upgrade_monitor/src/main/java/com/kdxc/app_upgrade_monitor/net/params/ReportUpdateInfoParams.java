package com.kdxc.app_upgrade_monitor.net.params;


import com.kdxc.app_upgrade_monitor.net.bean.base.BaseBean;

/**
 * Created by phe on 2018/5/24.
 */

public class ReportUpdateInfoParams extends BaseBean {

    private String softwareVersion; //当前版本号	String	不为空
    private int updateStatus;   //更新状态	Integer	不为空 2：更新成功 3：更新失败
    private long updateFinishTime;  //更新完成时间	Long	不为空 时间戳
    private String failReason;	//失败原因	String

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public int getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(int updateStatus) {
        this.updateStatus = updateStatus;
    }

    public long getUpdateFinishTime() {
        return updateFinishTime;
    }

    public void setUpdateFinishTime(long updateFinishTime) {
        this.updateFinishTime = updateFinishTime;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }
}

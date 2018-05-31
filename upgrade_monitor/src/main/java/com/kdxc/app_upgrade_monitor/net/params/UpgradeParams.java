package com.kdxc.app_upgrade_monitor.net.params;

import com.kdxc.app_upgrade_monitor.net.bean.base.BaseBean;

/**
 * Created by phe on 2018/5/17.
 */

public class UpgradeParams extends BaseBean {

    private String softwareVersion; //当前版本号  不为空
    private double ram;	//内存使用情况
    private double cpu;	//cpu使用情况
    private double mainProgramRam;	//主程序所占内存
    private double mainProgramCpu;	//主程序CPU使用情况

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public double getRam() {
        return ram;
    }

    public void setRam(double ram) {
        this.ram = ram;
    }

    public double getCpu() {
        return cpu;
    }

    public void setCpu(double cpu) {
        this.cpu = cpu;
    }

    public double getMainProgramRam() {
        return mainProgramRam;
    }

    public void setMainProgramRam(double mainProgramRam) {
        this.mainProgramRam = mainProgramRam;
    }

    public double getMainProgramCpu() {
        return mainProgramCpu;
    }

    public void setMainProgramCpu(double mainProgramCpu) {
        this.mainProgramCpu = mainProgramCpu;
    }
}

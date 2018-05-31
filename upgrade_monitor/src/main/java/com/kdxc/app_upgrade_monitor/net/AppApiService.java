package com.kdxc.app_upgrade_monitor.net;


import com.kdxc.app_upgrade_monitor.net.bean.ReportUpdateInfo;
import com.kdxc.app_upgrade_monitor.net.bean.TimeBean;
import com.kdxc.app_upgrade_monitor.net.bean.UpgradeInfoBean;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;


public interface AppApiService {

    /**
     * 获取时间戳
     */
    @GET("/commonmgr/api/sys/getSysTime")
    Observable<HttpResultV1<TimeBean>> getSysTime();

    //上传魔镜信息
    @POST("/commonmgr/api/software/recordMirrorInfo/{macAddress}")
    Observable<HttpResultV1<UpgradeInfoBean>> recordMirrorInfo(
            @Path("macAddress") String macAddress,
            @Body RequestBody recordMirrorInfo
    );

    //报告应用更新状况
    @POST("/commonmgr/api/software/reportUpdateInfo/{dcId}/{macAddress}")
    Observable<HttpResultV1<ReportUpdateInfo>> reportUpdateInfo(
            @Path("dcId") int dcId,
            @Path("macAddress") String macAddress,
            @Body RequestBody reportUpdateInfo
    );
}


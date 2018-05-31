package com.kdxc.app_upgrade_monitor.net;

import com.kdxc.app_upgrade_monitor.Utils.NetworkInfoHelper;
import com.kdxc.app_upgrade_monitor.app.AppApiContact;
import com.kdxc.app_upgrade_monitor.net.bean.ReportUpdateInfo;
import com.kdxc.app_upgrade_monitor.net.bean.TimeBean;
import com.kdxc.app_upgrade_monitor.net.bean.UpgradeInfoBean;
import com.kdxc.app_upgrade_monitor.net.params.ReportUpdateInfoParams;
import com.kdxc.app_upgrade_monitor.net.params.UpgradeParams;
import com.kdxc.app_upgrade_monitor.sdk.UpgradeMonitor;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Author:    Xiao_Tian
 * Version    V1.0
 * Date:      16/10/9 10:32
 * Description: 网络请求的封装类
 * Why & What is modified:
 */
public class HttpApiMethods {
    static volatile HttpApiMethods singleton = null;
    private AppApiService appApiService;
    public static final String NETWORK_ERROR_MSG = "没有网络连接,请打开你的网络连接";
    public static final String DEFAULT_ERROR_MSG = "网络不给力,请稍后重试~";

    //构造方法私有
    private HttpApiMethods(AppApiService appApiService) {
        this.appApiService = appApiService;
    }

    //在访问HttpMethods时创建单例
    public static HttpApiMethods with(AppApiService appApiService) {
        if (singleton == null) {
            synchronized (HttpApiMethods.class) {
                if (singleton == null) {
                    singleton = new Builder(appApiService).build();
                }
            }
        }
        return singleton;
    }


    public static class Builder {
        private AppApiService appApiService;

        public Builder(AppApiService appApiService) {
            if (appApiService == null) {
                throw new IllegalArgumentException("AppApiService must not be null.");
            }
            this.appApiService = appApiService;
        }

        public HttpApiMethods build() {
            return new HttpApiMethods(appApiService);
        }
    }

    private static final Observable.Transformer ioTransformer = new Observable.Transformer() {
        @Override
        public Object call(Object observable) {
            return ((Observable) observable).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    private void toSubscribe(Observable<?> observable, Subscriber subscriber) {
        if (checkNetwork(subscriber)) {
            observable.compose(applyIoSchedulers())
                    .subscribe(subscriber);
        }
    }

    public static <T> Observable.Transformer<T, T> applyIoSchedulers() {
        return (Observable.Transformer<T, T>) ioTransformer;
    }

    /**
     * 创建文本类型的 RequestBody
     *
     * @param json 文本数据
     */
    private RequestBody createRequestBody(String json) {
        return RequestBody.create(MediaType.parse(AppApiContact.HTTP_INPUT_TYPE), json);

    }


    /**
     * 检查是否有网络连接
     *
     * @param subscriber 订阅事件
     * @return true 有网络连接
     */
    protected boolean checkNetwork(Subscriber subscriber) {
        if (!NetworkInfoHelper.isOnline(UpgradeMonitor.getInstance().getmContext())) {
            subscriber.onError(new ApiException(NETWORK_ERROR_MSG));
            return false;
        }
        return true;
    }


    /**
     * 获取服务器的时间戳
     *
     * @param subscriber
     */
    public void getSysTime(Subscriber<TimeBean> subscriber) {
        toSubscribe(appApiService.getSysTime().
                        compose(new RedirectResponseV1Transformer<TimeBean>()),
                subscriber);
    }


    //记录魔镜信息 以及查询是否需要更新应用
    public void recordMirrorInfo(Subscriber<UpgradeInfoBean>subscriber, String macAddress, UpgradeParams params) {
        toSubscribe(appApiService.recordMirrorInfo(macAddress, createRequestBody(params.toJson())).
                        compose(new RedirectResponseV1Transformer<UpgradeInfoBean>()),
                subscriber);
    }

    //报告应用更新状况
    public void reportUpdateInfo(Subscriber<ReportUpdateInfo>subscriber, int dcId, String macAddress, ReportUpdateInfoParams params) {
        toSubscribe(appApiService.reportUpdateInfo(dcId, macAddress, createRequestBody(params.toJson())).
                        compose(new RedirectResponseV1Transformer<ReportUpdateInfo>()),
                subscriber);
    }
}

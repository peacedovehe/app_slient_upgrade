package com.kdxc.app_upgrade_monitor.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.kdxc.app_upgrade_monitor.Utils.NetworkInfoHelper;
import com.kdxc.app_upgrade_monitor.Utils.SharedPreferencesHelper;
import com.kdxc.app_upgrade_monitor.Utils.SignatureHelper;
import com.kdxc.app_upgrade_monitor.app.AppApiContact;
import com.kdxc.app_upgrade_monitor.net.APIUrl;
import com.kdxc.app_upgrade_monitor.net.AppApiService;
import com.kdxc.app_upgrade_monitor.net.HttpApiMethods;
import com.kdxc.app_upgrade_monitor.net.TokenInterceptor;
import com.kdxc.app_upgrade_monitor.service.UpgradeMonitorService;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by phe on 2018/5/21.
 */

public class UpgradeMonitor {

    private static final String TAG = "UpgradeMonitor";
    private MyConnection sc;
    private Context mContext;
    private UpgradeMonitorService.MyBinder serviceBinder;
    private static UpgradeMonitor instance = null;
    private static final int DEFAULT_TIMEOUT = 15;
    private HttpApiMethods httpApiMethods;


    private UpgradeMonitor () {

    }

    public static synchronized UpgradeMonitor getInstance() {
        if (instance == null) {
            synchronized (UpgradeMonitor.class) {
                if (instance == null) {
                    instance = new UpgradeMonitor();
                }
            }
        }

        return instance;
    }

    public void initUpgradeMonitor(Context context) {
        mContext = context;
        httpApiMethods = buildHttpApiMethods();
        SharedPreferencesHelper.getInstance().Builder(context);
//        Intent intent = new Intent(mContext, UpgradeMonitorService.class);
//        mContext.startService(intent);
        //bindService();
    }

    public void startUpgradeMonitor(Context context) {
        Intent intent = new Intent(context, UpgradeMonitorService.class);
        context.startService(intent);
    }

    public String getSDKVersion() {
        return AppApiContact.VERSION_NAME;
    }

    private void bindService() {
        sc = new MyConnection();
        Intent intent = new Intent(mContext, UpgradeMonitorService.class);
        mContext.bindService(intent, sc, Context.BIND_AUTO_CREATE);
    }

    private class MyConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.i(TAG, "Main activity binding service");
            serviceBinder = (UpgradeMonitorService.MyBinder) binder;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w(TAG, "service disconnected from Main activity");
        }
    }

    private HttpApiMethods buildHttpApiMethods() {

        Interceptor requestInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                long time = SignatureHelper.getTime();
                String url = request.url().toString();
                int i = url.lastIndexOf("/commonmgr");
                String substring = url.substring(i,url.length());
                String signature = SignatureHelper.getSignature(substring, time);
                //LogUtils.d("url", url);
                return chain.proceed(request.newBuilder()
                        .addHeader("timestamp", String.valueOf(time))
                        .addHeader("signature", signature)
                        .addHeader("deviceClass", AppApiContact.DEVICE_CLASS)
                        .addHeader("token", SharedPreferencesHelper.getInstance().getToken())
                        .build());
            }
        };

        Interceptor cacheInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                String CACHE_CONTROL = "Cache-Control";
                Response originalResponse = chain.proceed(chain.request());
                if (NetworkInfoHelper.isNetworkAvailable(mContext)) {
                    int maxAge = 60; // 在线缓存在1分钟内可读取
                    return originalResponse.newBuilder()
                            .removeHeader("Pragma")
                            .removeHeader(CACHE_CONTROL)
                            .header(CACHE_CONTROL, "public, max-age=" + maxAge)
                            .build();
                } else {
                    int maxStale = 60 * 60 * 24 * 28; // 离线时缓存保存4周
                    return originalResponse.newBuilder()
                            .removeHeader("Pragma")
                            .removeHeader(CACHE_CONTROL)
                            .header(CACHE_CONTROL, "public, only-if-cached, max-stale=" + maxStale)
                            .build();
                }
            }
        };

        File httpCacheDirectory = new File(mContext.getCacheDir(), "KDXCCache");
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(cacheInterceptor)
                .addInterceptor(cacheInterceptor)
                .addInterceptor(requestInterceptor)
                .addInterceptor(new TokenInterceptor())
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .cache(cache)
                .build();

        AppApiService service = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_API_URL)//"http://139.196.148.201:8080"
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(AppApiService.class);
        return HttpApiMethods.with(service);
    }

    public HttpApiMethods getHttpApiMethods() {
        return httpApiMethods;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setBaseApiUrl(String url) {
        APIUrl.BASE_API_URL = url;
    }
}

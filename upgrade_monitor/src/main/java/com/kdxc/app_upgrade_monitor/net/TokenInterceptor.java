package com.kdxc.app_upgrade_monitor.net;

import com.google.gson.Gson;
import com.kdxc.app_upgrade_monitor.net.bean.base.BaseMetaV1;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class TokenInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        /**通过如下的办法曲线取到请求完成的数据
         *
         * 原本想通过  originalResponse.body().string()
         * 去取到请求完成的数据,但是一直报错,不知道是okhttp的bug还是操作不当
         *
         * 然后去看了okhttp的源码,找到了这个曲线方法,取到请求完成的数据后,根据特定的判断条件去判断token过期
         */

        ResponseBody responseBody = response.body();
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer();
        Charset charset = Charset.forName("UTF-8");
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(Charset.forName("UTF-8"));
        }
        String bodyString = buffer.clone().readString(charset);

//        if (isTokenExpired(bodyString)) {//根据和服务端的约定判断token过期
//
//            //同步请求方式，获取最新的Token
//            String newSession = getNewToken();
//            if (StringHelper.notEmpty(newSession)) {
//                SharedPreferencesHelper.getInstance().setToken(newSession);
//                //使用新的Token，创建新的请求
//                Request newRequest = chain.request()
//                        .newBuilder()
//                        .header("token", newSession)
//                        .build();
//                //重新请求
//                return chain.proceed(newRequest);
//            }
//        }
        return response;
    }

    /**
     * 根据服务器的约定，判断Token是否失效
     */
    private boolean isTokenExpired(String response) {
        Gson gson = new Gson();
        BaseMetaV1 meta = gson.fromJson(response, BaseMetaV1.class);
        return meta.isOverdueToken();
    }

    /**
     * 同步请求方式，获取最新的Token
     */
//    private String getNewToken() throws IOException {
//
//        return new RefreshTokenModel().getNewToken();
//    }


}

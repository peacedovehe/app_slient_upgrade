package com.kdxc.app_upgrade_monitor.net;

import com.kdxc.app_upgrade_monitor.Utils.StringHelper;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RedirectResponseV1Transformer<T> implements Observable.Transformer<HttpResultV1<T>, T> {
    @Override
    public Observable<T> call(Observable<HttpResultV1<T>> httpResultV1Observable) {
        return httpResultV1Observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .lift(new Observable.Operator<T, HttpResultV1<T>>() {
                    @Override
                    public Subscriber<? super HttpResultV1<T>> call(
                            final Subscriber<? super T> subscriber) {
                        return new Subscriber<HttpResultV1<T>>() {
                            @Override
                            public void onCompleted() {
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onError(Throwable e) {
                                subscriber.onError(e);
                            }

                            @Override
                            public void onNext(HttpResultV1<T> httpResult) {
                                //LogUtils.json("http", httpResult.toJson());
//                                if (httpResult.isRenew()){
//                                    //重新登录
//                                    EventBus.getDefault().post(new ReLoginEvent());
//                                }
                                if (httpResult.isSuccess()) {
                                    subscriber.onNext(httpResult.getDatas());
                                } else {
                                    String msg = HttpApiMethods.DEFAULT_ERROR_MSG;
                                    if (StringHelper.notEmpty(httpResult.getMsg())) {
                                        msg = httpResult.getMsg();
                                    }
                                    subscriber.onError(new ApiException(msg));
                                }
                            }
                        };
                    }
                });
    }
}

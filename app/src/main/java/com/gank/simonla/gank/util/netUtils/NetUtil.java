package com.gank.simonla.gank.util.netUtils;

import com.gank.simonla.gank.data.bean.Girl;
import com.gank.simonla.gank.data.bean.RemoteGirlBean;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by simonla on 2016/7/6.
 * Have a good day.
 */
public class NetUtil {

    private static final int DEFAULT_TIMEOUT = 5;

    private Retrofit mRetrofit;
    private INetUtil mINetUtil;

    public NetUtil() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        mRetrofit = new Retrofit.Builder()
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl("http://gank.io/api/data/")
                .build();
        mINetUtil = mRetrofit.create(INetUtil.class);
    }

    private static class SingletonHolder {
        public static final NetUtil INSTANCE = new NetUtil();
    }

    public static NetUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void getGirls(Subscriber<RemoteGirlBean.ResultsBean> subscriber, int page) {
        mINetUtil.getGirls(page)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<RemoteGirlBean, Observable<RemoteGirlBean.ResultsBean>>() {
                    @Override
                    public Observable<RemoteGirlBean.ResultsBean> call(RemoteGirlBean girl) {
                        return Observable.from(girl.getResults());
                    }
                })
                .subscribe(subscriber);
    }
}

package com.gank.simonla.gank.data;

import android.support.annotation.NonNull;

import com.gank.simonla.gank.data.bean.Girl;

import rx.Subscriber;

/**
 * Created by simonla on 2016/7/4.
 * Have a good day.
 */
public interface GirlDataSource {

    void getGirls(@NonNull Subscriber<Girl> subscriber, int page);



}

package com.gank.simonla.gank.data.remote;

import android.support.annotation.NonNull;

import com.gank.simonla.gank.data.GirlDataSource;
import com.gank.simonla.gank.data.bean.Girl;

import rx.Subscriber;

/**
 * Created by simonla on 2016/7/4.
 * Have a good day.
 */
public class GirlDataRemoteRepository implements GirlDataSource {

    private static GirlDataRemoteRepository INSTANCE = null;

    private GirlDataRemoteRepository() {}

    public static GirlDataRemoteRepository getInstance() {
        if (INSTANCE == null) {
            synchronized (GirlDataRemoteRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new GirlDataRemoteRepository();
                }
            }
        }
        return INSTANCE;
    }

    @Override

    public void getGirls(@NonNull Subscriber<Girl> subscriber, int page) {

    }
}

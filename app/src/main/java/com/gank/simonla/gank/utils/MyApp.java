package com.gank.simonla.gank.utils;

import android.app.Application;

import com.gank.simonla.gank.R;
import com.mobile.simonla.library.PhotoLibrary.PhotoLoader;

/**
 * Created by asus on 2016/4/3.
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PhotoLoader.init(getApplicationContext());
        PhotoLoader.setLoadDefault(R.drawable.ic_loading);
        PhotoLoader.setFailDefault(R.drawable.ic_fail);
        PhotoLoader.setCompressionRatio(100);
        PhotoLoader.setResamplingRate(2);
        PhotoLoader.setIsFailTouchToReload(false);
        com.gank.simonla.gank.utils.PhotoLibrary.PhotoLoader.init(getApplicationContext());
        com.gank.simonla.gank.utils.PhotoLibrary.PhotoLoader.setCompressionRatio(100);
        com.gank.simonla.gank.utils.PhotoLibrary.PhotoLoader.setLoadDefault(R.drawable.ic_loading);
        com.gank.simonla.gank.utils.PhotoLibrary.PhotoLoader.setFailDefault(R.drawable.ic_fail);
        com.gank.simonla.gank.utils.PhotoLibrary.PhotoLoader.setIsFailTouchToReload(false);
        com.gank.simonla.gank.utils.PhotoLibrary.PhotoLoader.setResamplingRate(0);
    }
}

package com.gank.simonla.gank.utils;

import android.app.Application;

import com.gank.simonla.gank.R;
import com.gank.simonla.gank.utils.PhotoLibrary.PhotoLoader;

/**
 * Created by asus on 2016/4/3.
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PhotoLoader.init(getApplicationContext());
        PhotoLoader.setLoadDefault(R.drawable.ic_loading);
    }
}

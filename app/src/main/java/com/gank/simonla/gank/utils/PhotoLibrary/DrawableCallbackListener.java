package com.gank.simonla.gank.utils.PhotoLibrary;

import android.graphics.Bitmap;

/**
 * Created by Simonla on 2016/3/28.
 */
public interface DrawableCallbackListener {
    void onBitmapFinish(Bitmap response);

    void onError(Exception e);
}

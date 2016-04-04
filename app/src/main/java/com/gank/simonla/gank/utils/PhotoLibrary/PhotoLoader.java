package com.gank.simonla.gank.utils.PhotoLibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by Simonla on 2016/3/28.
 */
public class PhotoLoader {

    private static Context sContext;
    private static int sResLoad;
    private static int sResFail;
    private static int sCompressionRatio = 100;
    public static int mPos;

    public static void init(Context context) {
        sContext = context;
    }

    public static void setLoadDefault(int res) {
        sResLoad = res;
    }

    public static void setFailDefault(int res) {
        sResFail = res;
    }

    public static void setCompressionRatio(int compressionRatio) {
        sCompressionRatio = compressionRatio;
    }

    public static void open(final String url, final ImageView iv, final int pos) {
        mPos = pos;
        new Thread(new Runnable() {
            @Override
            public void run() {
                getHttpBitmap(url, iv,new DrawableCallbackListener() {
                            @Override
                            public void onBitmapFinish(final Bitmap response) {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        iv.setImageBitmap(response);
                                    }
                                });
                            }

                            @Override
                            public void onError(Exception e) {
                                e.printStackTrace();
                            }
                        }
                );
            }
        }).start();
    }

    private static Bitmap getHttpBitmap(final String address, final ImageView iv, final DrawableCallbackListener listener) {
        if (address == null) {
            iv.setImageResource(sResLoad);
        } else {
            final int a = address.hashCode();
            if (getBitmapFromNative(a) != null) {
                listener.onBitmapFinish(getBitmapFromNative(a));
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        iv.setImageResource(sResLoad);
                    }
                });
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setUseCaches(true);
                    connection.setConnectTimeout(4000);
                    connection.setReadTimeout(4000);
                    InputStream in = connection.getInputStream();
                    Bitmap response = BitmapFactory.decodeStream(in);
                    saveFile(a, response);
                    in.close();
                    if (listener != null) {
                        listener.onBitmapFinish(response);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }
        return null;
    }

    private static void saveFile(int name, Bitmap bitmap) {
        FileOutputStream outputStream = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, out);
        byte[] result = out.toByteArray();
        try {
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            outputStream = sContext.openFileOutput(String.valueOf(name), Context.MODE_PRIVATE);
            outputStream.write(result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Bitmap getBitmapFromNative(int name) {
        FileInputStream in = null;
        try {
            in = sContext.openFileInput(String.valueOf(name));
            return BitmapFactory.decodeStream(in);
        } catch (Exception ignored) {
        }
        try {
            if (in != null) {
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface DrawableCallbackListener {
        void onBitmapFinish(Bitmap response);

        void onError(Exception e);
    }

}

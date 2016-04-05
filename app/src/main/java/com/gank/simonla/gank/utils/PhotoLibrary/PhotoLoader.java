package com.gank.simonla.gank.utils.PhotoLibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by Simonla on 2016/3/28.
 */
public class PhotoLoader {

    public static final String TAG = "PhotoLoader";
    private static Context sContext;
    private static int sResLoad;
    private static int sResFail;
    private static int sCompressionRatio = 100;
    private static boolean sIsFailTouchToReload = false;


    //链式调用什么的会有的
    public static void init(Context context) {
        sContext = context;
    }

    public static void setIsFailTouchToReload(boolean isFailTouchToReload) {
        sIsFailTouchToReload = isFailTouchToReload;
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

    public static void open(final String url, final ImageView iv) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getHttpBitmap(url, iv, new DrawableCallbackListener() {
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
                            public void onError(final Exception e) {
                                e.printStackTrace();
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        iv.setImageResource(sResFail);
                                        reLoad();
                                    }

                                    private void reLoad() {
                                        if (sIsFailTouchToReload) {
                                            iv.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    open(url, iv);
                                                    Toast.makeText(sContext, "请检查网络", Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, "onClick: " + e);
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                );
            }
        }).start();
    }

    private static Bitmap getHttpBitmap(final String address, final ImageView iv, final DrawableCallbackListener listener) {
        if (address == null) {
            iv.setImageResource(sResFail);
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
                    connection.setConnectTimeout(2000);
                    connection.setReadTimeout(200);
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
        bitmap.compress(Bitmap.CompressFormat.JPEG, sCompressionRatio, out);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            //TODO 获取异常种类进行处理
        }
        return null;
    }

    public interface DrawableCallbackListener {
        void onBitmapFinish(Bitmap response);

        void onError(Exception e);
    }

}

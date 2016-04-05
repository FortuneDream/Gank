package com.gank.simonla.gank.utils.PhotoLibrary;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.gank.simonla.gank.R;

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

    private static boolean sIsFailTouchToReload = false;
    private static Context sContext;
    private static int sResLoad;
    private static int sResFail;
    private static int sCompressionRatio = 100;

    public static void setIsFailTouchToReload(boolean isFailTouchToReload) {
        sIsFailTouchToReload = isFailTouchToReload;
    }

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

    // 重载一个不要外部处理的，就和你原来一样了
    public static void open(String url, ImageView iv) {
        open(url, iv, null);
    }

    // 你应该放一个回调出去……否则下载完了我想干点别的事情都不行了
    public static void open(final String url, final ImageView iv, final DrawableCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getHttpBitmap(url, iv, new DrawableCallbackListener() {
                            @Override
                            public void onBitmapFinish(final Bitmap response) {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 如果tag对应上了
                                        if (iv.getTag() == url) {
                                            iv.setImageBitmap(response);
                                            // 让外面也可以自己添加额外的操作
                                            if (listener != null) listener.onBitmapFinish(response);
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onError(final Exception e) {
                                e.printStackTrace();
                                //如果发生错误可以点击重新加载
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(sContext, "出现错误，点击重新加载：" + e, Toast.LENGTH_SHORT).show();
                                    }
                                });
                                reLoad(iv, url);
                            }
                        }
                );
            }
        }).start();
    }

    private static void getHttpBitmap(final String address, final ImageView iv, final DrawableCallbackListener listener) {
        if (address == null) {
            iv.setImageResource(sResFail);
        } else {
            final int a = address.hashCode();
            if (getBitmapFromNative(a) !=null) {
                if (listener != null) {
                    listener.onBitmapFinish(getBitmapFromNative(a));
                }
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
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);
                    InputStream in = connection.getInputStream();
                    Bitmap response = BitmapFactory.decodeStream(in);
                    in.close();
                    if (listener != null) {
                        saveFile(a, response);
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
    }

    private static void saveFile(int name, Bitmap bitmap) {
        FileOutputStream outputStream = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, sCompressionRatio, out);
        }
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

    private static void reLoad(final ImageView iv, final String url) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                iv.setImageResource(sResFail);
            }
        });
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(sContext, "重新加载中", Toast.LENGTH_SHORT).show();
                        loadAgain();
                    }
                    private void loadAgain() {
                        if (sIsFailTouchToReload) {
                            iv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    open(url, iv);
                                }
                            });
                        }
                    }
                });
            }
        });
    }


    public interface DrawableCallbackListener {
        void onBitmapFinish(Bitmap response);

        void onError(Exception e);
    }

}

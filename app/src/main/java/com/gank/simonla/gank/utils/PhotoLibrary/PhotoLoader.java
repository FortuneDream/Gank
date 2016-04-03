package com.gank.simonla.gank.utils.PhotoLibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
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
    private static final String B_SEND_RESPONSE = "send";
    private static final int LOADING = 3;
    private static final int LOAD_ERROR = 0;
    private static final int LOAD_RIGHT = 1;
    private static ImageView sImageView;
    private static int sResLoad;
    private static int sResFail;

    public static void init(Context context) {
        sContext = context;
    }

    public static void setLoadDefault(int res) {
        sResLoad = res;
    }

    public static void setFailDefault(int res) {
        sResFail = res;
    }

    public static void open(final String url, final ImageView iv) {
        sImageView = iv;
        sImageView.setImageResource(sResLoad);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getHttpBitmap(url, new DrawableCallbackListener() {
                            @Override
                            public void onBitmapFinish(Bitmap response) {
                                Bundle bundle = new Bundle();
                                bundle.putParcelable(B_SEND_RESPONSE, response);
                                Message message = new Message();
                                message.setData(bundle);
                                message.what = LOAD_RIGHT;
                                mHandler.sendMessage(message);
                            }

                            @Override
                            public void onError(Exception e) {
                                Message message = new Message();
                                message.what = LOAD_ERROR;
                                mHandler.sendMessage(message);
                                e.printStackTrace();
                            }
                        }
                );
            }
        }).start();
    }

    private static Bitmap getHttpBitmap(final String address, final DrawableCallbackListener listener) {
        if (address == null) {
            sImageView.setImageResource(sResLoad);
        } else {
            final int a = address.hashCode();
            if (getBitmapFromNative(a) != null) {
                if (listener != null) {
                    listener.onBitmapFinish(getBitmapFromNative(a));
                }
            } else {
                //  new Thread(new Runnable() {
                //    @Override
                //  public void run() {
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
            //  }).start();
//        }
        }
        return null;
    }

    private static void saveFile(int name, Bitmap bitmap) {
        FileOutputStream outputStream = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, out);
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

    private static android.os.Handler mHandler = new android.os.Handler(Looper.getMainLooper()) {
        public void handleMessage(Message message) {
            switch (message.what) {
                case LOAD_RIGHT:
                    Bitmap bitmap = message.getData().getParcelable(B_SEND_RESPONSE);
                    sImageView.setImageBitmap(bitmap);
                case LOAD_ERROR:
                    //  sImageView.setImageResource(R.drawable.fail_load);
            }
        }
    };

    public interface DrawableCallbackListener {
        void onBitmapFinish(Bitmap response);

        void onError(Exception e);
    }

}

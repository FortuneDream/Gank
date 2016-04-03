package com.gank.simonla.gank.bean;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.gank.simonla.gank.utils.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by asus on 2016/4/3.
 */
public class GirlsLab {

    public static final String TAG = "GirlsLab";

    public static final String ADDRESS = "http://gank.io/api/data/%E7%A6%8F%E5%88%A9";
    private static GirlsLab sGirlsLab;
    private Context mContext;
    private ArrayList<Girls.ResultsBean> mGirls;

    private GirlsLab(Context context) {
        mContext = context;
        mGirls = new ArrayList<Girls.ResultsBean>();
    }

    public static GirlsLab get(Context context) {
        if (sGirlsLab == null) {
            sGirlsLab = new GirlsLab(context.getApplicationContext());
        }
        return sGirlsLab;
    }

    public ArrayList<Girls.ResultsBean> getGirls() {
        return mGirls;
    }

    public void getGirlsFromWeb(int count, int page, final FinishListener listener) {
        HttpUtil.get(ADDRESS + "/" + count + "/" + page, new HttpUtil.HttpCallbackListener() {
            @Override
            public void onFinish(String response) throws InterruptedException {
                parseJSONWithJSONObject(response);
                if (listener != null) {
                    listener.onFinish();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void parseJSONWithJSONObject(String jsonData) {
        try {
            JSONObject object = new JSONObject(jsonData);
            JSONArray jsonArray = object.getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Girls.ResultsBean girl = new Girls.ResultsBean();
                girl.set_id(jsonObject.getString("_id"));
                girl.set_ns(jsonObject.getString("_ns"));
                girl.setCreatedAt(jsonObject.getString("createdAt"));
                girl.setDesc(jsonObject.getString("desc"));
                girl.setPublishedAt(jsonObject.getString("publishedAt"));
                girl.setUrl(jsonObject.getString("url"));
                girl.setType(jsonObject.getString("type"));
                girl.setWho(jsonObject.getString("who"));
                girl.setSource(jsonObject.getString("source"));
                girl.setUsed(jsonObject.getBoolean("used"));
                mGirls.add(girl);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface FinishListener{
        void onFinish();
    }
}

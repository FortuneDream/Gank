package com.gank.simonla.gank.girlList;

import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gank.simonla.gank.R;
import com.gank.simonla.gank.data.bean.RemoteGirlBean;
import com.gank.simonla.gank.util.photoLibrary.PhotoLoader;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by simonla on 2016/7/11.
 * Have a good day.
 */
public class GirlListAdapter extends RecyclerView.Adapter<GirlListAdapter.ViewHolder> {

    private ArrayList<RemoteGirlBean.ResultsBean> mGirls;

    public static final String TAG = "GirlListAdapter";

    public GirlListAdapter(ArrayList<RemoteGirlBean.ResultsBean> girls) {
        mGirls = girls;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_girls_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final RemoteGirlBean.ResultsBean girl = mGirls.get(position);
        final String url = girl.getUrl();
        final ImageView photo = holder.mIvGirlPhoto;

        Log.d(TAG, "onBindViewHolder: " + url);

        photo.setTag(url);
        PhotoLoader.open(url, photo, new PhotoLoader.DrawableCallbackListener() {
            @Override
            public void onBitmapFinish(Bitmap response) {
                photo.setImageBitmap(response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGirls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_girl_photo)
        ImageView mIvGirlPhoto;
        @BindView(R.id.cv_item_girl)
        CardView mCvItemGirl;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

package com.gank.simonla.gank.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.gank.simonla.gank.R;
import com.gank.simonla.gank.bean.Girls;
import com.mobile.simonla.library.PhotoLibrary.PhotoLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by asus on 2016/4/3.
 */
public class GirlsAdapter extends RecyclerView.Adapter<GirlsAdapter.ViewHolder> {

    private ArrayList<Girls.ResultsBean> mGirlsList;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    // 存放图片的比例
    private Map<String, Double> mPicRatios = new HashMap<>();
    private int screenWidth;

    public GirlsAdapter(ArrayList<Girls.ResultsBean> girlsList) {
        mGirlsList = girlsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 这里得到一下屏幕的宽度，不优雅，但我是不想写Context了，直接这里获取
        screenWidth = getScreenWidth(parent.getContext());
        return new ViewHolder(LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_girls_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        final Girls.ResultsBean girl = mGirlsList.get(position);
        final String url = girl.getUrl();
        final Bitmap bitmap = girl.getBitmap();
        CardView cardView = holder.mCardView;
        final ImageView photo = holder.mPhoto;
        if (bitmap != null) {
            photo.setImageBitmap(bitmap);
        } else {
            // 其实你的顺序还是会错乱的，这里还是给iv一个tag
            photo.setTag(url);
            // 给iv清空图片，否则看到图片变动感觉和跳加载错乱跳动一样
            photo.setImageResource(R.drawable.ic_loading);
            // 如果事先保存了图片的比例就给iv设置相应的高度
            if (mPicRatios.get(url) != null) {
                FrameLayout.LayoutParams rlp = (FrameLayout.LayoutParams) photo.getLayoutParams();
                // 这里为什么用屏幕的宽度/2, 不用rlp.width呢？因为你cardView的width是MATCH_PARENT
                // 并不是一个具体值，返回的是 -1, 当滑动非常快的时候还是会有一点item移动
                // 为什么？因为iv的宽小于屏幕/2, cardView那家伙还有边距的嘛，这个我就不算了
                rlp.height = (int) (mPicRatios.get(url) * screenWidth / 2);
                holder.mPhoto.setLayoutParams(rlp);
            }

            if (mOnItemClickListener != null) {
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(holder.itemView, position);
                    }
                });
            }
            if (mOnItemLongClickListener != null) {
                cardView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mOnItemLongClickListener.onItemLongClick(holder.itemView, position);
                        return true;
                    }
                });
            }

            PhotoLoader.open(url, photo, new PhotoLoader.DrawableCallbackListener() {
                @Override
                public void onBitmapFinish(Bitmap response) {
                    // 当下载完bitmap，把它的比例给记下来
                    if (response != null) {
                        mPicRatios.put(url, response.getHeight() / (double) response.getWidth());
                    }
                    //   photo.setImageBitmap(FastBlurUtil.doBlur(response, 8, false));
                }

                @Override
                public void onError(Exception e) {
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mGirlsList != null) {
            return mGirlsList.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mPhoto;
        public CardView mCardView;

        public ViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.cv_item_girl);
            mPhoto = (ImageView) itemView.findViewById(R.id.iv_girl_photo);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    // 这个工具我就先给你写在这里吧
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }
}
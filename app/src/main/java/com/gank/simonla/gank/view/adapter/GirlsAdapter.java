package com.gank.simonla.gank.view.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gank.simonla.gank.R;
import com.gank.simonla.gank.bean.Girls;
import com.gank.simonla.gank.utils.PhotoLibrary.PhotoLoader;

import java.util.ArrayList;

/**
 * Created by asus on 2016/4/3.
 */
public class GirlsAdapter extends RecyclerView.Adapter<GirlsAdapter.ViewHolder> {

    private ArrayList<Girls.ResultsBean> mGirlsList;
    public static final String TAG = "GirlsAdapter";
    private OnItemClickListener mOnItemClickListener;

    public GirlsAdapter(ArrayList<Girls.ResultsBean> girlsList) {
        mGirlsList = girlsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_girls_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CardView cardView = holder.mCardView;
        ImageView photo = holder.mPhoto;
        Log.d(TAG, "onBindViewHolder: "+position);
        PhotoLoader.open(mGirlsList.get(position).getUrl(), photo, position);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + position);
            }
        });
       // name.setText(girl.getWho());
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

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}

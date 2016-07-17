package com.gank.simonla.gank.girlList;

import android.support.annotation.NonNull;
import android.util.Log;

import com.gank.simonla.gank.data.GirlDataSource;
import com.gank.simonla.gank.data.bean.RemoteGirlBean;
import com.gank.simonla.gank.util.netUtils.NetUtil;

import java.util.ArrayList;

import rx.Subscriber;

/**
 * Created by simonla on 2016/7/11.
 * Have a good day.
 */
public class GirlListPresenter implements GirlListContract.Presenter {

    @NonNull
    private final GirlDataSource mGirlDataSource;

    @NonNull
    private final GirlListContract.View mView;

    private int mPage = 0;
    private ArrayList<RemoteGirlBean.ResultsBean> mGirls;
    private boolean mIsLoading = false;

    public static final String TAG = "GirlListPresenter";

    public GirlListPresenter(@NonNull GirlDataSource girlDataSource,
                             @NonNull GirlListContract.View view,
                             int page) {
        mGirlDataSource = girlDataSource;
        mView = view;
        mPage = page;
        mGirls = new ArrayList<>();
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        mView.showProgressBar();
        mIsLoading = true;
        Subscriber<RemoteGirlBean.ResultsBean> subscriber = new Subscriber<RemoteGirlBean.ResultsBean>() {
            @Override
            public void onCompleted() {
                mView.showGirls(mGirls);
                mView.cancelProgressBar();
                mPage++;
                mIsLoading = false;
            }

            @Override
            public void onError(Throwable e) {
                mView.showError(e.toString());
                mView.cancelProgressBar();
                mIsLoading = false;
            }

            @Override
            public void onNext(RemoteGirlBean.ResultsBean resultsBean) {
                mGirls.add(resultsBean);
            }
        };

        NetUtil.getInstance().getGirls(subscriber, mPage);
    }

    @Override
    public void getMoreGirls() {
        if (!mIsLoading) {
            mIsLoading = true;
            Subscriber<RemoteGirlBean.ResultsBean> subscriber = new Subscriber<RemoteGirlBean.ResultsBean>() {
                @Override
                public void onCompleted() {
                    mView.showMoreGirls(mGirls);
                    mPage++;
                    mIsLoading = false;
                    Log.d(TAG, "onCompleted: "+mPage);
                }

                @Override
                public void onError(Throwable e) {
                    mView.showError(e.toString());
                    mIsLoading = false;
                }

                @Override
                public void onNext(RemoteGirlBean.ResultsBean resultsBean) {
                    mGirls.add(resultsBean);
                }
            };

            NetUtil.getInstance().getGirls(subscriber, mPage);
        }
    }

    @Override
    public void refresh() {

    }
}

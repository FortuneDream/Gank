package com.gank.simonla.gank.girlList;

import android.support.annotation.NonNull;

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

    private int mPage;
    private ArrayList<RemoteGirlBean.ResultsBean> mGirls;

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
    public void getGirls() {
        start();
    }

    @Override
    public void start() {
        Subscriber<RemoteGirlBean.ResultsBean> subscriber = new Subscriber<RemoteGirlBean.ResultsBean>() {
            @Override
            public void onCompleted() {
                mView.showGirls(mGirls);
            }

            @Override
            public void onError(Throwable e) {
                mView.showError(e.toString());
            }

            @Override
            public void onNext(RemoteGirlBean.ResultsBean resultsBean) {
                mGirls.add(resultsBean);
            }
        };

        NetUtil.getInstance().getGirls(subscriber, mPage);
    }
}

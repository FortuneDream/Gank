package com.gank.simonla.gank.girlList;

import com.gank.simonla.gank.BasePresenter;
import com.gank.simonla.gank.BaseView;
import com.gank.simonla.gank.data.bean.RemoteGirlBean;

import java.util.ArrayList;

/**
 * Created by simonla on 2016/7/11.
 * Have a good day.
 */
public interface GirlListContract {

    interface View extends BaseView<Presenter> {
        void showGirls(ArrayList<RemoteGirlBean.ResultsBean> arrayList);

        void showError(String e);

        void refresh();
    }

    interface Presenter extends BasePresenter {
        void getGirls();
    }

}

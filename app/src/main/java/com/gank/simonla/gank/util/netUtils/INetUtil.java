package com.gank.simonla.gank.util.netUtils;

import com.gank.simonla.gank.data.bean.RemoteGirlBean;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by simonla on 2016/7/6.
 * Have a good day.
 */
public interface INetUtil {

    @GET("%E7%A6%8F%E5%88%A9/10/{page}")
    Observable<RemoteGirlBean> getGirls(
            @Path("page") int page
    );

}

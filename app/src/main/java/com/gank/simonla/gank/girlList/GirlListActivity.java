package com.gank.simonla.gank.girlList;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.Toast;

import com.gank.simonla.gank.R;
import com.gank.simonla.gank.data.bean.RemoteGirlBean;
import com.gank.simonla.gank.data.remote.GirlDataRemoteRepository;
import com.gank.simonla.gank.util.SpacesItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GirlListActivity extends AppCompatActivity implements GirlListContract.View {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.iv_test)
    ImageView mIvTest;
    @BindView(R.id.rv_girls)
    RecyclerView mRvGirls;
    @BindView(R.id.srw_girls)
    SwipeRefreshLayout mSrwGirls;

    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private GirlListContract.Presenter mPresenter;
    private ArrayList<RemoteGirlBean.ResultsBean> mGirls;

    public static final String TAG = "GirlListActivity";

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        setRecyclerView();

        mRvGirls.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = mStaggeredGridLayoutManager.getChildCount();
                int totalItemCount = mStaggeredGridLayoutManager.getItemCount();
                int[] into = new int[2];
                into = mStaggeredGridLayoutManager.findFirstVisibleItemPositions(into);
                int pastVisibleItems = (into[0] + into[1]) / 2;
                if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                    mPresenter.getMoreGirls();
                }
            }
        });

        mSrwGirls = new SwipeRefreshLayout(this);

        mSrwGirls.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

        new GirlListPresenter(
                GirlDataRemoteRepository.getInstance(),
                this,
                0
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
    }

    private void setRecyclerView() {
        mStaggeredGridLayoutManager
                = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRvGirls.setLayoutManager(mStaggeredGridLayoutManager);
        SpacesItemDecoration decoration = new SpacesItemDecoration(16);
        mRvGirls.addItemDecoration(decoration);
    }

    @Override
    public void showGirls(ArrayList<RemoteGirlBean.ResultsBean> arrayList) {
        mGirls = arrayList;
        mRvGirls.setAdapter(new GirlListAdapter(mGirls));
    }

    @Override
    public void showMoreGirls(ArrayList<RemoteGirlBean.ResultsBean> arrayList) {
        mGirls = arrayList;
    }

    @Override
    public void showError(String e) {
        Toast.makeText(GirlListActivity.this, e, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressBar() {
        mSrwGirls.setRefreshing(true);
    }

    @Override
    public void cancelProgressBar() {
        mSrwGirls.setRefreshing(false);
    }

    @Override
    public void setPresenter(GirlListContract.Presenter presenter) {
        mPresenter = presenter;
    }
}

package com.gank.simonla.gank.girlList;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.Toast;

import com.gank.simonla.gank.R;
import com.gank.simonla.gank.data.GirlDataSource;
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

    private int mPage = 0;
    private GirlListContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        setRecyclerView();

        mPresenter=new GirlListPresenter(
                GirlDataRemoteRepository.getInstance(),
                this,
                mPage
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
    }

    private void setRecyclerView() {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRvGirls.setLayoutManager(staggeredGridLayoutManager);
        SpacesItemDecoration decoration = new SpacesItemDecoration(16);
        mRvGirls.addItemDecoration(decoration);
    }

    @Override
    public void showGirls(ArrayList<RemoteGirlBean.ResultsBean> arrayList) {
        mRvGirls.setAdapter(new GirlListAdapter(arrayList));
    }

    @Override
    public void showError(String e) {
        Toast.makeText(GirlListActivity.this, e, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void refresh() {

    }

    @Override
    public void setPresenter(GirlListContract.Presenter presenter) {

    }
}

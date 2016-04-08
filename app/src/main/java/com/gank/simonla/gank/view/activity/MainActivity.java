package com.gank.simonla.gank.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.gank.simonla.gank.R;
import com.gank.simonla.gank.bean.Girls;
import com.gank.simonla.gank.bean.GirlsLab;
import com.gank.simonla.gank.utils.OnVerticalScrollListener;
import com.gank.simonla.gank.utils.SpacesItemDecoration;
import com.gank.simonla.gank.view.adapter.GirlsAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final int ERROR = 1;
    public static final int FINISH = 0;
    public static final int COUNT = 10;
    public static final int UPDATE = 2;
    private Toolbar mToolbar;
    private ArrayList<Girls.ResultsBean> mGirls;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private GirlsAdapter mGirlsAdapter;
    private int mPage = 1;
    private ProgressDialog mProgressDialog;
    private boolean mIsLoading = false;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getGirlsFormLab();
        initView();
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int[] into = new int[2];
                into = mStaggeredGridLayoutManager.findLastVisibleItemPositions(into);
                int lastVisibleItem = Math.max(into[0], into[1]);
                int totalItemCount = mStaggeredGridLayoutManager.getItemCount();
                if (lastVisibleItem >= totalItemCount - 8 && dy > 0) {
                    if (!mIsLoading) {
                        mPage++;
                        mIsLoading = true;
                        GirlsLab.get(MainActivity.this).getGirlsFromWeb(COUNT, mPage, new GirlsLab.FinishListener() {
                            @Override
                            public void onFinish() {
                                Message message = new Message();
                                message.what = UPDATE;
                                sHandler.sendMessage(message);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                    }
                }
            }
        });
    }

    private void setRecyclerView() {
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        SpacesItemDecoration decoration = new SpacesItemDecoration(16);
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setAdapter(mGirlsAdapter = new GirlsAdapter(mGirls));
        mGirlsAdapter.setOnItemClickListener(new GirlsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, DetailedActivity.class);
                startActivity(intent);

            }
        });
    }

    private void getGirlsFormLab() {
        showProgressDialog();
        GirlsLab.get(MainActivity.this).getGirlsFromWeb(COUNT, mPage, new GirlsLab.FinishListener() {
            @Override
            public void onFinish() {
                Message message = new Message();
                message.what = FINISH;
                sHandler.sendMessage(message);
            }

            @Override
            public void onError(Exception e) {
                Message message = new Message();
                message.what = ERROR;
                sHandler.sendMessage(message);
            }
        });
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srw_girls);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_girls);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Handler sHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FINISH:
                    mGirls = GirlsLab.get(MainActivity.this).getGirls();
                    setRecyclerView();
                    mProgressDialog.cancel();
                case ERROR:
                    mIsLoading = false;
                case UPDATE:
                    mGirls = GirlsLab.get(MainActivity.this).getGirls();
                    if (mGirls != null) {
                        if (mGirlsAdapter != null) {
                            for (int i = mGirlsAdapter.getItemCount(); i < mGirlsAdapter.getItemCount() + 10; i++) {
                                mGirlsAdapter.notifyItemChanged(mPage);
                            }
                        }
                    }
                    mIsLoading = false;
            }
        }
    };

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}

package com.gank.simonla.gank.view.activity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.gank.simonla.gank.R;
import com.gank.simonla.gank.bean.Girls;
import com.gank.simonla.gank.bean.GirlsLab;
import com.gank.simonla.gank.utils.SpacesItemDecoration;
import com.gank.simonla.gank.view.adapter.GirlsAdapter;
import com.mobile.simonla.library.FastBlurUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private int firstVisibleItem;
    private int lastVisibleItem;
    public static final int ERROR = 1;
    public static final int FINISH = 0;
    public static final int COUNT = 20;
    public static final int UPDATE = 2;
    private ArrayList<Girls.ResultsBean> mGirls;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private GirlsAdapter mGirlsAdapter;
    private int mPage = 1;
    private ProgressDialog mProgressDialog;
    private boolean mIsLoading = false;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getGirlsFormLab();
        initView();
        setRefresh();
    }

    private void setInterestedFun() {
        mGirlsAdapter.setOnItemClickListener(new GirlsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
            }
        });
        mGirlsAdapter.setOnItemLongClickListener(new GirlsAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View v, final int position) {
                for (int i = firstVisibleItem; i < lastVisibleItem+1; i++) {
                    if (i == position) {
                        continue;
                    }
                    final Girls.ResultsBean girl = mGirls.get(i);
                    final String URL = girl.getUrl();
                    final int finalI = i;
                    com.gank.simonla.gank.utils.PhotoLibrary.PhotoLoader.open(URL, mImageView,
                            new com.gank.simonla.gank.utils.PhotoLibrary
                                    .PhotoLoader.DrawableCallbackListener() {
                        @Override
                        public void onBitmapFinish(Bitmap response) {
                            girl.setBitmap(FastBlurUtil.doBlur(response, 60, false));
                            mGirlsAdapter.notifyItemChanged(finalI);
                        }

                        @Override
                        public void onError(Exception e) {
                        }
                    });
                }
                Snackbar.make(v, R.string.snackBar_load, Snackbar.LENGTH_LONG).setAction("是", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        download(position);
                    }
                }).show();
                cancelBlur();
            }
        });
    }

    private void download(int position) {
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        final String URL = mGirls.get(position).getUrl();
        DownloadManager.Request request = new DownloadManager.Request(Uri
                .parse("http://dl.360safe.com/inst.exe"));
        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "Gank.apk");
        request.setTitle(getString(R.string.notice_title_loading));
        request.setDescription(getString(R.string.notice_description_loading));
        Toast.makeText(MainActivity.this, "ha", Toast.LENGTH_SHORT).show();
    }

    private void cancelBlur() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int i = 0;
                for (Girls.ResultsBean girl : mGirls) {
                    if (girl.getBitmap() != null) {
                        girl.setBitmap(null);
                        mGirlsAdapter.notifyItemChanged(i);
                    }
                    i++;
                }
                setRefresh();
            }
        });
    }

    private void setRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int[] into = new int[2];
                into = mStaggeredGridLayoutManager.findLastVisibleItemPositions(into);
                int[] first = new int[2];
                first = mStaggeredGridLayoutManager.findFirstVisibleItemPositions(first);
                firstVisibleItem = Math.min(first[0], first[1]);
                lastVisibleItem = Math.max(into[0], into[1]);
                int totalItemCount = mStaggeredGridLayoutManager.getItemCount();
                if (lastVisibleItem >= totalItemCount - 8 && dy > 0) {
                    if (!mIsLoading) {
                        mPage++;
                        mIsLoading = true;
                        GirlsLab.get(MainActivity.this).getGirlsFromWeb(COUNT, mPage,
                                new GirlsLab.FinishListener() {
                            @Override
                            public void onFinish() {
                                Message message = new Message();
                                message.what = UPDATE;
                                sHandler.sendMessage(message);
                                cancelBlur();
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
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager
                .VERTICAL);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        SpacesItemDecoration decoration = new SpacesItemDecoration(16);
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setAdapter(mGirlsAdapter = new GirlsAdapter(mGirls));
        setInterestedFun();
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srw_girls);
        mImageView = (ImageView) findViewById(R.id.iv_test);
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
                            for (int i = mGirlsAdapter.getItemCount(); i < mGirlsAdapter
                                    .getItemCount() + 10; i++) {
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

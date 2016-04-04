package com.gank.simonla.gank.view.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.SwipeDismissBehavior;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gank.simonla.gank.R;
import com.gank.simonla.gank.bean.Girls;
import com.gank.simonla.gank.bean.GirlsLab;
import com.gank.simonla.gank.utils.SpacesItemDecoration;
import com.gank.simonla.gank.view.adapter.GirlsAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final int ERROR = 1;
    public static final int FINISH = 0;
    private Toolbar mToolbar;
    private ArrayList<Girls.ResultsBean> mGirls;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private GirlsAdapter mGirlsAdapter;
    private ProgressBar mProgressBar;
    private String mError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getGirlsFormLab();
        initView();
    }

    private void setRecyclerView() {
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
       //  mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        SpacesItemDecoration decoration = new SpacesItemDecoration(16);
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setAdapter(mGirlsAdapter = new GirlsAdapter(mGirls));
        mGirlsAdapter.setOnItemClickListener(new GirlsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                //TODO
            }
        });
    }

    private void getGirlsFormLab() {
        GirlsLab.get(MainActivity.this).getGirlsFromWeb(20, 1, new GirlsLab.FinishListener() {
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
                mError = e.toString();
            }
        });
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_main);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srw_girls);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_girls);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
                    mProgressBar.setVisibility(View.GONE);
                    mGirlsAdapter.notifyDataSetChanged();
                case ERROR:
                    Toast.makeText(MainActivity.this, "出现错误: " + mError, Toast.LENGTH_SHORT).show();
            }
        }
    };
}

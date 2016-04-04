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

import com.gank.simonla.gank.R;
import com.gank.simonla.gank.bean.Girls;
import com.gank.simonla.gank.bean.GirlsLab;
import com.gank.simonla.gank.utils.SpacesItemDecoration;
import com.gank.simonla.gank.view.adapter.GirlsAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final int FINISH = 0;
    private Toolbar mToolbar;
    private ArrayList<Girls.ResultsBean> mGirls;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private GirlsAdapter mGirlsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        getGirlsFormLab();
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        SpacesItemDecoration decoration = new SpacesItemDecoration(16);
        mRecyclerView.addItemDecoration(decoration);
    }

    private void setRecyclerView() {
        mRecyclerView.setAdapter(mGirlsAdapter = new GirlsAdapter(mGirls));
        mGirlsAdapter.setOnItemClickListener(new GirlsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                //TODO
            }
        });
    }

    private void getGirlsFormLab() {
        GirlsLab.get(MainActivity.this).getGirlsFromWeb(10, 1, new GirlsLab.FinishListener() {
            @Override
            public void onFinish() {
                Message message = new Message();
                message.what = FINISH;
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
            }
        }
    };
}

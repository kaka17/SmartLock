package com.sht.smartlock.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.AppManager;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.layoutmanager.MyLayoutManager;
import com.sht.smartlock.ui.activity.layoutmanager.MyRecycleLayout;
import com.sht.smartlock.ui.activity.myview.MyRefresh;
import com.sht.smartlock.ui.activity.myview.SpaceItemDecoration;
import com.sht.smartlock.ui.adapter.OpenDoorRecord_Adapter;
import com.sht.smartlock.ui.entity.OpenDoorRecordEntity;

import java.util.ArrayList;
import java.util.List;

public class OpenDoorRecordActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2 {
    private PullToRefreshListView reFreshMyopen_Door;
    private ListView lvOpenDoor;
    private OpenDoorRecord_Adapter adapter;
    private List<OpenDoorRecordEntity> list = new ArrayList<>();
    private String room_id;
    private boolean isrefresh = true;
    private int pageid = 1;
    private String book_id;
    private TextView tv_Empty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_open_door_record);
        Bundle bundle = getIntent().getExtras();
        room_id = bundle.getString("room_id");
        book_id = bundle.getString(Config.BOOK_ID);
        onBack();
        initView();
        initData();
    }

    //获取布局文件
    @Override
    protected int getLayoutId() {
        return R.layout.activity_open_door_record;
    }

    private void onBack() {
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.getAppManager().finishActivity(OpenDoorRecordActivity.class);
            }
        });
    }

    protected boolean hasToolBar() {
        return false;
    }

    private void initView() {
        reFreshMyopen_Door = (PullToRefreshListView) findViewById(R.id.reFreshMyopen_Door);
        lvOpenDoor = reFreshMyopen_Door.getRefreshableView();
        reFreshMyopen_Door.setMode(PullToRefreshBase.Mode.BOTH);
//        MyRecycleLayout latout = new MyRecycleLayout(this, LinearLayout.VERTICAL, true);
//        recyc_Myopen_Door.setLayoutManager(new LinearLayoutManager(this));
////        recyc_Myopen_Door.setLayoutManager(latout);
//        // 设置ItemAnimator
//        recyc_Myopen_Door.setItemAnimator(new DefaultItemAnimator());
//        // 设置固定大小
//        recyc_Myopen_Door.setHasFixedSize(true);
//        recyc_Myopen_Door.addItemDecoration(new SpaceItemDecoration(2));
        adapter = new OpenDoorRecord_Adapter(getApplicationContext(), list);
        lvOpenDoor.setAdapter(adapter);

        tv_Empty = (TextView) findViewById(R.id.tv_Empty);

        reFreshMyopen_Door.setOnRefreshListener(this);
    }
    private void initData() {
        HttpClient.instance().get_myroom_unlock_list(room_id, pageid, book_id, new HttpCallBack() {
            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                reFreshMyopen_Door.onRefreshComplete();
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                reFreshMyopen_Door.onRefreshComplete();
                AppContext.toLog("record" + responseBean.toString());
                try {
                    List<OpenDoorRecordEntity> lists = responseBean.getListData(OpenDoorRecordEntity.class);
                    //刷新清空数据
                    if (isrefresh) {
                        list.clear();
                        if (lists.size()>0){
                            tv_Empty.setVisibility(View.GONE);
                        }else {
                            tv_Empty.setVisibility(View.VISIBLE);
                        }
                    } else {//加载就不清数据
                        isrefresh = true;
                        if (lists.size() < 1) {
                            BaseApplication.toast("没有更多数据");
                        }
                    }
                    list.addAll(lists);
                    adapter.notifyDataSetChanged();


                } catch (Exception e) {

                }
            }
        });
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        pageid = 1;
        initData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isrefresh = false;
        pageid++;
        initData();
    }
}

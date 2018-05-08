package com.sht.smartlock.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.fragment.NewDoorFragment;
import com.sht.smartlock.ui.activity.myinterface.MyItemClickListener;
import com.sht.smartlock.ui.adapter.TaskAdapter;
import com.sht.smartlock.ui.entity.Entitys;
import com.sht.smartlock.ui.entity.TaskEntity;
import com.sht.smartlock.ui.entity.TaskListEntity;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TastByListActivity extends BaseActivity implements View.OnClickListener,MyItemClickListener {

    private TextView tvNewTask;
    private TextView tv_title;
    private PullToRefreshListView  pullToRefreshListView;
    private ListView lvTask;
    private  TaskAdapter adapter;
    private  List<TaskEntity> list = new ArrayList<>();
    private  String servicerType;
    private  MyBroadcastReceiver myBroadcastReceiver;
    private RelativeLayout rlEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_tast_by_list);
        servicerType = getIntent().getExtras().getString(Config.SERVICE_TYPE);
        onBack();
        initView();
        setOnClistListener();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tast_by_list;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }

    private void onBack() {
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        tvNewTask = (TextView) findViewById(R.id.tvNewTask);
        tv_title = (TextView) findViewById(R.id.tv_title);
        rlEmpty = (RelativeLayout) findViewById(R.id.rlEmpty);
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lvTask);
        lvTask= pullToRefreshListView.getRefreshableView();
        adapter = new TaskAdapter(getApplicationContext(), list,this);
        adapter.setType(servicerType);
        lvTask.setAdapter(adapter);

    }

    private void setOnClistListener() {
        tvNewTask.setOnClickListener(this);

        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                initData(NewDoorFragment.list.get(NewDoorFragment.pos).getRoom_id(), servicerType);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvNewTask:
                Intent intent = new Intent(getApplicationContext(), NewTaskActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Config.SERVICE_TYPE, servicerType);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }

    private void initData(String room_id, String type) {
        HttpClient.instance().servicerTaskCatalog(room_id, type, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(TastByListActivity.this,"正在加载中...");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
                pullToRefreshListView.onRefreshComplete();
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                pullToRefreshListView.onRefreshComplete();
                ProgressDialog.disMiss();
                Log.e("par", "------------------===》" + responseBean.toString());
                Entitys data = responseBean.getData(Entitys.class);
                list.clear();
                if (data.getCode() == 1) {
                    try {
                        JSONArray jsonArray = new JSONArray(data.getResult());
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            TaskEntity entity=new TaskEntity();
                            entity.pareJson(jsonObject);
                            list.add(entity);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else {
                    AppContext.toast(data.getMsg());
                }
                if (list.size()>0){
                    rlEmpty.setVisibility(View.GONE);
                }else {
                    rlEmpty.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private   void initNotity() {
        HttpClient.instance().servicerTaskCatalog(NewDoorFragment.list.get(NewDoorFragment.pos).getRoom_id(), servicerType, new HttpCallBack() {

            @Override
            public void onSuccess(ResponseBean responseBean) {
                Log.e("par", "------------------===》" + responseBean.toString());
                Entitys data = responseBean.getData(Entitys.class);
                list.clear();
                if (data.getCode() == 1) {
                    try {
                        JSONArray jsonArray = new JSONArray(data.getResult());
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            TaskEntity entity=new TaskEntity();
                            entity.pareJson(jsonObject);
                            list.add(entity);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBroadReceiver();
        initData(NewDoorFragment.list.get(NewDoorFragment.pos).getRoom_id(), servicerType);
    }

    @Override
    public void onItemClick(View view, int postion) {
        switch (view.getId()){
            case R.id.tvTakeAgain://重新发布任务
                againTask(list.get(postion).getRoom_service_id());
                break;
             case R.id.tvCacelTask://取消任务
                 cancelTask(list.get(postion).getRoom_service_id());
                break;
             case R.id.tvTakeTask://聊天记录
                 if (list.get(postion).getState().equals("3")){
                     AppContext.toast("服务已完成,会话已结束");
                 }else {
                     Intent intent=new Intent(getApplicationContext(),SettingMorningCallActivity.class);
                     Bundle bundle=new Bundle();
                     bundle.putSerializable(Config.TASKEntity,list.get(postion));
                     bundle.putString(Config.SERVICE_TYPE,servicerType);
                     intent.putExtras(bundle);
                     unregisterReceiver(myBroadcastReceiver);// 不需要时注销
                     startActivity(intent);
                 }
                break;

        }
    }

    private void cancelTask(String room_service_id){
        HttpClient.instance().servicerTaskCancel(room_service_id, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(TastByListActivity.this,"正在取消服务...");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                Entitys data = responseBean.getData(Entitys.class);
                if (data.getCode()==1){//成功
                    AppContext.toast(data.getMsg());
                    initData(NewDoorFragment.list.get(NewDoorFragment.pos).getRoom_id(), servicerType);
                }else {
                    AppContext.toast(data.getMsg());
                }

            }
        });
    }
    private void againTask(String room_service_id){
        HttpClient.instance().servicerAgainTask(room_service_id, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(TastByListActivity.this, "正在发布,请稍等");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
                AppContext.toast("发布失败,请重试");
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                Entitys data = responseBean.getData(Entitys.class);
                if (data.getCode() == 1) {
                    AppContext.toast(data.getMsg());
                    initData(NewDoorFragment.list.get(NewDoorFragment.pos).getRoom_id(), servicerType);
                } else {
                    AppContext.toast(data.getMsg());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);// 不需要时注销
    }

    private void setBroadReceiver(){
        // 注册广播
            myBroadcastReceiver=new MyBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Config.SERVICERMESSAGE);
            this.registerReceiver(myBroadcastReceiver, filter);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        public static final String TAG = "MyBroadcastReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.w(TAG, "intent:" + intent);
            String servicerRoomID = intent.getStringExtra(Config.SERVICERMESSAGE_BYSRID);
            initNotity();
        }
    }

}

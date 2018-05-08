package com.sht.smartlock.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.fragment.NewDoorFragment;
import com.sht.smartlock.ui.activity.myinterface.MyItemClickListener;
import com.sht.smartlock.ui.adapter.WakeAdapter;
import com.sht.smartlock.ui.entity.Entitys;
import com.sht.smartlock.ui.entity.WakeTimeEntity;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import java.util.ArrayList;
import java.util.List;

public class SetWakeActivity extends BaseActivity implements View.OnClickListener,MyItemClickListener{

    private ImageView img_back;
    private ListView lvWake;
    private ImageView ivAddTime;
    private String servicerType;
    private WakeAdapter adapter;
    private List<WakeTimeEntity> list=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_set_wake);
        Bundle extras = getIntent().getExtras();
        servicerType = extras.getString(Config.SERVICE_TYPE);
        initView();
        setOnClickListener();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_wake;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }
    private void initView(){
        img_back = (ImageView) findViewById(R.id.img_back);
        ivAddTime = (ImageView) findViewById(R.id.ivAddTime);
        lvWake = (ListView) findViewById(R.id.lvWake);
        adapter=new WakeAdapter(getApplicationContext(),list,SetWakeActivity.this);
        lvWake.setAdapter(adapter);
    }
    private void setOnClickListener(){
        img_back.setOnClickListener(this);
        ivAddTime.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.ivAddTime:
                Intent intent=new Intent(getApplicationContext(),AddWakeTimeActivity.class);
                intent.putExtra(Config.SERVICE_TYPE,servicerType);

                startActivity(intent);
                break;
        }
    }
    private void initData(String roomID){
        HttpClient.instance().servicerCallList(roomID, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(SetWakeActivity.this,"正在加载中...");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                Log.e("Roomid", "------------->" + responseBean.toString());
                List<WakeTimeEntity> listData = responseBean.getListData(WakeTimeEntity.class);
                list.clear();
                if (listData.size()>0){
                    list.addAll(listData);
                    adapter.notifyDataSetChanged();
                }else {
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData(NewDoorFragment.list.get(NewDoorFragment.pos).getRoom_id());
    }

    @Override
    public void onItemClick(View view, int postion) {
        initDelete(list.get(postion).getMorning_call_id());
    }

    private void initDelete(String morning_call_id){
        HttpClient.instance().servicerCallListbyDelete(morning_call_id, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                Entitys data = responseBean.getData(Entitys.class);
                initData(NewDoorFragment.list.get(NewDoorFragment.pos).getRoom_id());
                if (data.getCode()==1){
                    AppContext.toast(data.getMsg());
                }else {
                    AppContext.toast(data.getMsg());
                }
            }
        });
    }

}

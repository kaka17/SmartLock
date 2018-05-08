package com.sht.smartlock.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.adapter.OtherOrderAdapter;
import com.sht.smartlock.ui.entity.OtherOderEntity;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import java.util.ArrayList;
import java.util.List;

public class OrderByOthersActivity extends BaseActivity {

    private PullToRefreshListView puRefresh;
    private ListView lvOrtherOrder;
    private OtherOrderAdapter adapter;
    private List<OtherOderEntity> list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_order_by_others);
        onBack();
        initView();
        initData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_by_others;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }
    private void onBack(){
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView(){

        puRefresh = (PullToRefreshListView) findViewById(R.id.lvOrtherOrder);
        lvOrtherOrder=puRefresh.getRefreshableView();
        adapter=new OtherOrderAdapter(getApplicationContext(),list);
        lvOrtherOrder.setAdapter(adapter);

        lvOrtherOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), OtherOrderInfoActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString(Config.SONSUMPTION_ORDERID, list.get(position).getConsumption_order_id());
////                bundle.putString(Config.SONSUMPTION_ORDERIDtotal,list.get(position).getTotal());
//                intent.putExtras(bundle);
                intent.putExtra(Config.SONSUMPTION_ORDERID, list.get((int) id).getConsumption_order_id());
                startActivity(intent);
            }
        });
        puRefresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        puRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });



    }

    private void initData(){
        HttpClient.instance().servicerOtherOrderList(new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(OrderByOthersActivity.this,"正在加载中...");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                puRefresh.onRefreshComplete();
                ProgressDialog.disMiss();
                List<OtherOderEntity> listData = responseBean.getListData(OtherOderEntity.class);
                list.clear();
                if (listData.size()>0){
                    list.addAll(listData);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

}

package com.sht.smartlock.ui.activity.mine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.model.MyBalanceInfo;
import com.sht.smartlock.ui.activity.RechargeActivity;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.adapter.MyBalanceAdapter;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import java.util.ArrayList;
import java.util.List;

public class MyBalanceActivity extends BaseActivity implements MyBalanceAdapter.ListItemClickHelp,PullToRefreshBase.OnRefreshListener2{
    private TextView tvTitlePanel;
    private ImageView btn_cancle;
    private PullToRefreshListView listBancle;
    private List<MyBalanceInfo> listBalanceInfo = new ArrayList<MyBalanceInfo>();
    private List<MyBalanceInfo> listBalanceAddInfo = new ArrayList<>();
    private MyBalanceAdapter balanceAdapter;
    private RelativeLayout reEnpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById();
        setOnClickLister();
        iniDate(1);
    }

    private void findViewById(){
        tvTitlePanel = (TextView)findViewById(R.id.tvTitlePanel);
        btn_cancle = (ImageView)findViewById(R.id.btn_cancle);
        listBancle = (PullToRefreshListView)findViewById(R.id.listBancle);
        listBancle.setMode(PullToRefreshBase.Mode.BOTH);
        reEnpty = (RelativeLayout) findViewById(R.id.reEnpty);

        listBancle.setOnRefreshListener(this);

        tvTitlePanel.setText("我的余额");
        balanceAdapter = new MyBalanceAdapter(MyBalanceActivity.this,listBalanceInfo,MyBalanceActivity.this);
        listBancle.setAdapter(balanceAdapter);
    }

    private void setOnClickLister(){
        btn_cancle.setOnClickListener(listener);
        listBancle.setOnItemClickListener(itemClickListener);
    }

    private int isTraion;
    private void iniDate(int i){
        isTraion = i;
        HttpClient.instance().recharge_account_user(new HttpCallBack() {

            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(mContext, "正在加载..");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
                listBancle.onRefreshComplete();
            }


            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                listBancle.onRefreshComplete();
                         try{
                          if(isTraion == 0){
                                      listBalanceInfo.clear();
                                      listBalanceAddInfo.clear();
                          }
                             listBalanceAddInfo = responseBean.getListData(MyBalanceInfo.class);
                             listBalanceInfo.addAll(listBalanceAddInfo);
                             balanceAdapter.notifyDataSetChanged();
                             if (listBalanceInfo.size()>0){
                                 reEnpty.setVisibility(View.GONE);
                             }else {
                                 reEnpty.setVisibility(View.VISIBLE);
                                 AppContext.toast("您没有充值记录 首次充值请前往酒店前台办理哦");
                             }
                         }catch (Exception e){
                             BaseApplication.toast("更新到最底了");
                             listBancle.onRefreshComplete();
                             LogUtil.log(e.toString());
                             reEnpty.setVisibility(View.VISIBLE);
                         }
            }
        });
    }


    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           finish();
        }
    };

    @Override
    protected boolean hasToolBar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_balance;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
//        BaseApplication.toast("aaaa");
//        listBancle.onRefreshComplete();
        iniDate(0);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
//        BaseApplication.toast("bbbb");
//        listBancle.onRefreshComplete();
        iniDate(0);
    }

    @Override
    public void onClick(View item, View widget, int position, int which) {
      switch (which){
          case R.id.btnAmount:
              Intent intent = new Intent(MyBalanceActivity.this,RechargeActivity.class);
              intent.putExtra(Config.AMOUNT_ID,listBalanceAddInfo.get(position).getRecharge_account_id());
//              intent.putExtra(Config.BALANCE,listBalanceAddInfo.get(position).getBalance());
              intent.putExtra(Config.BALANCE_ID,listBalanceAddInfo.get(position).getRecharge_account_user_id());
              intent.putExtra(Config.HOTELNAME,listBalanceAddInfo.get(position).getHotel_caption());
              intent.putExtra(Config.COMMENT,listBalanceAddInfo.get(position).getComment());
              startActivity(intent);
              break;
      }
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent1 = new Intent(MyBalanceActivity.this,AmountChargeActivity.class);
            intent1.putExtra(Config.BALANCE_ID,listBalanceAddInfo.get(i-1).getRecharge_account_user_id());
            intent1.putExtra(Config.AMOUNT_ID,listBalanceAddInfo.get(i-1).getRecharge_account_id());
            intent1.putExtra(Config.BALANCE,listBalanceAddInfo.get(i-1).getBalance());
            intent1.putExtra(Config.HOTELNAME,listBalanceAddInfo.get(i-1).getHotel_caption());
            intent1.putExtra(Config.COMMENT, listBalanceAddInfo.get(i-1).getComment());
            intent1.putExtra(Config.ITEMSLIST, listBalanceAddInfo.get(i-1).getItems());
            startActivity(intent1);
        }
    };
}

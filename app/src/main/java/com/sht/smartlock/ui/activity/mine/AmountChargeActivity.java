package com.sht.smartlock.ui.activity.mine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.sht.smartlock.helper.PopMenu;
import com.sht.smartlock.model.AccountBalanceDetailInfo;
import com.sht.smartlock.model.MyBalanceInfo;
import com.sht.smartlock.ui.activity.RechargeActivity;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.adapter.BalanceDetailAdapter;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AmountChargeActivity extends BaseActivity implements PopMenu.OnItemClickListener,PullToRefreshBase.OnRefreshListener2 {
    private TextView tvTitlePanel;
    private ImageView btn_cancle;
    private ImageView ivAmountChargeHotelChain;
    private PullToRefreshListView listBalanceDetail;
    private TextView tvAmountChargeHotelName;
    private TextView tvBalanceValue;
    private TextView tvRechargeCourtesy;
    private Button btnBalanceValue;

    private PopMenu popMenu;
    private MyBalanceInfo balanceInfo;

    private List<AccountBalanceDetailInfo> listAccountBalanceDetail = new ArrayList<AccountBalanceDetailInfo>();
    private List<AccountBalanceDetailInfo> listAddAccountBalanceDetail = new ArrayList<AccountBalanceDetailInfo>();

    private ArrayList<HashMap<String, String>> listItem;

    private BalanceDetailAdapter detailAdapter;
    private String recharge_account_user_id;
    private String recharge_account_id;
    private String hotel_caption;
    private String balance;
    private String comment;
    private String items;

    private String strHotelChain;

    private int pageid=1;
    private boolean isResult = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById();
        setOnClickLister();

        date();
//        initDate(1);
    }

   private void findViewById(){
       tvTitlePanel = (TextView)findViewById(R.id.tvTitlePanel);
       btn_cancle = (ImageView)findViewById(R.id.btn_cancle);
       ivAmountChargeHotelChain = (ImageView)findViewById(R.id.ivAmountChargeHotelChain);
       listBalanceDetail = (PullToRefreshListView)findViewById(R.id.listBalanceDetail);
       tvAmountChargeHotelName = (TextView)findViewById(R.id.tvAmountChargeHotelName);
       tvBalanceValue = (TextView)findViewById(R.id.tvBalanceValue);
       tvRechargeCourtesy = (TextView)findViewById(R.id.tvRechargeCourtesy);
       btnBalanceValue = (Button)findViewById(R.id.btnBalanceValue);
       tvTitlePanel.setText("余额记录");
       listBalanceDetail.setMode(PullToRefreshBase.Mode.BOTH);

       listBalanceDetail.setOnRefreshListener(this);
       detailAdapter = new BalanceDetailAdapter(AmountChargeActivity.this,listAccountBalanceDetail);
       listBalanceDetail.setAdapter(detailAdapter);

       listItem = new ArrayList<HashMap<String, String>>();
   }

    private void date(){
        Bundle bundle = getIntent().getExtras();
        recharge_account_user_id =bundle.getString(Config.BALANCE_ID);
        recharge_account_id =  bundle.getString(Config.AMOUNT_ID);
        balance = bundle.getString(Config.BALANCE);
        hotel_caption = bundle.getString(Config.HOTELNAME);
        comment = bundle.getString(Config.COMMENT);
        items =  bundle.getString(Config.ITEMSLIST);

        tvAmountChargeHotelName.setText(hotel_caption);
        tvRechargeCourtesy.setText(comment);
        tvBalanceValue.setText("￥"+balance+"元");

        LogUtil.log("items=" + items + "comment="+comment);
        try {
            JSONArray jArray = new JSONArray(items);
            strHotelChain = "";
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("item_caption", json_data.getString("item_caption"));
                listItem.add(map);
            }
            LogUtil.log("item_caption" + listItem.size());
            if(listItem.size() > 0){
                for(int j = 0;j<listItem.size();j ++){
                    if(!listItem.get(j).get("item_caption").toString().equals("")&&
                            !listItem.get(j).get("item_caption").toString().equals("null")){
                        strHotelChain += listItem.get(j).get("item_caption")+",";
                    }
                }
            }
        }catch (Exception e){
          LogUtil.log(e.toString());
        }


        popMenu = new PopMenu(this);
        LogUtil.log("hotel_caption="+hotel_caption+"--------------"+strHotelChain);
        popMenu.addItems(new String[]{hotel_caption, strHotelChain});
        popMenu.setOnItemClickListener(this);
    }

    private void setOnClickLister(){
        btn_cancle.setOnClickListener(clickListener);
        ivAmountChargeHotelChain.setOnClickListener(listener);
        btnBalanceValue.setOnClickListener(listener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
                    finish();
        }
    };

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.ivAmountChargeHotelChain:
                    popMenu.showAsDropDown(view);
                    break;
                case R.id.btnBalanceValue:
                    Intent intent = new Intent(AmountChargeActivity.this,RechargeActivity.class);
                    intent.putExtra(Config.AMOUNT_ID,recharge_account_id);
                    intent.putExtra(Config.BALANCE_ID,recharge_account_user_id);
//              intent.putExtra(Config.BALANCE,listBalanceAddInfo.get(position).getBalance());
                    intent.putExtra(Config.HOTELNAME,hotel_caption);
                    intent.putExtra(Config.COMMENT,comment);
                    startActivity(intent);
                    break;
            }
        }
    };

    private int isTraion;
    private void initDate(int i){
        isTraion = i;
        LogUtil.log("i=="+i);
        HttpClient.instance().recharge_account_user_details(recharge_account_user_id,pageid, new HttpCallBack() {

            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(mContext, "正在加载..");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
                listBalanceDetail.onRefreshComplete();
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                 ProgressDialog.disMiss();
                listBalanceDetail.onRefreshComplete();
                if(pageid == 1){
                    listAccountBalanceDetail.clear();
                }
                try{
//                    if(isResult) {
                        if (isTraion == 0) {
//                            if (listAccountBalanceDetail.size() > 0) {
//                                if (listAddAccountBalanceDetail.size() > 0) {
                                    listAccountBalanceDetail.clear();
                                    listAddAccountBalanceDetail.clear();
//                                }
//                            }
                        }else{
                            listAddAccountBalanceDetail.clear();
                        }
//                    }else {
//                        isResult = true;
//                    }
                    listAddAccountBalanceDetail = responseBean.getListDataWithGson(AccountBalanceDetailInfo.class);
                    listAccountBalanceDetail.addAll(listAddAccountBalanceDetail);
                    detailAdapter.notifyDataSetChanged();
//                    LogUtil.log("qqq==="+listAccountBalanceDetail.get(2).getBalance());

                    HttpClient.instance().recharge_account_balance(recharge_account_user_id, new NetWorkHttpCallBack());
                }catch (Exception e){
                    BaseApplication.toast("已到底了!");
                 LogUtil.log(e.toString());
                }
            }
        });
    }


    class NetWorkHttpCallBack extends HttpCallBack{
        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onSuccess(ResponseBean responseBean) {
            try{
                JSONObject jsonObject = new JSONObject(responseBean.toString());
                ;
//                LogUtil.log("aa=="+jsonObject.getString("result"));
                tvBalanceValue.setText("￥" + jsonObject.getString("result") +"元");
            }catch (Exception e){

            }


        }

        @Override
        public void onFailure(String error, String message) {
            super.onFailure(error, message);
        }


    }

    @Override
    public void onItemClick(int index) {
//        switch (index)
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        pageid = 1;
        initDate(0);

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        pageid ++;
        isResult = false;
        initDate(1);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initDate(1);
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_amount_charge;
    }
}

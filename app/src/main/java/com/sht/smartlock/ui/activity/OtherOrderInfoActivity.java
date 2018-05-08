package com.sht.smartlock.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.model.OtherOderDetailEntity;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.adapter.OtherOrderInfoAdapter;
import com.sht.smartlock.ui.entity.Entitys;
import com.sht.smartlock.ui.entity.OtherOderInfoEntity;
import com.sht.smartlock.util.DateUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class OtherOrderInfoActivity extends BaseActivity implements View.OnClickListener{

    private TextView tvIsSure,tvOder,tvPayType,tvOderMoney01,tvOderRoomNum,tvOderTime;
    private TextView tvOderAllMoney02,tvAllOderZheKou01,tvAllOderZheKouMoney02,tvOderServiceMoney02;
    private ListView lvOrderInfo;
    private OtherOrderInfoAdapter adapter;
    private List<OtherOderInfoEntity> list=new ArrayList<>();
    private ImageView ivSure;
    private String consumption_order_id;
    private DecimalFormat formatOld = new DecimalFormat("#0.0");
    private DecimalFormat formatOld01 = new DecimalFormat("#0.00");
    private boolean isGT;
    private ImageView imgBack,img_back;
    private String bookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_other_order_info);
//        Bundle bundle = getIntent().getExtras();

        consumption_order_id= getIntent().getStringExtra(Config.SONSUMPTION_ORDERID);
        isGT = getIntent().getBooleanExtra(Config.GTCIDBYISTRUE,false);
        initView();
        setOnClickListener();
        if (isGT){
            consumption_order_id=getIntent().getStringExtra(Config.GTCIDBYJSOn);
            bookingId=getIntent().getStringExtra(Config.GTBOOKINGID);
            imgBack.setVisibility(View.VISIBLE);
            img_back.setVisibility(View.GONE);
            initAlert(consumption_order_id,bookingId,true);
        }else {
            imgBack.setVisibility(View.GONE);
            img_back.setVisibility(View.VISIBLE);
            initData(consumption_order_id,true);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_other_order_info;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }


    private void initView(){
        imgBack = (ImageView) findViewById(R.id.imgBack);
        img_back = (ImageView) findViewById(R.id.img_back);
        tvIsSure = (TextView) findViewById(R.id.tvIsSure);
        tvOder = (TextView) findViewById(R.id.tvOder);
        tvPayType = (TextView) findViewById(R.id.tvPayType);
        tvOderMoney01 = (TextView) findViewById(R.id.tvOderMoney01);
        tvOderRoomNum = (TextView) findViewById(R.id.tvOderRoomNum);
        tvOderTime = (TextView) findViewById(R.id.tvOderTime);

        tvOderAllMoney02 = (TextView) findViewById(R.id.tvOderAllMoney02);
        tvAllOderZheKou01 = (TextView) findViewById(R.id.tvAllOderZheKou01);
        tvAllOderZheKouMoney02 = (TextView) findViewById(R.id.tvAllOderZheKouMoney02);
        tvOderServiceMoney02 = (TextView) findViewById(R.id.tvOderServiceMoney02);
        ivSure = (ImageView) findViewById(R.id.ivSure);

        lvOrderInfo = (ListView) findViewById(R.id.lvOrderInfo);
        adapter=new OtherOrderInfoAdapter(getApplicationContext(),list);
        lvOrderInfo.setAdapter(adapter);


    }
    private void setOnClickListener(){
        ivSure.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        img_back.setOnClickListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        isGT = getIntent().getBooleanExtra(Config.GTCIDBYISTRUE,false);
        if (isGT){
            consumption_order_id=getIntent().getStringExtra(Config.GTCIDBYJSOn);
            imgBack.setVisibility(View.VISIBLE);
            img_back.setVisibility(View.GONE);
        }else {
            imgBack.setVisibility(View.GONE);
            img_back.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivSure:
                initSure(consumption_order_id);
                break;
            case R.id.imgBack:
                finish();
                break;
            case R.id.img_back:
                finish();
                break;
        }
    }

    private void initData(String consumption_order_id, final boolean isRefresh){
        HttpClient.instance().servicerOtherOrderDetail(consumption_order_id, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                if (isRefresh) {
                    ProgressDialog.show(OtherOrderInfoActivity.this, "正在加载数据...");
                }
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                OtherOderDetailEntity data = responseBean.getData(OtherOderDetailEntity.class);
                if (data != null && data.getItem() != null && !data.getItem().equals("[]") && !data.getItem().equals("null")) {
                    try {
                        JSONArray jsonArray = new JSONArray(data.getItem());
                        list.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            OtherOderInfoEntity entity = new OtherOderInfoEntity();
                            entity.pareJson(object);
                            list.add(entity);
                        }
                        adapter.notifyDataSetChanged();

                        tvOder.setText("订单ID：" + data.getID());
                        tvOderMoney01.setText("￥" + data.getTotal());
                        tvOderRoomNum.setText("房间号：" + data.getRoom_no());
                        tvOderTime.setText("计算时间: " + DateUtil.formatTimeByFormat(data.getCreate_time(), DateUtil.PATTERNBOTHERORDER));

                        tvOderAllMoney02.setText("￥" + formatOld01.format(data.getNo_discount_total()));//总价合计
                        tvAllOderZheKou01.setText(formatOld.format(data.getDiscount() * 10) + "折");
                        tvAllOderZheKouMoney02.setText("￥" + formatOld01.format(data.getTotal_price()));//折后价
                        tvOderServiceMoney02.setText("￥" + formatOld01.format(data.getService_charge()));
                        if (data.getConfirm_config().equals("1")) {
                            if (data.getConfirm().equals("1")) {
                                tvIsSure.setText("已确认");
                                ivSure.setVisibility(View.GONE);
                            } else {
                                ivSure.setVisibility(View.VISIBLE);
                                tvIsSure.setText("未确认");
                            }
                        } else {
                            ivSure.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void initAlert(final String consumption_order_id,String bookingId, final boolean isRefresh){
        HttpClient.instance().gtAlert(consumption_order_id, bookingId, new HttpCallBack() {

            @Override
            public void onStart() {
                super.onStart();
                if (isRefresh) {
                    ProgressDialog.show(OtherOrderInfoActivity.this, "正在加载数据...");
                }
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
            }
            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                OtherOderDetailEntity data = responseBean.getData(OtherOderDetailEntity.class);
                if (data != null && data.getItem() != null && !data.getItem().equals("[]") && !data.getItem().equals("null")) {
                    try {
                        JSONArray jsonArray = new JSONArray(data.getItem());
                        list.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            OtherOderInfoEntity entity = new OtherOderInfoEntity();
                            entity.pareJson(object);
                            list.add(entity);
                        }
                        adapter.notifyDataSetChanged();

                        tvOder.setText("订单ID：" + data.getID());
                        tvOderMoney01.setText("￥" + data.getTotal());
                        tvOderRoomNum.setText("房间号：" + data.getRoom_no());
                        tvOderTime.setText("计算时间: " + DateUtil.formatTimeByFormat(data.getCreate_time(), DateUtil.PATTERNBOTHERORDER));

                        tvOderAllMoney02.setText("￥" + formatOld01.format(data.getNo_discount_total()));//总价合计
                        tvAllOderZheKou01.setText(formatOld.format(data.getDiscount() * 10) + "折");
                        tvAllOderZheKouMoney02.setText("￥" + formatOld01.format(data.getTotal_price()));//折后价
                        tvOderServiceMoney02.setText("￥" + formatOld01.format(data.getService_charge()));
                        if (data.getConfirm_config().equals("1")) {
                            if (data.getConfirm().equals("1")) {
                                tvIsSure.setText("已确认");
                                ivSure.setVisibility(View.GONE);
                            } else {
                                ivSure.setVisibility(View.VISIBLE);
                                tvIsSure.setText("未确认");
                            }
                        } else {
                            ivSure.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void initSure(final String consumption_order_id){
        HttpClient.instance().otherOrderDetailToSure(consumption_order_id, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(OtherOrderInfoActivity.this,"正在确认中...");
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
                AppContext.toast(data.getMsg());
                if (isGT){
                    initAlert(consumption_order_id,bookingId,false);
                }else {
                    initData(consumption_order_id,true);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressDialog.disMiss();
    }

}

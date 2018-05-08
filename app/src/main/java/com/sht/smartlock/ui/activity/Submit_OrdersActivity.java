package com.sht.smartlock.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.AppManager;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.booking.PayBillActivity;
import com.sht.smartlock.ui.activity.fragment.NewDoorFragment;
import com.sht.smartlock.ui.adapter.Submit_Order_Adapter;
import com.sht.smartlock.ui.entity.OrderEntity;
import com.sht.smartlock.ui.entity.ServierDetailEntity;
import com.sht.smartlock.ui.entity.Submit_OrderEntity;
import com.sht.smartlock.ui.ordering.entity.Product;
import com.sht.smartlock.ui.ordering.orderingutil.NumberUtils;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;

public class Submit_OrdersActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_submit_order_old, tv_IsInfo, tv_Lock_ISOrders;
    private ImageView iv_Order_old;
    private ImageView iv_LockReceive;
    private ImageView iv_Order_Delivery;
    private ListView lv_Ordering_Info;
    private TextView tv_person_name;
    private TextView tv_person_phone;
    private TextView tv_person_Lock_Num;
    private TextView tv_person_Pay_Mode;
    private TextView tv_person_Remarks;
    private TextView tv_person_order_time;
    private OrderEntity entity;
    private List<Product> myShopList;
    private String tab;
    private String remarks;
    private TextView tv_Tile;
    private TextView tv_Money, tv_Pay;
    private Submit_Order_Adapter adapter;
    private String service_id;
    private String pay_type;
    private String pay_state;
    private String room_id;
    private String state = "-1";
    private String mainOrOrder, pay_channel;
    private boolean ispay = true;
    private TextView tv_person_result;
    private int pay_nun = 1;
    private String totalPic = "";
    private TextView tvPeiName,tvPeiMoney;
    private RelativeLayout rePei;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_submit__orders);
        Bundle bundle = getIntent().getExtras();
        //购物。订餐信息
        entity = (OrderEntity) bundle.getSerializable("OrderEntity");
        //我的信息
        if (entity != null) {
            myShopList = entity.getMyShopList();
        }
        tab = bundle.getString(Config.ORDER_TAB);//购物1/订餐 2
        remarks = bundle.getString(Config.REMARKS);//备注
        service_id = bundle.getString(Config.ORDER_NUMVER);//订单号
        pay_type = bundle.getString(Config.PAY_TYPE);//支付类型
        room_id = bundle.getString(Config.ROOM_ID);
        mainOrOrder = bundle.getString(Config.MAINORORDER);

        AppContext.setProperty(Config.PAYDATAstrBillnum,null);
        AppContext.setProperty(Config.PAYDATAroom_mode,null);
        AppContext.setProperty(Config.PAYDATAtab,null);
        AppContext.setProperty(Config.PAYDATAroom_id,null);
        AppContext.setProperty(Config.PAYDATApayAction,null);
        onBack();
        initView();
        setOnClicklistener();
//        initData();
        initNewData(service_id);
        if (tab.equals("1")) {//购物
            tv_Tile.setText(R.string.order_info_submit_Shopping);
        } else {//订餐
            tv_Tile.setText(R.string.order_info_submit_Order);
        }
    }

    //获取布局文件
    @Override
    protected int getLayoutId() {
        return R.layout.activity_submit__orders;
    }

    protected boolean hasToolBar() {
        return false;
    }

    private void onBack() {
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.getAppManager().finishActivity(Submit_OrdersActivity.class);
            }
        });
    }

    private void initView() {
        //标题
        tv_Tile = (TextView) findViewById(R.id.tv_Tile);
        //订单已提交
        tv_submit_order_old = (TextView) findViewById(R.id.tv_submit_order_old);
        //取消订单
        tv_IsInfo = (TextView) findViewById(R.id.tv_IsInfo);
        //等待酒店 接单
        tv_Lock_ISOrders = (TextView) findViewById(R.id.tv_Lock_ISOrders);
        //订单提交状态
        iv_Order_old = (ImageView) findViewById(R.id.iv_Order_old);
        //酒店接单状态
        iv_LockReceive = (ImageView) findViewById(R.id.iv_LockReceive);
        //已送达状态
        iv_Order_Delivery = (ImageView) findViewById(R.id.iv_Order_Delivery);

        tv_person_result = (TextView) findViewById(R.id.tv_person_result);
        tv_person_result.setText(service_id);
        //订餐人信息
        tv_person_name = (TextView) findViewById(R.id.tv_person_name);
        tv_person_phone = (TextView) findViewById(R.id.tv_person_phone);
        tv_person_Lock_Num = (TextView) findViewById(R.id.tv_person_Lock_Num);
        tv_person_Pay_Mode = (TextView) findViewById(R.id.tv_person_Pay_Mode);
        tv_person_Remarks = (TextView) findViewById(R.id.tv_person_Remarks);
        tv_person_order_time = (TextView) findViewById(R.id.tv_person_order_time);

        //服务费
        tvPeiName = (TextView) findViewById(R.id.tvPeiName);
        tvPeiMoney = (TextView) findViewById(R.id.tvPeiMoney);
        rePei = (RelativeLayout) findViewById(R.id.rePei);

        //订单列表
        lv_Ordering_Info = (ListView) findViewById(R.id.lv_Ordering_Info);
        //总价格
        tv_Money = (TextView) findViewById(R.id.tv_Money);

        //去支付
        tv_Pay = (TextView) findViewById(R.id.tv_Pay);

        //设置订单列表适配器
        adapter = new Submit_Order_Adapter(myShopList, getApplicationContext());
        lv_Ordering_Info.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //假如是从下单页面进来，就直接加载数据，
        if (entity != null) {
            adapter = new Submit_Order_Adapter(myShopList, getApplicationContext());
            lv_Ordering_Info.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            setData();
        }
    }

    private void setData() {
        double totalPrice = getTotalPrice();
        tv_Money.setText(" 共￥" + totalPrice);
        //根据开门页的房间选择房间
        tv_person_Lock_Num.setText(NewDoorFragment.list.get(NewDoorFragment.pos).getRoom_no());
        tv_person_Pay_Mode.setText(R.string.Toast19);
        tv_person_Remarks.setText(remarks);


        getDialog();
    }

    private void setOnClicklistener() {
        tv_IsInfo.setOnClickListener(this);
        tv_Pay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_IsInfo:
                if (tv_IsInfo.getText().toString().equals("取消订单")) {
                    getCancelDialog();
                }
                break;
            case R.id.tv_Pay:
                if (tv_Pay.getText().toString().equals("去支付")) {
//                    if (pay_channel.equals("1")) {
//                        //在线
//
//
//                    } else {
//                        BaseApplication.toast("已计入房费");
//                    }
                    getPayDialog();
                }
                break;
        }
    }

    /**
     * 获取总金额
     *
     * @return
     */
    public double getTotalPrice() {
        double totalProce = 0;
        if (myShopList != null || myShopList.size() == 0) {
            return totalProce;
        }
        for (int i = 0; i < myShopList.size(); i++) {
            Product product = myShopList.get(i);
            totalProce += product.getPrices() * product.getNum();
            totalProce = NumberUtils.toDouble(NumberUtils.format(totalProce));
        }
        return totalProce;
    }

    /*
    *   开始Dialog
    *
    * */
    private void getDialog() {
        final Dialog dialog = new Dialog(Submit_OrdersActivity.this, R.style.OrderDialog);
        LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());
        View view = mInflater.inflate(R.layout.order_dialog, null);
        dialog.setContentView(view);
        //我知道了
        view.findViewById(R.id.tv_IKnow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    /*
   *   去支付
   *
   * */
    private void getPayDialog() {
        final Dialog dialog = new Dialog(Submit_OrdersActivity.this, R.style.OrderDialog);
        LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());
        View view = mInflater.inflate(R.layout.paydialog, null);
        dialog.setContentView(view);
        RadioGroup rg_Pay = (RadioGroup) view.findViewById(R.id.rg_Pay);

        rg_Pay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_pay01://房费
                        ispay = true;
                        pay_nun = 1;
                        break;
                    case R.id.rb_pay02://在线支付
                        ispay = false;
                        pay_nun = 2;
                        break;
                    case R.id.rb_pay03:
                        pay_nun = 3;
                        break;

                }
            }
        });
        view.findViewById(R.id.tv_No).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //重新支付
        view.findViewById(R.id.tv_Sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (pay_nun == 1) {//房费
                    HttpClient.instance().re_sumbit_service(service_id, "3", new HttpCallBack() {
                        @Override
                        public void onStart() {
                            super.onStart();
                            ProgressDialog.show(Submit_OrdersActivity.this, "正在重新提交。。。");
                        }

                        @Override
                        public void onFailure(String error, String message) {
                            super.onFailure(error, message);
                            ProgressDialog.disMiss();
                            AppContext.toast("支付失败，请重试");
                        }

                        @Override
                        public void onSuccess(ResponseBean responseBean) {
                            ProgressDialog.disMiss();
                            try {
//                                AppContext.toast(result+"订单号"+responseBean.toString());
                                AppContext.toLog(responseBean.toString());
                                JSONObject jsonobject = new JSONObject(responseBean.toString());
//                                boolean result_pay = jsonobject.getBoolean("result");
                                String result_pay = jsonobject.getString("result");
                                if (result_pay.equals("true")) {
//                                    initData();
                                    initNewData(service_id);
                                } else {
                                    JSONObject result = jsonobject.getJSONObject("result");
                                    AppContext.toast(result.getString("msg"));
                                }
                            } catch (Exception e) {
//                                AppContext.toast(responseBean.toString());
                            }
                        }
                    });

                } else if (pay_nun == 2) {

                    HttpClient.instance().re_sumbit_service(service_id, "4", new HttpCallBack() {
                        @Override
                        public void onSuccess(ResponseBean responseBean) {
                            try {
                                AppContext.toLog(responseBean.toString());
                                JSONObject jsonobject = new JSONObject(responseBean.toString());
                                boolean result_pay = jsonobject.getBoolean("result");
                                if (result_pay) {
                                    //去支付
                                    Intent i = new Intent(getApplicationContext(), PayBillActivity.class);
                                    i.putExtra(Config.KEY_BILL_TYPE, Config.BILL_TYPE_SERVER);
                                    //总价格
                                    if (totalPic.equals("")) {
                                        i.putExtra(Config.KEY_HOTEL_ROOMTOTALPRICE, getTotalPrice());
                                    } else {
                                        i.putExtra(Config.KEY_HOTEL_ROOMTOTALPRICE, Double.parseDouble(totalPic));
                                        Double.parseDouble(totalPic);
//                                        AppContext.toast("totalPic"+totalPic);
                                    }
//                                    i.putExtra(Config.KEY_HOTEL_ROOMTOTALPRICE, getTotalPrice());
                                    //订单号
                                    i.putExtra(Config.KEY_BOOKING_BILLNUM, service_id);
                                    i.putExtra(Config.PAYACTION, Config.ORDERING);
                                    i.putExtra(Config.ORDER_TAB, tab);
                                    i.putExtra(Config.ROOM_ID, room_id);
                                    startActivity(i);
                                    AppManager.getAppManager().finishActivity(Submit_OrdersActivity.class);
                                } else {
                                    BaseApplication.toast("支付失败，请重新选择");
                                }
                            } catch (Exception e) {

                            }
                        }
                    });
                } else {//现金支付
                    HttpClient.instance().re_sumbit_service(service_id, "1", new HttpCallBack() {
                        @Override
                        public void onSuccess(ResponseBean responseBean) {
                            try {
                                AppContext.toLog(responseBean.toString());
                                JSONObject jsonobject = new JSONObject(responseBean.toString());
                                boolean result_pay = jsonobject.getBoolean("result");
                                if (result_pay) {
//                                    initData();
                                    initNewData(service_id);
                                } else {
                                    BaseApplication.toast("支付失败，请重新选择");
                                }
                            } catch (Exception e) {

                            }
                        }
                    });
                }
            }
        });
        dialog.show();

    }

    /*
    *   取消订单的Dialog
    * */
    private void getCancelDialog() {
        final Dialog dialog = new Dialog(Submit_OrdersActivity.this, R.style.OrderDialog);
        LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());
        View view = mInflater.inflate(R.layout.dialog_cancel, null);
        dialog.setContentView(view);
        //
        view.findViewById(R.id.tv_No).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_Sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                makeOrderChang();
            }
        });
        dialog.show();
    }

    //订单修改
    private void makeOrderChang() {
        HttpClient.instance().cancel_service_order(service_id, new HttpCallBack() {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                try {
                    JSONObject jsonObject = new JSONObject(responseBean.toString());
                    boolean result = jsonObject.getBoolean("result");
                    if (result) {
                        tv_IsInfo.setText(R.string.Order_state111);
                        tv_IsInfo.setTextColor(getResources().getColor(R.color.OrderInfoGray));
                        tv_IsInfo.setBackgroundResource(R.drawable.bg_btn_gray);
                        state = "-1";
                        tv_Pay.setVisibility(View.GONE);
                        tv_IsInfo.setClickable(false);
                        iv_Order_old.setImageResource(R.drawable.pic_submit_gray);
                    } else {
                        BaseApplication.toast(R.string.Toast20);
                        AppContext.toLog(responseBean.toString());
                    }
                    initNewData(service_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
            }
        });
    }

    private void initNewData(String service_id) {
        HttpClient.instance().servicerOrderDetail(service_id, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(Submit_OrdersActivity.this,"正在加载...");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                ServierDetailEntity data = responseBean.getData(ServierDetailEntity.class);
                tv_IsInfo.setVisibility(View.VISIBLE);
                tv_person_order_time.setText(data.getCreate_time());
                tv_person_name.setText(data.getName());
                tv_person_phone.setText(data.getPhone_no());
                //根据开门页的房间选择房间
                tv_person_Lock_Num.setText(data.getRoom_no());
                if (data.getContent()==null||data.getContent().equals("null")){
                    tv_person_Remarks.setText("无");
                }else {
                    tv_person_Remarks.setText(data.getContent());
                }


                tv_Money.setText(" 共￥" +data.getTotal());
                if (data.getService_charge_caption()==null||data.getService_charge_caption().equals("null")){
                    rePei.setVisibility(View.GONE);
                }else {
//                    tv_Money.setText(" 共￥" + (Double.parseDouble(data.getTotal()) + Double.parseDouble(data.getService_charge())) );
                    tvPeiMoney.setText("￥"+data.getService_charge());
                    tvPeiName.setText(data.getService_charge_caption());
                }
                totalPic = data.getTotal();
                switch (data.getState()) {
                    case "-1"://取消订单
                        tv_IsInfo.setText(R.string.Order_state111);
                        tv_IsInfo.setTextColor(getResources().getColor(R.color.OrderInfoGray));
                        tv_IsInfo.setBackgroundResource(R.drawable.bg_btn_gray);
                        tv_Pay.setVisibility(View.GONE);
                        tv_IsInfo.setClickable(false);
                        iv_Order_old.setImageResource(R.drawable.pic_submit_gray);
                        switch (data.getService_pay_state()) {
                            case "0"://未支付
                                tv_Lock_ISOrders.setText("订单已取消");
                                tv_Pay.setVisibility(View.GONE);
                                break;
                            case "1"://支付成功
                                tv_Lock_ISOrders.setText(R.string.pay_Success_onSure);
                                tv_Pay.setVisibility(View.GONE);
                                break;
                            case "2"://支付失败
                                tv_Lock_ISOrders.setText("订单已取消");
                                tv_Pay.setVisibility(View.GONE);
                                break;
                            case "3"://退款成功
                                tv_Pay.setText(R.string.Submit_Refundding_Success);
                                tv_Pay.setVisibility(View.GONE);
                                break;
                            case "4"://退款失败
                                tv_Pay.setText(R.string.Submit_Refundding_Failure);
                                tv_Pay.setVisibility(View.GONE);
                                break;
                            case "5"://退款中
                                tv_Pay.setText(R.string.Submit_Refundding);
                                tv_Pay.setVisibility(View.GONE);
                                break;
                        }
                        break;
                    case "0"://客户未确认
                        tv_IsInfo.setText(R.string.Order_state11);
                        tv_IsInfo.setTextColor(getResources().getColor(R.color.BiLLTextColor));
                        tv_IsInfo.setBackgroundResource(R.drawable.bg_btn_yellow);
                        tv_Pay.setText(R.string.pay_now);
                        tv_Pay.setVisibility(View.VISIBLE);
                        tv_Pay.setTextColor(getResources().getColor(R.color.OrderInfoBlue));
                        tv_Pay.setBackgroundResource(R.drawable.bg_btn_blue);
                        tv_Pay.setClickable(true);
                        tv_IsInfo.setClickable(true);
                        iv_Order_old.setImageResource(R.drawable.pic_submit_gray);
                        tv_Lock_ISOrders.setText("未确认订单，请重新确认");
                        break;
                    case "1"://客户已确认
                        tv_IsInfo.setText(R.string.Order_state11);
                        tv_IsInfo.setTextColor(getResources().getColor(R.color.BiLLTextColor));
                        tv_IsInfo.setBackgroundResource(R.drawable.bg_btn_yellow);
                        iv_Order_old.setImageResource(R.drawable.pic_submit_blue);
                        tv_Pay.setVisibility(View.GONE);
                        switch (data.getService_pay_state()) {
                            case "0"://未支付
                                tv_Lock_ISOrders.setText("订单已提交，等待酒店确认");
                                tv_Pay.setText(R.string.pay_now);
                                tv_Pay.setTextColor(getResources().getColor(R.color.OrderInfoBlue));
                                tv_Pay.setBackgroundResource(R.drawable.bg_btn_blue);
                                tv_Pay.setVisibility(View.GONE);
                                break;
                            case "1"://支付成功
                                tv_Lock_ISOrders.setText(R.string.pay_Success_onSure);
                                tv_Pay.setVisibility(View.GONE);
                                break;
                            case "2"://支付失败
                                tv_Lock_ISOrders.setText("订单已提交，等待酒店确认");
                                tv_Pay.setText(R.string.pay_now);
                                tv_Pay.setTextColor(getResources().getColor(R.color.OrderInfoBlue));
                                tv_Pay.setBackgroundResource(R.drawable.bg_btn_blue);
                                tv_Pay.setVisibility(View.GONE);
                                break;
                            case "3"://退款成功
                                tv_Lock_ISOrders.setText("退款成功");
                                break;
                            case "4"://退款失败
                                tv_Lock_ISOrders.setText("退款失败");
                                break;
                            case "5"://退款中
                                tv_Lock_ISOrders.setText("退款中");
                                break;
                        }
                        break;
                    case "2"://服务商已响应
                        tv_IsInfo.setText(R.string.Order_state03);
                        tv_IsInfo.setVisibility(View.GONE);
                        tv_Pay.setVisibility(View.GONE);
                        iv_LockReceive.setImageResource(R.drawable.pic_checkin_blue);
                        iv_Order_old.setImageResource(R.drawable.pic_submit_gray);
                        tv_Lock_ISOrders.setText(R.string.pay_Success_onSure02);
                        switch (data.getService_pay_state()) {
                            case "0"://未支付
                                tv_Lock_ISOrders.setText("订单已提交，等待酒店确认");
                                tv_Pay.setText(R.string.pay_now);
                                tv_Pay.setVisibility(View.GONE);
                                tv_Pay.setTextColor(getResources().getColor(R.color.OrderInfoBlue));
                                tv_Pay.setBackgroundResource(R.drawable.bg_btn_blue);
                                break;
                            case "1"://支付成功
                                tv_Lock_ISOrders.setText(R.string.pay_Success_onSure);
                                tv_Pay.setVisibility(View.GONE);
                                break;
                            case "2"://支付失败
                                tv_Lock_ISOrders.setText("订单已提交，等待酒店确认");
                                tv_Pay.setText(R.string.pay_now);
                                tv_Pay.setVisibility(View.GONE);
                                break;
                            case "3"://退款成功
                                tv_Lock_ISOrders.setText("退款成功");
                                break;
                            case "4"://退款失败
                                tv_Lock_ISOrders.setText("退款失败");
                                break;
                            case "5"://退款中
                                tv_Lock_ISOrders.setText("退款中");
                                break;
                        }
                        break;
                    case "3"://服务已完成
                        tv_IsInfo.setText(R.string.Order_state04);
                        tv_IsInfo.setVisibility(View.GONE);
                        iv_Order_old.setImageResource(R.drawable.pic_submit_gray);
                        iv_Order_Delivery.setImageResource(R.drawable.pic_recieved_blue);
                        tv_Pay.setVisibility(View.GONE);
                        switch (data.getService_pay_state()) {
                            case "0"://未支付
                                tv_Lock_ISOrders.setText("未支付，等待酒店确认");
                                tv_Pay.setVisibility(View.GONE);
                                break;
                            case "1"://支付成功
                                tv_Lock_ISOrders.setText(R.string.pay_Success_onSure02);
                                tv_Pay.setVisibility(View.GONE);
                                break;
                            case "2"://支付失败
                                tv_Lock_ISOrders.setText("未支付，等待酒店确认");
                                tv_Pay.setVisibility(View.GONE);
                                break;
                            case "3"://退款成功
                                break;
                            case "4"://退款失败
                                break;
                            case "5"://退款中
                                break;
                        }
                        break;
                }

                //-----------------------------------------
                switch (data.getPay_channel()) {
                    case "null":
                        switch (data.getService_pay_state()) {
                            case "0":
                                tv_person_Pay_Mode.setText("(房费)未支付");
                                if (data.getState().equals("1")) {
                                    tv_Pay.setText(R.string.pay_now);
                                    tv_Pay.setTextColor(getResources().getColor(R.color.OrderInfoBlue));
                                    tv_Pay.setBackgroundResource(R.drawable.bg_btn_blue);
                                    tv_Pay.setVisibility(View.VISIBLE);
                                }
                                break;
                            case "1":
                                tv_person_Pay_Mode.setText("(房费)(已支付)");
                                break;
                            case "2":
                                tv_person_Pay_Mode.setText("(房费)未支付");
                                break;
                            case "3":
                                tv_person_Pay_Mode.setText("(房费)已退款");
                                break;
                            case "4":
                                tv_person_Pay_Mode.setText("(房费)退款失败");
                                break;
                            case "5":
                                tv_person_Pay_Mode.setText("(房费)退款中");
                                break;
                        }

                        break;
                    case "1"://现金

                        switch (data.getService_pay_state()) {
                            case "0"://未支付
                                tv_person_Pay_Mode.setText("现金(未支付)");
                                break;
                            case "1"://支付成功
                                tv_person_Pay_Mode.setText("现金(已支付)");
                                break;
                            case "2"://支付失败
                                tv_person_Pay_Mode.setText("现金(未支付)");
                                break;
                            case "3"://退款成功
                                tv_person_Pay_Mode.setText("现金(已退款)");
                                break;
                            case "4"://退款失败
                                tv_person_Pay_Mode.setText("现金(退款失败)");
                                break;
                            case "5"://退款中
                                tv_person_Pay_Mode.setText("现金(退款中)");
                                break;
                        }
                        break;
                    case "2"://刷卡
                        switch (data.getService_pay_state()) {
                            case "0"://未支付
                                tv_person_Pay_Mode.setText("刷卡(未支付)");
                                break;
                            case "1"://支付成功
                                tv_person_Pay_Mode.setText("刷卡(已支付)");
                                break;
                            case "2"://支付失败
                                tv_person_Pay_Mode.setText("刷卡(未支付)");
                                break;
                            case "3"://退款成功
                                tv_person_Pay_Mode.setText("刷卡(已退款)");
                                break;
                            case "4"://退款失败
                                tv_person_Pay_Mode.setText("刷卡(退款失败)");
                                break;
                            case "5"://退款中
                                tv_person_Pay_Mode.setText("刷卡(退款中)");
                                break;
                        }
                        break;
                    case "3"://支付宝
                        switch (data.getService_pay_state()) {
                            case "0"://未支付
                                tv_person_Pay_Mode.setText("支付宝(未支付)");
                                break;
                            case "1"://支付成功
                                tv_person_Pay_Mode.setText("支付宝(已支付)");
                                break;
                            case "2"://支付失败
                                tv_person_Pay_Mode.setText("支付宝(未支付)");
                                break;
                            case "3"://退款成功
                                tv_person_Pay_Mode.setText("支付宝(已退款)");
                                break;
                            case "4"://退款失败
                                tv_person_Pay_Mode.setText("支付宝(退款失败)");
                                break;
                            case "5"://退款中
                                tv_person_Pay_Mode.setText("支付宝(退款中)");
                                break;
                        }
                        break;
                    case "4"://微信
                        switch (data.getService_pay_state()) {
                            case "0"://未支付
                                tv_person_Pay_Mode.setText("微信(未支付)");
                                break;
                            case "1"://支付成功
                                tv_person_Pay_Mode.setText("微信(已支付)");
                                break;
                            case "2"://支付失败
                                tv_person_Pay_Mode.setText("微信(未支付)");
                                break;
                            case "3"://退款成功
                                tv_person_Pay_Mode.setText("微信(已退款)");
                                break;
                            case "4"://退款失败
                                tv_person_Pay_Mode.setText("微信(退款失败)");
                                break;
                            case "5"://退款中
                                tv_person_Pay_Mode.setText("微信(退款中)");
                                break;
                        }
                        break;
                    case "5"://银联
                        switch (data.getService_pay_state()) {
                            case "0"://未支付
                                tv_person_Pay_Mode.setText("银联(未支付)");
                                break;
                            case "1"://支付成功
                                tv_person_Pay_Mode.setText("银联(已支付)");
                                break;
                            case "2"://支付失败
                                tv_person_Pay_Mode.setText("银联(未支付)");
                                break;
                            case "3"://退款成功
                                tv_person_Pay_Mode.setText("银联(已退款)");
                                break;
                            case "4"://退款失败
                                tv_person_Pay_Mode.setText("银联(退款失败)");
                                break;
                            case "5"://退款中
                                tv_person_Pay_Mode.setText("银联(退款中)");
                                break;
                        }
                        break;
                    case "6"://余额
                        switch (data.getService_pay_state()) {
                            case "0"://未支付
                                tv_person_Pay_Mode.setText("余额(未支付)");
                                break;
                            case "1"://支付成功
                                tv_person_Pay_Mode.setText("余额(已支付)");
                                break;
                            case "2"://支付失败
                                tv_person_Pay_Mode.setText("余额(未支付)");
                                break;
                            case "3"://退款成功
                                tv_person_Pay_Mode.setText("余额(已退款)");
                                break;
                            case "4"://退款失败
                                tv_person_Pay_Mode.setText("余额(退款失败)");
                                break;
                            case "5"://退款中
                                tv_person_Pay_Mode.setText("余额(退款中)");
                                break;
                        }
                        break;
                    case "7"://协议账号
                        switch (data.getService_pay_state()) {
                            case "0"://未支付
                                tv_person_Pay_Mode.setText("协议账号(未支付)");
                                break;
                            case "1"://支付成功
                                tv_person_Pay_Mode.setText("协议账号(已支付)");
                                break;
                            case "2"://支付失败
                                tv_person_Pay_Mode.setText("协议账号(未支付)");
                                break;
                            case "3"://退款成功
                                tv_person_Pay_Mode.setText("协议账号(已退款)");
                                break;
                            case "4"://退款失败
                                tv_person_Pay_Mode.setText("协议账号(退款失败)");
                                break;
                            case "5"://退款中
                                tv_person_Pay_Mode.setText("协议账号(退款中)");
                                break;
                        }
                        break;
                }
                //判断是否是房费支付
                if (!data.getState().equals("-1") && !data.getState().equals("0")) {//已确认状态
                    if (data.getPay_channel().equals("1")) {//现金支付
                        switch (data.getState()) {
                            case "1":
                                tv_IsInfo.setVisibility(View.VISIBLE);
                                break;
                            default://服务商接单后
                                tv_IsInfo.setVisibility(View.GONE);
                                break;
                        }
                    } else {
                        if (data.getService_pay_state().equals("0") || data.getService_pay_state().equals("2")) {
                            tv_person_Pay_Mode.setText("(房费)");
                            tv_Pay.setText(R.string.pay_now);
                            tv_Pay.setTextColor(getResources().getColor(R.color.OrderInfoBlue));
                            tv_Pay.setBackgroundResource(R.drawable.bg_btn_blue);
                            tv_Pay.setVisibility(View.VISIBLE);
                            switch (data.getState()) {
                                case "1"://服务商未接单前
                                    tv_IsInfo.setVisibility(View.VISIBLE);
                                    tv_Pay.setVisibility(View.VISIBLE);
                                    break;
                                default://服务商接单后
                                    tv_IsInfo.setVisibility(View.GONE);
                                    tv_Pay.setVisibility(View.GONE);
                                    break;
                            }
                        } else if (data.getService_pay_state().equals("1")) {
                            tv_person_Pay_Mode.setText("(已支付)");
                            tv_IsInfo.setVisibility(View.GONE);
                        } else {
                            tv_IsInfo.setVisibility(View.GONE);
                        }
                    }
                }
                getlistForJson(data.getItem().toString());
            }
        });
    }

    private void getlistForJson(String str) {
        try {
            JSONArray array = new JSONArray(str);
            myShopList = new ArrayList<Product>();
            if (entity == null) {
                adapter = new Submit_Order_Adapter(myShopList, getApplicationContext());
                lv_Ordering_Info.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            myShopList.clear();
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                Product product = new Product();
                product.setCaption(jsonObject.getString("caption"));
//                        product.setPrice(jsonObject.getString("price"));
                product.setHotel_price(jsonObject.getString("price"));
                String quantity = jsonObject.getString("quantity");//数量
                double quantity_d = Double.parseDouble(quantity);
                int n = (int) quantity_d;
                product.setNum(n);
                product.setDiscount(Double.parseDouble(jsonObject.getString("discount")));
                myShopList.add(product);
            }
            AppContext.toLog(myShopList.size() + ";" + myShopList.toString());
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //订餐数据
    }

}

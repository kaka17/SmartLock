package com.sht.smartlock.ui.activity.mine;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.sht.smartlock.ui.entity.RedPackgeEntity;
import com.sht.smartlock.util.DateUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.helper.PermissionHelper;
import com.sht.smartlock.model.HotelStatsInfo;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.booking.PayBillActivity;
import com.sht.smartlock.ui.activity.chatlocation.Common;
import com.sht.smartlock.ui.activity.chatlocation.LocationSvc;
import com.sht.smartlock.ui.activity.myview.SharePopwindow;
import com.sht.smartlock.ui.entity.HotelDetail;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HotelStatusActivity extends BaseActivity implements View.OnClickListener {
    private ImageView btn_cancle;
    private TextView tvTitlePanel;

    private Button btnToPay;
    private Button btnHotelCancleOrder;
    private ImageView ivOrdersOut;
    private ImageView ivOrdersCheck;
    private ImageView ivOrdersSubmitted;
    private Button btnNavigation;
    private TextView tvOrderAmount;
    private TextView tvCheckInTime;
    private TextView tvCheckInRoomType;
    private TextView tvCheckInRoomNumber;
    private TextView tvReservationr;
    private TextView tvLatestShopShow;
    private TextView tvLatestShop;
    private TextView tvHotelName;
    private TextView tvHotelAddress;
    private TextView tvOrdersSubmitted;
    private TextView tvOrderID;

    private LinearLayout relatLatestShop;

    private String str_booking_id;//获取订单id
    private String str_checkin_time;//获取入住时间
    private String str_roommode;//入住房型
    private Double longitude = 22.612130;
    private Double latitude = 114.022295;
    private String strBillnum;
    private String pay_state;
    private String term_type;
    private SharePopwindow sharePopwindow;
    private double dbTotalPrice;
    private int isRefund;//判断是否退款
    private HotelDetail data;
    private  RedPackgeEntity redPackge;
    //权限检测类
    private PermissionHelper mPermissionHelper;
    //返回值
    private static final int  WRITE_RESULT_CODE = 12;
    //权限名称
    private static final String  WRITE_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String  WRITE_PERMISSION02 = Manifest.permission.ACCESS_FINE_LOCATION;
    private List<String> listPermission=new ArrayList<>();
    private ImageView ivRenPacket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPermissionHelper = new PermissionHelper(this);
        listPermission.add(WRITE_PERMISSION);
        listPermission.add(WRITE_PERMISSION02);
        findviewbyid();
        intdata();
        setOnClickLister();
    }

    private void findviewbyid() {
        btn_cancle = (ImageView) findViewById(R.id.btn_cancle);
        tvTitlePanel = (TextView) findViewById(R.id.tvTitlePanel);
        btnNavigation = (Button) findViewById(R.id.btnNavigation);

        btnHotelCancleOrder = (Button) findViewById(R.id.btnHotelCancleOrder);
        btnToPay = (Button) findViewById(R.id.btnToPay);
        ivOrdersOut = (ImageView) findViewById(R.id.ivOrdersOut);
        ivOrdersCheck = (ImageView) findViewById(R.id.ivOrdersCheck);
        ivOrdersSubmitted = (ImageView) findViewById(R.id.ivOrdersSubmitted);
        tvOrderAmount = (TextView) findViewById(R.id.tvOrderAmount);
        tvCheckInTime = (TextView) findViewById(R.id.tvCheckInTime);
        tvCheckInRoomType = (TextView) findViewById(R.id.tvCheckInRoomType);
        tvCheckInRoomNumber = (TextView) findViewById(R.id.tvCheckInRoomNumber);
        tvReservationr = (TextView) findViewById(R.id.tvReservationr);
        tvLatestShopShow = (TextView)findViewById(R.id.tvLatestShopShow);
        tvLatestShop = (TextView) findViewById(R.id.tvLatestShop);
        tvHotelName = (TextView) findViewById(R.id.tvHotelName);
        tvHotelAddress = (TextView) findViewById(R.id.tvHotelAddress);
        tvOrdersSubmitted = (TextView) findViewById(R.id.tvOrdersSubmitted);
        relatLatestShop = (LinearLayout)findViewById(R.id.relatLatestShop);

        tvOrderID = (TextView)findViewById(R.id.tvOrderID);
        tvTitlePanel.setText("订单详情");

        ivRenPacket = (ImageView) findViewById(R.id.ivRenPacket);
    }


    private void intdata() {
        Bundle bundle = getIntent().getExtras();
        str_booking_id = bundle.getString("booking_id");//订单id
        str_checkin_time = bundle.getString("checkin_time");
        pay_state = bundle.getString("pay_state");
        term_type = bundle.getString("term_type");
        LogUtil.log("str_checkin_time=" + pay_state);

        if (!pay_state.equals("-1")) {
            btnToPay.setVisibility(View.GONE);
        } else{
            btnToPay.setVisibility(View.VISIBLE);
        }

    }

    private void setOnClickLister() {
        btn_cancle.setOnClickListener(listener);
        btnNavigation.setOnClickListener(this);
        btnToPay.setOnClickListener(this);
        btnHotelCancleOrder.setOnClickListener(this);
        ivRenPacket.setOnClickListener(this);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           finish();
        }
    };

    private int type;
    private void getDialog(String message,int i) {
        final Dialog dialog = new Dialog(HotelStatusActivity.this, R.style.OrderDialog);
        LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());
        View view = mInflater.inflate(R.layout.orderdialog, null);
        TextView tvMessageLocal = (TextView)view.findViewById(R.id.tvMessageLocal);
        Button btnOrderCancle = (Button) view.findViewById(R.id.btnOrderCancle);
        final Button btnOrderSave = (Button) view.findViewById(R.id.btnOrderSave);
        dialog.setContentView(view);
        tvMessageLocal.setText(message);
        btnOrderCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        type = i;
        btnOrderSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type == 1){//取消订单
                    HttpClient.instance().cancel_order(str_booking_id, new HttpCallBack() {
                        @Override
                        public void onSuccess(ResponseBean responseBean) {
                            try {
                                JSONTokener jsonTokener = new JSONTokener(responseBean.toString());
                                JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                                if (jsonObject.getString("result").equals("true")) {
                                    BaseApplication.toast(R.string.Order_state111);
//                                    HttpClient.instance().get_room_booking_details(str_booking_id, new NetworkRequestLoginResult());
//                                    finish();
                                    initData(str_booking_id);
                                } else if (jsonObject.getString("result").equals("false")) {
                                    BaseApplication.toast(R.string.Toast20);
                                }
                            } catch (Exception e) {
                                BaseApplication.toast(R.string.Toast20);
                            }
                        }
                    });
                    dialog.dismiss();
                }else if(type == 2){//退订
                    /* 0服务器错误-1 没有任何支付记录，不能退款-2 该订单的最新支付记录不是已成功，不能退款。
                       -3 该订单已经结算，不能退款。-4退款单创建失败-5 退款单创建成功，但是退款失败1 退款成功*/
                    if (data!=null){
                        if (data.getRefund_bool().equals("false")){
                            AppContext.toast("该订单不允许退订，请到前台处理");
                            dialog.dismiss();
                            return;
                        }
                    }
                    HttpClient.instance().refund_order(str_booking_id, new HttpCallBack() {
                        @Override
                        public void onSuccess(ResponseBean responseBean) {
                            try {
                                dialog.dismiss();
                                JSONTokener jsonTokener = new JSONTokener(responseBean.toString());
                                JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                                if (jsonObject.getString("result").equals("0")) {
                                    BaseApplication.toast(R.string.Server_error);
                                } else if (jsonObject.getString("result").equals("-1")) {
                                    BaseApplication.toast(R.string.Not_payment_history);
                                } else if (jsonObject.getString("result").equals("-2")) {
                                    BaseApplication.toast(R.string.Not_new_payment_history);
                                } else if (jsonObject.getString("result").equals("-3")) {
                                    BaseApplication.toast(R.string.Order_has_been_settled);
                                } else if (jsonObject.getString("result").equals("-4")) {
                                    BaseApplication.toast(R.string.Refund_list_creation_failed);
                                } else if (jsonObject.getString("result").equals("-5")) {
                                    BaseApplication.toast(R.string.Refund_list_create_success);
                                } else if (jsonObject.getString("result").equals("-6")) {
                                    BaseApplication.toast(R.string.Go_to_hotel_desk_refund);
                                } else if (jsonObject.getString("result").equals("1")) {
                                    BaseApplication.toast(R.string.Successfully_canceled_order_please_patient_refund);
                                    btnHotelCancleOrder.setVisibility(View.GONE);
                                } else if (jsonObject.getString("result").equals("-7")) {
                                    AppContext.toast("已经有子订单入住成功,不允许退订");
                                    btnOrderSave.setClickable(false);
                                }else if (jsonObject.getString("result").equals("-8")) {
                                    AppContext.toast("该订单只能在前台退订");
                                    btnOrderSave.setClickable(false);
                                }
                            } catch (Exception e) {
                                BaseApplication.toast(R.string.Unsubscribe_from_failure);
                                dialog.dismiss();
                            }
                        }
                    });
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
//        HttpClient.instance().get_room_booking_details(str_booking_id, new NetworkRequestLoginResult());
        initData(str_booking_id);
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_hotel_status;
    }
    private Intent intent;
    private Bundle bundle;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNavigation://地图导航
//                intent = new Intent();
//                intent.setClass(getApplicationContext(), MapNavigationActivity.class);
//                bundle = new Bundle();
//                bundle.putDouble(Config.LATITUDE, latitude);//22.599307
//                bundle.putDouble(Config.LONGITUDE, longitude);//114.053608
//                intent.putExtras(bundle);
//                startActivity(intent);
//                dialog.dismiss();
                //先定位 ，在调起百度地图
                getToBaiduMap();
//                openGPSSettings();
                break;
            case R.id.btnToPay:
                HttpClient.instance().order_expired(str_booking_id, new HttpCallBack() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialog.show(HotelStatusActivity.this,"正在支付...");
                    }

                    @Override
                    public void onFailure(String error, String message) {
                        super.onFailure(error, message);
                        ProgressDialog.disMiss();
                    }

                    @Override
                    public void onSuccess(ResponseBean responseBean) {
                        ProgressDialog.disMiss();
                        LogUtil.log("aa="+responseBean.toString());
                        try {
                            JSONTokener jsonTokener = new JSONTokener(responseBean.toString());
                            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();

                            if (jsonObject.getString("result").equals("false")) {
                                intent = new Intent();
                                intent.setClass(getApplicationContext(), PayBillActivity.class);
                                bundle = new Bundle();
                                bundle.putString(Config.KEY_BOOKING_BILLNUM, str_booking_id);
                                bundle.putDouble(Config.KEY_HOTEL_ROOMTOTALPRICE, Double.parseDouble(data.getOrder_price()));
                                bundle.putString(Config.KEY_HOTEL_ID, data.getHotel_id());
//                                bundle.putString(Config.ORDER_PRICE, hotelStatsInfo.getOrder_price());
                                if (data.getStart_date() == data.getEnd_date()) {
                                    bundle.putString(Config.KEY_SHOW_ROOM_MODE, Config.ROOM_MODE_HOUR);
                                } else {
                                    bundle.putString(Config.KEY_SHOW_ROOM_MODE, Config.ROOM_MODE_DAY);
                                }
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }else{
                                BaseApplication.toast("订单已过期");
                            }
                        } catch (Exception e) {
                            LogUtil.log(e.toString());
                        }

                    }
                });

                break;
            case R.id.btnHotelCancleOrder://取消订单
                if (btnHotelCancleOrder.getText().toString().equals("取消订单")) {//取消订单
                    if (data.getCancel().equals("true")){
                        if (data.getOther_room().equals("[]")||data.getOther_room().equals("null")){
                            getDialog("确定取消订单?",1);
                        }else {
                            getDialog("确定取消订单及其关联的其他房间?",1);
                        }
                    }else {
                        AppContext.toast("该订单不能取消");
                    }

                } else if (btnHotelCancleOrder.getText().toString().equals("去评论")) {//评论
                    intent = new Intent();
                    intent.setClass(getApplicationContext(), CommentActivity.class);
                    bundle = new Bundle();
                    bundle.putString("hotel_id", data.getHotel_id());
                    bundle.putString("hotel_caption", data.getHotel_caption());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else if(btnHotelCancleOrder.getText().toString().equals("退订")){
                    getDialog("确定退订?",2);
                }
                break;
            case R.id.ivRenPacket:
                getDialog();
                break;
        }
    }

    /**
     * 判断是否安装目标应用
     *
     * @param packageName 目标应用安装后的包名
     * @return 是否已安装目标应用
     */
    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    //判断GPS是否开启
    private void openGPSSettings() {
        LocationManager alm = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        if (alm
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            // "GPS模块正常 "
            boolean b = mPermissionHelper.checkPermissions(WRITE_PERMISSION, WRITE_PERMISSION02);
            if (b){
                getLocation();
            }else {
                mPermissionHelper.permissionsCheck(listPermission, WRITE_RESULT_CODE);
            }
            // 等待提示
//            dialog = new ProgressDialog(MyLockGroupsActivity.this);
//            dialog.setMessage("正在定位...");
//            dialog.setCancelable(true);
//            dialog.show();
            return;
        }
        Toast.makeText(this, "请开启GPS！", Toast.LENGTH_SHORT).show();
        //这个为跳转到安全页面
//        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
//        startActivityForResult(intent,0); //此为设置完成后返回到获取界面
    }

    private void getLocation() {
        // 注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Common.LOCATION_ACTION);
        this.registerReceiver(new LocationBroadcastReceiver(), filter);
        // 启动服务
        Intent intent = new Intent();
        intent.setClass(this, LocationSvc.class);
        startService(intent);
    }

    /*
   *  下载百度地图
   *
   * */
    private void getCancelDialog() {
        final Dialog dialog = new Dialog(HotelStatusActivity.this, R.style.OrderDialog);
        LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());
        View view = mInflater.inflate(R.layout.download_map, null);
        dialog.setContentView(view);
        view.findViewById(R.id.dialog_Cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.dialog_Sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //显示手机上所有的market商店
                Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");//id=包名
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);

            }
        });
        dialog.show();
    }

    private class LocationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(Common.LOCATION_ACTION)) return;
            String locationInfo = intent.getStringExtra(Common.LOCATION);
            Bundle bundle = intent.getExtras();
            double mylatitude = bundle.getDouble(Config.LATITUDE);//纬度
            double mylongitude = bundle.getDouble(Config.LONGITUDE);//经度
            try {
                //移劢 APP 调起 Android 百度地图方式举例
//              Intent intent1 = Intent.getIntent("intent://map/direction?origin=latlng:22.612130,114.023295|name:我家&destination=深圳通大夏&mode=driving&region=深圳&src=网客网|smartlock#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                Intent intent1 = Intent.getIntent("intent://map/direction?destination=latlng:" + latitude + "," + longitude + "|name:" + tvHotelName + "&mode=driving&region=深圳&src=千万鼎|smartlock#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                if (isInstallByread("com.baidu.BaiduMap")) {
                    startActivity(intent1); //启动调用
                    Log.e("GasStation", "百度地图客户端已经安装");
                } else {
                    getCancelDialog();
                }
            } catch (Exception e) {
                e.printStackTrace();
                getCancelDialog();
            }
            HotelStatusActivity.this.unregisterReceiver(this);// 不需要时注销
        }
    }


    private LatLng gpsToBaiduMap( LatLng sourceLatLng){
        // 将GPS设备采集的原始GPS坐标转换成百度坐标
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case WRITE_RESULT_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //如果请求成功，则进行相应的操作，比如我们这里打开一个新的Activity
//                    Intent intent = new Intent(MyLockGroupsActivity.this,PermissionActivity.class);
//                    startActivity(intent);
//                    AppContext.toast("ok");
                    openGPSSettings();

                } else {
                    //如果请求失败
                    AppContext.toast("请手动打开定位权限");
                    mPermissionHelper.startAppSettings();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
//                openGPSSettings();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }


    private void initData(String booking_id){
        HttpClient.instance().roomOrderDetail(booking_id, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                data = responseBean.getData(HotelDetail.class);
                if (!data.getBonus_id().equals("false")){
                    getRedPackge(data.getBonus_id());
                    ivRenPacket.setVisibility(View.VISIBLE);
                }else {
                    ivRenPacket.setVisibility(View.GONE);
                }
                tvHotelName.setText(data.getHotel_caption());
                tvHotelAddress.setText(data.getAddress());
                tvOrderID.setText(data.getID());
                tvOrderAmount.setText("￥" + data.getPrice());
                tvCheckInRoomType.setText(data.getCaption());
                tvCheckInRoomNumber.setText(data.getRoom_no());
                tvReservationr.setText(data.getCheckin_name());
                if (data.getLongitude() != null && !data.getLongitude().equals("null")) {
                    longitude = Double.parseDouble(data.getLongitude()) / 1000000;//转换正常数据
                }
                if (data.getLatitude() != null && !data.getLatitude().equals("null")) {
                    latitude = Double.parseDouble(data.getLatitude()) / 1000000;
                    LatLng latLng = new LatLng(latitude, longitude);
                    LatLng latLng1 = gpsToBaiduMap(latLng);
                    longitude = latLng1.longitude;
                    latitude = latLng1.latitude;
                }
                if (data.getTerm_type().equals("1")) {
                    relatLatestShop.setVisibility(View.GONE);
                } else {//钟点房最晚到点时间
                    relatLatestShop.setVisibility(View.VISIBLE);
                    tvLatestShop.setText(data.getExpect_checkin_time());
                }
                if (term_type.equals("1")) {//全日房

                    DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date d1 = dfm.parse(data.getEnd_date());
                        Date d2 = dfm.parse(data.getStart_date());

                        long diff = d1.getTime() - d2.getTime();//这样得到的差值是微秒级别
                        long days = diff / (1000 * 60 * 60 * 24);

                        long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
                        long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
                        tvCheckInTime.setText(data.getStart_date() + "至" + data.getEnd_date() + "(共" + String.valueOf(days) + "晚）");
                    } catch (Exception e) {
                        LogUtil.log(e.toString());
                    }
                } else {
                    tvCheckInTime.setText(data.getHour_end() + "至" + data.getHour_start()+ " 共："
                            + DateUtil.getHours(DateUtil.getLongTime(data.getHour_end()), DateUtil.getLongTime(data.getHour_start()))+"h");
                }
                switch (data.getState()) {
                    case "-1"://已删除订单
                        break;
                    case "1"://已确认订单
                        ivOrdersSubmitted.setImageResource(R.drawable.pic_submit_blue);
                        switch (data.getPay_state()) {
                            case "0"://未支付
                                btnHotelCancleOrder.setVisibility(View.VISIBLE);
                                tvOrdersSubmitted.setText("已提交" + "\n" + "(" + "未支付" + ")");
                                btnToPay.setVisibility(View.VISIBLE);
                                break;
                            case "1"://已支付
                                btnHotelCancleOrder.setVisibility(View.VISIBLE);
                                btnToPay.setVisibility(View.GONE);
                                btnHotelCancleOrder.setText(R.string.hotel_state_unsubscribe);
                                btnHotelCancleOrder.setVisibility(View.VISIBLE);
                                btnHotelCancleOrder.setBackgroundResource(R.drawable.bg_btn_gray);
                                btnHotelCancleOrder.setTextColor(getResources().getColor(R.color.OrderInfoGray));
                                tvOrdersSubmitted.setText("已提交" + "\n" + "(" + "支付成功" + ")");
                                break;
                            case "2"://已退款
                                btnToPay.setVisibility(View.GONE);
                                tvOrdersSubmitted.setText(R.string.hotel_state_refund_success);
                                break;
                            case "3"://支付失败
                                btnHotelCancleOrder.setVisibility(View.VISIBLE);
                                tvOrdersSubmitted.setText("已提交" + "\n" + "(" + "未支付" + ")");
                                break;
                            case "4"://退款失败
                                tvOrdersSubmitted.setText(R.string.hotel_state_refund);
                                break;
                        }
                        break;
                    case "2"://已经入住
                        ivOrdersSubmitted.setImageResource(R.drawable.pic_leave_blue);
                        ivOrdersCheck.setImageResource(R.drawable.pic_checkin_blue);
                        btnHotelCancleOrder.setVisibility(View.GONE);
                        tvOrdersSubmitted.setText(R.string.hotel_state_already_check_in);
                        ivOrdersCheck.invalidate();
                        btnToPay.setVisibility(View.GONE);
//
//                        switch (data.getPay_state()){
//                            case "0"://未支付
//                                break;
//                            case "1"://已支付
//                                break;
//                            case "2"://已退款
//                                break;
//                            case "3"://支付失败
//                                break;
//                            case "4":
//                                break;
//                        }
                        break;
                    case "3"://已退房
                        ivOrdersSubmitted.setImageResource(R.drawable.pic_leave_blue);
                        ivOrdersCheck.setImageResource(R.drawable.pic_checkin_blue);
                        btnHotelCancleOrder.setVisibility(View.GONE);
                        tvOrdersSubmitted.setText(R.string.hotel_state_already_check_in);
                        ivOrdersCheck.invalidate();
                        btnToPay.setVisibility(View.GONE);
                        break;
                    case "4"://结算完成
                        ivOrdersSubmitted.setImageResource(R.drawable.pic_submit_blue);
                        ivOrdersCheck.setImageResource(R.drawable.pic_checkin_blue);
                        ivOrdersOut.setImageResource(R.drawable.pic_leave_blue);
                        tvOrdersSubmitted.setText(R.string.hotel_state_already_shop);
                        btnHotelCancleOrder.setVisibility(View.VISIBLE);
                        btnHotelCancleOrder.setText(R.string.hotel_state_go_review);
                        btnHotelCancleOrder.setBackgroundResource(R.drawable.bg_btn_blue);
                        btnHotelCancleOrder.setTextColor(getResources().getColor(R.color.OrderInfoBlue));
                        btnToPay.setVisibility(View.GONE);
                        ivOrdersCheck.invalidate();
                        break;
                    case "5"://已取消订单
                        btnToPay.setVisibility(View.GONE);
                        ivOrdersSubmitted.setImageResource(R.drawable.pic_submit_gray);
                        btnHotelCancleOrder.setVisibility(View.GONE);
                        tvOrdersSubmitted.setText(R.string.hotel_state_has_canceled);
                        btnToPay.setVisibility(View.GONE);
                        switch (data.getPay_state()) {
                            case "0"://未支付
                                btnHotelCancleOrder.setVisibility(View.VISIBLE);
                                btnToPay.setVisibility(View.GONE);
                                btnHotelCancleOrder.setText(R.string.hotel_state_unsubscribe);
                                btnHotelCancleOrder.setVisibility(View.GONE);
                                tvOrdersSubmitted.setText("已取消" + "\n" + "(" + "未支付" + ")");
                                break;
                            case "1"://已支付
                                btnHotelCancleOrder.setVisibility(View.VISIBLE);
                                btnToPay.setVisibility(View.GONE);
                                btnHotelCancleOrder.setText(R.string.hotel_state_unsubscribe);
                                btnHotelCancleOrder.setBackgroundResource(R.drawable.bg_btn_gray);
                                btnHotelCancleOrder.setTextColor(getResources().getColor(R.color.OrderInfoGray));
                                btnHotelCancleOrder.setVisibility(View.VISIBLE);
                                tvOrdersSubmitted.setText("已取消" + "\n" + "(" + "支付成功" + ")");
                                break;
                            case "2"://已退款
                                btnToPay.setVisibility(View.GONE);
                                tvOrdersSubmitted.setText(R.string.hotel_state_refund_success);
                                break;
                            case "3"://支付失败
                                btnToPay.setVisibility(View.GONE);
                                tvOrdersSubmitted.setText("已取消" + "\n" + "(" + "未支付" + ")");
                                btnHotelCancleOrder.setVisibility(View.GONE);
                                ivOrdersSubmitted.setImageResource(R.drawable.pic_submit_gray);
                                break;
                            case "4":
                                btnToPay.setVisibility(View.GONE);
                                tvOrdersSubmitted.setText("已取消" + "\n" + "(" + "退款失败" + ")");
                                btnHotelCancleOrder.setVisibility(View.GONE);
                                ivOrdersSubmitted.setImageResource(R.drawable.pic_submit_gray);
                                break;
                            case "5":
                                btnToPay.setVisibility(View.GONE);
                                tvOrdersSubmitted.setText("已取消" + "\n" + "(" + "退款中" + ")");
                                btnHotelCancleOrder.setVisibility(View.GONE);
                                ivOrdersSubmitted.setImageResource(R.drawable.pic_submit_gray);
                                break;
                        }
                        break;
                }
            }
        });

    }
    /*
       *   分享红包Dialog
       *
       * */
    private void getDialog() {

        final Dialog dialog = new Dialog(HotelStatusActivity.this,R.style.Dialog_Fullscreen);//,
        LayoutInflater mInflater = LayoutInflater.from(HotelStatusActivity.this);
        final View view = mInflater.inflate(R.layout.redpackagedialog, null);
        dialog.setContentView(view);
        //关闭
        view.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.ivShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialog.dismiss();
//                mController.openShare(getActivity(), false);
                sharePopWindow(view,dialog);
            }
        });
        dialog.show();

    }

    private void sharePopWindow(View view, final Dialog dialog){
        if(sharePopwindow==null||!sharePopwindow.isShowing()){
            sharePopwindow=new SharePopwindow(HotelStatusActivity.this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharePopwindow.dismiss();
                    switch (v.getId()){
                        case R.id.ivWinXin:
                            toShare(SHARE_MEDIA.WEIXIN);
                            break;
                        case R.id.ivWinXinFriend:
                            toShare(SHARE_MEDIA.WEIXIN_CIRCLE);
                            break;
                        case R.id.ivQQ:
                            toShare(SHARE_MEDIA.QQ);
                            break;
                        case R.id.ivQQSpace:
                            toShare(SHARE_MEDIA.QZONE);
                            break;

                    }
                }
            });
            // 加了下面这行，onItemClick才好用
            sharePopwindow.setFocusable(true);
            sharePopwindow.showAtLocation(view, Gravity.BOTTOM,0,0);
        }else {
            sharePopwindow.dismiss();
        }
        sharePopwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dialog.dismiss();
            }
        });
    }

    private void toShare(final SHARE_MEDIA qqtype){

        HttpClient.instance().sendRedPackge(data.getBonus_id(),data.getNum_user() + "", data.getID(), new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(HotelStatusActivity.this, "正在领取红包...");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
                AppContext.toast("服务器错误，红包获取失败");
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                if (responseBean.getData().equals("true")) {
                    UMImage image = new UMImage(HotelStatusActivity.this, R.drawable.sharered_packge);
                    if (redPackge != null) {
                        new ShareAction(HotelStatusActivity.this).setPlatform(qqtype).withText(redPackge.getContent())
                                .withTitle(redPackge.getCaption())
                                .withTargetUrl(redPackge.getContent_url())
                                .withMedia(image)
                                .setCallback(umShareListener).share();
                    } else {
                        AppContext.toast("红包领取失败，请稍后再试");
                    }
                } else {
                    AppContext.toast("红包获取失败，请稍后再试");
                }
            }
        });




    }



    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            com.umeng.socialize.utils.Log.d("plat", "platform" + platform);
            if(platform.name().equals("WEIXIN_FAVORITE")){
                Toast.makeText(HotelStatusActivity.this,platform + " 收藏成功啦",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(HotelStatusActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(HotelStatusActivity.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            if(t!=null){
                com.umeng.socialize.utils.Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(HotelStatusActivity.this,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };
    private  void getRedPackge(String bonus_id){
        HttpClient.instance().activeDetail(bonus_id, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                redPackge = responseBean.getData(RedPackgeEntity.class);
            }
        });
    }

    private void getToBaiduMap(){
        try {
            //移劢 APP 调起 Android 百度地图方式举例
//              Intent intent1 = Intent.getIntent("intent://map/direction?origin=latlng:22.612130,114.023295|name:我家&destination=深圳通大夏&mode=driving&region=深圳&src=网客网|smartlock#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
            Intent intent1 = Intent.getIntent("intent://map/direction?destination=latlng:" + latitude + "," + longitude + "|name:" + tvHotelName + "&mode=driving&region=深圳&src=千万鼎|smartlock#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
            if (isInstallByread("com.baidu.BaiduMap")) {
                startActivity(intent1); //启动调用
                Log.e("GasStation", "百度地图客户端已经安装");
            } else {
                getCancelDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
            getCancelDialog();
        }
    }

}

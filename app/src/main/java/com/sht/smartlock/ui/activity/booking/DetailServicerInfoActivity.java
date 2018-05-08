package com.sht.smartlock.ui.activity.booking;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.helper.PermissionHelper;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.chatlocation.Common;
import com.sht.smartlock.ui.activity.chatlocation.LocationSvc;
import com.sht.smartlock.ui.adapter.LockCommentAdapter;
import com.sht.smartlock.ui.adapter.LockServicerAdapter;
import com.sht.smartlock.ui.entity.Comment;
import com.sht.smartlock.ui.entity.LockDetaileInfo;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DetailServicerInfoActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvLockName;
    private ImageView ivAddress,ivCall;
    private GridView gvServicer;
    private ImageView ivMore;
    private TextView tvTime,tvRoomNum,tvInfo;
    private ListView lvComment;
    private LockDetaileInfo data;
    private String[] str=new String[]{"停车场","WiFi","24小时热水","大床","空调","电视机","电吹风","行李员","接机服务","早餐服务","商务中心","棋牌室","中式餐厅",
            "暖气","接待外宾","洗衣服务","电梯","会议室","叫车服务","邮政服务","全天前台","信用卡","桑拿","SPA","叫醒服务","婚姻服务"};
    private int[] strImg=new int[]{R.drawable.icon_park,R.drawable.icon_net,R.drawable.icon_water,R.drawable.icon_bed,R.drawable.icon_aircondition,R.drawable.icon_tv,R.drawable.icon_drier,
            R.drawable.icon_aicon_baggage_clerk,R.drawable.icon_airport_pickup,R.drawable.icon_breakfast,R.drawable.icon_business,R.drawable.icon_chess_room,R.drawable.icon_chinese_food,R.drawable.icon_hotter,
            R.drawable.icon_jiedaiwaibin,R.drawable.icon_laundry,R.drawable.icon_liftservicer,R.drawable.icon_meeting_room,R.drawable.icon_panggil_mobil,R.drawable.icon_postalservice,R.drawable.icon_recepter,R.drawable.icon_reditcard,
            R.drawable.icon_sauna,R.drawable.icon_spa,R.drawable.icon_wake_up,R.drawable.icon_wedding_party};
    private LockServicerAdapter servicerAdapter;
    private List<String> listFacility=new ArrayList<>();
    private List<String> listImg=new ArrayList<>();
    private List<String> listALLImg=new ArrayList<>();
    private LockCommentAdapter commentAdapter;
    private List<Comment> comments=new ArrayList<>();
    private String strHotelId;
    //权限检测类
    private PermissionHelper mPermissionHelper;
    //返回值
    private static final int  WRITE_RESULT_CODE = 12;
    //权限名称
    private static final String  WRITE_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String  WRITE_PERMISSION02 = Manifest.permission.ACCESS_FINE_LOCATION;
    private List<String> listPermission=new ArrayList<>();
    private Double longitude = 22.612130;
    private Double latitude = 114.022295;
    private int pageid=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_detail_servicer_info);
        mPermissionHelper = new PermissionHelper(this);
        listPermission.add(WRITE_PERMISSION);
        listPermission.add(WRITE_PERMISSION02);
        Intent i=getIntent();
        Bundle extras = i.getExtras();
        data= (LockDetaileInfo) extras.getSerializable(Config.LOCKDETAILDATA);
        strHotelId = i.getStringExtra(Config.KEY_HOTEL_ID);
        onBack();
        initView();
        initOnClickListenter();
        initData(strHotelId);

    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail_servicer_info;
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
        tvLockName = (TextView) findViewById(R.id.tvLockName);
        ivAddress = (ImageView) findViewById(R.id.ivAddress);
        ivCall = (ImageView) findViewById(R.id.ivCall);
        gvServicer = (GridView) findViewById(R.id.gvServicer);
        ivMore = (ImageView) findViewById(R.id.ivMore);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvRoomNum = (TextView) findViewById(R.id.tvRoomNum);
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        lvComment = (ListView) findViewById(R.id.lvComment);

        tvLockName.setText(data.getCaption());
        tvInfo.setText(data.getIntr());
        servicerAdapter=new LockServicerAdapter(listFacility,listImg,getApplicationContext());
        gvServicer.setAdapter(servicerAdapter);
        //酒店有哪些服务（WIFI之类的）
        getServicerInfo();

        commentAdapter=new LockCommentAdapter(comments,getApplicationContext());
        lvComment.setAdapter(commentAdapter);
    }

    private void initOnClickListenter(){
        ivAddress.setOnClickListener(this);
        ivCall.setOnClickListener(this);
        ivMore.setOnClickListener(this);
    }
    private void getServicerInfo(){
        if (!data.getHotel_facility().equals("[]")) {
            try {
                JSONArray jsonArrayFacility = new JSONArray(data.getHotel_facility());
                for (int i = 0; i < jsonArrayFacility.length(); i++) {
                    listFacility.add(jsonArrayFacility.getString(i));
                }

                JSONArray jsonArrayFacility_url = new JSONArray(data.getFacility_url());
                for (int i = 0; i < jsonArrayFacility_url.length(); i++) {
                    if (i<12){
                        listImg.add(jsonArrayFacility_url.getString(i));
                    }
                    listALLImg.add(jsonArrayFacility_url.getString(i));
                }
                if (listALLImg.size()>12){
                    ivMore.setVisibility(View.VISIBLE);
                }else {
                    ivMore.setVisibility(View.GONE);
                }
//                for (int i = 0; i < str.length; i++) {
//                    if (listFacility.contains(str[i]) ) {
//                        listImg.add(strImg[i]);
//                    }
//                }
                servicerAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initData(String hotelId){
        HttpClient.instance().lockDetailComment(hotelId,pageid+"", new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                List<Comment> listData = responseBean.getListData(Comment.class);
                if (listData!=null){
                    if (listData.size()>5){
                        for (int i=0;i<5;i++){
                            comments.add(listData.get(i));
                        }
                    }else {
                        comments.addAll(listData);
                    }
                }
                commentAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivAddress:
//                openGPSSettings();
                getToBaiduMap();
                break;
            case R.id.ivCall: //调用拨号面板：
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + data.getPhone_no()));
                startActivity(intent);
                break;
            case R.id.ivMore:
                if (listImg.size()==listALLImg.size()){//将展示部分
                    listImg.clear();
                    for (int i=0;i<listALLImg.size();i++){
                        if (i<12){
                            listImg.add(listALLImg.get(i));
                        }
                    }
                    servicerAdapter.notifyDataSetChanged();
                    ivMore.setImageResource(R.drawable.btn_more);
                }else {//将全部展示
                    listImg.clear();
                    listImg.addAll(listALLImg);
                    servicerAdapter.notifyDataSetChanged();
                    ivMore.setImageResource(R.drawable.btn_upper);
                }
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
        final Dialog dialog = new Dialog(DetailServicerInfoActivity.this, R.style.OrderDialog);
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
            //转百度经纬度
            LatLng latLngmy=new LatLng(mylatitude,mylongitude);
            LatLng latLngmy01 = gpsToBaiduMap(latLngmy);
            mylatitude=latLngmy01.latitude;
            mylongitude=latLngmy01.longitude;
            try {
                //移劢 APP 调起 Android 百度地图方式举例
//              Intent intent1 = Intent.getIntent("intent://map/direction?origin=latlng:22.612130,114.023295|name:我家&destination=深圳通大夏&mode=driving&region=深圳&src=网客网|smartlock#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                if (!data.getLatitude().equals("null")) {
                    latitude = Double.parseDouble(data.getLatitude()) / 1000000;
                    longitude = Double.parseDouble(data.getLongitude()) / 1000000;
                    LatLng latLnglock=new LatLng(latitude,longitude);
                    LatLng latLnglock01 = gpsToBaiduMap(latLnglock);
                    latitude=latLnglock01.latitude;
                    longitude=latLnglock01.longitude;
                }
                Intent intent1 = Intent.getIntent("intent://map/direction?destination=latlng:" + latitude + "," + longitude + "|name:" + data.getCaption() + "&mode=driving&region=深圳&src=千万鼎|smartlock#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
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
            DetailServicerInfoActivity.this.unregisterReceiver(this);// 不需要时注销
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
    private void getToBaiduMap(){
        try {
            if (!data.getLatitude().equals("null")) {
                latitude = Double.parseDouble(data.getLatitude()) / 1000000;
                longitude = Double.parseDouble(data.getLongitude()) / 1000000;
                LatLng latLnglock=new LatLng(latitude,longitude);
                LatLng latLnglock01 = gpsToBaiduMap(latLnglock);
            }
            //移劢 APP 调起 Android 百度地图方式举例
//              Intent intent1 = Intent.getIntent("intent://map/direction?origin=latlng:22.612130,114.023295|name:我家&destination=深圳通大夏&mode=driving&region=深圳&src=网客网|smartlock#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
            Intent intent1 = Intent.getIntent("intent://map/direction?destination=latlng:" + latitude + "," + longitude + "|name:" + data.getCaption() + "&mode=driving&region=深圳&src=千万鼎|smartlock#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
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

package com.sht.smartlock.ui.activity;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.AppManager;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.chatlocation.Common;
import com.sht.smartlock.ui.activity.chatlocation.LocationByPower;
import com.sht.smartlock.ui.activity.chatlocation.LocationByTest;

public class OtherActivity extends BaseActivity {
    private ImageView btn_cancle;
    private TextView tvTitlePanel;
    final StringBuffer sb=new StringBuffer();
    private TextView tvTest;
    public LocationClient mLocationClient;//定位SDK的核心类
    public MyLocationListener mMyLocationListener;//定义监听类
    // 在Android进行通知处理，首先需要重系统哪里获得通知管理器NotificationManager，它是一个系统Service。
    NotificationManager manager;
    private static final int NOTIFICATION_FLAG = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findviewbyid();
        setClickLister();
    }

    private void findviewbyid() {
        btn_cancle = (ImageView) findViewById(R.id.btn_cancle);
        tvTitlePanel = (TextView) findViewById(R.id.tvTitlePanel);
        manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        tvTitlePanel.setText("其他");
        tvTest = (TextView) findViewById(R.id.tvTest);

        findViewById(R.id.bnTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openGPSSettings();
//                InitLocation();
            }


        });


//        HttpClient.instance().addhotelService("1", "1", "aa", "2", new NetworkRequestLoginResult());
    }

    private void setClickLister(){
        btn_cancle.setOnClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AppManager.getAppManager().finishActivity();
        }
    };

    class  NetworkRequestLoginResult extends HttpCallBack {
        @Override
        public void onFailure(String error, String message) {
            super.onFailure(error, message);
        }

        @Override
        public void onFinish() {
            super.onFinish();
        }

        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onSuccess(ResponseBean responseBean) {
            BaseApplication.toast(responseBean.toString());
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_other;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }



    //判断GPS是否开启
    private void openGPSSettings() {
        LocationManager alm = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        if (alm
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            // "GPS模块正常 "
            getLocation();
            // 等待提示
            return;
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(OtherActivity.this);
            dialog.setMessage("请打开GPS");
            dialog.setPositiveButton("确定",
                    new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            // 转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                            arg0.dismiss();
                        }
                    });
            dialog.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            });
            dialog.show();
        }
    }

    private void getLocation() {
        // 注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Common.LOCATION_TEST);
        this.registerReceiver(new LocationBroadcastReceiver(), filter);
        // 启动服务
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), LocationByTest.class);
        startService(intent);
        AppContext.toast("开启服务");

    }

    private class LocationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(Common.LOCATION_TEST)) return;
            String locationInfo = intent.getStringExtra(Common.LOCATION);
            Bundle bundle = intent.getExtras();
            double latitude = bundle.getDouble(Config.LATITUDE);//纬度
            double longitude = bundle.getDouble(Config.LONGITUDE);//经度
            String distance = bundle.getString(Config.DISTANCE);
            AppContext.toast("接收广播");
            setNotification();
////            latitude=22.612130;
////            longitude=114.022295;
//            isCanClosePower(distance, this);
//            BaseApplication.toast(distance + "米");
            sb.append("longitude="+longitude);
            sb.append("latitude=" + latitude + ";--;" +distance+"米"+n+"次");
            n++;
            tvTest.setText(sb.toString());
//            tvTest.setText("longitude="+longitude+"latitude=" + latitude + ";--;"+distance+"米");
            Log.e("TAAG", "--------------------------" + distance);
//            unregisterReceiver(this);// 不需要时注销
        }
    }



//    @Override
//    protected void onStop() {
//        mLocationClient.stop();
//        super.onStop();
//    }

    private void InitLocation(){
        mLocationClient = new LocationClient(getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
       //
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置高精度定位定位模式
        option.setCoorType("bd09ll");//设置百度经纬度坐标系格式
        option.setScanSpan(10000);//设置发起定位请求的间隔时间为1000ms
        option.setIsNeedAddress(true);//反编译获得具体位置，只有网络定位才可以
        mLocationClient.setLocOption(option);
        //
        mLocationClient.start();
    }
    /**
     * 实现实位回调监听
     */
    int n=0;
    public class MyLocationListener implements BDLocationListener {

        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            AppContext.toast("第"+n+"次");
            setNotification();
            n++;
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());//获得当前时间
            sb.append("\nerror code : ");
            sb.append(location.getLocType());//获得erro code得知定位现状
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());//获得纬度
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());//获得经度
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation){//通过GPS定位
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());//获得速度
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\ndirection : ");
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());//获得当前地址
                sb.append(location.getDirection());//获得方位
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){//通过网络连接定位
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());//获得当前地址
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());//获得经营商？
            }
            logMsg(sb.toString());
            Log.i("BaiduLocationApiDem", sb.toString());
        }
    }

    /**
     * 显示请求字符串
     * @param str
     */
    public void logMsg(String str) {
        try {
            if (tvTest != null)
                tvTest.setText(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setNotification() {
        PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        // 通过Notification.Builder来创建通知，注意API Level
        // API11之后才支持
        Notification notify2 = new Notification.Builder(this)
//                .setSmallIcon(R.drawable.icon) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap
                        // icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.icon))
                .setTicker("网客网:" + "您有新短消息，请注意查收！")// 设置在status
                        // bar上显示的提示文字
                .setContentTitle("出门通知")// 设置在下拉status
                        // bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
                .setContentText("你已出门，请注意关闭电源")// TextView中显示的详细内容
                .setContentIntent(pendingIntent2) // 关联PendingIntent
                .setNumber(1) // 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。
                .getNotification(); // 需要注意build()是在API level
        // 16及之后增加的，在API11中可以使用getNotificatin()来代替
        notify2.flags |= Notification.FLAG_AUTO_CANCEL;
        //设置默认声音
        notify2.defaults = Notification.DEFAULT_SOUND;
        manager.notify(NOTIFICATION_FLAG, notify2);

    }

}

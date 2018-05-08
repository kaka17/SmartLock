package com.sht.smartlock.ui.activity.mine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.sht.smartlock.R;

public class BaiduMapActivity extends AppCompatActivity {
    private MapView mapView;
    private BaiduMap mBaiduMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 在使用SDK各组件之前初始化context信息，传入ApplicationContext
        // 注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        // 通过广播接收器验证百度地图KEY及各功能可否使用（key,网络，权限）
        checkKEY();

        // 装载布局文件
        setContentView(R.layout.activity_baidu_map);
        mapView = (MapView) findViewById(R.id.mapView);

        mBaiduMap = mapView.getMap();
        // 普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        // 卫星地图
        // mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);

        // 开启交通图
        mBaiduMap.setTrafficEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // activity 恢复时同时恢复地图控件
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // activity 暂停时同时暂停地图控件
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // activity 销毁时同时销毁地图控件
        mapView.onDestroy();
    }

    private void checkKEY() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter
                .addAction(SDKInitializer.SDK_BROADTCAST_INTENT_EXTRA_INFO_KEY_ERROR_CODE);
        MyKeyCheckReceiver myKeyCheckReceiver = new MyKeyCheckReceiver();
        registerReceiver(myKeyCheckReceiver, intentFilter);
    }

    class MyKeyCheckReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            if (intent
                    .getAction()
                    .equals(SDKInitializer.SDK_BROADTCAST_INTENT_EXTRA_INFO_KEY_ERROR_CODE)) {
                setTitle("baiduMap权限错误");
            }
        }

    }
}

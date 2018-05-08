package com.sht.smartlock.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
//import com.baidu.nplatform.comapi.basestruct.GeoPoint;
import com.sht.smartlock.AppManager;
import com.sht.smartlock.R;
import com.sht.smartlock.ui.activity.base.BaseActivity;

import java.net.URISyntaxException;

public class MyLockMapNavigationActivity extends BaseActivity {

    private TextView tv_Tile;
    private Intent intent;
    // 天安门坐标
    double mLat1 = 39.915291;
    double mLon1 = 116.403857;
    // 百度大厦坐标
    double mLat2 = 40.056858;
    double mLon2 = 116.308194;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_my_lock_map_navigation);


        //移劢 APP 调起 Android 百度地图方式举例
        try {
           Intent intent = Intent.getIntent("intent://map/direction?origin=latlng:22.612130,114.023295|name:我家&destination=深圳通大夏&mode=driving&region=深圳&src=网客网|smartlock#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
            startActivity(intent); //启劢调用
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

//网页应用调起 Android 百度地图方式举例


        onBack();
        initView();
        setOnClinckListener();
    }
    @Override
    protected boolean hasToolBar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_lock_map_navigation;
    }
    private void onBack(){
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppManager.getAppManager().finishActivity(MyLockMapNavigationActivity.class);
            }
        });
    }


    private void initView(){
        tv_Tile = (TextView) findViewById(R.id.tv_Tile);



    }
    private void setOnClinckListener(){

    }
    /**
     * 开始导航
     *
     * @param view
     */
    public void startNavi(View view) {
        int lat = (int) (mLat1 * 1E6);
        int lon = (int) (mLon1 * 1E6);
//        GeoPoint pt1 = new GeoPoint(lat, lon);
        lat = (int) (mLat2 * 1E6);
        lon = (int) (mLon2 * 1E6);
//        GeoPoint pt2 = new GeoPoint(lat, lon);
        // 构建 导航参数
//        NaviPara para = new NaviPara();
//        para.startPoint = pt1;
//        para.startName = "从这里开始";
//        para.endPoint = pt2;
//        para.endName = "到这里结束";

        try {

//            BaiduMapNavigation.openBaiduMapNavi(para, this);

        } catch (BaiduMapAppNotSupportNaviException e) {
            e.printStackTrace();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
            builder.setTitle("提示");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

//                    BaiduMapNavigation.GetLatestBaiduMapApp(MyLockMapNavigationActivity.this);

                }
            });

            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create().show();
        }
    }

}

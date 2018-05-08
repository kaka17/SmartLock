package com.sht.smartlock.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
//import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
//import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
//import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.core.TaxiInfo;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.base.BaseApplication;

import java.text.DecimalFormat;


public class MapNavigationActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_Bus;
    private ImageView iv_Car;
    private ImageView iv_Walk;
    private ImageView iv_Bicycle;
    private TextView tv_Tile;
    private TextView tv_Distance;
    private MapView mapView;
    private BaiduMap mBaiduMap;
    private double Latitude;// 纬度
    private double Longitude;// 经度
    private String address = "";// 地址
    private RoutePlanSearch routePlanSearch = null;
    private RouteLine route = null;
    private PlanNode startNode;
    private PlanNode endNode;
    private double lock_Latitude; // 酒店纬度
    private double lock_Longitude;// 酒店经度
    private Handler handler = new Handler();

    private LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private LinearLayout lin_Distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        // 通过广播接收器验证百度地图KEY及各功能可否使用（key,网络，权限）
        checkKEY();
        setContentView(R.layout.activity_map_navigation);
        Bundle bundle = getIntent().getExtras();

        lock_Latitude = bundle.getDouble(Config.LATITUDE);//纬度
        lock_Longitude = bundle.getDouble(Config.LONGITUDE);


        onBack();
        initView();
        setOnClinckListener();
        //设置定位监听
        setLocation();
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.requestLocation();
        } else {
            Log.d("LocSDK5", "locClient is null or not started");
        }
        //设置定位参数包括：定位模式（高精度定位模式，低功耗定位模式和仅用设备定位模式）
        // ，返回坐标类型，是否打开GPS，是否返回地址信息、位置语义化信息、POI信息等等。
//        initLocation();
        currentpositioning();
        //启动定位SDK
        mLocationClient.start();

    }

    private void onBack() {
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }


    private void initView() {
        iv_Bus = (ImageView) findViewById(R.id.iv_Bus);
        iv_Car = (ImageView) findViewById(R.id.iv_Car);
        iv_Walk = (ImageView) findViewById(R.id.iv_Walk);
        iv_Bicycle = (ImageView) findViewById(R.id.iv_Bicycle);
        tv_Tile = (TextView) findViewById(R.id.tv_Tile);
        tv_Distance = (TextView) findViewById(R.id.tv_Distance);
        lin_Distance = (LinearLayout) findViewById(R.id.lin_Distance);

        //
        mapView = (MapView) findViewById(R.id.mapView);
        mBaiduMap = mapView.getMap();
        // 普通地图
//        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        // 开启交通图
        mBaiduMap.setTrafficEnabled(true);


    }

    private void setOnClinckListener() {
        iv_Bus.setOnClickListener(this);
        iv_Car.setOnClickListener(this);
        iv_Walk.setOnClickListener(this);
        iv_Bicycle.setOnClickListener(this);


    }

    //设置定位监听
    private void setLocation() {
//        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
//        mLocationClient.registerLocationListener(myListener);    //注册监听函数
//
//        //线路规划
//        routePlanSearch = RoutePlanSearch.newInstance();
//
//        routePlanSearch
//                .setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
//
//                    @Override
//                    public void onGetWalkingRouteResult(
//                            WalkingRouteResult result) {
//                        if (result == null
//                                || result.error != SearchResult.ERRORNO.NO_ERROR) {
//                            Toast.makeText(getApplicationContext(), "抱歉，未找到结果",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
//                            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
//                            // result.getSuggestAddrInfo()
//                            return;
//                        }
//                        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
//                            try {
//                                route = result.getRouteLines().get(0);
//                                WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(
//                                        mBaiduMap);
//                                mBaiduMap.setOnMarkerClickListener(overlay);
//                                // routeOverlay = overlay;
//                                overlay.setData(result.getRouteLines().get(0));
//                                overlay.addToMap();
//                                overlay.zoomToSpan();
//                                TaxiInfo taxiInfo = result.getTaxiInfo();
//                                if (taxiInfo != null) {
//                                    try {
//                                        tv_Distance.setText(result.getTaxiInfo().getDistance() + "m");
//
//                                    } catch (Exception e) {
//
//                                    }
//                                }
//                            } catch (Exception e) {
//
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onGetTransitRouteResult(
//                            TransitRouteResult result) {
//                        if (result == null
//                                || result.error != SearchResult.ERRORNO.NO_ERROR) {
//                            Toast.makeText(getApplicationContext(), "抱歉，未找到结果",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
//                            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
//                            // result.getSuggestAddrInfo()
//                            return;
//                        }
//                        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
//                            try {
//
//                                TransitRouteOverlay overlay = new MyTransitRouteOverlay(
//                                        mBaiduMap);
//                                mBaiduMap.setOnMarkerClickListener(overlay);
//                                overlay.setData(result.getRouteLines().get(0));
//                                overlay.addToMap();
//                                overlay.zoomToSpan();
//                                TaxiInfo taxiInfo = result.getTaxiInfo();
//                                if (taxiInfo != null) {
//                                    try {
//                                        tv_Distance.setText(result.getTaxiInfo().getDistance() + "m");
//                                    } catch (Exception e) {
//
//                                    }
//                                }
//
//                            } catch (Exception e) {
//
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onGetDrivingRouteResult(
//                            DrivingRouteResult result) {
//                        if (result == null
//                                || result.error != SearchResult.ERRORNO.NO_ERROR) {
//                            Toast.makeText(getApplicationContext(), "抱歉，未找到结果",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
//                            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
//                            // result.getSuggestAddrInfo()
//                            return;
//                        }
//                        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
//                            try {
//
//
//                                DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(
//                                        mBaiduMap);
//                                mBaiduMap.setOnMarkerClickListener(overlay);
//                                overlay.setData(result.getRouteLines().get(0));
//                                overlay.addToMap();
//                                overlay.zoomToSpan();
//
//                                TaxiInfo taxiInfo = result.getTaxiInfo();
//                                if (taxiInfo != null) {
//                                    try {
//                                        tv_Distance.setText(result.getTaxiInfo().getDistance() + "m");
//
//                                    } catch (Exception e) {
//                                    }
//                                }
//                            } catch (Exception e) {
//
//                            }
//                        }
//                    }
//                });
//

    }

    //设置定位参数包括：定位模式（高精度定位模式，低功耗定位模式和仅用设备定位模式）
    // ，返回坐标类型，是否打开GPS，是否返回地址信息、位置语义化信息、POI信息等等。
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);


    }

    // 获取当前位置
    private void currentpositioning() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
        // option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
        mLocationClient.setLocOption(option);
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

    private void setLineByCar() {
        // 重置浏览节点的路线数据
//        route = null;
//        mBaiduMap.clear();
        LatLng cenpt_start = new LatLng(Latitude, Longitude);
        LatLng cenpt_end = new LatLng(lock_Latitude, lock_Longitude);
        startNode = PlanNode.withLocation(cenpt_start);
        endNode = PlanNode.withLocation(cenpt_end);
        routePlanSearch.drivingSearch((new DrivingRoutePlanOption()).from(
                startNode).to(endNode));
        double distance = getDistance(cenpt_start, cenpt_end);
        DecimalFormat df = new DecimalFormat("######0.0");
        String format = df.format(distance);
        if (distance != 0) {
//            lin_Distance.setVisibility(View.VISIBLE);
            tv_Distance.setText(format + "m");
        }
    }


    @Override
    public void onClick(View v) {
        // 重置浏览节点的路线数据
        route = null;
        mBaiduMap.clear();
        LatLng cenpt_start = new LatLng(Latitude, Longitude);
        LatLng cenpt_end = new LatLng(lock_Latitude, lock_Longitude);
        startNode = PlanNode.withLocation(cenpt_start);
        endNode = PlanNode.withLocation(cenpt_end);

        double distance = getDistance(cenpt_start, cenpt_end);
        DecimalFormat df = new DecimalFormat("######0.0");
        String format = df.format(distance);
        if (distance != 0) {
//            lin_Distance.setVisibility(View.VISIBLE);
            tv_Distance.setText(format + "m");
        }
        switch (v.getId()) {
            case R.id.iv_Bus://公交
                routePlanSearch.transitSearch((new TransitRoutePlanOption())
                        .from(startNode).city(address).to(endNode));
                iv_Bus.setImageResource(R.drawable.iv_bus_02);
                iv_Car.setImageResource(R.drawable.iv_car_01);
                iv_Walk.setImageResource(R.drawable.iv_walk_01);
                break;
            case R.id.iv_Car://自驾
                routePlanSearch.drivingSearch((new DrivingRoutePlanOption()).from(
                        startNode).to(endNode));
                iv_Bus.setImageResource(R.drawable.iv_bus_01);
                iv_Car.setImageResource(R.drawable.iv_car_02);
                iv_Walk.setImageResource(R.drawable.iv_walk_01);
                break;
            case R.id.iv_Walk://步行
                routePlanSearch.walkingSearch((new WalkingRoutePlanOption()).from(
                        startNode).to(endNode));
                iv_Bus.setImageResource(R.drawable.iv_bus_01);
                iv_Car.setImageResource(R.drawable.iv_car_01);
                iv_Walk.setImageResource(R.drawable.iv_walk_02);
                break;
            case R.id.iv_Bicycle://自行车
                routePlanSearch.walkingSearch((new WalkingRoutePlanOption()).from(
                        startNode).to(endNode));

                break;
        }

    }

    /**
     * 计算两点之间距离
     *
     * @param start
     * @param end
     * @return 米
     */
    public double getDistance(LatLng start, LatLng end) {
        double lat1 = (Math.PI / 180) * start.latitude;
        double lat2 = (Math.PI / 180) * end.latitude;

        double lon1 = (Math.PI / 180) * start.longitude;
        double lon2 = (Math.PI / 180) * end.longitude;

//      double Lat1r = (Math.PI/180)*(gp1.getLatitudeE6()/1E6);
//      double Lat2r = (Math.PI/180)*(gp2.getLatitudeE6()/1E6);
//      double Lon1r = (Math.PI/180)*(gp1.getLongitudeE6()/1E6);
//      double Lon2r = (Math.PI/180)*(gp2.getLongitudeE6()/1E6);

        //地球半径
        double R = 6371;

        //两点间距离 km，如果想要米的话，结果*1000就可以了
        double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * R;

        return d * 1000;
    }

    // 定位 ，确定自己所在的位置
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
            }
            System.out.println("sb.toString() ==" + sb.toString());
            Latitude = location.getLatitude();// 纬度
            Longitude = location.getLongitude();// 经度

            address = location.getCity();
            // 实时我的位置更新数据
//            modify_user_login_time(Longitude + "", Latitude + "");

            System.out.println("add =" + address);
            System.out.println(Latitude + Longitude);
            try {
                LatLng cenpt = new LatLng(Latitude, Longitude);
                // LatLng cenpt = new LatLng(30.688781, 117.530598);
                // 定义地图状态
                MapStatus mMapStatus = new MapStatus.Builder().target(cenpt)
                        .zoom(18).build();
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.locationposition);
                // 构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions().position(cenpt).icon(
                        bitmap);
                // 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化06-07 15:07:49.207:
                MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
                        .newMapStatus(mMapStatus);
                // 改变地图状态
                mBaiduMap.setMapStatus(mMapStatusUpdate);
                // 每一次清楚数据
                mBaiduMap.clear();
                // 在地图上添加Marker，并显示
                mBaiduMap.addOverlay(option);

                //设置初始化路线
                setLineByCar();
            } catch (Exception e) {
//                 BaseApplication.toast(e.toString());
                AppContext.toLog(e.toString());
            }
        }
    }


//    class MyWalkingRouteOverlay extends WalkingRouteOverlay {
//
//        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
//            super(baiduMap);
//        }
//
//        @Override
//        public BitmapDescriptor getStartMarker() {
//            return BitmapDescriptorFactory.fromResource(R.drawable.icon_start);
//        }
//
//        @Override
//        public BitmapDescriptor getTerminalMarker() {
//            return BitmapDescriptorFactory.fromResource(R.drawable.icon_end);
//        }
//    }
//
//    class MyTransitRouteOverlay extends TransitRouteOverlay {
//
//        public MyTransitRouteOverlay(BaiduMap baiduMap) {
//            super(baiduMap);
//            // TODO Auto-generated constructor stub
//        }
//
//        @Override
//        public BitmapDescriptor getStartMarker() {
//            return BitmapDescriptorFactory.fromResource(R.drawable.icon_start);
//        }
//
//        @Override
//        public BitmapDescriptor getTerminalMarker() {
//            return BitmapDescriptorFactory.fromResource(R.drawable.icon_end);
//        }
//    }
//
//    class MyDrivingRouteOverlay extends DrivingRouteOverlay {
//
//        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
//            super(baiduMap);
//        }
//
//        @Override
//        public BitmapDescriptor getStartMarker() {
//            return BitmapDescriptorFactory.fromResource(R.drawable.icon_start);
//        }
//
//        @Override
//        public BitmapDescriptor getTerminalMarker() {
//            return BitmapDescriptorFactory.fromResource(R.drawable.icon_end);
//        }
//    }


    // 注册广播  用于监听地图初始化问题
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

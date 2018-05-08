package com.sht.smartlock.ui.activity.chatlocation;


import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.sht.smartlock.Config;
import com.sht.smartlock.Manifest;

import java.io.IOException;
import java.util.List;

/**
 * @author kaiser
 * @version 1.0
 * @date 2015-10-01
 * @desc 定位服务
 */
public class LocationSvc extends Service implements LocationListener {

    private static final String TAG = "LocationSvc";
    private LocationManager locationManager;
    public static final int time = 600;
    public static final int distance = 500;
    private static final double EARTH_RADIUS = 6378137;
    LocationUtil instance;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        instance = LocationUtil.getInstance(this);
        getLocation();
    }

    private void getLocation() {
        // 获取位置管理服务
//        LocationManager locationManager;
//        String serviceName = Context.LOCATION_SERVICE;
//        locationManager = (LocationManager)this.getSystemService(serviceName);
        // 查找到服务信息
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
        String provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息
        Location location = locationManager.getLastKnownLocation(provider); // 通过GPS获取位置
        // 设置监听*器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N米(500米)
        locationManager.requestLocationUpdates(provider, time * 1000, distance,
                this);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//   >23版本
            //已经存在权限
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                try {
                    getBaiduLocation();
                } catch (Exception e) {
                    Log.e("TAgg", "-------555----->1e=" + e.toString());
                    try {
                        getBaiduLocation();
                    }catch (Exception ee){
                        Log.e("TAgg", "-------444----->2ee=" + e.toString());
                    }
                }
            }else {
                Log.e("TAgg", "-------没有定位权限   动态定位权限----->e=");
            }
        } else if (locationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null) {//23版本一下
                locationManager
                        .requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                                this);
        } else if (locationManager.getProvider(LocationManager.GPS_PROVIDER) != null) {
            Log.e("TAAAH", "------------>GPS_PROVIDER");
            locationManager
                    .requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                            this);
        } else {
            Toast.makeText(this, "无法定位", Toast.LENGTH_SHORT).show();
        }
    }

    private void getBaiduLocation(){
        instance.startMonitor();
        BDLocation location = instance.getLocation();
        Intent intentloc = new Intent();
        intentloc.setAction(Common.LOCATION_ACTION);
        intentloc.putExtra(Common.LOCATION, location.toString());
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        String city = location.getCity();
        Bundle bundle = new Bundle();
        bundle.putDouble(Config.LATITUDE, latitude);
        bundle.putDouble(Config.LONGITUDE, longitude);
        bundle.putString(Config.ADDRESS, city);
        intentloc.putExtras(bundle);
        sendBroadcast(intentloc);

        // 如果只是需要定位一次，这里就移除监听，停掉服务。如果要进行实时定位，可以在退出应用或者其他时刻停掉定位服务。
        Log.e("TAgg", "-------555----->" + location.getLatitude() + "：" + location.getLongitude());
        locationManager.removeUpdates(this);
        stopSelf();
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Get the current position \n" + location);
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //把GPS坐标转换成百度地图坐标
//		LatLng mylatlng = gpsToBaiduMap(latLng);//
        //通知Activity
        Intent intent = new Intent();
        intent.setAction(Common.LOCATION_ACTION);
        intent.putExtra(Common.LOCATION, location.toString());
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        String city = updateWithNewLocation(location);
        Bundle bundle = new Bundle();
        bundle.putDouble(Config.LATITUDE, latitude);
        bundle.putDouble(Config.LONGITUDE, longitude);
        bundle.putString(Config.ADDRESS, city);
        intent.putExtras(bundle);
        sendBroadcast(intent);

        // 如果只是需要定位一次，这里就移除监听，停掉服务。如果要进行实时定位，可以在退出应用或者其他时刻停掉定位服务。
        locationManager.removeUpdates(this);
        stopSelf();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }


    /**
     * 标准的GPS经纬度坐标直接在地图上绘制会有偏移，这是测绘局和地图商设置的加密，要转换成百度地图坐标
     *
     * @return 百度地图坐标
     */

    private LatLng gpsToBaiduMap(LatLng sourceLatLng) {
        // 将GPS设备采集的原始GPS坐标转换成百度坐标
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }

    /**
     * 更新location
     *
     * @param location
     * @return cityName
     */
    private static Geocoder geocoder;   //此对象能通过经纬度来获取相应的城市等信息

    private String updateWithNewLocation(Location location) {
        geocoder = new Geocoder(getApplicationContext());
        String mcityName = "";
        double lat = 0;
        double lng = 0;
        List<Address> addList = null;
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
        } else {
            Log.e("TAG", "-----------》" + "无法获取地理位置");
            System.out.println("无法获取地理信息");
        }

        try {

            addList = geocoder.getFromLocation(lat, lng, 1);    //解析经纬度
            Log.e("TAG", "-----------》addList.size()" + addList.size());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (addList != null && addList.size() > 0) {
            for (int i = 0; i < addList.size(); i++) {
                android.location.Address add = addList.get(i);
                mcityName += add.getLocality();
                Log.e("TAG", "-----------》add.getLocality()=" + add.getLocality());
            }
        }
        if (mcityName.length() != 0) {

            return mcityName.substring(0, (mcityName.length() - 1));
        } else {
            return mcityName;
        }
    }


}

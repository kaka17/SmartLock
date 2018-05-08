package com.sht.smartlock.ui.entity;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by Administrator on 2016/5/27.
 */
public class MyLocation implements LocationListener {
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    private void getLocation() {
        // 获取位置管理服务
//        LocationManager locationManager;
//        String serviceName = Context.LOCATION_SERVICE;
//        locationManager = (LocationManager)this.getSystemService(serviceName);
        // 查找到服务信息
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
//        criteria.setAltitudeRequired(false);
//        criteria.setBearingRequired(false);
//        criteria.setCostAllowed(true);
//        criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
//        String provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息
//        Location location = locationManager.getLastKnownLocation(provider); // 通过GPS获取位置
//        updateToNewLocation(location);
//        // 设置监听*器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N米(500米)
//        locationManager.requestLocationUpdates(provider, time * 1000, distance,
//                this);
    }

}

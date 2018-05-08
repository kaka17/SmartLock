package com.sht.smartlock.ui.activity.chatlocation;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.base.BaseApplication;

/**
 * Created by kaiser on 2016/1/28.
 */
public class LocationByPower extends Service implements LocationListener {

    private static final String TAG = "LocationSvc";
    private LocationManager locationManager;
    public static final int time = 600;
    public static final int distance = 500;
    private static final double EARTH_RADIUS = 6378137;

    public LocationClient mLocationClient;//定位SDK的核心类
    public MyLocationListener mMyLocationListener;//定义监听类
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

    @Override
    public void onStart(Intent intent, int startId) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//   >23版本
            //已经存在权限
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                try {
                    instance.startMonitor();
                    BDLocation location = instance.getLocation();
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
//        if (MainTabIndexFragment.list.size()>0){
                    Log.e("TAG", "---->latitude==" + latitude + "longitude==" + longitude + "accuracy==");

                    if (AppContext.getProperty(Config.CHINCKIN_LAD) == null) {
                        return;
                    }
                    double lad2 = Double.parseDouble(AppContext.getProperty(Config.CHINCKIN_LAD)) / 1000000;
                    double lng2 = Double.parseDouble(AppContext.getProperty(Config.CHINCKIN_LON)) / 1000000;
                    double m = DistanceOfTwoPoints(latitude, longitude, lad2, lng2);
                    if (m > 500) {
                        if (AppContext.getProperty(Config.CLOSEPOWER) != null) {//退房后关闭服务
                            stopSelf();
                            // 如果只是需要定位一次，这里就移除监听，停掉服务。如果要进行实时定位，可以在退出应用或者其他时刻停掉定位服务。
                            locationManager.removeUpdates(this);
                        } else {
                            //通知Activity
                            Intent intentLock = new Intent();
                            intentLock.setAction(Common.LOCATION_ACTION_BYPOWER);
                            intentLock.putExtra(Common.LOCATION, location.toString());
                            Bundle bundle = new Bundle();
                            bundle.putDouble(Config.LATITUDE, latitude);
                            bundle.putDouble(Config.LONGITUDE, longitude);
                            bundle.putString(Config.DISTANCE, String.valueOf(m));
                            intentLock.putExtras(bundle);
                            sendBroadcast(intentLock);
                            // 如果只是需要定位一次，这里就移除监听，停掉服务。如果要进行实时定位，可以在退出应用或者其他时刻停掉定位服务。
//                locationManager.removeUpdates(this);
                        }
                    } else {
                        if (AppContext.getProperty(Config.CLOSEPOWER) != null) {
                            stopSelf();
                            // 如果只是需要定位一次，这里就移除监听，停掉服务。如果要进行实时定位，可以在退出应用或者其他时刻停掉定位服务。
                            locationManager.removeUpdates(this);
                        } else {
                            Intent intentLock = new Intent();
                            intentLock.setAction(Common.LOCATION_ACTION_BYPOWER);
                            intentLock.putExtra(Common.LOCATION, location.toString());
                            Bundle bundle = new Bundle();
                            bundle.putDouble(Config.LATITUDE, latitude);
                            bundle.putDouble(Config.LONGITUDE, longitude);
                            bundle.putString(Config.DISTANCE, String.valueOf(m));
                            intentLock.putExtras(bundle);
                            sendBroadcast(intentLock);
                        }
                    }

                    // 如果只是需要定位一次，这里就移除监听，停掉服务。如果要进行实时定位，可以在退出应用或者其他时刻停掉定位服务。
                    Log.e("TAgg", "-------555----->" + location.getLatitude() + "：" + location.getLongitude());
//                    locationManager.removeUpdates(this);
//                    stopSelf();
                } catch (Exception e) {
                    Log.e("TAgg", "-------555----->e=" + e.toString());
                }
            } else {
                Log.e("TAgg", "-------没有定位权限   动态定位权限----->e=");
            }
        } else if (locationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null)
            locationManager
                    .requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                            this);
        else if (locationManager.getProvider(LocationManager.GPS_PROVIDER) != null) locationManager
                .requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                        this);
        else Toast.makeText(this, "无法定位", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Get the current position \n" + location);
        float accuracy = location.getAccuracy();//定位的经度 。
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
//        if (MainTabIndexFragment.list.size()>0){
        Log.e("TAG", "---->latitude==" + latitude + "longitude==" + longitude + "accuracy==" + accuracy);

        if (AppContext.getProperty(Config.CHINCKIN_LAD) == null) {
            return;
        }
        double lad2 = Double.parseDouble(AppContext.getProperty(Config.CHINCKIN_LAD)) / 1000000;
        double lng2 = Double.parseDouble(AppContext.getProperty(Config.CHINCKIN_LON)) / 1000000;
        double m = DistanceOfTwoPoints(latitude, longitude, lad2, lng2);
        if (m > 500) {
            if (AppContext.getProperty(Config.CLOSEPOWER) != null) {//退房后关闭服务
                stopSelf();
                // 如果只是需要定位一次，这里就移除监听，停掉服务。如果要进行实时定位，可以在退出应用或者其他时刻停掉定位服务。
                if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
                locationManager.removeUpdates(this);
            } else {
                //通知Activity
                Intent intent = new Intent();
                intent.setAction(Common.LOCATION_ACTION_BYPOWER);
                intent.putExtra(Common.LOCATION, location.toString());
                Bundle bundle = new Bundle();
                bundle.putDouble(Config.LATITUDE, latitude);
                bundle.putDouble(Config.LONGITUDE, longitude);
                bundle.putString(Config.DISTANCE, String.valueOf(m));
                intent.putExtras(bundle);
                sendBroadcast(intent);
                // 如果只是需要定位一次，这里就移除监听，停掉服务。如果要进行实时定位，可以在退出应用或者其他时刻停掉定位服务。
//                locationManager.removeUpdates(this);
            }
        } else {
            if (AppContext.getProperty(Config.CLOSEPOWER) != null) {
                stopSelf();
                // 如果只是需要定位一次，这里就移除监听，停掉服务。如果要进行实时定位，可以在退出应用或者其他时刻停掉定位服务。
                locationManager.removeUpdates(this);
            } else {
                Intent intent = new Intent();
                intent.setAction(Common.LOCATION_ACTION_BYPOWER);
                intent.putExtra(Common.LOCATION, location.toString());
                Bundle bundle = new Bundle();
                bundle.putDouble(Config.LATITUDE, latitude);
                bundle.putDouble(Config.LONGITUDE, longitude);
                bundle.putString(Config.DISTANCE, String.valueOf(m));
                intent.putExtras(bundle);
                sendBroadcast(intent);
            }
        }

//        InitLocation();

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
        updateToNewLocation(location);
        // 设置监听*器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N米(500米)
        locationManager.requestLocationUpdates(provider, time * 1000, distance,
                this);
    }

    private void updateToNewLocation(Location location) {
        TextView tv1;
//        tv1 = (TextView)this.findViewById(R.id.tv1);
//        if (location != null) {
//            double latitude = location.getLatitude();
//            double longitude=location.getLongitude();
//            tv1.setText( 维度： + latitude+ \n经度 +longitude);
//        } else {
//            tv1.setText( 无法获取地理信息 );
//        }
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 根据两点间经纬度坐标（double值），计算两点间距离，
     *
     * @param lat1 纬度
     * @param lng1 经度
     * @param lat2
     * @param lng2
     * @return 距离：单位为米
     */
    public static double DistanceOfTwoPoints(double lat1, double lng1,
                                             double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    //百度地图点位

    //百度地图定位

    private void InitLocation(){
        mLocationClient = new LocationClient(getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        //
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置高精度定位定位模式
        option.setCoorType("bd09ll");//设置百度经纬度坐标系格式
        option.setScanSpan(1000000);//设置发起定位请求的间隔时间为1000ms
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

////            setNotification();
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
            double  lad2=22.614927;
            double lng2=114.032671;
//            double lad2 = Double.parseDouble(AppContext.getProperty(Config.CHINCKIN_LAD)) / 1000000;
//            double lng2 = Double.parseDouble(AppContext.getProperty(Config.CHINCKIN_LON)) / 1000000;
            double m = DistanceOfTwoPoints(location.getLatitude(), location.getLongitude(), lad2, lng2);
            AppContext.toast("第" + n + "次"+m+"米");
            if (m > 5) {
                if (AppContext.getProperty(Config.CLOSEPOWER) != null) {//退房后关闭服务
                    stopSelf();
                    // 如果只是需要定位一次，这里就移除监听，停掉服务。如果要进行实时定位，可以在退出应用或者其他时刻停掉定位服务。
                    locationManager.removeUpdates(LocationByPower.this);
                } else {
                    //通知Activity
                    Intent intent = new Intent();
                    intent.setAction(Common.LOCATION_ACTION_BYPOWER);
                    intent.putExtra(Common.LOCATION, location.toString());
                    Bundle bundle = new Bundle();
                    bundle.putDouble(Config.LATITUDE, location.getLatitude());
                    bundle.putDouble(Config.LONGITUDE, location.getLongitude());
                    bundle.putString(Config.DISTANCE, String.valueOf(m));
                    intent.putExtras(bundle);
                    sendBroadcast(intent);
                    AppContext.toast(">5m第" + n + "次" + m + "米");
                    // 如果只是需要定位一次，这里就移除监听，停掉服务。如果要进行实时定位，可以在退出应用或者其他时刻停掉定位服务。
//                locationManager.removeUpdates(this);
                }
            } else {
                if (AppContext.getProperty(Config.CLOSEPOWER) != null) {
                    stopSelf();
                    // 如果只是需要定位一次，这里就移除监听，停掉服务。如果要进行实时定位，可以在退出应用或者其他时刻停掉定位服务。
                    locationManager.removeUpdates(LocationByPower.this);
                } else {
                    AppContext.toast("<5m第" + n + "次" + m + "米");
                    Intent intent = new Intent();
                    intent.setAction(Common.LOCATION_ACTION_BYPOWER);
                    intent.putExtra(Common.LOCATION, location.toString());
                    Bundle bundle = new Bundle();
                    bundle.putDouble(Config.LATITUDE, location.getLatitude());
                    bundle.putDouble(Config.LONGITUDE, location.getLongitude());
                    bundle.putString(Config.DISTANCE, String.valueOf(m));
                    intent.putExtras(bundle);
                    sendBroadcast(intent);
                }
            }
            mLocationClient.stop();
        }
    }


}

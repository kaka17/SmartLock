package com.sht.smartlock.ui.activity.chatlocation;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.sht.smartlock.Config;

import java.util.List;

/**
 * @author kaiser
 * @date 2015-10-01
 * @version 1.0
 * @desc 定位服务
 * 
 */
public class LocationSvcBy60 extends Service implements LocationListener {

	private static final String TAG = "LocationSvc";
	private LocationManager locationManager;
	private String provider;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	}

	@Override
	public void onStart(Intent intent, int startId) {
//		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//		// 获取所有可用的位置提供器
//		List<String> providerList = locationManager.getProviders(true);
//		if (providerList.contains(LocationManager.GPS_PROVIDER)) {
//			provider = LocationManager.GPS_PROVIDER;
//		} else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
//			provider = LocationManager.NETWORK_PROVIDER;
//		} else {
//			// 当没有可用的位置提供器时，弹出Toast提示用户
//			Toast.makeText(this, "No location provider to use", Toast.LENGTH_SHORT).show();
//			Log.e("TAG","------------->No location provider to use");
//			return;
//		}
//		Location location = locationManager.getLastKnownLocation(provider);
//		Log.e("TAG","------------->No location provider to use");
//		if (location != null) {
//			// 显示当前设备的位置信息
////			showLocation(location);
//		}
//		locationManager.requestLocationUpdates(provider, 5000, 1, this);



		if (locationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null) locationManager
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
		Log.e(TAG, "Get the current position \n" + location);
		LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
		//把GPS坐标转换成百度地图坐标
		LatLng mylatlng = gpsToBaiduMap(latLng);//
		//通知Activity
		Intent intent = new Intent();
		intent.setAction(Common.LOCATION_ACTION);
		intent.putExtra(Common.LOCATION, location.toString());
		double latitude=location.getLatitude();
		double longitude= location.getLongitude();
		Bundle bundle=new Bundle();
		bundle.putDouble(Config.LATITUDE,mylatlng.latitude);
		bundle.putDouble(Config.LONGITUDE,mylatlng.longitude);
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

	private LatLng gpsToBaiduMap( LatLng sourceLatLng){
		// 将GPS设备采集的原始GPS坐标转换成百度坐标
		CoordinateConverter converter = new CoordinateConverter();
		converter.from(CoordinateConverter.CoordType.GPS);
		// sourceLatLng待转换坐标
		converter.coord(sourceLatLng);
		LatLng desLatLng = converter.convert();
		return desLatLng;
	}

}

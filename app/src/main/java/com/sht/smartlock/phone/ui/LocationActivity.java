package com.sht.smartlock.phone.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.sht.smartlock.R;
import com.sht.smartlock.phone.ui.chatting.ChattingActivity;

/**
 * 
 * @author luhuashan 定位界面
 *
 */
public class LocationActivity extends ECSuperActivity implements
		OnGetGeoCoderResultListener, OnClickListener {

	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	BitmapDescriptor mCurrentMarker;

	MapView mMapView;
	BaiduMap mBaiduMap;

	boolean isFirstLoc = true;
	private View viewCache;
	private TextView tvResult;
	GeoCoder mSearch = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,
				R.string.app_panel_location, this);
		
		
		isFirstLoc=true;
		
		
		viewCache = LayoutInflater.from(this)
				.inflate(R.layout.pop_layout, null);
		tvResult = (TextView) viewCache.findViewById(R.id.location_tips);
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);

		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMyLocationEnabled(true);
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll"); 
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
		      
            @Override  
            public boolean onMapPoiClick(MapPoi arg0) {
                // TODO Auto-generated method stub  
                return false;  
            }  
  
            @Override  
            public void onMapClick(LatLng latLng) {
                double latitude = latLng.latitude;  
                double longitude = latLng.longitude;  
                mBaiduMap.clear();  
                LatLng point = new LatLng(latitude, longitude);
                
                lat=latitude;
                lon=longitude;
                MarkerOptions options = new MarkerOptions().position(point)
                        .icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon_marka));
                mBaiduMap.addOverlay(options);  
                
                
                ReverseGeoCodeOption op = new ReverseGeoCodeOption();
                op.location(latLng);  
                mSearch.reverseGeoCode(op);  
                 
            }  
        });  

	}

	public class MyLocationListenner implements BDLocationListener {

		private InfoWindow infoWindow;

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null || mMapView == null) {
				return;
			}
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			LatLng ll = null;
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				ll = new LatLng(location.getLatitude(), location.getLongitude());
//				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
//				mBaiduMap.animateMapStatus(u);
			}

			if (ll != null) {
				lat=ll.latitude;
				lon=ll.longitude;
				mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ll));

			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
		isTop=false;
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
		isTop=true;
	}
	private boolean isTop=false;

	@Override
	protected void onDestroy() {
		mLocClient.stop();
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {

	}
	private double lat;
	private double lon;
	private String address;

	
	//传入a经纬度进行位置反编码
	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		// TODO Auto-generated method stub

		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			return;
		}
		
		if(mBaiduMap==null){
			return;
		}
		if(isTop){
		  getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt,
                R.drawable.btn_style_green, null,
                getString(R.string.dialog_ok_button),
                getString(R.string.app_panel_location), null, this);
		}
		tvResult.setText(result.getAddress());
		address=result.getAddress();

		InfoWindow infoWindow = new InfoWindow(viewCache, result.getLocation(),
				-90);
		mBaiduMap.showInfoWindow(infoWindow);
		
		
//		MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(16);
//		mBaiduMap.animateMapStatus(u);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(result.getLocation());
		
		
		mBaiduMap.animateMapStatus(u);
		
		mBaiduMap.addOverlay(new MarkerOptions()
				.position(result.getLocation())
				.title(result.getAddress())
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_marka)));
		
		
		
		

	}

	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_location;
	}

	@Override
	public void onClick(View v) {
		
		 switch (v.getId()) {
         case R.id.btn_left:
             hideSoftKeyboard();
             finish();
             break;
         
         case R.id.text_right:
         	
        	 Intent intent=new Intent();
        	 LocationInfo locationInfo =new LocationInfo();
        	 locationInfo.setLat(lat);
        	 locationInfo.setLon(lon);
        	 locationInfo.setAddress(address);
        	 intent.putExtra("location", locationInfo);
        	 setResult(ChattingActivity.RESULT_OK, intent);
        	 
        	 finish();
        	 
         	
         	break;
         default:
             break;
     }
		
	}

}

package com.sht.smartlock.phone.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.sht.smartlock.R;

/**
 * 
 * @author luhuashan显示收到或者发送的位置地图
 *
 */
public class ShowBaiDuMapActivity extends ECSuperActivity implements
		OnClickListener {
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private View viewCache;
	private TextView tvResult;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,
				R.string.app_panel_location, this);

		viewCache = LayoutInflater.from(this)
				.inflate(R.layout.pop_layout, null);
		tvResult = (TextView) viewCache.findViewById(R.id.location_tips);

		Intent intent = getIntent();
		mMapView = (MapView) findViewById(R.id.baidu_map);
		LocationInfo b = (LocationInfo) intent.getSerializableExtra("location");
		
		String address=b.getAddress();
		
		if(TextUtils.isEmpty(address)){
			finish();
		}
		LatLng p = new LatLng(b.getLat(), b.getLon());


		mBaiduMap = mMapView.getMap();
		
		
		tvResult.setText(b.getAddress());
		InfoWindow infoWindow = new InfoWindow(viewCache, p, -90);
		
		
		MapStatus mMapStatus = new MapStatus.Builder()
        .target(p)  
        .zoom(17)  
        .build();  
  
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态  
        mBaiduMap.setMapStatus(mMapStatusUpdate); 
        
		
		mBaiduMap.animateMapStatus(mMapStatusUpdate);
		mBaiduMap.showInfoWindow(infoWindow);
		mBaiduMap.addOverlay(new MarkerOptions()
				.position(p)
				.title(b.getAddress())
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_marka)));
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}

	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.baidu_map;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_left:
			hideSoftKeyboard();
			finish();
			break;

		default:
			break;
		}

	}

}

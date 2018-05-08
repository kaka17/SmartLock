package com.sht.smartlock.ui.activity.booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.model.booking.Hotel;
import com.sht.smartlock.ui.activity.base.BaseActivity;

import java.util.List;

public class SearchMapActivity extends BaseActivity implements View.OnClickListener {
    ImageView tvGoBack;
    TextView tvList;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private List<Hotel> mListHotel;
    private BitmapDescriptor mIconMaker;
    String mRoomMode=Config.ROOM_MODE_DAY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvGoBack = (ImageView) findViewById(R.id.goBack);
        tvList = (TextView) findViewById(R.id.tvShowList);
        tvGoBack.setOnClickListener(this);
        tvList.setOnClickListener(this);
        mListHotel= (List<Hotel>) getIntent().getSerializableExtra(Config.KEY_HOTEL_LIST);
        mRoomMode=getIntent().getStringExtra(Config.KEY_SHOW_ROOM_MODE);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        mIconMaker = BitmapDescriptorFactory.fromResource(R.drawable.maker);
        mBaiduMap.setMapStatus(msu);//设置中心点
        initOverlay();
        //对Marker的点击
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                //获得marker中的数据
               // Info info = (Info) marker.getExtraInfo().get("info");
                Hotel hotel= (Hotel) marker.getExtraInfo().get("hotel");
                Intent i=new Intent(SearchMapActivity.this,ShowHotelDetail02Activity.class);
                i.putExtra(Config.KEY_HOTEL_ID, hotel.getID());
                i.putExtra(Config.KEY_HOTEL_URL, hotel.getIntroduction());
                i.putExtra(Config.KEY_HOTEL_CAPTION, hotel.getCaption());
                i.putExtra(Config.KEY_SHOW_ROOM_MODE,mRoomMode);
                startActivity(i);


//                InfoWindow mInfoWindow;
//                //生成一个TextView用户在地图中显示InfoWindow
//                TextView location = new TextView(getApplicationContext());
//                location.setBackgroundResource(R.drawable.location_tips);
//                location.setPadding(30, 20, 30, 50);
//                location.setText(hotel.getCaption());
//                //将marker所在的经纬度的信息转化成屏幕上的坐标
//                final LatLng ll = marker.getPosition();
//                Point p = mBaiduMap.getProjection().toScreenLocation(ll);
//                Log.e("point", "--!" + p.x + " , " + p.y);
//                p.y -= 47;
//                LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
//                //为弹出的InfoWindow添加点击事件
//
//                mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(location), llInfo,-47,
//                        new OnInfoWindowClickListener() {
//
//                            @Override
//                            public void onInfoWindowClick() {
//                                //隐藏InfoWindow
//                                mBaiduMap.hideInfoWindow();
//                            }
//                        });
//                //显示InfoWindow
//                mBaiduMap.showInfoWindow(mInfoWindow);
                //设置详细信息布局为可见
              //  mMarkerInfoLy.setVisibility(View.VISIBLE);
                //根据商家信息为详细信息布局设置信息
             //   popupInfo(mMarkerInfoLy, info);
                return true;
            }
        });
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {

            @Override
            public boolean onMapPoiClick(MapPoi arg0) {
                return false;
            }

            @Override
            public void onMapClick(LatLng arg0) {
                mBaiduMap.hideInfoWindow();

            }
        });
    }
    private void initOverlay() {
        LatLng latLng=null;
        OverlayOptions overlayOptions=null;
        Marker marker=null;
        for(Hotel hotel:mListHotel){
          //  View view=new View(getApplicationContext());
            //view= LayoutInflater.from(mContext).inflate()
            TextView location = new TextView(getApplicationContext());
            location.setBackgroundResource(R.drawable.location_tips);
            location.setPadding(30, 20, 30, 50);
            location.setText(hotel.getCaption()+"\n   "+getString(R.string.currency_sign)+hotel.getPrice());
            latLng=new LatLng(Double.parseDouble(hotel.getLatitude())/1000000,Double.parseDouble(hotel.getLongitude())/1000000);
            //将gps坐标转换成百度坐标
            LatLng latLng1 = gpsToBaiduMap(latLng);
            latLng=latLng1;
            overlayOptions=new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromView(location)).zIndex(5);
            marker= (Marker) mBaiduMap.addOverlay(overlayOptions);
            Bundle bundle=new Bundle();
            bundle.putSerializable("hotel",hotel);
            marker.setExtraInfo(bundle);
        }
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(u);
    }

    @Override
    protected int getLayoutId() {
        SDKInitializer.initialize(getApplicationContext());//初始化百度地图

        return R.layout.activity_search_map;
    }

    protected boolean hasToolBar() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mMapView = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goBack:
                finish();
                break;
            case R.id.tvShowList:
              //  startActivity(new Intent(SearchMapActivity.this, SearchListActivity.class));
                finish();
                break;
        }
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

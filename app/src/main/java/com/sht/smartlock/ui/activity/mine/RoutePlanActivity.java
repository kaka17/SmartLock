package com.sht.smartlock.ui.activity.mine;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
//import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
//import com.baidu.mapapi.overlayutil.OverlayManager;
//import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
//import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.sht.smartlock.R;

public class RoutePlanActivity extends AppCompatActivity {
    private RoutePlanSearch routePlanSearch = null;
    private PlanNode startNode;
    private PlanNode endNode;
    private EditText editText_routeplan_start;
    private EditText editText_routeplan_end;
    private MapView mapView_routeplan = null; // 地图View
    private BaiduMap mBaidumap = null;
    private RouteLine route = null;
//    private OverlayManager routeOverlay = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_route_plan);

        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_route_plan);
        CharSequence titleLable = "路线规划功能";
        setTitle(titleLable);

        editText_routeplan_start = (EditText) findViewById(R.id.editText_routeplan_start);
        editText_routeplan_end = (EditText) findViewById(R.id.editText_routeplan_end);

        mapView_routeplan = (MapView) findViewById(R.id.mapView_routeplan);
        mBaidumap = mapView_routeplan.getMap();

        routePlanSearch = RoutePlanSearch.newInstance();

//        routePlanSearch
//                .setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
//
//                    @Override
//                    public void onGetWalkingRouteResult(
//                            WalkingRouteResult result) {
//                        if (result == null
//                                || result.error != SearchResult.ERRORNO.NO_ERROR) {
//                            Toast.makeText(RoutePlanActivity.this, "抱歉，未找到结果",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
//                            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
//                            // result.getSuggestAddrInfo()
//                            return;
//                        }
//                        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
//                            route = result.getRouteLines().get(0);
//                            WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(
//                                    mBaidumap);
//                            mBaidumap.setOnMarkerClickListener(overlay);
//                            // routeOverlay = overlay;
//                            overlay.setData(result.getRouteLines().get(0));
//                            overlay.addToMap();
//                            overlay.zoomToSpan();
//                        }
//                    }
//
//                    @Override
//                    public void onGetTransitRouteResult(
//                            TransitRouteResult result) {
//                        if (result == null
//                                || result.error != SearchResult.ERRORNO.NO_ERROR) {
//                            Toast.makeText(RoutePlanActivity.this, "抱歉，未找到结果",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
//                            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
//                            // result.getSuggestAddrInfo()
//                            return;
//                        }
//                        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
//                            TransitRouteOverlay overlay = new MyTransitRouteOverlay(
//                                    mBaidumap);
//                            mBaidumap.setOnMarkerClickListener(overlay);
//                            overlay.setData(result.getRouteLines().get(0));
//                            overlay.addToMap();
//                            overlay.zoomToSpan();
//                        }
//                    }
//
//                    @Override
//                    public void onGetDrivingRouteResult(
//                            DrivingRouteResult result) {
//                        if (result == null
//                                || result.error != SearchResult.ERRORNO.NO_ERROR) {
//                            Toast.makeText(RoutePlanActivity.this, "抱歉，未找到结果",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
//                            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
//                            // result.getSuggestAddrInfo()
//                            return;
//                        }
//                        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
//                            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(
//                                    mBaidumap);
//                            mBaidumap.setOnMarkerClickListener(overlay);
//                            overlay.setData(result.getRouteLines().get(0));
//                            overlay.addToMap();
//                            overlay.zoomToSpan();
//                        }
//                    }
//                });

    }




    public void clickButton(View view) {
//         重置浏览节点的路线数据
//        route = null;
//        mBaidumap.clear();
//        startNode = PlanNode.withCityNameAndPlaceName("北京",
//                editText_routeplan_start.getText().toString());
//        endNode = PlanNode.withCityNameAndPlaceName("北京",
//                editText_routeplan_end.getText().toString());

        switch (view.getId()) {
            case R.id.button_route_drive:
//                routePlanSearch.drivingSearch((new DrivingRoutePlanOption()).from(
//                        startNode).to(endNode));
                Uri uri =Uri.parse("http://map.baidu.com/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.button_route_transit:
                routePlanSearch.transitSearch((new TransitRoutePlanOption())
                        .from(startNode).city("北京").to(endNode));
                break;
            case R.id.button_route_walk:
                routePlanSearch.walkingSearch((new WalkingRoutePlanOption()).from(
                        startNode).to(endNode));
                break;
        }
//        finish();
    }

    @Override
    protected void onPause() {
        mapView_routeplan.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView_routeplan.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        routePlanSearch.destroy();
        mapView_routeplan.onDestroy();
        super.onDestroy();
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
//
//        public BitmapDescriptor getStartMarker() {
//            return BitmapDescriptorFactory.fromResource(R.drawable.icon_start);
//        }
//
//
//        public BitmapDescriptor getTerminalMarker() {
//            return BitmapDescriptorFactory.fromResource(R.drawable.icon_end);
//        }
//    }


}

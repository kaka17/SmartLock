package com.sht.smartlock.ui.activity.hourroom;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.helper.PermissionHelper;
import com.sht.smartlock.model.booking.Hotel;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.booking.SearchMapActivity;
import com.sht.smartlock.ui.activity.booking.ShowHotelDetail02Activity;
import com.sht.smartlock.ui.activity.booking.ShowHotelDetailActivity;
import com.sht.smartlock.ui.activity.chatlocation.Common;
import com.sht.smartlock.ui.activity.chatlocation.LocationSvc;
import com.sht.smartlock.ui.adapter.NearHotelAdapter;
import com.sht.smartlock.ui.adapter.booking.HotelAdapter;
import com.sht.smartlock.ui.entity.NearHotelEntity;
import com.sht.smartlock.util.DateUtil;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.widget.calendar2.CalendarViewSingle;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SearchHourListActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvRankAsc, tvRankDesc, tvDistance;
    ImageView tvShowMap;
    TextView tvStayTime, tvStartTime;
    ImageView tvGoback;
    private LinearLayout mLyDistance;
    private ImageView ivHLine, ivLLine, ivDistance;
    private PullToRefreshListView lvHotel;
    private List<Hotel> mListHotel;
    private String mCity, mDestination, mStrCheckInDate, mStartTime;
    private int mPageId;
//    HotelAdapter mHotelAdapter;
    private NearHotelAdapter adapter;
    boolean nearby = false;
    int orderindex = -1;
    String ranktype, rankorder;
    String longitude, latitude;
    SearchHourRoomPopWindow popWindow;
    boolean isGetLocation = false;
    //权限检测类
    private PermissionHelper mPermissionHelper;
    //返回值
    private static final int WRITE_RESULT_CODE = 12;
    //权限名称
    private static final String WRITE_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String WRITE_PERMISSION02 = Manifest.permission.ACCESS_FINE_LOCATION;
    private List<String> listPermission = new ArrayList<>();
    private ImageView iv01, iv02, iv03;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPermissionHelper = new PermissionHelper(this);
        listPermission.add(WRITE_PERMISSION);
        listPermission.add(WRITE_PERMISSION02);
        InitView();
    }

    private void InitView() {
        mCity = AppContext.getProperty(getString(R.string.booking_cityname));
        mDestination = AppContext.getProperty(getString(R.string.booking_destination));
        if (mDestination == null) {
            mDestination = "";
        }
        mStrCheckInDate = AppContext.getProperty(Config.KEY_HOUR_CHECKIN);
        mStartTime = AppContext.getProperty(Config.KEY_HOUR_STARTTIME);
        tvRankAsc = (TextView) findViewById(R.id.rankAsc);
        tvRankDesc = (TextView) findViewById(R.id.rankDesc);
        tvDistance = (TextView) findViewById(R.id.shortdistance);
        tvGoback = (ImageView) findViewById(R.id.goBack);
        tvShowMap = (ImageView) findViewById(R.id.ivShowMap);
        lvHotel = (PullToRefreshListView) findViewById(R.id.lvHotelList);
        ivHLine = (ImageView) findViewById(R.id.high_tab_line);
        ivLLine = (ImageView) findViewById(R.id.low_tab_line);
        ivDistance = (ImageView) findViewById(R.id.distance_tab_line);
        mLyDistance = (LinearLayout) findViewById(R.id.lyDistance);
        tvStayTime = (TextView) findViewById(R.id.tvStayTime);
        tvStartTime = (TextView) findViewById(R.id.tvStartTime);

        iv01 = (ImageView) findViewById(R.id.iv01);
        iv02 = (ImageView) findViewById(R.id.iv02);
        iv03 = (ImageView) findViewById(R.id.iv03);


        if (AppContext.getProperty(Config.LONGITUDE) != null) {
            longitude = AppContext.getProperty(Config.LONGITUDE);
            latitude = AppContext.getProperty(Config.LATITUDE);
        }
        Intent i = getIntent();
        if (i.getStringExtra(Config.HOTEL_TYPE).equals("nearby")) {
            nearby = true;
            orderindex = 0;
            ranktype = Config.RANKTYPE_DISTANCE;
            rankorder = Config.RANKORDER_ASC;
        } else {
            mLyDistance.setVisibility(View.GONE);
            iv01.setVisibility(View.GONE);
            orderindex = 1;
            ranktype = Config.RANKTYPE_PRICE;
            rankorder = Config.RANKORDER_ASC;
        }
        mListHotel = new ArrayList<>();
        adapter = new NearHotelAdapter(mContext, mListHotel, nearby);
        lvHotel.setAdapter(adapter);
        lvHotel.setMode(PullToRefreshBase.Mode.BOTH);
        lvHotel.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPageId = 1;
                if (nearby == false) {
//                    HttpClient.instance().hourSearch(mStrCheckInDate,
//                            mStartTime,
//                            ranktype,
//                            rankorder,
//                            mPageId,
//                            mDestination,
//                            mCity, new GetHotelCallBack()
//                    );
                            newSearchHotel(mCity,mStrCheckInDate,mStartTime,mDestination,Config.PRICEORDISTANCE,mPageId);
                } else {
                    if (isGetLocation) {
//                        HttpClient.instance().nearHour(longitude, latitude, mStrCheckInDate, mStartTime, ranktype, rankorder, mPageId, new GetHotelCallBack());
                        newNearHotel(longitude, latitude, mStrCheckInDate, mStartTime, Config.PRICEORDISTANCE, mPageId);
                    } else {
                        openGPSSettings();
                    }
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPageId++;
                if (nearby == false) {
//                    HttpClient.instance().hourSearch(mStrCheckInDate,
//                            mStartTime,
//                            ranktype,
//                            rankorder,
//                            mPageId,
//                            mDestination,
//                            mCity, new GetHotelCallBack()
//                    );
                    newSearchHotel(mCity,mStrCheckInDate,mStartTime,mDestination,Config.PRICEORDISTANCE,mPageId);
                } else {
                    if (isGetLocation) {
//                        HttpClient.instance().nearHour(longitude, latitude, mStrCheckInDate, mStartTime, ranktype, rankorder, mPageId, new GetHotelCallBack());

//                        AppContext.toast("sor="+Config.PRICEORDISTANCE+"  mPageId="+mPageId);
                        newNearHotel(longitude, latitude, mStrCheckInDate, mStartTime, Config.PRICEORDISTANCE, mPageId);
                    } else {
                        openGPSSettings();
                    }
                }
            }
        });
        lvHotel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Hotel hotel = (Hotel) adapter.getItem(position - 1);
                Intent i = new Intent(mContext, ShowHotelDetail02Activity.class);
                i.putExtra(Config.KEY_HOTEL_ID, hotel.getID());
                i.putExtra(Config.KEY_HOTEL_URL, hotel.getIntroduction());
                i.putExtra(Config.KEY_HOTEL_CAPTION, hotel.getCaption());
                i.putExtra(Config.KEY_SHOW_ROOM_MODE, Config.ROOM_MODE_HOUR);
                i.putExtra(Config.KEY_HOTEL_IS_COLLECT, hotel.getIs_collection());
                i.putExtra(Config.LOCKPIC,hotel.getPicture());
                i.putExtra(Config.ISNEAR,nearby);
                startActivity(i);
            }
        });
        tvDistance.setOnClickListener(this);
        tvRankAsc.setOnClickListener(this);
        tvRankDesc.setOnClickListener(this);
        tvGoback.setOnClickListener(this);
        tvShowMap.setOnClickListener(this);
        tvStartTime.setOnClickListener(this);
        tvStayTime.setOnClickListener(this);
        initTabLine();
    }

    private void initTabLine() {
        ivHLine.setVisibility(View.GONE);
        ivLLine.setVisibility(View.GONE);
        ivDistance.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCalendarDate();
        setStartTime();
        switch (orderindex) {
            case 0:
                ivDistance.setVisibility(View.GONE);
                iv01.setVisibility(View.VISIBLE);
                iv02.setVisibility(View.INVISIBLE);
                iv03.setVisibility(View.INVISIBLE);
                break;
            case 1:
                ivLLine.setVisibility(View.GONE);
                if (nearby) {
                    iv01.setVisibility(View.INVISIBLE);
                }
                iv02.setVisibility(View.VISIBLE);
                iv03.setVisibility(View.INVISIBLE);
                break;
            case 2:
                ivHLine.setVisibility(View.GONE);
                if (nearby) {
                    iv01.setVisibility(View.INVISIBLE);
                }
                iv02.setVisibility(View.INVISIBLE);
                iv03.setVisibility(View.VISIBLE);
                break;
        }
        mPageId = 1;
        adapter.clear();
        isGetLocation = false;
        ProgressDialog.show(mContext, R.string.on_loading);  //首次加载提示
        if (nearby == false) {
//            HttpClient.instance().hourSearch(mStrCheckInDate,
//                    mStartTime,
//                    ranktype,
//                    rankorder,
//                    mPageId,
//                    mDestination,
//                    mCity, new GetHotelCallBack()
//            );
            Config.PRICEORDISTANCE="2";
            newSearchHotel(mCity, mStrCheckInDate, mStartTime, mDestination, Config.PRICEORDISTANCE, mPageId);
        } else {
            openGPSSettings();
        }
    }

    public void setStartTime() {
        if (AppContext.getProperty(Config.KEY_HOUR_STARTTIME) != null) {
            tvStartTime.setText(AppContext.getProperty(Config.KEY_HOUR_STARTTIME));
        }
    }

    public void setCalendarDate() {
        String strCheckInDate = AppContext.getProperty(Config.KEY_HOUR_CHECKIN);

        if (strCheckInDate != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.YEAR_MONTH_DAY);
            Date checkInDate;

            try {
                checkInDate = simpleDateFormat.parse(strCheckInDate);
                if (checkInDate.after(Calendar.getInstance().getTime())) {   //当前时间是否大于缓存时间
                    Calendar c = Calendar.getInstance();
                    c.setTime(checkInDate);
                    String showStart = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日" + " " + DateUtil.getWeekByDay(strCheckInDate);
                    tvStayTime.setText(showStart);

                } else {
                    checkInDate = new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(checkInDate);
                    String showStart = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日" + " " + DateUtil.getWeekByDay(checkInDate);
                    strCheckInDate = DateUtil.getDateString(c);
                    AppContext.setProperty(Config.KEY_HOUR_CHECKIN, strCheckInDate);
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    tvStayTime.setText(showStart);

                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            Date checkInDate = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(checkInDate);
            String showStart = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日" + " " + DateUtil.getWeekByDay(checkInDate);
            strCheckInDate = DateUtil.getDateString(c);
            AppContext.setProperty(Config.KEY_HOUR_CHECKIN, strCheckInDate);
            tvStayTime.setText(showStart);
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_hour_list;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }

    @Override
    public void onClick(View v) {
        Intent i;
        initTabLine();
        switch (v.getId()) {
            case R.id.goBack:
                finish();
                break;
            case R.id.rankAsc://价格最低
                mPageId = 1;
                adapter.clear();
                Config.PRICEORDISTANCE="2";
                ivLLine.setVisibility(View.VISIBLE);
                if (nearby) {
                    iv01.setVisibility(View.INVISIBLE);
                }
                iv02.setVisibility(View.VISIBLE);
                iv03.setVisibility(View.INVISIBLE);

                orderindex = 1;
                ranktype = Config.RANKTYPE_PRICE;
                rankorder = Config.RANKORDER_ASC;
                if (nearby == false) {
//                    HttpClient.instance().hourSearch(mStrCheckInDate,
//                            mStartTime,
//                            ranktype,
//                            rankorder,
//                            mPageId,
//                            mDestination,
//                            mCity, new GetHotelCallBack()
//                    );
                    newSearchHotel(mCity, mStrCheckInDate, mStartTime, mDestination, Config.PRICEORDISTANCE, mPageId);
                } else {
                    if (isGetLocation) {
//                        HttpClient.instance().nearHour(longitude, latitude, mStrCheckInDate, mStartTime, ranktype, rankorder, mPageId, new GetHotelCallBack());
                        //价格降序
                        newNearHotel(longitude, latitude,mStrCheckInDate,mStartTime,Config.PRICEORDISTANCE,1);
                    } else {
                        openGPSSettings();
                    }
                }

                break;
            case R.id.rankDesc://价格最高
                mPageId = 1;
                adapter.clear();
                orderindex = 2;
                Config.PRICEORDISTANCE="1";
                rankorder = Config.RANKORDER_DESC;
                ranktype = Config.RANKTYPE_PRICE;
                ivHLine.setVisibility(View.VISIBLE);

                if (nearby) {
                    iv01.setVisibility(View.INVISIBLE);
                }
                iv02.setVisibility(View.INVISIBLE);
                iv03.setVisibility(View.VISIBLE);

                if (nearby == false) {
//                    HttpClient.instance().hourSearch(mStrCheckInDate,
//                            mStartTime,
//                            ranktype,
//                            rankorder,
//                            mPageId,
//                            mDestination,
//                            mCity, new GetHotelCallBack()
//                    );
                    newSearchHotel(mCity, mStrCheckInDate, mStartTime, mDestination, Config.PRICEORDISTANCE, mPageId);
                } else {
                    if (isGetLocation) {
//                        HttpClient.instance().nearHour(longitude, latitude, mStrCheckInDate, mStartTime, ranktype, rankorder, mPageId, new GetHotelCallBack());
                        newNearHotel(longitude, latitude, mStrCheckInDate, mStartTime, Config.PRICEORDISTANCE, 1);
                    } else {
                        openGPSSettings();
                    }
                }
                break;
            case R.id.shortdistance://距离最近
                mPageId = 1;
                adapter.clear();
                orderindex = 0;
                Config.PRICEORDISTANCE="3";
                ranktype = Config.RANKTYPE_DISTANCE;
                rankorder = Config.RANKORDER_ASC;
                ivDistance.setVisibility(View.VISIBLE);

                iv01.setVisibility(View.VISIBLE);
                iv02.setVisibility(View.INVISIBLE);
                iv03.setVisibility(View.INVISIBLE);

                if (isGetLocation) {
//                    HttpClient.instance().nearHour(longitude, latitude, mStrCheckInDate, mStartTime, ranktype, rankorder, mPageId, new GetHotelCallBack());
                    newNearHotel(longitude, latitude,mStrCheckInDate,mStartTime,Config.PRICEORDISTANCE,1);
                } else {
                    openGPSSettings();
                }
                break;
            case R.id.tvStayTime:
                i = new Intent(mContext, CalendarViewSingle.class);
                startActivity(i);
                break;
            case R.id.tvStartTime:
                popWindow = new SearchHourRoomPopWindow(mContext);
                popWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        setStartTime();
                        switch (orderindex) {
                            case 0:
                                ivDistance.setVisibility(View.VISIBLE);
                                break;
                            case 1:
                                ivLLine.setVisibility(View.VISIBLE);
                                break;
                            case 2:
                                ivHLine.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                });
                break;
            case R.id.ivShowMap:
                i = new Intent(mContext, SearchMapActivity.class);
                i.putExtra(Config.KEY_HOTEL_LIST, (Serializable) adapter.getmHotelList());
                i.putExtra(Config.KEY_SHOW_ROOM_MODE, Config.ROOM_MODE_HOUR);
                startActivity(i);
                break;
        }
    }

    public class GetHotelCallBack extends HttpCallBack {

        @Override
        public void onSuccess(ResponseBean responseBean) {
            LogUtil.log("paramss re" + responseBean.toString());
            if (mPageId == 1) {
                adapter.clear();
            }
            try {
                List<Hotel> tempList = responseBean.getListDataWithGson(Hotel.class);
                if (tempList.size() == 0) {
                    toastFail(getString(R.string.no_result_left));
                }
                adapter.addAll(tempList);
            } catch (Exception e) {
                toastFail(getString(R.string.no_result_left));
            }
        }

        @Override
        public void onStart() {
            super.onStart();
            //  ProgressDialog.show(mContext, R.string.on_loading);
        }

        @Override
        public void onFailure(String error, String message) {
            super.onFailure(error, message);
            LogUtil.log("paramss re" + error);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            ProgressDialog.disMiss();
            LogUtil.log("paramss finish");
            lvHotel.onRefreshComplete();
        }
    }

    private void openGPSSettings() {
        LocationManager alm = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        if (alm
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            // "GPS模块正常 "

            boolean b = mPermissionHelper.checkPermissions(WRITE_PERMISSION, WRITE_PERMISSION02);
            if (b) {
                getLocation();
            } else {
                mPermissionHelper.permissionsCheck(listPermission, WRITE_RESULT_CODE);
            }
            return;
        }
        ProgressDialog.disMiss();
        lvHotel.onRefreshComplete();
        Toast.makeText(mContext, "请开启GPS后再试！", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
//        startActivityForResult(intent, 0); //此为设置完成后返回到获取界面
    }


    private void getLocation() {
        // 注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Common.LOCATION_ACTION);
        registerReceiver(new LocationBroadcastReceiver(), filter);

        // 启动服务
        Intent intent = new Intent();
        intent.setClass(this, LocationSvc.class);
        startService(intent);

        // 等待提示
//        dialog = new ProgressDialog(getActivity());
//        dialog.setMessage("正在定位...");
//        dialog.setCancelable(true);
//        dialog.show();
    }

    private class LocationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(Common.LOCATION_ACTION)) return;
            String locationInfo = intent.getStringExtra(Common.LOCATION);
            Bundle bundle = intent.getExtras();
            double latitude = bundle.getDouble(Config.LATITUDE);//纬度
            double longitude = bundle.getDouble(Config.LONGITUDE);//经度
            DecimalFormat dr = new DecimalFormat("#.000000");
            AppContext.setProperty(Config.LONGITUDE, dr.format(longitude));
            AppContext.setProperty(Config.LATITUDE, dr.format(latitude));
//            latitude=22.612130;
//            longitude=114.022295;
            isGetLocation = true;
            //  BaseApplication.toast("定位成功！");
            // initData(latitude,longitude);
            Config.PRICEORDISTANCE="3";//距离最近
            newNearHotel(dr.format(longitude), dr.format(latitude), mStrCheckInDate, mStartTime, Config.PRICEORDISTANCE, 1);
//            HttpClient.instance().nearHour(dr.format(longitude), dr.format(latitude), mStrCheckInDate, mStartTime, ranktype, rankorder, mPageId, new GetHotelCallBack());

            //   dialog.dismiss();
            //     com.sht.smartlock.widget.dialog.ProgressDialog.disMiss();
            // BaseApplication.toastSuccess(context.getString(R.string.get_location_success));
            unregisterReceiver(this);// 不需要时注销
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case WRITE_RESULT_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //如果请求成功，则进行相应的操作，比如我们这里打开一个新的Activity
//                    Intent intent = new Intent(MyLockGroupsActivity.this,PermissionActivity.class);
//                    startActivity(intent);
//                    AppContext.toast("ok");
                    openGPSSettings();

                } else {
                    //如果请求失败
                    AppContext.toast("请手动打开定位权限");
                    mPermissionHelper.startAppSettings();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                openGPSSettings();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void newNearHotel(String longitude, String latitude, String day, String start_time, String sort, int pageid) {
        if(start_time.equals(getString(R.string.current_time))){
            //当前时间
            Calendar c = Calendar.getInstance();
            start_time=c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE);
        }

        if (longitude.equals("null")||latitude.equals("null")){
            //定位
            openGPSSettings();
        }else {
            HttpClient.instance().bookNearHotel(longitude, latitude, day, start_time, sort, pageid, new HttpCallBack() {
                @Override
                public void onFailure(String error, String message) {
                    super.onFailure(error, message);
                    lvHotel.onRefreshComplete();
                    ProgressDialog.disMiss();
                }

                @Override
                public void onSuccess(ResponseBean responseBean) {
                    lvHotel.onRefreshComplete();
                    Log.e("Near", "--------->" + responseBean.toString());
                    ProgressDialog.disMiss();
//                List<Hotel> listData = responseBean.getListData(Hotel.class);
                    List<Hotel> listData = responseBean.getListDataWithGson(Hotel.class);
                    if (mPageId == 1) {
                        adapter.clear();
                    }
                    try {
                        if (listData.size() == 0) {
                            toastFail(getString(R.string.no_result_left));
                        }
                        adapter.addAll(listData);
                    } catch (Exception e) {
                        toastFail(getString(R.string.no_result_left));
                    }
                }
            });
        }
    }

    private void newSearchHotel(String city, String day,String start_time,String destination,  String sort, int pageid){
        if(start_time.equals(getString(R.string.current_time))){
            start_time="now";
        }
        HttpClient.instance().bookHourHotelListSearsh(city, day, start_time, destination, sort, pageid, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                lvHotel.onRefreshComplete();
                ProgressDialog.disMiss();
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                lvHotel.onRefreshComplete();
                Log.e("Near", "--------->" + responseBean.toString());
                ProgressDialog.disMiss();
//
                List<Hotel> listData = responseBean.getListDataWithGson(Hotel.class);
                if (mPageId == 1) {
                    adapter.clear();
                }
                try {
                    if (listData.size() == 0) {
                        toastFail(getString(R.string.no_result_left));
                    }
                    adapter.addAll(listData);
                } catch (Exception e) {
                    toastFail(getString(R.string.no_result_left));
                }
            }
        });
    }

}

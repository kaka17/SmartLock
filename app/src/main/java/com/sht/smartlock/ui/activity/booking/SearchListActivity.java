package com.sht.smartlock.ui.activity.booking;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.sht.smartlock.ui.activity.chatlocation.Common;
import com.sht.smartlock.ui.activity.chatlocation.LocationSvc;
import com.sht.smartlock.ui.adapter.booking.HotelAdapter;
import com.sht.smartlock.util.DateUtil;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.widget.calendar2.CalendarView;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SearchListActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvRankAsc, tvRankDesc, tvDistance;
    ImageView tvGoback, ivShowMap;
    private ImageView ivHLine, ivLLine, ivDistance;
    private TextView mCheckInDate, mCheckOutDate, mTotalDay;
    TextView tvCheckInWeek, tvCheckOutWeek;

    private LinearLayout mDateTimePicker, mLyDistance;
    private PullToRefreshListView lvHotel;
    private List<Hotel> mListHotel;
    private String mCity, mDestination, mPriceRangeStart, mPriceRangeEnd, mStrCheckInDate, mStrCheckOutDate;
    private int mPageId;//请求服务器分页
    String[] price = new String[]{"0", "150", "300", "500", "1000000"};
    HotelAdapter mHotelAdapter;
    boolean nearby = false; //靠此参数区分是附近酒店还是搜索酒店
    int orderindex = -1;
    String ranktype, rankorder;  //排序的类型 以及排序
    String longitude, latitude;
    boolean isGetLocation = false;
    //返回值
    private static final int  WRITE_RESULT_CODE = 12;
    //权限名称
    //权限检测类
    private PermissionHelper mPermissionHelper;
    private static final String  WRITE_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String  WRITE_PERMISSION02 = Manifest.permission.ACCESS_FINE_LOCATION;
    private List<String> listPermission=new ArrayList<>();
    private ImageView iv01,iv02,iv03;
    private TextView tvIn,tvOut;
    private RelativeLayout reLayTime;

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
        // mDestination = "深圳";
        if (AppContext.getProperty(getString(R.string.price_range_start)) != null) {
            mPriceRangeStart = AppContext.getProperty(getString(R.string.price_range_start));
            mPriceRangeEnd = AppContext.getProperty(getString(R.string.price_range_end));
            mPriceRangeStart = price[Integer.parseInt(mPriceRangeStart)];
            mPriceRangeEnd = price[Integer.parseInt(mPriceRangeEnd)];
        } else {
            mPriceRangeStart = price[0];
            mPriceRangeEnd = price[price.length - 1];
        }
        mStrCheckInDate = AppContext.getProperty(Config.KEY_CHECKIN_DATE);
        mStrCheckOutDate = AppContext.getProperty(Config.KEY_CHECKOUT_DATE);
        mCheckInDate = (TextView) findViewById(R.id.checkInDate);
        mCheckOutDate = (TextView) findViewById(R.id.checkOutDate);
        mTotalDay = (TextView) findViewById(R.id.totalDay);
        mDateTimePicker = (LinearLayout) findViewById(R.id.ly_datetimepicker);
        mLyDistance = (LinearLayout) findViewById(R.id.lyDistance);
        tvCheckInWeek = (TextView) findViewById(R.id.checkInWeek);
        tvCheckOutWeek = (TextView) findViewById(R.id.checkOutWeek);
        tvRankAsc = (TextView) findViewById(R.id.rankAsc);
        tvRankDesc = (TextView) findViewById(R.id.rankDesc);
        tvDistance = (TextView) findViewById(R.id.shortdistance);
        tvGoback = (ImageView) findViewById(R.id.goBack);
        ivShowMap = (ImageView) findViewById(R.id.ivShowMap);
        lvHotel = (PullToRefreshListView) findViewById(R.id.lvHotelList);
        //
        ivHLine = (ImageView) findViewById(R.id.high_tab_line);
        ivLLine = (ImageView) findViewById(R.id.low_tab_line);
        ivDistance = (ImageView) findViewById(R.id.distance_tab_line);

        //
        reLayTime = (RelativeLayout) findViewById(R.id.reLayTime);
        tvIn = (TextView) findViewById(R.id.tvIn);
        tvOut = (TextView) findViewById(R.id.tvOut);
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
            iv01.setVisibility(View.VISIBLE);
        } else {
            mLyDistance.setVisibility(View.GONE);
            iv01.setVisibility(View.GONE);
            orderindex = 1;
            ranktype = Config.RANKTYPE_PRICE;
            rankorder = Config.RANKORDER_ASC;
        }
        mListHotel = new ArrayList<>();
        mHotelAdapter = new HotelAdapter(mContext, mListHotel, nearby);
        lvHotel.setAdapter(mHotelAdapter);
        lvHotel.setMode(PullToRefreshBase.Mode.BOTH);
        lvHotel.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPageId = 1;
                if (nearby == false) {
//                    HttpClient.instance().hotelSearch(mCity,
//                            mDestination,
//                            mPriceRangeStart,
//                            mPriceRangeEnd,
//                            mStrCheckInDate,
//                            mStrCheckOutDate,
//                            mPageId,
//                            ranktype,
//                            rankorder,
//                            new GetHotelCallBack());
                    initSearshHotel(mCity,mDestination, mPriceRangeStart, mPriceRangeEnd,mStrCheckInDate,mStrCheckOutDate,Config.SORTFORNEARHOTEL,mPageId);
                } else {
                    if (!isGetLocation) {
                        openGPSSettings();
                    } else {
                        if (AppContext.getProperty(Config.LONGITUDE) != null && !AppContext.getProperty(Config.LONGITUDE).equals("null")) {
                            longitude = AppContext.getProperty(Config.LONGITUDE);
                            latitude = AppContext.getProperty(Config.LATITUDE);
//                            HttpClient.instance().nearHotel(longitude, latitude, mStrCheckInDate, mStrCheckOutDate, ranktype, rankorder, mPageId, new GetHotelCallBack());
                            initData(longitude, latitude, mStrCheckInDate, mStrCheckOutDate, Config.SORTFORNEARHOTEL, mPageId);

                        } else {
                            openGPSSettings();
                        }
                    }
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPageId++;
                if (nearby == false) {
//                    HttpClient.instance().hotelSearch(mCity,
//                            mDestination,
//                            mPriceRangeStart,
//                            mPriceRangeEnd,
//                            mStrCheckInDate,
//                            mStrCheckOutDate,
//                            mPageId,
//                            ranktype,
//                            rankorder,
//                            new GetHotelCallBack());
                    initSearshHotel(mCity, mDestination, mPriceRangeStart, mPriceRangeEnd, mStrCheckInDate, mStrCheckOutDate, Config.SORTFORNEARHOTEL, mPageId);
                } else {
                    if (!isGetLocation) {
                        openGPSSettings();
                    } else {
                        if (AppContext.getProperty(Config.LONGITUDE) != null && !AppContext.getProperty(Config.LONGITUDE).equals("null")) {
                            longitude = AppContext.getProperty(Config.LONGITUDE);
                            latitude = AppContext.getProperty(Config.LATITUDE);
//                            HttpClient.instance().nearHotel(longitude, latitude, mStrCheckInDate, mStrCheckOutDate, ranktype, rankorder, mPageId, new GetHotelCallBack());
                            initData(longitude, latitude, mStrCheckInDate, mStrCheckOutDate, Config.SORTFORNEARHOTEL, mPageId);

                        } else {
                            openGPSSettings();
                        }
                    }
                }
            }
        });
        lvHotel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Hotel hotel = (Hotel) mHotelAdapter.getItem(position - 1);
                Intent i = new Intent(mContext, ShowHotelDetail02Activity.class);
                i.putExtra(Config.KEY_HOTEL_ID, hotel.getID());
                i.putExtra(Config.KEY_HOTEL_URL, hotel.getIntroduction());
                i.putExtra(Config.KEY_HOTEL_CAPTION, hotel.getCaption());
                i.putExtra(Config.KEY_SHOW_ROOM_MODE, Config.ROOM_MODE_DAY);
                i.putExtra(Config.KEY_HOTEL_IS_COLLECT, hotel.getIs_collection());
                i.putExtra(Config.LOCKPIC,hotel.getPicture());
                i.putExtra(Config.ISNEAR,nearby);
//                AppContext.setProperty(Config.CACHE_HOTELID, hotel.getID());
//                AppContext.setProperty(Config.CACHE_HOTELCAPTION,hotel.getCaption());
//                AppContext.setProperty(Config.CACHE_HOTELURL, hotel.getIntroduction());
                startActivity(i);
            }
        });
        tvDistance.setOnClickListener(this);
        tvRankAsc.setOnClickListener(this);
        tvRankDesc.setOnClickListener(this);
        tvGoback.setOnClickListener(this);
        mDateTimePicker.setOnClickListener(this);
        ivShowMap.setOnClickListener(this);
        reLayTime.setOnClickListener(this);
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
        switch (orderindex) {
            case 0:
                ivDistance.setVisibility(View.GONE);
                iv01.setVisibility(View.VISIBLE);
                iv02.setVisibility(View.INVISIBLE);
                iv03.setVisibility(View.INVISIBLE);
                break;
            case 1:
                iv02.setVisibility(View.VISIBLE);
                iv03.setVisibility(View.INVISIBLE);
                ivLLine.setVisibility(View.GONE);
                break;
            case 2:
                ivHLine.setVisibility(View.GONE);
                iv02.setVisibility(View.INVISIBLE);
                iv03.setVisibility(View.VISIBLE);
                break;
        }
        mPageId = 1;
        mHotelAdapter.clear();
        isGetLocation = false;
//        ProgressDialog.show(mContext, R.string.on_loading);  //首次加载提示
        if (nearby == false) {
//            HttpClient.instance().hotelSearch(mCity,
//                    mDestination,
//                    mPriceRangeStart,
//                    mPriceRangeEnd,
//                    mStrCheckInDate,
//                    mStrCheckOutDate,
//                    mPageId,
//                    ranktype,
//                    rankorder,
//                    new GetHotelCallBack());
            Config.SORTFORNEARHOTEL="2";
            initSearshHotel(mCity, mDestination, mPriceRangeStart, mPriceRangeEnd, mStrCheckInDate, mStrCheckOutDate, Config.SORTFORNEARHOTEL, mPageId);
        } else {
            openGPSSettings();
            //   HttpClient.instance().nearHotel(longitude, latitude, mStrCheckInDate, mStrCheckOutDate, ranktype, rankorder, mPageId, new GetHotelCallBack());

        }
    }

    public void setCalendarDate() {
        mStrCheckInDate = AppContext.getProperty(Config.KEY_CHECKIN_DATE);
        mStrCheckOutDate = AppContext.getProperty(Config.KEY_CHECKOUT_DATE);
        if (mStrCheckInDate != null && mStrCheckOutDate != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.YEAR_MONTH_DAY);
            Date checkInDate;
            Date checkOutDate;
            try {
                checkInDate = simpleDateFormat.parse(mStrCheckInDate);
                checkOutDate = simpleDateFormat.parse(mStrCheckOutDate);
                if (checkInDate.after(Calendar.getInstance().getTime()) || mStrCheckInDate.equals(DateUtil.getDateStr(Calendar.getInstance().getTimeInMillis(), DateUtil.YEAR_MONTH_DAY))) {   //当前时间是否大于缓存时间
                    Calendar c = Calendar.getInstance();
                    c.setTime(checkInDate);
                    String showStart = (c.get(Calendar.MONTH) + 1) + "/" + (c.get(Calendar.DAY_OF_MONTH)) + "";
                    c.setTime(checkOutDate);
                    String showEnd = (c.get(Calendar.MONTH) + 1) + "/" + (c.get(Calendar.DAY_OF_MONTH)) + "";
                    mCheckOutDate.setText(showEnd);
                    mCheckInDate.setText(showStart);
                    tvIn.setText("入："+showStart);
                    tvOut.setText("离："+showEnd);
                    tvCheckInWeek.setText(DateUtil.getWeekByDay(mStrCheckInDate));
                    tvCheckOutWeek.setText(DateUtil.getWeekByDay(mStrCheckOutDate));
                    mTotalDay.setText(getString(R.string.total) + DateUtil.getDayBetweenDate(checkInDate, checkOutDate) + getString(R.string.night));
                } else {
                    checkInDate = new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(checkInDate);
                    String showStart = (c.get(Calendar.MONTH) + 1) + "/" + (c.get(Calendar.DAY_OF_MONTH)) + "";
                    mStrCheckInDate = DateUtil.getDateString(c);
                    AppContext.setProperty(Config.KEY_CHECKIN_DATE, mStrCheckInDate);
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    checkOutDate = c.getTime();
                    mStrCheckOutDate = DateUtil.getDateString(c);
                    AppContext.setProperty(Config.KEY_CHECKOUT_DATE, mStrCheckOutDate);
                    String showEnd = (c.get(Calendar.MONTH) + 1) + "/" + (c.get(Calendar.DAY_OF_MONTH)) + "";
                    mCheckOutDate.setText(showEnd);
                    mCheckInDate.setText(showStart);
                    tvIn.setText("入："+showStart);
                    tvOut.setText("离："+showEnd);
                    tvCheckInWeek.setText(DateUtil.getWeekByDay(mStrCheckInDate));
                    tvCheckOutWeek.setText(DateUtil.getWeekByDay(mStrCheckOutDate));
                    mTotalDay.setText(getString(R.string.total) + DateUtil.getDayBetweenDate(checkInDate, checkOutDate) + getString(R.string.night));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
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
                mHotelAdapter.clear();
                orderindex = 1;
                Config.SORTFORNEARHOTEL="2";
                ivLLine.setVisibility(View.GONE);
                if (nearby){
                    iv01.setVisibility(View.INVISIBLE);
                }
                iv02.setVisibility(View.VISIBLE);
                iv03.setVisibility(View.INVISIBLE);
                ivLLine.setVisibility(View.GONE);

                ranktype = Config.RANKTYPE_PRICE;
                rankorder = Config.RANKORDER_ASC;
                if (nearby == false) {
//                    HttpClient.instance().hotelSearch(mCity,
//                            mDestination,
//                            mPriceRangeStart,
//                            mPriceRangeEnd,
//                            mStrCheckInDate,
//                            mStrCheckOutDate,
//                            mPageId,
//                            ranktype,
//                            rankorder,
//                            new GetHotelCallBack());

                    initSearshHotel(mCity, mDestination, mPriceRangeStart, mPriceRangeEnd, mStrCheckInDate, mStrCheckOutDate, Config.SORTFORNEARHOTEL, mPageId);
                } else {
                    if (!isGetLocation) {
                        openGPSSettings();
                    } else {
//                        HttpClient.instance().nearHotel(longitude, latitude, mStrCheckInDate, mStrCheckOutDate, ranktype, rankorder, mPageId, new GetHotelCallBack());
                        initData(longitude, latitude, mStrCheckInDate, mStrCheckOutDate, Config.SORTFORNEARHOTEL, mPageId);

                    }
                }

                break;
            case R.id.rankDesc://价格最高
                mPageId = 1;
                mHotelAdapter.clear();
                orderindex = 2;
                Config.SORTFORNEARHOTEL="1";

                ivDistance.setVisibility(View.GONE);
                ivHLine.setVisibility(View.GONE);
                if (nearby){
                    iv01.setVisibility(View.INVISIBLE);
                }
                iv02.setVisibility(View.INVISIBLE);
                iv03.setVisibility(View.VISIBLE);

                if (nearby == false) {
//                    HttpClient.instance().hotelSearch(mCity,
//                            mDestination,
//                            mPriceRangeStart,
//                            mPriceRangeEnd,
//                            mStrCheckInDate,
//                            mStrCheckOutDate,
//                            mPageId,
//                            ranktype,
//                            rankorder,
//                            new GetHotelCallBack());
                    initSearshHotel(mCity, mDestination, mPriceRangeStart, mPriceRangeEnd, mStrCheckInDate, mStrCheckOutDate, Config.SORTFORNEARHOTEL, mPageId);
                } else {
                    if (!isGetLocation) {
                        openGPSSettings();
                    } else {
//                        HttpClient.instance().nearHotel(longitude, latitude, mStrCheckInDate, mStrCheckOutDate, ranktype, rankorder, mPageId, new GetHotelCallBack());
                        initData(longitude, latitude, mStrCheckInDate, mStrCheckOutDate, Config.SORTFORNEARHOTEL, mPageId);
                    }
                }
                break;
            case R.id.shortdistance://距离最近
                mPageId = 1;
                mHotelAdapter.clear();
                orderindex = 0;
                ranktype = Config.RANKTYPE_DISTANCE;
                rankorder = Config.RANKORDER_ASC;
                Config.SORTFORNEARHOTEL="3";
                iv01.setVisibility(View.VISIBLE);
                iv02.setVisibility(View.INVISIBLE);
                iv03.setVisibility(View.INVISIBLE);

                ivDistance.setVisibility(View.GONE);
                if (!isGetLocation) {
                    openGPSSettings();
                } else {
//                    HttpClient.instance().nearHotel(longitude, latitude, mStrCheckInDate, mStrCheckOutDate, ranktype, rankorder, mPageId, new GetHotelCallBack());
                    initData(longitude, latitude, mStrCheckInDate, mStrCheckOutDate, Config.SORTFORNEARHOTEL, 1);
                }
                break;
            case R.id.ly_datetimepicker://旧的 废弃
                mPageId = 1;
                i = new Intent(mContext, CalendarView.class);
                startActivity(i);
                break;
            case R.id.reLayTime://新的
                mPageId = 1;
                i = new Intent(mContext, CalendarView.class);
                startActivity(i);
                break;
            case R.id.ivShowMap:
                if (mHotelAdapter.getmHotelList().size() != 0) {
                    i = new Intent(mContext, SearchMapActivity.class);
                    i.putExtra(Config.KEY_HOTEL_LIST, (Serializable) mHotelAdapter.getmHotelList());
                    i.putExtra(Config.KEY_SHOW_ROOM_MODE, Config.ROOM_MODE_DAY);
                    startActivity(i);
                }
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_list;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }

    public class GetHotelCallBack extends HttpCallBack {

        @Override
        public void onSuccess(ResponseBean responseBean) {
            //   LogUtil.log("paramss re" + responseBean.toString());
            lvHotel.onRefreshComplete();
            if (mPageId == 1) {
                mHotelAdapter.clear();
            }
            try {
                List<Hotel> tempList = responseBean.getListDataWithGson(Hotel.class);
                mHotelAdapter.addAll(tempList);
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
            lvHotel.onRefreshComplete();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            ProgressDialog.disMiss();
            // LogUtil.log("paramss finish");

        }
    }

    private void openGPSSettings() {
        LocationManager alm = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        if (alm
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            // "GPS模块正常 "

            boolean b = mPermissionHelper.checkPermissions(WRITE_PERMISSION, WRITE_PERMISSION02);
            if (b){
                getLocation();
            }else {
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
        intent.setClass(SearchListActivity.this, LocationSvc.class);
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
            DecimalFormat dr = new DecimalFormat("#0.000000");
            AppContext.setProperty(Config.LONGITUDE, dr.format(longitude));
            AppContext.setProperty(Config.LATITUDE, dr.format(latitude));
            isGetLocation = true;
//            HttpClient.instance().nearHotel(dr.format(longitude), dr.format(latitude), mStrCheckInDate, mStrCheckOutDate, ranktype, rankorder, mPageId, new GetHotelCallBack());
            Config.SORTFORNEARHOTEL="3";
            initData(dr.format(longitude), dr.format(latitude),mStrCheckInDate, mStrCheckOutDate,Config.SORTFORNEARHOTEL,1);
//            latitude=22.612130;
//            longitude=114.022295;
            //   isGetLocation = true;
            //  BaseApplication.toast("定位成功！");
            // initData(latitude,longitude);

            //   dialog.dismiss();
            //  com.sht.smartlock.widget.dialog.ProgressDialog.disMiss();
            // BaseApplication.toastSuccess(context.getString(R.string.get_location_success));
            unregisterReceiver(this);// 不需要时注销
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
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
        switch (requestCode){
            case 0:
                openGPSSettings();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initData(String longitude, String latitude,  String start_day,String end_day, String sort, int pageid){

        if (longitude.equals("null")||latitude.equals("null")){
            //定位
            openGPSSettings();
        }else {
            HttpClient.instance().bookNearHotelList(longitude, latitude, start_day, end_day, sort, pageid, new HttpCallBack() {

                @Override
                public void onStart() {
                    super.onStart();
                    ProgressDialog.show(SearchListActivity.this, "正在加载数据...");
                }

                @Override
                public void onFailure(String error, String message) {
                    super.onFailure(error, message);
                    ProgressDialog.disMiss();
                    lvHotel.onRefreshComplete();
                }

                @Override
                public void onSuccess(ResponseBean responseBean) {
                    lvHotel.onRefreshComplete();
                    ProgressDialog.disMiss();
                    List<Hotel> listData = responseBean.getListDataWithGson(Hotel.class);
                    if (mPageId == 1) {
                        mHotelAdapter.clear();
                    }
                    if (listData != null && listData.size() > 0) {
                        mHotelAdapter.addAll(listData);
                        mHotelAdapter.notifyDataSetChanged();
                    } else {
                        AppContext.toast("未找到更多酒店");
                    }
                }
            });
        }
    }

    private void initSearshHotel(String city, String destination,String min_price,String max_price, String start_day,String end_day, String sort, int pageid){
        HttpClient.instance().bookNearHotelListSearsh(city, destination, min_price, max_price, start_day, end_day, sort, pageid, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(SearchListActivity.this,"正在加载数据...");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                lvHotel.onRefreshComplete();
                ProgressDialog.disMiss();
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                lvHotel.onRefreshComplete();
                List<Hotel> listData = responseBean.getListDataWithGson(Hotel.class);
                if (mPageId == 1) {
                    mHotelAdapter.clear();
                }
                if (listData != null && listData.size() > 0) {
                    mHotelAdapter.addAll(listData);
                    mHotelAdapter.notifyDataSetChanged();
                } else {
                    AppContext.toast("未找到更多酒店");
                }
            }
        });
    }

}

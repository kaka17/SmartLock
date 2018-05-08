package com.sht.smartlock.ui.activity.booking;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.helper.PermissionHelper;
import com.sht.smartlock.model.booking.HotelRoom;
import com.sht.smartlock.ui.activity.LoginByNameActivity;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.chatlocation.Common;
import com.sht.smartlock.ui.activity.chatlocation.LocationSvc;
import com.sht.smartlock.ui.activity.hourroom.BookingHourRoomActivity;
import com.sht.smartlock.ui.activity.hourroom.SearchHourRoomPopWindow;
import com.sht.smartlock.ui.activity.myinterface.MyItemClickListener;
import com.sht.smartlock.ui.activity.myview.CircleNavigationBar;
import com.sht.smartlock.ui.activity.myview.RoundImageView;
import com.sht.smartlock.ui.adapter.LockServicerAdapter;
import com.sht.smartlock.ui.adapter.booking.MyPagerAdapter;
import com.sht.smartlock.ui.adapter.booking.RoomsAdapter;
import com.sht.smartlock.ui.entity.HotelRoomEntity;
import com.sht.smartlock.ui.entity.LockDetaileInfo;
import com.sht.smartlock.util.DateUtil;
import com.sht.smartlock.widget.calendar2.CalendarView;
import com.sht.smartlock.widget.calendar2.CalendarViewSingle;
import com.sht.smartlock.widget.dialog.ProgressDialog;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ShowHotelDetail02Activity extends BaseActivity implements View.OnClickListener, MyItemClickListener {

    private ViewPager vpImg;
    private CircleNavigationBar InNum;
    private TextView tvInTime, tvLeverTime, tvTime;
    private List<View> list = new ArrayList<View>();
    private ListView lvRoom;
    private List<HotelRoom> listRooms = new ArrayList<>();
    private RoomsAdapter adapter;
    String strHotelId, strHotelURL, strHotelCaption;
    String mRoomMode = Config.ROOM_MODE_ALL;//房间显示模式，选房间的时候用，默认是全模式显示
    private ImageView ivServicer01, ivServicer02, ivServicer03, ivServicer04, ivPhone;
    private LinearLayout linServicerInfo, linAddress;
    private TextView tvAddress;
    private MyPagerAdapter pagerAdapter;
    private TextView tvLockName;
    ImageView tvCollect, tvShare, tvGoBack;
    private List<String> listFacility = new ArrayList<>();
    private String[] str = new String[]{"停车场", "WiFi", "24小时热水", "大床", "空调", "电视机", "电吹风", "行李员", "接机服务", "早餐服务", "商务中心", "棋牌室", "中式餐厅",
            "暖气", "接待外宾", "洗衣服务", "电梯", "会议室", "叫车服务", "邮政服务", "全天前台", "信用卡", "桑拿", "SPA", "叫醒服务", "婚姻服务"};
    private int[] strImg = new int[]{R.drawable.icon_park, R.drawable.icon_net, R.drawable.icon_water, R.drawable.icon_bed, R.drawable.icon_aircondition, R.drawable.icon_tv, R.drawable.icon_drier,
            R.drawable.icon_aicon_baggage_clerk, R.drawable.icon_airport_pickup, R.drawable.icon_breakfast, R.drawable.icon_business, R.drawable.icon_chess_room, R.drawable.icon_chinese_food, R.drawable.icon_hotter,
            R.drawable.icon_jiedaiwaibin, R.drawable.icon_laundry, R.drawable.icon_liftservicer, R.drawable.icon_meeting_room, R.drawable.icon_panggil_mobil, R.drawable.icon_postalservice, R.drawable.icon_recepter, R.drawable.icon_reditcard,
            R.drawable.icon_sauna, R.drawable.icon_spa, R.drawable.icon_wake_up, R.drawable.icon_wedding_party};
    private int inNum = 1;
    private int is_collect = 0;
    private String mStrCheckInDate, mStrCheckOutDate, mPriceRangeStart, mPriceRangeEnd;
    String[] price = new String[]{"0", "150", "300", "500", "1000000"};
    //    String room_mode = Config.ROOM_MODE_ALL;
    private String phone = "";
    private String hotelName;
    private LockDetaileInfo data;
    //权限检测类
    private PermissionHelper mPermissionHelper;
    //返回值
    private static final int WRITE_RESULT_CODE = 12;
    //权限名称
    private static final String WRITE_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String WRITE_PERMISSION02 = Manifest.permission.ACCESS_FINE_LOCATION;
    private List<String> listPermission = new ArrayList<>();
    private Double longitude = 22.612130;
    private Double latitude = 114.022295;
    private TextView tvStayDateTime, tvStartHoureTime;
    private LinearLayout linDateLock, linHoureLock, linCollDateOrHoure;
    private ImageView ivDate, ivHour;
    private boolean isday = true;
    SearchHourRoomPopWindow popWindow;
    boolean nearby = false;
    private String mainPic = "";
    private GridView gvServicer;
    private LockServicerAdapter servicerAdapter;
    private List<String> listImg = new ArrayList<>();
    private TextView tvManySheBei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_show_hotel_detail02);
        mPermissionHelper = new PermissionHelper(this);
        listPermission.add(WRITE_PERMISSION);
        listPermission.add(WRITE_PERMISSION02);

        Intent i = getIntent();
        strHotelId = i.getStringExtra(Config.KEY_HOTEL_ID);
        strHotelURL = i.getStringExtra(Config.KEY_HOTEL_URL);
        strHotelCaption = i.getStringExtra(Config.KEY_HOTEL_CAPTION);
        mRoomMode = i.getStringExtra(Config.KEY_SHOW_ROOM_MODE);
        mainPic = i.getStringExtra(Config.LOCKPIC);
        is_collect = i.getIntExtra(Config.KEY_HOTEL_IS_COLLECT, 0);
        nearby = i.getBooleanExtra(Config.ISNEAR, false);
        if (AppContext.getProperty(getString(R.string.price_range_start)) != null) {
            mPriceRangeStart = AppContext.getProperty(getString(R.string.price_range_start));
            mPriceRangeEnd = AppContext.getProperty(getString(R.string.price_range_end));
            mPriceRangeStart = price[Integer.parseInt(mPriceRangeStart)];
            mPriceRangeEnd = price[Integer.parseInt(mPriceRangeEnd)];
        } else {
            mPriceRangeStart = price[0];
            mPriceRangeEnd = price[price.length - 1];
        }
        if (mRoomMode.equals(Config.ROOM_MODE_ALL)) {//从非搜索页面跳转的链接把价格置为不限
            mPriceRangeStart = price[0];
            mPriceRangeEnd = price[price.length - 1];
        }
        mStrCheckInDate = AppContext.getProperty(Config.KEY_CHECKIN_DATE);
        mStrCheckOutDate = AppContext.getProperty(Config.KEY_CHECKOUT_DATE);
        initView();
        setOnClickListenter();
        initData(strHotelId);

        if (is_collect == 1) {
            tvCollect.setImageResource(R.drawable.icon_favorited_cancel);//已收藏
        } else {
            tvCollect.setImageResource(R.drawable.icon_favorite);//未收藏
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_show_hotel_detail02;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }

    private void initView() {
        tvCollect = (ImageView) findViewById(R.id.collect);
        tvShare = (ImageView) findViewById(R.id.share);
        tvGoBack = (ImageView) findViewById(R.id.goBack);
        vpImg = (ViewPager) findViewById(R.id.vpImg);
        InNum = (CircleNavigationBar) findViewById(R.id.InNum);
        linServicerInfo = (LinearLayout) findViewById(R.id.linServicerInfo);
        tvLockName = (TextView) findViewById(R.id.tvLockName);
        ivServicer01 = (ImageView) findViewById(R.id.ivServicer01);
        ivServicer02 = (ImageView) findViewById(R.id.ivServicer02);
        ivServicer03 = (ImageView) findViewById(R.id.ivServicer03);
        ivServicer04 = (ImageView) findViewById(R.id.ivServicer04);
        ivPhone = (ImageView) findViewById(R.id.ivPhone);

        tvManySheBei = (TextView) findViewById(R.id.tvManySheBei);
        gvServicer = (GridView) findViewById(R.id.gvServicer);
        servicerAdapter = new LockServicerAdapter(listFacility, listImg, getApplicationContext());
        gvServicer.setAdapter(servicerAdapter);
        //
        tvStayDateTime = (TextView) findViewById(R.id.tvStayTime);
        tvStartHoureTime = (TextView) findViewById(R.id.tvStartTime);

        linDateLock = (LinearLayout) findViewById(R.id.linDateLock);
        linHoureLock = (LinearLayout) findViewById(R.id.linHoureLock);
        linCollDateOrHoure = (LinearLayout) findViewById(R.id.linCollDateOrHoure);
        ivDate = (ImageView) findViewById(R.id.ivDate);
        ivHour = (ImageView) findViewById(R.id.ivHour);

        linAddress = (LinearLayout) findViewById(R.id.linAddress);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
//        tvStartTime = (TextView) findViewById(R.id.tvStartTime);

        tvInTime = (TextView) findViewById(R.id.tvInTime);
        tvLeverTime = (TextView) findViewById(R.id.tvLeverTime);
        tvTime = (TextView) findViewById(R.id.tvTime);
        lvRoom = (ListView) findViewById(R.id.lvRoom);
        adapter = new RoomsAdapter(listRooms, getApplicationContext());
        lvRoom.setAdapter(adapter);
        adapter.setOnItemClickListenter(this);


        int[] imgs = new int[]{R.drawable.pic_hotel_defact, R.drawable.pic_hotel_defact};
        for (int i = 0; i < 1; i++) {
            View v = new ImageView(ShowHotelDetail02Activity.this);
            v.setBackgroundResource(imgs[i]);
            list.add(v);
        }
        // 加载activity
        pagerAdapter = new MyPagerAdapter(list);
        vpImg.setAdapter(pagerAdapter);
        InNum.setContentViewPager(vpImg);
        vpImg.setCurrentItem(0);
    }

    private void setOnClickListenter() {
        tvGoBack.setOnClickListener(this);
        tvCollect.setOnClickListener(this);
        tvShare.setOnClickListener(this);
        linServicerInfo.setOnClickListener(this);
        ivPhone.setOnClickListener(this);
        linAddress.setOnClickListener(this);
        ivDate.setOnClickListener(this);
        ivHour.setOnClickListener(this);
        linDateLock.setOnClickListener(this);
        linHoureLock.setOnClickListener(this);
        tvStartHoureTime.setOnClickListener(this);


        vpImg.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                InNum.scrollToPosition(position, 2000);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initData(String hotel_id) {
        HttpClient.instance().lockDetail(hotel_id, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                Log.e("TAAF", "------>" + responseBean.toString());
                data = responseBean.getData(LockDetaileInfo.class);
                tvLockName.setText(data.getCaption());
                hotelName = data.getCaption();
                if (nearby) {//附近显示距离
                    double localLongitude = Double.parseDouble(AppContext.getProperty(Config.LONGITUDE));
                    double localLatitude = Double.parseDouble(AppContext.getProperty(Config.LATITUDE));
                    double longitude = Double.parseDouble(data.getLongitude()) / 1000000;
                    double latitude = Double.parseDouble(data.getLatitude()) / 1000000;
                    tvAddress.setText(data.getAddress() + "   " + AppContext.getDistanceStr(localLongitude, localLatitude, longitude, latitude));
                } else {
                    tvAddress.setText(data.getAddress());
                }
                phone = data.getPhone_no();
                if (!data.getImages().equals("[]")) {
                    try {
                        JSONArray jsonArrayImg = new JSONArray(data.getImages());
                        list.clear();
                        for (int i = 0; i < jsonArrayImg.length(); i++) {
                            final ImageView imageView = new ImageView(ShowHotelDetail02Activity.this);
                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//                            ImageLoader.getInstance().displayImage(jsonArrayImg.getString(i), imageView);
                            ImageLoader.getInstance().displayImage(jsonArrayImg.getString(i), imageView, new ImageLoadingListener() {
                                @Override
                                public void onLoadingStarted(String s, View view) {

                                }

                                @Override
                                public void onLoadingFailed(String s, View view, FailReason failReason) {
                                    ImageLoader.getInstance().displayImage(mainPic, imageView);
//                                    AppContext.toast(s+"");
//                                    ImageLoader.getInstance().displayImage("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1487335522810&di=faddd8bab41a500614313c2753bd680d&imgtype=0&src=http%3A%2F%2Fawb.img1.xmtbang.com%2Fwechatmsg%2Farticle%2F20141218%2Fthumb%2Fc353487066de4ad0a9d4fac91bf3c9a3.jpg", imageView);
                                }

                                @Override
                                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                                    imageView.setImageBitmap(bitmap);
                                }

                                @Override
                                public void onLoadingCancelled(String s, View view) {
//                                    AppContext.toast(s+"22");
                                    ImageLoader.getInstance().displayImage(mainPic, imageView);
//                                    ImageLoader.getInstance().displayImage("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1487335522810&di=faddd8bab41a500614313c2753bd680d&imgtype=0&src=http%3A%2F%2Fawb.img1.xmtbang.com%2Fwechatmsg%2Farticle%2F20141218%2Fthumb%2Fc353487066de4ad0a9d4fac91bf3c9a3.jpg", imageView);
                                }
                            });
                            list.add(imageView);
                        }
                        pagerAdapter = new MyPagerAdapter(list);
                        vpImg.setAdapter(pagerAdapter);
                        InNum.setContentViewPager(vpImg);
                        pagerAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
//                        ImageLoader.getInstance().displayImage(mainPic, imageView);
                        list.clear();
                        ImageView imageView = new ImageView(ShowHotelDetail02Activity.this);
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        ImageLoader.getInstance().displayImage(mainPic, imageView);
                        list.add(imageView);
                        pagerAdapter = new MyPagerAdapter(list);
                        vpImg.setAdapter(pagerAdapter);
                        InNum.setContentViewPager(vpImg);
                        pagerAdapter.notifyDataSetChanged();
                    }
                } else {
                    list.clear();
                    ImageView imageView = new ImageView(ShowHotelDetail02Activity.this);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    ImageLoader.getInstance().displayImage(mainPic, imageView);
                    list.add(imageView);
                    pagerAdapter = new MyPagerAdapter(list);
                    vpImg.setAdapter(pagerAdapter);
                    InNum.setContentViewPager(vpImg);
                    pagerAdapter.notifyDataSetChanged();
                }
                if (!data.getHotel_facility().equals("[]")) {
                    try {
                        JSONArray jsonArrayFacility = new JSONArray(data.getHotel_facility());
                        for (int i = 0; i < jsonArrayFacility.length(); i++) {
                            listFacility.add(jsonArrayFacility.getString(i));
                        }
                        JSONArray jsonArrayFacility_url = new JSONArray(data.getFacility_url());
                        for (int i = 0; i < jsonArrayFacility_url.length(); i++) {
                            if (i < 4) {
                                listImg.add(jsonArrayFacility_url.getString(i));
                            }
                        }
                        if (jsonArrayFacility_url.length() > 4) {
                            tvManySheBei.setVisibility(View.VISIBLE);
                        } else {
                            tvManySheBei.setVisibility(View.GONE);
                        }
                        servicerAdapter.notifyDataSetChanged();


//                        for (int i = 0; i < str.length; i++) {
//                            if (listFacility.contains(str[i]) && inNum < 5) {
//                                if (inNum == 1) {
//                                    ivServicer01.setImageResource(strImg[i]);
//                                    ivServicer01.setVisibility(View.VISIBLE);
//                                } else if (inNum == 2) {
//                                    ivServicer02.setImageResource(strImg[i]);
//                                    ivServicer02.setVisibility(View.VISIBLE);
//                                } else if (inNum == 3) {
//                                    ivServicer03.setImageResource(strImg[i]);
//                                    ivServicer03.setVisibility(View.VISIBLE);
//                                } else if (inNum == 4) {
//                                    ivServicer04.setImageResource(strImg[i]);
//                                    ivServicer04.setVisibility(View.VISIBLE);
//                                }
//                                inNum++;
//                            }
//                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRoomMode.equals(Config.ROOM_MODE_DAY)) {
            //全日房
            linDateLock.setVisibility(View.VISIBLE);
            linHoureLock.setVisibility(View.GONE);
            setCalendarDate();
            initRoomData(strHotelId, mStrCheckInDate, mStrCheckOutDate, mPriceRangeStart, mPriceRangeEnd);
        } else if (mRoomMode.equals(Config.ROOM_MODE_HOUR)) {//钟点房
            linDateLock.setVisibility(View.GONE);
            linHoureLock.setVisibility(View.VISIBLE);
            setStartTime();
            setHoureCalendarDate();
            getHourRoom();

        } else if (mRoomMode.equals(Config.ROOM_MODE_ALL)) {//可以切换到钟点房
            linCollDateOrHoure.setVisibility(View.VISIBLE);
            if (isday) {
                //全日房
                linDateLock.setVisibility(View.VISIBLE);
                linHoureLock.setVisibility(View.GONE);
                setCalendarDate();
                initRoomData(strHotelId, mStrCheckInDate, mStrCheckOutDate, mPriceRangeStart, mPriceRangeEnd);
                ivHour.setImageResource(R.drawable.btn_room_hour);
                ivDate.setImageResource(R.drawable.btn_room_24h_chosed);
            } else {
                linDateLock.setVisibility(View.GONE);
                linHoureLock.setVisibility(View.VISIBLE);
                setStartTime();
                setHoureCalendarDate();
                getHourRoom();
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goBack:
                finish();
                break;
            case R.id.collect:
                inCollec(strHotelId);
                break;
            case R.id.share:
                new ShareAction(ShowHotelDetail02Activity.this).setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SMS, SHARE_MEDIA.EMAIL)
                        .withTitle("手机开门，轻松快捷")
                        .withText("我在用来住吧开门软件，你也来试试吧!")
                        .withMedia(new UMImage(ShowHotelDetail02Activity.this, R.drawable.icon))
                        .withTargetUrl("http://www.smarthoteltech.com/")
                        .setCallback(umShareListener)
                        .open();
                break;
            case R.id.linServicerInfo:
                Intent intent1 = new Intent(getApplicationContext(), DetailServicerInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Config.LOCKDETAILDATA, data);
                intent1.putExtras(bundle);
                intent1.putExtra(Config.KEY_HOTEL_ID, strHotelId);
                startActivity(intent1);
                break;
            case R.id.linAddress:
//                openGPSSettings();
                getToBaiduMap();
                break;
            case R.id.ivPhone:
                //调用拨号面板：
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);
                break;
            case R.id.ivDate://全日房
                ivHour.setImageResource(R.drawable.btn_room_hour);
                ivDate.setImageResource(R.drawable.btn_room_24h_chosed);
                linHoureLock.setVisibility(View.GONE);
                linDateLock.setVisibility(View.VISIBLE);
                setCalendarDate();
                initRoomData(strHotelId, mStrCheckInDate, mStrCheckOutDate, mPriceRangeStart, mPriceRangeEnd);
                break;
            case R.id.ivHour://钟点房
                ivHour.setImageResource(R.drawable.btn_room_hour_chosed);
                ivDate.setImageResource(R.drawable.btn_room_24h);
                linHoureLock.setVisibility(View.VISIBLE);
                linDateLock.setVisibility(View.GONE);
                setStartTime();
                setHoureCalendarDate();
                getHourRoom();
                break;
            case R.id.linDateLock:
                Intent i = new Intent(getApplicationContext(), CalendarView.class);
                startActivity(i);
                isday = true;
                break;
            case R.id.linHoureLock:
                Intent ii = new Intent(getApplicationContext(), CalendarViewSingle.class);
                startActivity(ii);
                isday = false;
                break;
            case R.id.tvStartTime:
                popWindow = new SearchHourRoomPopWindow(mContext);
                popWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        //    AppContext.setProperty(Config.KEY_HOUR_STARTTIME, getString(R.string.current_time));
                        if (AppContext.getProperty(Config.KEY_HOUR_STARTTIME) != null && AppContext.getProperty(Config.KEY_HOUR_STARTTIME).equals(getString(R.string.current_time))) {
//                            rbCurrentTime.setChecked(true);
                        } else {
                            tvStartHoureTime.setText(AppContext.getProperty(Config.KEY_HOUR_STARTTIME));
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void onItemClick(View view, int postion) {
//        AppContext.toast(postion+"");
        Intent i = new Intent(mContext, BookingBillActivity.class);
        i.putExtra(Config.KEY_HOTEL_ID, strHotelId);
        i.putExtra(Config.KEY_HOTEL_CAPTION, hotelName);
        i.putExtra(Config.KEY_HOTEL_ROOMID, listRooms.get(postion).getID());
        i.putExtra(Config.KEY_HOTEL_ROOMCAPTION, listRooms.get(postion).getCaption());
        i.putExtra(Config.KEY_HOTEL_ROOMPRICE, listRooms.get(postion).getPrice());
        if (listRooms.get(postion).getHour() != 0) {
            i.putExtra(Config.KEY_SHOW_ROOM_MODE, Config.ROOM_MODE_HOUR);
            i.putExtra(Config.KEY_HOUR_ROOM_SPAN_LIMIT, listRooms.get(postion).getHour());
        } else {
            i.putExtra(Config.KEY_SHOW_ROOM_MODE, Config.ROOM_MODE_DAY);
        }
        mContext.startActivity(i);
    }

    private void inCollec(String hotelID) {
        if (AppContext.getShareUserSessinid() != null) {
            if (is_collect == 0) {
                HttpClient.instance().addHotelFavorites(hotelID, new HttpCallBack() {
                    @Override
                    public void onSuccess(ResponseBean responseBean) {
                        ProgressDialog.disMiss();
                        if (responseBean.optString("result").equals("true")) {
                            toastSuccess(getString(R.string.operate_success));
                            tvCollect.setImageResource(R.drawable.icon_favorited_cancel);
                            is_collect = 1;
                        } else {
                            toastFail(R.string.operate_fail);
                        }
                    }

                    @Override
                    public void onFailure(String error, String message) {
                        super.onFailure(error, message);
                        ProgressDialog.disMiss();
                        toastFail(getString(R.string.operate_fail));
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialog.show(mContext, R.string.on_loading);
                    }
                });
            } else {
                HttpClient.instance().delHotelFavorites(hotelID, new HttpCallBack() {
                    @Override
                    public void onSuccess(ResponseBean responseBean) {
                        ProgressDialog.disMiss();
                        if (responseBean.optString("result").equals("true")) {
                            toastSuccess(getString(R.string.operate_success));
                            tvCollect.setImageResource(R.drawable.icon_favorite);
                            is_collect = 0;
                        } else {
                            toastFail(getString(R.string.operate_fail));
                        }
                    }

                    @Override
                    public void onFailure(String error, String message) {
                        super.onFailure(error, message);
                        ProgressDialog.disMiss();
                        toastFail(getString(R.string.operate_fail));
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialog.show(mContext, R.string.on_loading);
                    }
                });
            }
        } else {
            Intent i;
            i = new Intent(mContext, LoginByNameActivity.class);
            startActivity(i);
        }
    }


    public void setCalendarDate() {
        String strCheckInDate = AppContext.getProperty(Config.KEY_CHECKIN_DATE);
        String strCheckOutDate = AppContext.getProperty(Config.KEY_CHECKOUT_DATE);

        if (strCheckInDate != null && strCheckOutDate != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.YEAR_MONTH_DAY);
            Date checkInDate;
            Date checkOutDate;
            try {
                checkInDate = simpleDateFormat.parse(strCheckInDate);
                checkOutDate = simpleDateFormat.parse(strCheckOutDate);
                if (checkInDate.after(Calendar.getInstance().getTime()) || strCheckInDate.equals(DateUtil.getDateStr(Calendar.getInstance().getTimeInMillis(), DateUtil.YEAR_MONTH_DAY))) {   //当前时间是否大于缓存时间

                    Calendar c = Calendar.getInstance();
                    c.setTime(checkInDate);
                    String showStart = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日";
                    c.setTime(checkOutDate);
                    String showEnd = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日";
                    tvLeverTime.setText(showEnd);
                    tvInTime.setText(showStart);
                    mStrCheckInDate = strCheckInDate;
                    mStrCheckOutDate = strCheckOutDate;
//                    tvCheckInWeek.setText(DateUtil.getWeekByDay(strCheckInDate));
//                    tvCheckOutWeek.setText(DateUtil.getWeekByDay(strCheckOutDate));
                    tvTime.setText(getString(R.string.total) + DateUtil.getDayBetweenDate(checkInDate, checkOutDate) + getString(R.string.night));
                } else {
                    checkInDate = new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(checkInDate);
                    String showStart = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日";
                    strCheckInDate = DateUtil.getDateString(c);
                    AppContext.setProperty(Config.KEY_CHECKIN_DATE, strCheckInDate);
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    checkOutDate = c.getTime();
                    strCheckOutDate = DateUtil.getDateString(c);
                    AppContext.setProperty(Config.KEY_CHECKOUT_DATE, strCheckOutDate);
                    String showEnd = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日";
                    tvLeverTime.setText(showEnd);
                    tvInTime.setText(showStart);
                    mStrCheckInDate = strCheckInDate;
                    mStrCheckOutDate = strCheckOutDate;

//                    tvCheckInWeek.setText(DateUtil.getWeekByDay(strCheckInDate));
//                    tvCheckOutWeek.setText(DateUtil.getWeekByDay(strCheckOutDate));
                    tvTime.setText(getString(R.string.total) + DateUtil.getDayBetweenDate(checkInDate, checkOutDate) + getString(R.string.night));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            Date checkInDate = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(checkInDate);
            String showStart = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日";
            strCheckInDate = DateUtil.getDateString(c);
            AppContext.setProperty(Config.KEY_CHECKIN_DATE, strCheckInDate);
            c.add(Calendar.DAY_OF_MONTH, 1);
            Date checkOutDate = c.getTime();
            strCheckOutDate = DateUtil.getDateString(c);
            AppContext.setProperty(Config.KEY_CHECKOUT_DATE, strCheckOutDate);
            String showEnd = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日";
            tvLeverTime.setText(showEnd);
            tvInTime.setText(showStart);
            mStrCheckInDate = strCheckInDate;
            mStrCheckOutDate = strCheckOutDate;
//            tvCheckInWeek.setText(DateUtil.getWeekByDay(strCheckInDate));
//            tvCheckOutWeek.setText(DateUtil.getWeekByDay(strCheckOutDate));
            tvTime.setText(getString(R.string.total) + DateUtil.getDayBetweenDate(checkInDate, checkOutDate) + getString(R.string.night));
        }

    }

    public void setStartTime() {
        if (AppContext.getProperty(Config.KEY_HOUR_STARTTIME) != null) {
            if (AppContext.getProperty(Config.KEY_HOUR_STARTTIME).equals(getString(R.string.current_time))) {
                AppContext.setProperty(Config.KEY_HOUR_STARTTIME, DateUtil.getHourMinTime());
            }
            tvStartHoureTime.setText(AppContext.getProperty(Config.KEY_HOUR_STARTTIME));
        } else {
            AppContext.setProperty(Config.KEY_HOUR_STARTTIME, "11:00");
            tvStartHoureTime.setText("11:00");
        }
    }

    public void setHoureCalendarDate() {
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
                    tvStayDateTime.setText(showStart);

                } else {
                    checkInDate = new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(checkInDate);
                    String showStart = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日" + " " + DateUtil.getWeekByDay(checkInDate);
                    strCheckInDate = DateUtil.getDateString(c);
                    AppContext.setProperty(Config.KEY_HOUR_CHECKIN, strCheckInDate);
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    tvStayDateTime.setText(showStart);

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
            tvStayDateTime.setText(showStart);
        }

    }

    //全日房 房间类型
    private void initRoomData(String strHotelId, String mStrCheckInDate, String mStrCheckOutDate, String mPriceRangeStart, String mPriceRangeEnd) {
        HttpClient.instance().getHoteRoomList(strHotelId, mStrCheckInDate, mStrCheckOutDate, mPriceRangeStart, mPriceRangeEnd, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                try {
                    listRooms.clear();
                    List<HotelRoom> temp = responseBean.getListDataWithGson(HotelRoom.class);
                    listRooms.addAll(temp);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "没有可预订的房型，换个酒店试试吧~~", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);


                Toast.makeText(getApplicationContext(), R.string.load_fail, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getHourRoom() {
        HttpClient.instance().getHourRoomTypeList(AppContext.getProperty(Config.KEY_HOUR_CHECKIN), tvStartHoureTime.getText().toString(), strHotelId, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
//                lvHotelRoom.onRefreshComplete();
//                mListHotelRoom.clear();
                try {
                    listRooms.clear();
                    List<HotelRoom> temp = responseBean.getListDataWithGson(HotelRoom.class);
                    listRooms.addAll(temp);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "没有可预订的房型，换个酒店试试吧~~", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
//                lvHotelRoom.onRefreshComplete();
                Toast.makeText(getApplicationContext(), R.string.load_fail, Toast.LENGTH_SHORT).show();
            }
        });

    }


    /**
     * 判断是否安装目标应用
     *
     * @param packageName 目标应用安装后的包名
     * @return 是否已安装目标应用
     */
    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    //判断GPS是否开启
    private void openGPSSettings() {
        LocationManager alm = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        if (alm
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            // "GPS模块正常 "
            boolean b = mPermissionHelper.checkPermissions(WRITE_PERMISSION, WRITE_PERMISSION02);
            if (b) {
                getLocation();
            } else {
                mPermissionHelper.permissionsCheck(listPermission, WRITE_RESULT_CODE);
            }
            // 等待提示
//            dialog = new ProgressDialog(MyLockGroupsActivity.this);
//            dialog.setMessage("正在定位...");
//            dialog.setCancelable(true);
//            dialog.show();
            return;
        }
        Toast.makeText(this, "请开启GPS！", Toast.LENGTH_SHORT).show();
        //这个为跳转到安全页面
//        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
//        startActivityForResult(intent,0); //此为设置完成后返回到获取界面
    }

    private void getLocation() {
        // 注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Common.LOCATION_ACTION);
        this.registerReceiver(new LocationBroadcastReceiver(), filter);
        // 启动服务
        Intent intent = new Intent();
        intent.setClass(this, LocationSvc.class);
        startService(intent);
    }

    /*
   *  下载百度地图
   *
   * */
    private void getCancelDialog() {
        final Dialog dialog = new Dialog(ShowHotelDetail02Activity.this, R.style.OrderDialog);
        LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());
        View view = mInflater.inflate(R.layout.download_map, null);
        dialog.setContentView(view);
        view.findViewById(R.id.dialog_Cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.dialog_Sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //显示手机上所有的market商店
                try {
                    Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");//id=包名
                    Intent it = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(it);
                } catch (Exception e) {
                    AppContext.toast("未找到该下载商城，请自行下载");
                }

            }
        });
        dialog.show();
    }

    private class LocationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(Common.LOCATION_ACTION)) return;
            String locationInfo = intent.getStringExtra(Common.LOCATION);
            Bundle bundle = intent.getExtras();
            double mylatitude = bundle.getDouble(Config.LATITUDE);//纬度
            double mylongitude = bundle.getDouble(Config.LONGITUDE);//经度
            //转百度经纬度
            LatLng latLngmy = new LatLng(mylatitude, mylongitude);
            LatLng latLngmy01 = gpsToBaiduMap(latLngmy);
            mylatitude = latLngmy01.latitude;
            mylongitude = latLngmy01.longitude;
            try {
                //移劢 APP 调起 Android 百度地图方式举例
//              Intent intent1 = Intent.getIntent("intent://map/direction?origin=latlng:22.612130,114.023295|name:我家&destination=深圳通大夏&mode=driving&region=深圳&src=网客网|smartlock#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                if (!data.getLatitude().equals("null")) {
                    latitude = Double.parseDouble(data.getLatitude()) / 1000000;
                    longitude = Double.parseDouble(data.getLongitude()) / 1000000;
                    LatLng latLnglock = new LatLng(latitude, longitude);
                    LatLng latLnglock01 = gpsToBaiduMap(latLnglock);
                    latitude = latLnglock01.latitude;
                    longitude = latLnglock01.longitude;
                }
//                Intent intent1 = Intent.getIntent("intent://map/direction?origin=latlng:" + mylatitude + "," + mylongitude + "|name:当前位置&destination=latlng:" + latitude + "," + longitude + "|name:" + strHotelCaption + "&mode=driving&region=深圳&src=千万鼎|smartlock#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                Intent intent1 = Intent.getIntent("intent://map/direction?destination=latlng:" + latitude + "," + longitude + "|name:" + strHotelCaption + "&mode=driving&region=深圳&src=千万鼎|smartlock#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                if (isInstallByread("com.baidu.BaiduMap")) {
                    startActivity(intent1); //启动调用
                    Log.e("GasStation", "百度地图客户端已经安装");
                } else {
                    getCancelDialog();
                }
            } catch (Exception e) {
                e.printStackTrace();
                getCancelDialog();
            }
            ShowHotelDetail02Activity.this.unregisterReceiver(this);// 不需要时注销
        }
    }


    private LatLng gpsToBaiduMap(LatLng sourceLatLng) {
        // 将GPS设备采集的原始GPS坐标转换成百度坐标
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }


    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            com.umeng.socialize.utils.Log.d("plat", "platform" + platform);
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(ShowHotelDetail02Activity.this, platform + " 收藏成功啦", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ShowHotelDetail02Activity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(ShowHotelDetail02Activity.this, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            if (t != null) {
                com.umeng.socialize.utils.Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(ShowHotelDetail02Activity.this, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        UMSsoHandler ssoHandler = SocializeConfig.getSocializeConfig().getSsoHandler(requestCode);
//        if (ssoHandler != null) {
//            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
//        }
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        com.umeng.socialize.utils.Log.d("result", "onActivityResult");
    }

    private void getToBaiduMap(){
        try {
            //移劢 APP 调起 Android 百度地图方式举例
            if (!data.getLatitude().equals("null")) {
                latitude = Double.parseDouble(data.getLatitude()) / 1000000;
                longitude = Double.parseDouble(data.getLongitude()) / 1000000;
                LatLng latLnglock = new LatLng(latitude, longitude);
                LatLng latLnglock01 = gpsToBaiduMap(latLnglock);
                latitude = latLnglock01.latitude;
                longitude = latLnglock01.longitude;
            }
//              Intent intent1 = Intent.getIntent("intent://map/direction?origin=latlng:22.612130,114.023295|name:我家&destination=深圳通大夏&mode=driving&region=深圳&src=网客网|smartlock#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
            Intent intent1 = Intent.getIntent("intent://map/direction?destination=latlng:" + latitude + "," + longitude + "|name:" + strHotelCaption + "&mode=driving&region=深圳&src=千万鼎|smartlock#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
            if (isInstallByread("com.baidu.BaiduMap")) {
                startActivity(intent1); //启动调用
                Log.e("GasStation", "百度地图客户端已经安装");
            } else {
                getCancelDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
            getCancelDialog();
        }
    }
}

package com.sht.smartlock.ui.activity.fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.ui.activity.booking.SearchListActivity;
import com.sht.smartlock.ui.activity.booking.SelectCityActivity;
import com.sht.smartlock.ui.activity.booking.SelectDestinationActivity;
import com.sht.smartlock.ui.activity.booking.SelectPricePopWindow;
import com.sht.smartlock.ui.activity.chatlocation.Common;
import com.sht.smartlock.ui.activity.chatlocation.LocationSvc;
import com.sht.smartlock.ui.activity.hourroom.SearchHourRoomActivity;
import com.sht.smartlock.util.DateUtil;
import com.sht.smartlock.widget.calendar2.CalendarView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainTabBookingFragment extends Fragment implements View.OnClickListener {

    TextView mCheckInDate, mCheckOutDate, mTotalDay, mCity, mDestination, mPrice;
    LinearLayout tvHourRoom, tvNearbyHotel;
    TextView tvCheckInWeek, tvCheckOutWeek;
    SelectPricePopWindow popWindow;
    View root;
    Button btnSearch;
    String[] price = new String[]{"0", "150", "300", "500", "不限"};
    private ProgressDialog dialog;
    boolean isGetLocation = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.main_tab_booking, container, false);
        LinearLayout mDateTimePicker = (LinearLayout) root.findViewById(R.id.ly_datetimepicker);
        mCheckInDate = (TextView) root.findViewById(R.id.checkInDate);
        mCheckOutDate = (TextView) root.findViewById(R.id.checkOutDate);
        mTotalDay = (TextView) root.findViewById(R.id.totalDay);
        mCity = (TextView) root.findViewById(R.id.tvCity);
        mDestination = (TextView) root.findViewById(R.id.tvDestination);
        mPrice = (TextView) root.findViewById(R.id.tvPrice);
        tvCheckInWeek = (TextView) root.findViewById(R.id.checkInWeek);
        tvCheckOutWeek = (TextView) root.findViewById(R.id.checkOutWeek);
        btnSearch = (Button) root.findViewById(R.id.btnSearch);
        tvHourRoom = (LinearLayout) root.findViewById(R.id.tvHourRoom);
        tvNearbyHotel = (LinearLayout) root.findViewById(R.id.tvNearbyHotel);
        tvHourRoom.setOnClickListener(this);
        tvNearbyHotel.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        mCity.setOnClickListener(this);
        mPrice.setOnClickListener(this);
        mDestination.setOnClickListener(this);
        mDateTimePicker.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.ly_datetimepicker:
                i = new Intent(getActivity(), CalendarView.class);
                startActivity(i);
                break;
            case R.id.tvCity:
                i = new Intent(getActivity(), SelectCityActivity.class);
                startActivity(i);
                break;
            case R.id.tvDestination:
                i = new Intent(getActivity(), SelectDestinationActivity.class);
                startActivity(i);
                break;
            case R.id.tvPrice:
                popWindow = new SelectPricePopWindow(getActivity(), null);
                popWindow.showAtLocation(root, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        setPrice();
                    }
                });
                break;
            case R.id.btnSearch:
                if(mCity.getText().toString().equals("")){
                    Toast.makeText(getActivity(),getString(R.string.please_select_city),Toast.LENGTH_SHORT).show();
                    return;
                }
                i = new Intent(getActivity(), SearchListActivity.class);
                i.putExtra(Config.HOTEL_TYPE, "search");
                startActivity(i);
                break;
            case R.id.tvNearbyHotel:

//                isGetLocation=true;// 模拟器绕过定位使用
//                AppContext.setProperty(Config.LATITUDE,"22.616579");
//                AppContext.setProperty(Config.LONGITUDE,"114.036937");
//                if(isGetLocation==false){
//                  BaseApplication.toastFail("定位失败！已启动重新定位,请稍候再试。。。");
//                    openGPSSettings();
//                    return;
//                }
                i = new Intent(getActivity(), SearchListActivity.class);
                i.putExtra(Config.HOTEL_TYPE, "nearby");
                startActivity(i);
                break;
            case R.id.tvHourRoom:
                i=new Intent(getActivity(), SearchHourRoomActivity.class);
                startActivity(i);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //设定选择日期
        setCalendarDate();
        setPrice();
        setCity();
        setDestination();



    }

    public void setDestination() {
        if (AppContext.getProperty(getString(R.string.booking_destination)) != null) {
            mDestination.setText(AppContext.getProperty(getString(R.string.booking_destination)));
            mDestination.setTextColor(getResources().getColor(R.color.text_font));
        }
    }

    public void setPrice() {
        if (AppContext.getProperty(getString(R.string.price_range_start)) != null) {
            int left = Integer.parseInt(AppContext.getProperty(getString(R.string.price_range_start)));
            int right = Integer.parseInt(AppContext.getProperty(getString(R.string.price_range_end)));
            mPrice.setText(price[left] + "-" + price[right]);
            mPrice.setTextColor(getResources().getColor(R.color.text_font));

        } else {
//            mPrice.setText("0-不限");
//            AppContext.setProperty(getString(R.string.price_range_start), "0");
//            AppContext.setProperty(getString(R.string.price_range_end), String.valueOf(price.length - 1));
        }
    }

    public void setCity() {
        if (AppContext.getProperty(getString(R.string.booking_cityname)) != null) {
            mCity.setText(AppContext.getProperty(getString(R.string.booking_cityname)));
            mCity.setTextColor(getResources().getColor(R.color.text_font));
        } else {
            //定位
//            AppContext.setProperty(getString(R.string.booking_cityname), "深圳");
//            AppContext.setProperty(getString(R.string.booking_cityid), "1");
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
                if (checkInDate.after(Calendar.getInstance().getTime())||strCheckInDate.equals(DateUtil.getDateStr(Calendar.getInstance().getTimeInMillis(),DateUtil.YEAR_MONTH_DAY))) {   //当前时间是否大于缓存时间

                    Calendar c = Calendar.getInstance();
                    c.setTime(checkInDate);
                    String showStart = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日";
                    c.setTime(checkOutDate);
                    String showEnd = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日";
                    mCheckOutDate.setText(showEnd);
                    mCheckInDate.setText(showStart);
                    tvCheckInWeek.setText(DateUtil.getWeekByDay(strCheckInDate));
                    tvCheckOutWeek.setText(DateUtil.getWeekByDay(strCheckOutDate));
                    mTotalDay.setText(getString(R.string.total) + DateUtil.getDayBetweenDate(checkInDate, checkOutDate) + getString(R.string.night));
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
                    mCheckOutDate.setText(showEnd);
                    mCheckInDate.setText(showStart);
                    tvCheckInWeek.setText(DateUtil.getWeekByDay(strCheckInDate));
                    tvCheckOutWeek.setText(DateUtil.getWeekByDay(strCheckOutDate));
                    mTotalDay.setText(getString(R.string.total) + DateUtil.getDayBetweenDate(checkInDate, checkOutDate) + getString(R.string.night));
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
            mCheckOutDate.setText(showEnd);
            mCheckInDate.setText(showStart);
            tvCheckInWeek.setText(DateUtil.getWeekByDay(strCheckInDate));
            tvCheckOutWeek.setText(DateUtil.getWeekByDay(strCheckOutDate));
            mTotalDay.setText(getString(R.string.total) + DateUtil.getDayBetweenDate(checkInDate, checkOutDate) + getString(R.string.night));
        }

    }


}

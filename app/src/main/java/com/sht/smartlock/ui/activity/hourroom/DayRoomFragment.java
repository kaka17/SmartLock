package com.sht.smartlock.ui.activity.hourroom;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.sht.smartlock.model.booking.HotelRoom;
import com.sht.smartlock.ui.adapter.booking.HotelRoomAdapter;
import com.sht.smartlock.util.DateUtil;
import com.sht.smartlock.widget.calendar2.CalendarView;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class DayRoomFragment extends Fragment implements View.OnClickListener {
    private TextView mCheckInDate, mCheckOutDate, mTotalDay;
    private LinearLayout mDateTimePicker;
    String mStrCheckInDate, mStrCheckOutDate, mPriceRangeStart, mPriceRangeEnd;
    String strHotelId, strHotelCaption;
    HotelRoomAdapter mHotelRoomAdater;
    List<HotelRoom> mListHotelRoom;
    PullToRefreshListView lvHotelRoom;
    BookingHourRoomActivity activity;
    TextView tvCheckInWeek, tvCheckOutWeek;
    String[] price = new String[]{"0", "150", "300", "500", "1000000"};
    View root;
    public DayRoomFragment(BookingHourRoomActivity activity) {
       this.activity=activity;
        strHotelCaption=activity.getStrHotelCaption();
        strHotelId=activity.getStrHotelId();
    }

    public DayRoomFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (AppContext.getProperty(getString(R.string.price_range_start)) != null) {
            mPriceRangeStart = AppContext.getProperty(getString(R.string.price_range_start));
            mPriceRangeEnd = AppContext.getProperty(getString(R.string.price_range_end));
            mPriceRangeStart = price[Integer.parseInt(mPriceRangeStart)];
            mPriceRangeEnd = price[Integer.parseInt(mPriceRangeEnd)];
        } else {
            mPriceRangeStart = price[0];
            mPriceRangeEnd = price[price.length - 1];
        }
        if(activity.getRoom_mode().equals(Config.ROOM_MODE_ALL)){//从非搜索页面跳转的链接把价格置为不限
            mPriceRangeStart=price[0];
            mPriceRangeEnd=price[price.length-1];
        }
        root= inflater.inflate(R.layout.fragment_day_room, container, false);
        mStrCheckInDate = AppContext.getProperty(Config.KEY_CHECKIN_DATE);
        mStrCheckOutDate = AppContext.getProperty(Config.KEY_CHECKOUT_DATE);
        mCheckInDate = (TextView) root.findViewById(R.id.checkInDate);
        mCheckOutDate = (TextView) root.findViewById(R.id.checkOutDate);
        mTotalDay = (TextView) root.findViewById(R.id.totalDay);
        tvCheckInWeek= (TextView) root.findViewById(R.id.checkInWeek);
        tvCheckOutWeek= (TextView) root.findViewById(R.id.checkOutWeek);
        mDateTimePicker = (LinearLayout) root.findViewById(R.id.ly_datetimepicker);
        mDateTimePicker.setOnClickListener(this);
        mListHotelRoom = new ArrayList<>();
        mHotelRoomAdater = new HotelRoomAdapter(mListHotelRoom, (BookingHourRoomActivity) getActivity());
        lvHotelRoom = (PullToRefreshListView) root.findViewById(R.id.lvHotelRoom);
        lvHotelRoom.setAdapter(mHotelRoomAdater);
        lvHotelRoom.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                HttpClient.instance().getHoteRoomList(strHotelId, mStrCheckInDate, mStrCheckOutDate,mPriceRangeStart,mPriceRangeEnd, new HttpCallBack() {
                    @Override
                    public void onSuccess(ResponseBean responseBean) {
                       lvHotelRoom.onRefreshComplete();
                        mListHotelRoom.clear();
                        try
                        {
                            List<HotelRoom> temp = responseBean.getListDataWithGson(HotelRoom.class);
                            mHotelRoomAdater.addAll(temp);}
                        catch(Exception e){

                            Toast.makeText(activity, "没有可预订的房型，换个酒店试试吧~~", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onStart() {
                        super.onStart();

                    }

                    @Override
                    public void onFailure(String error, String message) {
                        super.onFailure(error, message);
                     lvHotelRoom.onRefreshComplete();

                        Toast.makeText(activity, R.string.load_fail, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        setCalendarDate();
        HttpClient.instance().getHoteRoomList(strHotelId, mStrCheckInDate, mStrCheckOutDate,mPriceRangeStart,mPriceRangeEnd, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                mListHotelRoom.clear();
                try
                {
                    List<HotelRoom> temp = responseBean.getListDataWithGson(HotelRoom.class);
                    mHotelRoomAdater.addAll(temp);}
                catch(Exception e){
                    if(!isHidden() )
                    Toast.makeText(activity, "没有可预订的房型，换个酒店试试吧~~", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                if(!isHidden() )
                ProgressDialog.show(activity, R.string.on_loading);
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
                if(!isHidden() )
                Toast.makeText(activity, R.string.load_fail, Toast.LENGTH_SHORT).show();
            }
        });
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

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.ly_datetimepicker:
                i = new Intent(activity, CalendarView.class);
                startActivity(i);
                break;
        }
    }
}

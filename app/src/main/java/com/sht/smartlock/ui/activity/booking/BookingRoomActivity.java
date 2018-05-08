package com.sht.smartlock.ui.activity.booking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.AppManager;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.model.booking.HotelRoom;
import com.sht.smartlock.ui.activity.base.BaseActivity;
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

public class BookingRoomActivity extends BaseActivity implements View.OnClickListener {
    private TextView mCheckInDate, mCheckOutDate, mTotalDay;
    private TextView tvTitle;
    TextView tvCheckInWeek, tvCheckOutWeek;
    ImageView tvGoBack;
    private LinearLayout mDateTimePicker;
    String mStrCheckInDate, mStrCheckOutDate;
    String strHotelId, strHotelCaption;
    HotelRoomAdapter mHotelRoomAdater;
    List<HotelRoom> mListHotelRoom;
    ListView lvHotelRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitView();
    }

    private void InitView() {
        mCheckInDate = (TextView) findViewById(R.id.checkInDate);
        mCheckOutDate = (TextView) findViewById(R.id.checkOutDate);
        mTotalDay = (TextView) findViewById(R.id.totalDay);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvGoBack= (ImageView) findViewById(R.id.goBack);
        tvCheckInWeek= (TextView) findViewById(R.id.checkInWeek);
        tvCheckOutWeek= (TextView) findViewById(R.id.checkOutWeek);
        tvGoBack.setOnClickListener(this);
        mDateTimePicker = (LinearLayout) findViewById(R.id.ly_datetimepicker);
        mDateTimePicker.setOnClickListener(this);
        Intent i = getIntent();
        strHotelCaption = i.getStringExtra(Config.KEY_HOTEL_CAPTION);
        strHotelId = i.getStringExtra(Config.KEY_HOTEL_ID);
        tvTitle.setText(strHotelCaption);
        mListHotelRoom = new ArrayList<>();
      //  mHotelRoomAdater = new HotelRoomAdapter(mListHotelRoom, BookingRoomActivity.this);
        lvHotelRoom = (ListView) findViewById(R.id.lvHotelRoom);
        lvHotelRoom.setAdapter(mHotelRoomAdater);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_booking_room;
    }

    protected boolean hasToolBar() {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCalendarDate();
        HttpClient.instance().getHoteRoomList(strHotelId, mStrCheckInDate, mStrCheckOutDate,"0","1000000", new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                mListHotelRoom.clear();
                try
                {
                List<HotelRoom> temp = responseBean.getListDataWithGson(HotelRoom.class);
                mHotelRoomAdater.addAll(temp);}
                catch(Exception e){
                    toastFail("没有可预订的房型，换个酒店试试吧~~");
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(mContext, R.string.on_loading);
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
                toastFail(R.string.load_fail);
            }
        });
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
                if (checkInDate.after(Calendar.getInstance().getTime())||mStrCheckInDate.equals(DateUtil.getDateStr(Calendar.getInstance().getTimeInMillis(),DateUtil.YEAR_MONTH_DAY))) {   //当前时间是否大于缓存时间
                    Calendar c = Calendar.getInstance();
                    c.setTime(checkInDate);
                    String showStart = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日";
                    c.setTime(checkOutDate);
                    String showEnd = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日";
                    mCheckOutDate.setText(showEnd);
                    mCheckInDate.setText(showStart);
                    tvCheckInWeek.setText(DateUtil.getWeekByDay(mStrCheckInDate));
                    tvCheckOutWeek.setText(DateUtil.getWeekByDay(mStrCheckOutDate));
                    mTotalDay.setText(getString(R.string.total) + DateUtil.getDayBetweenDate(checkInDate, checkOutDate) + getString(R.string.night));
                } else {
                    checkInDate = new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(checkInDate);
                    String showStart = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日";
                    mStrCheckInDate = DateUtil.getDateString(c);
                    AppContext.setProperty(Config.KEY_CHECKIN_DATE, mStrCheckInDate);
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    checkOutDate = c.getTime();
                    mStrCheckOutDate = DateUtil.getDateString(c);
                    AppContext.setProperty(Config.KEY_CHECKOUT_DATE, mStrCheckOutDate);
                    String showEnd = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日";
                    mCheckOutDate.setText(showEnd);
                    mCheckInDate.setText(showStart);
                    tvCheckInWeek.setText(DateUtil.getWeekByDay(mStrCheckInDate));
                    tvCheckOutWeek.setText(DateUtil.getWeekByDay(mStrCheckOutDate));
                    mTotalDay.setText(getString(R.string.total) + DateUtil.getDayBetweenDate(checkInDate, checkOutDate) + getString(R.string.night));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    public String getStrHotelId() {
        return strHotelId;
    }

    public String getStrHotelCaption() {
        return strHotelCaption;
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.goBack:
                finish();
                break;
            case R.id.ly_datetimepicker:
                i = new Intent(mContext, CalendarView.class);
                startActivity(i);
                break;
        }
    }
}

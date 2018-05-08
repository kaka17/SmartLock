package com.sht.smartlock.ui.activity.hourroom;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.booking.SelectCityActivity;
import com.sht.smartlock.ui.activity.booking.SelectDestinationActivity;
import com.sht.smartlock.util.DateUtil;
import com.sht.smartlock.widget.calendar2.CalendarViewSingle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SearchHourRoomActivity extends BaseActivity implements View.OnClickListener {
    TextView tvStayTime, tvCity, tvDestination, btnSearch;
    LinearLayout llBtnSearch;
    TextView btnNearHour;
    RadioGroup rgCheckTime;
    RadioButton rbChooseTime, rbCurrentTime;
    ImageView ivGoback;
    SearchHourRoomPopWindow popWindow;
    private TextView tvStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitView();
        setStartTime();
    }

    private void InitView() {
        tvStartTime = (TextView) findViewById(R.id.tvStartTime);
        rgCheckTime = (RadioGroup) findViewById(R.id.rgCheckTime);
        rbChooseTime = (RadioButton) findViewById(R.id.rbChooseTime);
        rbCurrentTime = (RadioButton) findViewById(R.id.rbCurrentTime);
        tvStayTime = (TextView) findViewById(R.id.tvStayTime);
        tvCity = (TextView) findViewById(R.id.tvCity);
        tvDestination = (TextView) findViewById(R.id.tvDestination);
        btnSearch = (TextView) findViewById(R.id.btnSearch);
        btnNearHour = (TextView) findViewById(R.id.btnNearHour);
        ivGoback = (ImageView) findViewById(R.id.goBack);
        llBtnSearch = (LinearLayout) findViewById(R.id.llBtnSearch);
        tvStartTime.setOnClickListener(this);
        ivGoback.setOnClickListener(this);
        llBtnSearch.setOnClickListener(this);
        btnNearHour.setOnClickListener(this);
        tvDestination.setOnClickListener(this);
        tvCity.setOnClickListener(this);
        tvStayTime.setOnClickListener(this);
        rbChooseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow = new SearchHourRoomPopWindow(mContext);
                popWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        //    AppContext.setProperty(Config.KEY_HOUR_STARTTIME, getString(R.string.current_time));
                        if (AppContext.getProperty(Config.KEY_HOUR_STARTTIME)!=null && AppContext.getProperty(Config.KEY_HOUR_STARTTIME).equals(getString(R.string.current_time))) {
                            rbCurrentTime.setChecked(true);
                        } else {
                            rbChooseTime.setText(AppContext.getProperty(Config.KEY_HOUR_STARTTIME));
                        }
                    }
                });
            }
        });
        rbCurrentTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppContext.setProperty(Config.KEY_HOUR_STARTTIME, DateUtil.getHourMinTime());
                rbChooseTime.setText(getString(R.string.choose_time));
            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_hour_room;
    }

    protected boolean hasToolBar() {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCalendarDate();
        setCity();
        setDestination();


        //   openGPSSettings();
    }

    public void setCity() {
        if (AppContext.getProperty(getString(R.string.booking_cityname)) != null) {
            tvCity.setText(AppContext.getProperty(getString(R.string.booking_cityname)));
            tvCity.setTextColor(getResources().getColor(R.color.text_font));
        } else {
            //定位
//            AppContext.setProperty(getString(R.string.booking_cityname), "深圳");
//            AppContext.setProperty(getString(R.string.booking_cityid), "1");
        }
    }

    public void setDestination() {
        if (AppContext.getProperty(getString(R.string.booking_destination)) != null) {
            tvDestination.setText(AppContext.getProperty(getString(R.string.booking_destination)));
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
                    String showStart = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日";
                    tvStayTime.setText(showStart+"  "+DateUtil.getWeekByDay(checkInDate));
                    strCheckInDate = DateUtil.getDateString(c);
                    AppContext.setProperty(Config.KEY_HOUR_CHECKIN, strCheckInDate);
                } else {
                    checkInDate = new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(checkInDate);
                    String showStart = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日";
                    strCheckInDate = DateUtil.getDateString(c);
                    AppContext.setProperty(Config.KEY_HOUR_CHECKIN, strCheckInDate);
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    tvStayTime.setText(showStart+"  "+DateUtil.getWeekByDay(checkInDate));

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
            AppContext.setProperty(Config.KEY_HOUR_CHECKIN, strCheckInDate);
            tvStayTime.setText(showStart+"  "+DateUtil.getWeekByDay(checkInDate));
        }

    }

    public void setStartTime() {
        Calendar calendarNow=Calendar.getInstance(Locale.CHINA);
        String time=calendarNow.get(Calendar.HOUR_OF_DAY)+":"+calendarNow.get(Calendar.MINUTE);
        AppContext.setProperty(Config.KEY_HOUR_STARTTIME, time);
        tvStartTime.setText("当前    "+time);
//        if (AppContext.getProperty(Config.KEY_HOUR_STARTTIME) != null) {
//            if (AppContext.getProperty(Config.KEY_HOUR_STARTTIME).equals(getString(R.string.current_time))) {
//                rbCurrentTime.setChecked(true);
//            } else {
//                rbChooseTime.setChecked(true);
//                rbChooseTime.setText(AppContext.getProperty(Config.KEY_HOUR_STARTTIME));
//            }
//        } else {
//            rbCurrentTime.setChecked(true);
//            Calendar c = Calendar.getInstance();
//            int hour = c.get(Calendar.HOUR_OF_DAY);
////            AppContext.setProperty(Config.KEY_HOUR_STARTTIME,c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE));
//            AppContext.setProperty(Config.KEY_HOUR_STARTTIME,DateUtil.getHourMinTime());
//        }
    }

    private boolean valiateTime() {
        if (!AppContext.getProperty(Config.KEY_HOUR_STARTTIME).equals(getString(R.string.current_time))) {
            Date startTime = DateUtil.stringToDate(AppContext.getProperty(Config.KEY_HOUR_CHECKIN), DateUtil.YEAR_MONTH_DAY);
            String[] time = AppContext.getProperty(Config.KEY_HOUR_STARTTIME).split(":");
            if (DateUtil.isToday(startTime.getTime())) {
                Calendar c = Calendar.getInstance(Locale.CHINA);
                c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
                c.set(Calendar.MINUTE, Integer.parseInt(time[1]));
                Date temp = c.getTime();
                Calendar nowCal = Calendar.getInstance(Locale.CHINA);

                Date now = nowCal.getTime();
                // System.out.println(temp.getTime()" ");
//                if (temp.getTime() < (now.getTime() + 1000 * 60 * 10)) {
                if (temp.getTime() < (now.getTime() )) {
                    Toast.makeText(mContext, R.string.choose_effective_time, Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.goBack:
                finish();
                break;
            case R.id.tvStayTime:
                i = new Intent(mContext, CalendarViewSingle.class);
                startActivity(i);
                break;
            case R.id.tvCity:
                i = new Intent(mContext, SelectCityActivity.class);
                startActivity(i);
                break;
            case R.id.tvDestination:
                i = new Intent(mContext, SelectDestinationActivity.class);
                startActivity(i);
                break;
            case R.id.llBtnSearch:
                if (rbChooseTime.isChecked() && AppContext.getProperty(Config.KEY_HOUR_STARTTIME).equals(getString(R.string.current_time))) {
                    toastFail(getString(R.string.choose_arrive_time));
                    return;
                }
                if (tvCity.getText().toString().equals("")) {
                    toastFail(getString(R.string.please_select_city));
                    return;
                }
                if (!valiateTime()) {
                    return;
                }
                if (AppContext.getProperty(Config.KEY_HOUR_STARTTIME).equals(getString(R.string.current_time))){
                    Calendar nowCal = Calendar.getInstance(Locale.CHINA);
                    int strHour = nowCal.get(Calendar.HOUR_OF_DAY);
                    int strMinute = nowCal.get(Calendar.MINUTE);
                    AppContext.setProperty(Config.KEY_HOUR_STARTTIME, strHour + ":" + strMinute);
                }
                i = new Intent(mContext, SearchHourListActivity.class);
                i.putExtra(Config.HOTEL_TYPE, "search");
                startActivity(i);
                break;
            case R.id.btnNearHour:
//                if(isGetLocation==false){
//                    BaseApplication.toastFail("定位失败！已启动重新定位,请稍候再试。。。");
//                    openGPSSettings();
//                    return;
//                }
                if (!valiateTime()) {
                    return;
                }
                if (AppContext.getProperty(Config.KEY_HOUR_STARTTIME).equals(getString(R.string.current_time))){
                    Calendar nowCal = Calendar.getInstance(Locale.CHINA);
                    int strHour = nowCal.get(Calendar.HOUR_OF_DAY);
                    int strMinute = nowCal.get(Calendar.MINUTE);
                    AppContext.setProperty(Config.KEY_HOUR_STARTTIME, strHour + ":" + strMinute);
                }
                i = new Intent(mContext, SearchHourListActivity.class);
                i.putExtra(Config.HOTEL_TYPE, "nearby");
                startActivity(i);
                break;
            case R.id.tvStartTime:
                popWindow = new SearchHourRoomPopWindow(mContext);
                popWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        //    AppContext.setProperty(Config.KEY_HOUR_STARTTIME, getString(R.string.current_time));
//                        if (AppContext.getProperty(Config.KEY_HOUR_STARTTIME)!=null && AppContext.getProperty(Config.KEY_HOUR_STARTTIME).equals(getString(R.string.current_time))) {
//                            rbCurrentTime.setChecked(true);
//                        } else {
//                            rbChooseTime.setText(AppContext.getProperty(Config.KEY_HOUR_STARTTIME));
//                        }
                        tvStartTime.setText(AppContext.getProperty(Config.KEY_HOUR_STARTTIME));
                    }
                });

                break;
        }
    }


}

package com.sht.smartlock.ui.activity.hourroom;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.util.DateUtil;
import com.sht.smartlock.util.LogUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Administrator on 2015/10/20.
 */
public class SearchHourRoomPopWindow extends PopupWindow {
    Context mContext;
    TextView btnCancel, btnEnsure;
    TimePicker timePicker;
    View mView;

    public SearchHourRoomPopWindow(Context context) {
        super(context);
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.activity_search_hour_room_pop, null);
        btnCancel = (TextView) mView.findViewById(R.id.btnCancel);
        btnEnsure = (TextView) mView.findViewById(R.id.btnSure);
        btnEnsure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strHour, strMinute;
                int hourOfDay = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();
                strHour = String.valueOf(hourOfDay);
                strMinute = String.valueOf(minute);
                Date startTime = DateUtil.stringToDate(AppContext.getProperty(Config.KEY_HOUR_CHECKIN), DateUtil.YEAR_MONTH_DAY);
                if (DateUtil.isToday(startTime.getTime())) {
                    Calendar c = Calendar.getInstance(Locale.CHINA);
                    c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    c.set(Calendar.MINUTE, minute);
                    Date temp = c.getTime();
                    Calendar nowCal = Calendar.getInstance(Locale.CHINA);

                    Date now = nowCal.getTime();
                    // System.out.println(temp.getTime()" ");
                    if (temp.getTime() < (now.getTime() + 1000 * 60 * 10)) {
                        Toast.makeText(mContext, "预订时间需比当前时间晚十分钟！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (hourOfDay < 10) {
                    strHour = "0" + String.valueOf(hourOfDay);
                }
                if (minute < 10) {
                    strMinute = "0" + String.valueOf(minute);
                }

                AppContext.setProperty(Config.KEY_HOUR_STARTTIME, strHour + ":" + strMinute);
                dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        timePicker = (TimePicker) mView.findViewById(R.id.tpStartTime);
        timePicker.setIs24HourView(true);
        if (AppContext.getProperty(Config.KEY_HOUR_STARTTIME) == null || AppContext.getProperty(Config.KEY_HOUR_STARTTIME).equals(mContext.getResources().getString(R.string.current_time))) {
            Calendar c = Calendar.getInstance(Locale.CHINA);
            c.add(Calendar.MINUTE, 10);
            timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
        } else {
            Date startTime = DateUtil.stringToDate(AppContext.getProperty(Config.KEY_HOUR_CHECKIN), DateUtil.YEAR_MONTH_DAY);
            String[] time = AppContext.getProperty(Config.KEY_HOUR_STARTTIME).split(":");
            if (DateUtil.isToday(startTime.getTime())) {
                Calendar c = Calendar.getInstance(Locale.CHINA);
                c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
                c.set(Calendar.MINUTE, Integer.parseInt(time[1]));
                Date temp = c.getTime();
                Calendar nowCal = Calendar.getInstance(Locale.CHINA);
                nowCal.add(Calendar.MINUTE,10);
                Date now = nowCal.getTime();
                // System.out.println(temp.getTime()" ");
                if (temp.getTime() < now.getTime()) {
                  //  Toast.makeText(mContext, "预订时间需比当前时间晚十分钟！", Toast.LENGTH_SHORT).show();
               //     return;
                    Calendar cc = Calendar.getInstance(Locale.CHINA);
                    cc.add(Calendar.MINUTE, 10);
                    timePicker.setCurrentHour(cc.get(Calendar.HOUR_OF_DAY));
                    timePicker.setCurrentMinute(cc.get(Calendar.MINUTE));
                }else{
                    timePicker.setCurrentHour(Integer.parseInt(time[0]));
                    timePicker.setCurrentMinute(Integer.parseInt(time[1]));
                }
            }else {

                timePicker.setCurrentHour(Integer.parseInt(time[0]));
                timePicker.setCurrentMinute(Integer.parseInt(time[1]));
            }
        }

        setContentView(mView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        //setAnimationStyle(R.style.Animation_AppCompat_Dialog);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        setBackgroundDrawable(dw);
    }
}

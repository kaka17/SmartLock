package com.sht.smartlock.ui.activity.booking;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.util.DateUtil;
import com.sht.smartlock.widget.calendar.DatePickerController;
import com.sht.smartlock.widget.calendar.DayPickerView;
import com.sht.smartlock.widget.calendar.SimpleMonthAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CheckDateActivity extends BaseActivity implements DatePickerController {
    private DayPickerView dayPickerView;
    private TextView mTitleBar;
    private SimpleMonthAdapter mSimpleMonthAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dayPickerView = (DayPickerView) findViewById(R.id.pickerView);
        dayPickerView.setController(this);
        mSimpleMonthAdapter = dayPickerView.getMonthAdapter();
        if (AppContext.getProperty(Config.KEY_CHECKIN_DATE) != null) {
            String strStart=AppContext.getProperty(Config.KEY_CHECKIN_DATE);
            String strEnd=AppContext.getProperty(Config.KEY_CHECKOUT_DATE);
            mSimpleMonthAdapter.getSelectedDays().setFirst(new SimpleMonthAdapter.CalendarDay(strStart));
            mSimpleMonthAdapter.getSelectedDays().setLast(new SimpleMonthAdapter.CalendarDay(strEnd));
            mSimpleMonthAdapter.notifyDataSetChanged();
        }
        mTitleBar = (TextView) findViewById(R.id.tvTitleBar);
        mTitleBar.setText("选择时间");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_check_date;
    }

    protected boolean hasToolBar() {
        return false;
    }

    @Override
    public int getMaxYear() {
        return Calendar.getInstance().get(Calendar.YEAR) + 1;
    }

    @Override
    public void onDayOfMonthSelected(int year, int month, int day) {
        Log.e("Day Selected", day + " / " + month + " / " + year);
//        mSimpleMonthAdapter.getSelectedDays().setFirst(new SimpleMonthAdapter.CalendarDay(2015,9,14));
//        mSimpleMonthAdapter.getSelectedDays().setLast(new SimpleMonthAdapter.CalendarDay(year,month,day));
//        mSimpleMonthAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDateRangeSelected(SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> selectedDays) {
        Log.e("Date range selected", selectedDays.getFirst().toDateString() + " --> " + selectedDays.getLast().toDateString());
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(DateUtil.YEAR_MONTH_DAY);
        try {
            Date start=simpleDateFormat.parse(selectedDays.getFirst().toDateString());
            Date end=simpleDateFormat.parse(selectedDays.getLast().toDateString());
        if (end.before(start)) {
            AppContext.setProperty(Config.KEY_CHECKIN_DATE, selectedDays.getLast().toDateString());
            AppContext.setProperty(Config.KEY_CHECKOUT_DATE, selectedDays.getFirst().toDateString());
        } else {
            AppContext.setProperty(Config.KEY_CHECKIN_DATE, selectedDays.getFirst().toDateString());
            AppContext.setProperty(Config.KEY_CHECKOUT_DATE, selectedDays.getLast().toDateString());
        }
        finish();
        } catch (ParseException e) {
        e.printStackTrace();
    }
    }
}

package com.sht.smartlock.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.fragment.NewDoorFragment;
import com.sht.smartlock.ui.activity.myview.MonthDateView;
import com.sht.smartlock.ui.entity.Entitys;
import com.sht.smartlock.util.DateUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddWakeTimeActivity extends BaseActivity implements View.OnClickListener{

    private TimePicker timepicker;
    private ImageView ivLeft,ivRight;
    private TextView tvTodayTime,tvToday;
    private MonthDateView monthDateView;
    private  List<Integer> list = new ArrayList<Integer>();
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minutes;
    private Calendar cal;
    private ImageView ivYes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_wake_time);

        list.add(10);
        list.add(12);
        list.add(15);
        list.add(16);
        //获取年月日分秒信息
        cal     = Calendar.getInstance();
        year    = cal.get(Calendar.YEAR);
        month   = cal.get(Calendar.MONTH)+1;
        day     = cal.get(Calendar.DAY_OF_MONTH) ;
        hour    = cal.get(Calendar.HOUR_OF_DAY);
        minutes  = cal.get(Calendar.MINUTE);
        initView();
        onBack();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_wake_time;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }
    private void onBack(){
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView(){

        ivYes = (ImageView) findViewById(R.id.ivYes);

        timepicker = (TimePicker) findViewById(R.id.timepicker);
        ivLeft = (ImageView) findViewById(R.id.ivLeft);
        ivRight = (ImageView) findViewById(R.id.ivRight);
        tvTodayTime = (TextView) findViewById(R.id.tvTodayTime);
        tvToday = (TextView) findViewById(R.id.tvToday);
        monthDateView = (MonthDateView) findViewById(R.id.monthDateView);
        monthDateView.setmDayColor(R.color.TextBlack087);
        timepicker.setIs24HourView(true);
        monthDateView.setTextView(tvTodayTime, tvToday);
        monthDateView.setDaysHasThingList(list);
        //设置今天
//        monthDateView.setTodayToView();
        monthDateView.setDateClick(new MonthDateView.DateClick() {

            @Override
            public void onClickOnDate() {
                tvTodayTime.setText(monthDateView.getmSelYear()+"年"+monthDateView.getmSelMonth()+"月");
                initDate();

                Log.e("TIME","--->"+monthDateView.getmSelYear()+"-"+monthDateView.getmSelMonth()+1+"-"+monthDateView.getmSelDay());
            }
        });

        timepicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                hour=hourOfDay;
                minutes=minute;
//                Log.e("TIMW","----------->"+hourOfDay+"--"+minute);

            }
        });


        setOnlistener();
    }

    private void initDate(){
        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, monthDateView.getmSelYear());
        date.set(Calendar.MONTH, monthDateView.getmSelMonth());
        date.set(Calendar.DAY_OF_MONTH, monthDateView.getmSelDay());
        boolean today = DateUtil.isToday(date.getTimeInMillis());
        boolean TomorrDay = DateUtil.isTomorrowday(date.getTimeInMillis());
        if (today){
            tvToday.setText("今天");
            tvToday.setVisibility(View.VISIBLE);
        }else if(TomorrDay){
            tvToday.setText("明天");
            tvToday.setVisibility(View.VISIBLE);
        }else {
            tvToday.setVisibility(View.GONE);
        }
        year=monthDateView.getmSelYear();
        month=monthDateView.getmSelMonth()+1;
        day=monthDateView.getmSelDay();
    }
    private void setOnlistener(){
        ivLeft.setOnClickListener(this);
        ivRight.setOnClickListener(this);
        ivYes.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivLeft:
                monthDateView.onLeftClick();
                initDate();
                break;
            case R.id.ivRight:
                monthDateView.onRightClick();
                initDate();
                break;
            case R.id.ivYes:
                cal.set(year,month-1,day,hour,minutes);
                Date date = cal.getTime();

//                Date date = new Date(year,month,day,hour,minutes);
                SimpleDateFormat formatNew = new SimpleDateFormat(DateUtil.PATTERN);
                String  time = formatNew.format(date);
                Log.e("Time","---------------->>>"+time);
                initSetWakeTime(NewDoorFragment.list.get(NewDoorFragment.pos).getRoom_id(), time);
                break;

        }
    }
    private void initSetWakeTime(String room_id,String morning_call_time){
        HttpClient.instance().servicerMoringCall(room_id, morning_call_time, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(AddWakeTimeActivity.this,"正在添加服务中...");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
                AppContext.toast(getString(R.string.TaoskFaiully));
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                Log.e("par","--->"+responseBean.toString());
                Entitys data = responseBean.getData(Entitys.class);
                if (data.getCode()==1){//成功
                    AppContext.toast(data.getMsg());
                }else {
                    AppContext.toast(data.getMsg());
                }
            }
        });
    }
}

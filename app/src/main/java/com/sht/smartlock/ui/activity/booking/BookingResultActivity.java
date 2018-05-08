package com.sht.smartlock.ui.activity.booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.AppManager;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.ui.activity.MainActivity;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.util.DateUtil;

import java.text.DecimalFormat;

public class BookingResultActivity extends BaseActivity implements View.OnClickListener {
    TextView tvHotelCaption, tvRoomTypeAndCount, tvStayTime, tvArriveAtLast, tvConsumptionAmount;
    ImageView tvGoBack;
    Button btnPayNow, btnPayLater;
    LinearLayout llArriveTime;
    String strRoomCaption, strRoomTypeAndCount, strStayTime, strArriveAtLast,strBillnum;
    double dbConsumptionAmount;
    DecimalFormat df   =new   java.text.DecimalFormat("#.00");
    String room_mode,hourSpan;
    String hotelId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitView();
        Intent i = getIntent();
        strBillnum=i.getStringExtra(Config.KEY_BOOKING_BILLNUM);
        strRoomCaption = i.getStringExtra(Config.KEY_HOTEL_CAPTION);
        strRoomTypeAndCount = i.getStringExtra(Config.KEY_HOTEL_ROOMCAPTION) + "/" + String.valueOf(i.getIntExtra(Config.KEY_HOTEL_ROOMCOUNT,0));
        room_mode=i.getStringExtra(Config.KEY_SHOW_ROOM_MODE);
        hotelId=i.getStringExtra(Config.KEY_HOTEL_ID);
        if(room_mode.equals(Config.ROOM_MODE_HOUR)){
            hourSpan=i.getStringExtra(Config.KEY_HOUR_ROOM_SPAN);
            llArriveTime.setVisibility(View.GONE);
            strStayTime=AppContext.getProperty(Config.KEY_HOUR_CHECKIN)+"  "+AppContext.getProperty(Config.KEY_HOUR_ROOM_SPAN);
        }else{
            llArriveTime.setVisibility(View.VISIBLE);
            String strCheckInDate = AppContext.getProperty(Config.KEY_CHECKIN_DATE);
            String strCheckOutDate = AppContext.getProperty(Config.KEY_CHECKOUT_DATE);
            strStayTime = strCheckInDate + getString(R.string.to) + strCheckOutDate ;//+ "(共" + (DateUtil.getDayBetweenDate(strCheckInDate, strCheckOutDate)) + "晚)";
            strArriveAtLast = AppContext.getProperty(Config.KEY_ARRIVE_TIME).split("-")[1] + getString(R.string.inform_if_arrive_later);
            tvArriveAtLast.setText(strArriveAtLast);
        }

        dbConsumptionAmount = i.getDoubleExtra(Config.KEY_HOTEL_ROOMTOTALPRICE, 0);
        tvHotelCaption.setText(strRoomCaption);
        tvRoomTypeAndCount.setText(strRoomTypeAndCount+getString(R.string.room));
        tvStayTime.setText(strStayTime);
        tvConsumptionAmount.setText(getString(R.string.currency_sign)+df.format(dbConsumptionAmount));
    }

    private void InitView() {
        tvHotelCaption = (TextView) findViewById(R.id.tvRoomCaption);
        tvRoomTypeAndCount = (TextView) findViewById(R.id.tvRoomTypeAndCount);
        tvStayTime = (TextView) findViewById(R.id.tvStayTime);
        tvArriveAtLast = (TextView) findViewById(R.id.tvArriveAtLatest);
        tvConsumptionAmount = (TextView) findViewById(R.id.tvConsumptionAmount);
        tvGoBack= (ImageView) findViewById(R.id.goBack);
        btnPayNow = (Button) findViewById(R.id.btnPayNow);
        btnPayLater = (Button) findViewById(R.id.btnPayLater);
        llArriveTime= (LinearLayout) findViewById(R.id.llAriveTime);
        btnPayNow.setOnClickListener(this);
        btnPayLater.setOnClickListener(this);
        tvGoBack.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_booking_result;
    }

    protected boolean hasToolBar() {
        return false;
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.btnPayNow:
                i=new Intent(this,PayBillActivity.class);
                i.putExtra(Config.KEY_HOTEL_ROOMTOTALPRICE, dbConsumptionAmount);
                i.putExtra(Config.KEY_BOOKING_BILLNUM,strBillnum);
                i.putExtra(Config.KEY_BILL_TYPE, Config.BILL_TYPE_BOOK);
                i.putExtra(Config.KEY_SHOW_ROOM_MODE,room_mode);
                i.putExtra(Config.KEY_HOTEL_ID,hotelId);

                startActivity(i);
                break;
            case R.id.btnPayLater:
//                i=new Intent(this,ShowHotelDetailActivity.class);
//                i.putExtra(Config.KEY_HOTEL_ID, AppContext.getProperty(Config.CACHE_HOTELID));
//                i.putExtra(Config.KEY_HOTEL_URL, AppContext.getProperty(Config.CACHE_HOTELURL));
//                i.putExtra(Config.KEY_HOTEL_CAPTION, AppContext.getProperty(Config.CACHE_HOTELCAPTION));
//                startActivity(i);
//                finish();
                AppManager.getAppManager().finishToActivity(ShowHotelDetail02Activity.class);
                break;
            case R.id.goBack:
//                i=new Intent(mContext,ShowHotelDetailActivity.class);
//                i.putExtra(Config.KEY_HOTEL_ID, AppContext.getProperty(Config.CACHE_HOTELID));
//                i.putExtra(Config.KEY_HOTEL_URL, AppContext.getProperty(Config.CACHE_HOTELURL));
//                i.putExtra(Config.KEY_HOTEL_CAPTION, AppContext.getProperty(Config.CACHE_HOTELCAPTION));
//                startActivity(i);
//                finish();
                AppManager.getAppManager().finishToActivity(ShowHotelDetail02Activity.class);
                break;
        }
    }
}

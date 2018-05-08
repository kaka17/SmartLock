package com.sht.smartlock.ui.activity.booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.model.booking.RoomSubmit;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.util.DateUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookingBillActivity extends BaseActivity implements View.OnClickListener {
    TextView tvHotelCaption, tvBookingDetail, tvAdd, tvMinus, tvRoomCaption, tvRoomCount;
    ImageView tvGoBack;
    TextView tvTotalPrice, tvArriveTime;
    EditText etBookingBy, etPhoneNum;
    ImageView btnEnsure;
    RelativeLayout rlArriveTime;
    double totalPrice = 0, roomprice = 0;
    int roomcount = 1;
    String hotelid, hotelCaption, roomid, roomcaption;
    SelectArriveTimePopWindow popWindow;
    String strCheckInDate, strCheckOutDate;
    DecimalFormat df = new java.text.DecimalFormat("#0.00");
    String room_mode = "";
    String stayTime, startTime;
    int hour = 0;
    int endHour = 0;
    String hourSpan;
    int stayNight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitView();
        //获取最早可以到店时间
        getchinkinStartTime();
    }

    private void InitView() {
        tvGoBack = (ImageView) findViewById(R.id.goBack);
        tvHotelCaption = (TextView) findViewById(R.id.tvHotelCaption);
        tvBookingDetail = (TextView) findViewById(R.id.tvBookingDetail);
        tvAdd = (TextView) findViewById(R.id.tvAdd);
        tvMinus = (TextView) findViewById(R.id.tvMinus);
        tvRoomCount = (TextView) findViewById(R.id.tvRoomCount);
        etBookingBy = (EditText) findViewById(R.id.tvBookingBy);
        tvRoomCaption = (TextView) findViewById(R.id.tvRoomCaption);
        etPhoneNum = (EditText) findViewById(R.id.tvPhoneNum);
        tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);
        tvArriveTime = (TextView) findViewById(R.id.tvArriveTime);
        btnEnsure = (ImageView) findViewById(R.id.btnEnsure);
        rlArriveTime = (RelativeLayout) findViewById(R.id.rlArriveTime);
        tvAdd.setOnClickListener(this);
        tvMinus.setOnClickListener(this);
        tvArriveTime.setOnClickListener(this);
        btnEnsure.setOnClickListener(this);
        tvGoBack.setOnClickListener(this);
        Intent i = getIntent();
        roomprice = i.getDoubleExtra(Config.KEY_HOTEL_ROOMPRICE, 0);
        totalPrice = getTotalPrice();
        tvTotalPrice.setText(getString(R.string.currency_sign) + df.format(totalPrice) + "");
        roomid = i.getStringExtra(Config.KEY_HOTEL_ROOMID);
        hotelid = i.getStringExtra(Config.KEY_HOTEL_ID);
        hotelCaption = i.getStringExtra(Config.KEY_HOTEL_CAPTION);

        roomcaption = i.getStringExtra(Config.KEY_HOTEL_ROOMCAPTION);
        room_mode = i.getStringExtra(Config.KEY_SHOW_ROOM_MODE);
        stayNight = DateUtil.getDayBetweenDate(AppContext.getProperty(Config.KEY_CHECKIN_DATE), AppContext.getProperty(Config.KEY_CHECKOUT_DATE));
        if (room_mode.equals(Config.ROOM_MODE_HOUR)) {
            hour = i.getIntExtra(Config.KEY_HOUR_ROOM_SPAN_LIMIT, 0);
            rlArriveTime.setVisibility(View.GONE);
        }

        if (room_mode.equals(Config.ROOM_MODE_DAY)){
            getRoomprice();

        }
        tvRoomCaption.setText(roomcaption);
        tvHotelCaption.setText(hotelCaption);
        String strBookingDetail = getBookingDetail();
        tvBookingDetail.setText(strBookingDetail);
        if (AppContext.getProperty(Config.USERPHONE)!=null){
            etPhoneNum.setText(AppContext.getProperty(Config.USERPHONE));
        }else {
            etPhoneNum.setText("");
        }
        if (AppContext.getProperty(Config.KEY_USER_NAME)==null||AppContext.getProperty(Config.KEY_USER_NAME).equals("null")){
            etBookingBy.setText("");
        }else {
            etBookingBy.setText(AppContext.getProperty(Config.KEY_USER_NAME));
        }

        tvArriveTime.setText(DateUtil.getNextHourTime()+":00-"+(DateUtil.getNextHourTime()+1)+":00");
        AppContext.setProperty(Config.KEY_ARRIVE_TIME, tvArriveTime.getText().toString());
    }
private void getRoomprice(){

    HttpClient.instance().getBookPrice(roomid, AppContext.getProperty(Config.KEY_CHECKIN_DATE), AppContext.getProperty(Config.KEY_CHECKOUT_DATE), "1", new HttpCallBack() {
        @Override
        public void onSuccess(ResponseBean responseBean) {
            ProgressDialog.disMiss();
            String result = responseBean.getData();
            try{
                roomprice=Double.parseDouble(result);
                totalPrice = getTotalPrice();
                tvTotalPrice.setText(getString(R.string.currency_sign) + df.format(totalPrice) + "");
            }
            catch(Exception e){
               ProgressDialog.show(mContext,"加载价格失败，请稍候再试！");
            }
        }

        @Override
        public void onFailure(String error, String message) {
            super.onFailure(error, message);
            ProgressDialog.disMiss();
        }

        @Override
        public void onStart() {
            super.onStart();
            ProgressDialog.show(mContext,R.string.on_loading);
        }
    });

}
    private double getTotalPrice() {

        if (room_mode.equals(Config.ROOM_MODE_HOUR)) {
            return roomcount * roomprice;
        } else {
            return roomcount * roomprice ;
        }
        // return 0;
    }

    private String getBookingDetail() {
        String s = "";
        if (room_mode.equals(Config.ROOM_MODE_DAY)) {
            strCheckInDate = AppContext.getProperty(Config.KEY_CHECKIN_DATE);
            strCheckOutDate = AppContext.getProperty(Config.KEY_CHECKOUT_DATE);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.YEAR_MONTH_DAY);
            Date checkInDate;
            Date checkOutDate;
            try {
                checkInDate = simpleDateFormat.parse(strCheckInDate);
                checkOutDate = simpleDateFormat.parse(strCheckOutDate);
                Calendar c = Calendar.getInstance();
                c.setTime(checkInDate);
                String showStart = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日" ;//+ DateUtil.getWeekByDay(strCheckInDate) + ")";
                c.setTime(checkOutDate);
                String showEnd = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日" ;//+ DateUtil.getWeekByDay(strCheckOutDate) + ")";
                s = "入住：" + showStart + "    离店：" + showEnd + "    共" + DateUtil.getDayBetweenDate(checkInDate, checkOutDate) + "晚";
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            stayTime = AppContext.getProperty(Config.KEY_HOUR_CHECKIN);
            startTime = AppContext.getProperty(Config.KEY_HOUR_STARTTIME);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.YEAR_MONTH_DAY);
            Date checkInDate;
            try {
                checkInDate = simpleDateFormat.parse(stayTime);
                Calendar c = Calendar.getInstance();
                c.setTime(checkInDate);
                String showStart = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日(" + DateUtil.getWeekByDay(stayTime) + ")";
                if (!startTime.equals(getString(R.string.current_time))) {
                    String[] spliteHour = startTime.split(":");
                    endHour = Integer.parseInt(spliteHour[0]) + hour;
                    if (endHour > 24) {
                        endHour = 24;
                    }
                    hourSpan = startTime + "-" + endHour + ":" + spliteHour[1];
                } else {
                    hourSpan = getString(R.string.current_time) + "-" + hour + "小时后";
                }
                s = showStart + "    时间段：" + hourSpan;
            } catch (Exception e) {

            }
        }
        return s;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_booking_bill;
    }

    protected boolean hasToolBar() {
        return false;
    }

    private boolean isValiate() {

        if (etBookingBy.getText().toString().trim().length() == 0) {
            toastFail(getString(R.string.please_enter_booking_by));
            return false;
        }
        if (etPhoneNum.getText().length() == 0) {
            toastFail(getString(R.string.please_enter_correct_phonenum));
            return false;
        }
        if (etPhoneNum.getText().length() != 0) {
            Pattern p = null;
            Matcher m = null;
            boolean b = false;
            p = Pattern.compile("^[1][3,4,5,7,8,9][0-9]{9}$"); // 验证手机号
            m = p.matcher(etPhoneNum.getText().toString());
            b = m.matches();
            if (!b) {
                toastFail(getString(R.string.please_enter_correct_phonenum));
            }
            return b;
        }

        return true;
    }

    class BookBillCallback extends HttpCallBack {

        @Override
        public void onSuccess(ResponseBean responseBean) {
            ProgressDialog.disMiss();
            String result = responseBean.getData();
            try {
                JSONObject jsonObject = new JSONObject(responseBean.toString());
                if (jsonObject.optString("result") == "") {

                    toastFail(getString(R.string.book_fail_try_again));
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                toastFail(getString(R.string.occur_unkown_error));
                return;
            }
            if (result.equals("0")) {
                toastFail("没有可预订的房间，换一家酒店试试吧~~");
            } else if (result.equals("-1")) {
                toastFail("下单失败！时间格式不对！");
            } else if (result.equals("-2")) {
                toastFail("下单失败！请校验本地时间的准确性！");
            } else {
                Intent i = new Intent(mContext, BookingResultActivity.class);
                i.putExtra(Config.KEY_BOOKING_BILLNUM, result);
                i.putExtra(Config.KEY_HOTEL_CAPTION, hotelCaption);
                i.putExtra(Config.KEY_HOTEL_ROOMCAPTION, roomcaption);
                i.putExtra(Config.KEY_HOTEL_ROOMCOUNT, roomcount);
                i.putExtra(Config.KEY_HOTEL_ROOMTOTALPRICE, totalPrice);
                i.putExtra(Config.KEY_SHOW_ROOM_MODE, room_mode);
                i.putExtra(Config.KEY_HOTEL_ID,hotelid);
                AppContext.setProperty(Config.KEY_HOUR_ROOM_SPAN_LIMIT, String.valueOf(hour));
                //   String tempHourSpan=""
                if (room_mode.equals(Config.ROOM_MODE_HOUR)) {
                    if (AppContext.getProperty(Config.KEY_HOUR_STARTTIME) != null && AppContext.getProperty(Config.KEY_HOUR_STARTTIME).equals(getString(R.string.current_time))) {
                        String tempStartTime = AppContext.getProperty(Config.KEY_HOUR_REAL_CURRENT);
                        String[] spliteHour = tempStartTime.split(":");
                        endHour = Integer.parseInt(spliteHour[0]) + hour;
                        if (endHour > 24) {
                            endHour = 24;
                        }
                        hourSpan = tempStartTime + "-" + endHour + ":" + spliteHour[1];
                    }
                }
                AppContext.setProperty(Config.KEY_HOUR_ROOM_SPAN, hourSpan);
                startActivity(i);
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
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.goBack:
                finish();
                break;
            case R.id.tvAdd:
                roomcount++;
                tvRoomCount.setText(roomcount + "间");
                tvTotalPrice.setText("￥" + df.format(getTotalPrice()));
                totalPrice = getTotalPrice();
                break;
            case R.id.tvMinus:
                roomcount--;
                if (roomcount < 1) {
                    roomcount = 1;
                }
                tvRoomCount.setText(roomcount + "间");
                tvTotalPrice.setText("￥" + df.format(getTotalPrice()));
                totalPrice = getTotalPrice();
                break;
            case R.id.btnEnsure:
                if (!isValiate()) return;
                if (room_mode.equals(Config.ROOM_MODE_DAY)) {
//                    HttpClient.instance().submitBook(roomid, roomcount, strCheckInDate, strCheckOutDate, String.valueOf(totalPrice), tvArriveTime.getText().toString(), etBookingBy.getText().toString(),
//                            etPhoneNum.getText().toString(), new BookBillCallback());
                    initSubmit(roomid, strCheckInDate, strCheckOutDate, String.valueOf(totalPrice),tvArriveTime.getText().toString(), etBookingBy.getText().toString(), etPhoneNum.getText().toString(), roomcount + "");
                } else {
                    if (AppContext.getProperty(Config.KEY_HOUR_STARTTIME).equals(getString(R.string.current_time))) {
                        Calendar c = Calendar.getInstance(Locale.CHINA);
                        String minute =""+(c.get(Calendar.MINUTE)+2);//怕跟服务器有时间差，加个两分钟
                        if ((c.get(Calendar.MINUTE)+2) < 10) {
                            minute = "0" + (c.get(Calendar.MINUTE)+2);
                        }
                        String hour=""+c.get(Calendar.HOUR_OF_DAY);
                        if(c.get(Calendar.HOUR_OF_DAY)<10){
                            hour="0"+c.get(Calendar.HOUR_OF_DAY);
                        }
                        startTime = hour + ":" + minute;
                        AppContext.setProperty(Config.KEY_HOUR_REAL_CURRENT, startTime);
                    }
//                    HttpClient.instance().submitHourBook(roomid, stayTime, startTime, String.valueOf(totalPrice), roomcount, etPhoneNum.getText().toString(), etBookingBy.getText().toString(), new BookBillCallback());
                    initSubmitHourHotel(roomid, stayTime, startTime, String.valueOf(totalPrice), roomcount, etPhoneNum.getText().toString(), etBookingBy.getText().toString());

                }
                break;
            case R.id.tvArriveTime:
                if (AppContext.getProperty(Config.KEY_ARRIVE_HOTELID) != hotelid) {
                    final View vv = v;
                    HttpClient.instance().getCheckOutTime(hotelid, new HttpCallBack() {
                        @Override
                        public void onSuccess(ResponseBean responseBean) {
                            //缓存arrivetime
                            dismissOnLoading();
                            AppContext.setProperty(Config.KEY_ARRIVE_HOTELID, hotelid);
                            AppContext.setProperty(Config.KEY_ARRIVE_STARTTIME, responseBean.getData());
                            popWindow = new SelectArriveTimePopWindow(BookingBillActivity.this);
                            popWindow.showAtLocation(vv, Gravity.CENTER, 0, 0);
                            popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                                @Override
                                public void onDismiss() {
                                    tvArriveTime.setText(AppContext.getProperty(Config.KEY_ARRIVE_TIME));
                                }
                            });
                        }

                        @Override
                        public void onFailure(String error, String message) {
                            super.onFailure(error, message);
                            dismissOnLoading();
                            toastFail(R.string.system_busy);
                        }

                        @Override
                        public void onStart() {
                            super.onStart();
                            showOnLoading();
                        }
                    });
                } else {
                    popWindow = new SelectArriveTimePopWindow(BookingBillActivity.this);
                    popWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                    popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            tvArriveTime.setText(AppContext.getProperty(Config.KEY_ARRIVE_TIME));
                        }
                    });
                }

                break;
        }
    }

    private void getchinkinStartTime(){
        HttpClient.instance().getCheckOutTime(hotelid, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                dismissOnLoading();
                AppContext.setProperty(Config.KEY_ARRIVE_HOTELID, hotelid);
                AppContext.setProperty(Config.KEY_ARRIVE_STARTTIME, responseBean.getData());
               int startTime = Integer.parseInt(AppContext.getProperty(Config.KEY_ARRIVE_STARTTIME).split(":")[0]);
                if (startTime<DateUtil.getNextHourTime()) {//客户在酒店设定的最早到店时间内预定时显示下一小时 时间
                    tvArriveTime.setText(DateUtil.getNextHourTime() + ":00-" + (DateUtil.getNextHourTime() + 1) + ":00");
                }else {//客户在未到酒店设定的最早到店时间预定时显示酒店最早到店时间
                    tvArriveTime.setText(startTime + ":00-" + (startTime + 1) + ":00");
                }
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                dismissOnLoading();
                toastFail(R.string.system_busy);
            }

            @Override
            public void onStart() {
                super.onStart();
                showOnLoading();
            }
        });
    }

    private void initSubmit(String room_type_id,  String start_date,String end_date, String price,String expect_checkin_time,String checkin_name,String checkin_phone, String num){
        HttpClient.instance().bookRoomSubmit(room_type_id,start_date,end_date,price,expect_checkin_time,checkin_name,checkin_phone,num, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(BookingBillActivity.this,"正在下单中...");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                RoomSubmit data = responseBean.getData(RoomSubmit.class);
                if (data.getCode().equals("1")){
                    Intent i = new Intent(mContext, BookingResultActivity.class);
                    i.putExtra(Config.KEY_BOOKING_BILLNUM, data.getBook_id());
                    i.putExtra(Config.KEY_HOTEL_CAPTION, hotelCaption);
                    i.putExtra(Config.KEY_HOTEL_ROOMCAPTION, roomcaption);
                    i.putExtra(Config.KEY_HOTEL_ROOMCOUNT, roomcount);
                    i.putExtra(Config.KEY_HOTEL_ROOMTOTALPRICE, totalPrice);
                    i.putExtra(Config.KEY_SHOW_ROOM_MODE, room_mode);
                    i.putExtra(Config.KEY_HOTEL_ID,hotelid);
                    AppContext.setProperty(Config.KEY_HOUR_ROOM_SPAN_LIMIT, String.valueOf(hour));
                    //   String tempHourSpan=""
//                    if (room_mode.equals(Config.ROOM_MODE_HOUR)) {
//                        if (AppContext.getProperty(Config.KEY_HOUR_STARTTIME) != null && AppContext.getProperty(Config.KEY_HOUR_STARTTIME).equals(getString(R.string.current_time))) {
//                            String tempStartTime = AppContext.getProperty(Config.KEY_HOUR_REAL_CURRENT);
//                            String[] spliteHour = tempStartTime.split(":");
//                            endHour = Integer.parseInt(spliteHour[0]) + hour;
//                            if (endHour > 24) {
//                                endHour = 24;
//                            }
//                            hourSpan = tempStartTime + "-" + endHour + ":" + spliteHour[1];
//                        }
//                    }
                    AppContext.setProperty(Config.KEY_HOUR_ROOM_SPAN, hourSpan);
                    startActivity(i);
                }else if (data.getCode().equals("-1")){
                    AppContext.toast(data.getMsg());
                }else {
                    AppContext.toast("下单失败，请重新下单");
                }
            }
        });
    }

    private void initSubmitHourHotel(String roomid, String day, String starttime, String price, int num, String checkinphone, String checkinname){
        HttpClient.instance().submitHourBook(roomid, day, starttime, price, num, checkinphone, checkinname, new HttpCallBack() {

            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(BookingBillActivity.this, "正在下单中...");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
            }
            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                RoomSubmit data = responseBean.getData(RoomSubmit.class);
                if (data.getCode().equals("1")){
                    Intent i = new Intent(mContext, BookingResultActivity.class);
                    i.putExtra(Config.KEY_BOOKING_BILLNUM, data.getBook_id());
                    i.putExtra(Config.KEY_HOTEL_CAPTION, hotelCaption);
                    i.putExtra(Config.KEY_HOTEL_ROOMCAPTION, roomcaption);
                    i.putExtra(Config.KEY_HOTEL_ROOMCOUNT, roomcount);
                    i.putExtra(Config.KEY_HOTEL_ROOMTOTALPRICE, totalPrice);
                    i.putExtra(Config.KEY_SHOW_ROOM_MODE, room_mode);
                    i.putExtra(Config.KEY_HOTEL_ID,hotelid);
                    AppContext.setProperty(Config.KEY_HOUR_ROOM_SPAN_LIMIT, String.valueOf(hour));
                    //   String tempHourSpan=""
                    if (room_mode.equals(Config.ROOM_MODE_HOUR)) {
                        if (AppContext.getProperty(Config.KEY_HOUR_STARTTIME) != null && AppContext.getProperty(Config.KEY_HOUR_STARTTIME).equals(getString(R.string.current_time))) {
                            String tempStartTime = AppContext.getProperty(Config.KEY_HOUR_REAL_CURRENT);
                            String[] spliteHour = tempStartTime.split(":");
                            endHour = Integer.parseInt(spliteHour[0]) + hour;
                            if (endHour > 24) {
                                endHour = 24;
                            }
                            hourSpan = tempStartTime + "-" + endHour + ":" + spliteHour[1];
                        }
                    }
                    AppContext.setProperty(Config.KEY_HOUR_ROOM_SPAN, hourSpan);
                    startActivity(i);
                }else if (data.getCode().equals("-1")){
                    AppContext.toast(data.getMsg());
                }else {
                    AppContext.toast("下单失败，请重新下单");
                }
            }
        });
    }

}

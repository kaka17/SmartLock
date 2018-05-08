package com.sht.smartlock.ui.activity.booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.AppManager;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.model.booking.Bill;
import com.sht.smartlock.model.booking.HourBill;
import com.sht.smartlock.ui.activity.MainActivity;
import com.sht.smartlock.ui.activity.mine.MyHotelOrderListActivity;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.widget.dialog.ProgressDialog;

public class PayResultActivity extends BaseActivity implements View.OnClickListener {
    TextView tvHotelName, tvRoomTypeAndCount, tvStayTime, tvCheckingYouself, tvCheckInRoomNumber,tvPayResult;
    String billno;
    ImageView tvGoback;
    Bill mBill;
    String room_mode;
    HourBill hourBill;
Button btnBackToMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvGoback = (ImageView) findViewById(R.id.goBack);
        tvGoback.setOnClickListener(this);
        tvPayResult= (TextView) findViewById(R.id.tvPayResult);
        tvHotelName = (TextView) findViewById(R.id.tvHotelName);
        tvRoomTypeAndCount = (TextView) findViewById(R.id.tvRoomTypeAndCount);
        tvStayTime = (TextView) findViewById(R.id.tvStayTime);
        tvCheckingYouself = (TextView) findViewById(R.id.tvCheckingYouself);
        tvCheckInRoomNumber = (TextView) findViewById(R.id.tvCheckInRoomNumber);
        btnBackToMain= (Button) findViewById(R.id.btnBackToMain);
        btnBackToMain.setOnClickListener(this);
        Intent i = getIntent();
        billno = i.getStringExtra(Config.KEY_BOOKING_BILLNUM);
        room_mode = i.getStringExtra(Config.KEY_SHOW_ROOM_MODE);
        AppContext.setProperty(Config.PAYDATAstrBillnum,null);
        AppContext.setProperty(Config.PAYDATAroom_mode,null);
        AppContext.setProperty(Config.PAYDATAtab, null);
        AppContext.setProperty(Config.PAYDATAroom_id,null);
        AppContext.setProperty(Config.PAYDATApayAction,null);

        ProgressDialog.show(mContext, R.string.on_loading);
        HttpClient.instance().getRoomBooking(billno, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                if (room_mode.equals(Config.ROOM_MODE_DAY)) {
                    try {
                        mBill = responseBean.GetResultModel(Bill.class);
                        if (mBill == null) {
                            ProgressDialog.show(mContext, responseBean.toString());
                        }
                        tvHotelName.setText(mBill.getHotel_caption());
                        tvRoomTypeAndCount.setText(mBill.getCaption() + "/" + mBill.getNum() + getString(R.string.room));
                        tvStayTime.setText(mBill.getStart_date() + getString(R.string.to) + mBill.getEnd_date());
                        tvCheckInRoomNumber.setText(mBill.getRoom_no());
                        if (mBill.getCheckin_flag().equals("1")) {
                            tvCheckingYouself.setText("开通");
                        } else {
                            tvCheckingYouself.setText("未开通");
                        }
                    } catch (Exception e) {
                        System.out.println("paramss" + e.toString());
                    }
                } else {
                    try {
                        hourBill = responseBean.GetResultModel(HourBill.class);
                        if (hourBill == null) {
                            ProgressDialog.show(mContext, responseBean.toString());
                        }
                        tvHotelName.setText(hourBill.getCaption());
                        tvRoomTypeAndCount.setText(hourBill.getHotel_caption() + "/" + hourBill.getNum() + getString(R.string.room));
                        tvStayTime.setText(AppContext.getProperty(Config.KEY_HOUR_CHECKIN) + "  时间段:" + AppContext.getProperty(Config.KEY_HOUR_ROOM_SPAN));
                        tvCheckInRoomNumber.setText(hourBill.getRoom_no());
                        if (hourBill.getCheckin_flag().equals("1")) {
                            tvCheckingYouself.setText("开通");
                        } else {
                            tvCheckingYouself.setText("未开通");
                        }
                    } catch (Exception e) {
                        System.out.println("paramss" + e.toString());
                    }
                }
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
                tvPayResult.setText("支付出错了！");
                toastFail("error:"+error+""+message);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pay_result;
    }

    protected boolean hasToolBar() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goBack:
                AppManager.getAppManager().finishToActivity(ShowHotelDetail02Activity.class, MyHotelOrderListActivity.class);
                break;
            case R.id.btnBackToMain:
                AppManager.getAppManager().finishToActivity(MainActivity.class);
                break;
        }
    }
}

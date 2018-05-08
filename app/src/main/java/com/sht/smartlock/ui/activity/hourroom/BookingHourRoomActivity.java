package com.sht.smartlock.ui.activity.hourroom;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.ui.activity.base.BaseActivity;

public class  BookingHourRoomActivity extends BaseActivity implements View.OnClickListener {
    private DayRoomFragment frgDayRoom;
    private HourRoomFragment frgHourRoom;
    private LinearLayout llHeader, llTabHour, llTabDay;
    private ImageView mTabLine;
    private int screenWidth;



    String room_mode = Config.ROOM_MODE_ALL;
    ImageView ivGoback;
    TextView tvTitle;
    String strHotelId, strHotelCaption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitView();
        if (savedInstanceState == null) {
            frgDayRoom = new DayRoomFragment(BookingHourRoomActivity.this);
            frgHourRoom = HourRoomFragment.newInstance(strHotelCaption,strHotelId);
            if (room_mode.equals(Config.ROOM_MODE_HOUR)) {
                getFragmentManager().beginTransaction().add(R.id.FragmentContainer, frgHourRoom, null).commit();
            } else {
                getFragmentManager().beginTransaction().add(R.id.FragmentContainer, frgDayRoom, null).commit();
            }
        }
    }

    private void InitView() {
        llTabDay = (LinearLayout) findViewById(R.id.id_tab_day);
        llTabHour = (LinearLayout) findViewById(R.id.id_tab_hour);
        llHeader = (LinearLayout) findViewById(R.id.lyroomTab);
        ivGoback = (ImageView) findViewById(R.id.goBack);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        ivGoback.setOnClickListener(this);
        llTabDay.setOnClickListener(this);
        llTabHour.setOnClickListener(this);

        Intent i = getIntent();
        strHotelCaption = i.getStringExtra(Config.KEY_HOTEL_CAPTION);
        strHotelId = i.getStringExtra(Config.KEY_HOTEL_ID);
        room_mode = i.getStringExtra(Config.KEY_SHOW_ROOM_MODE);
        tvTitle.setText(strHotelCaption);
        if (room_mode.equals(Config.ROOM_MODE_ALL)) {
            llHeader.setVisibility(View.VISIBLE);
        } else {
            llHeader.setVisibility(View.GONE);
        }
        initTabLine();
    }

    private void initTabLine() {
        mTabLine = (ImageView) findViewById(R.id.id_tab_line);
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        screenWidth = outMetrics.widthPixels;
        LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mTabLine.getLayoutParams();
        lp.width = screenWidth / 2;
        mTabLine.setLayoutParams(lp);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        //防止内存回收fragment出现重叠现象
        if (frgDayRoom == null && fragment instanceof DayRoomFragment) {
            frgDayRoom = (DayRoomFragment) fragment;
        } else if (frgHourRoom == null && fragment instanceof HourRoomFragment) {
            frgHourRoom = (HourRoomFragment) fragment;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_booking_hour_room;
    }

    protected boolean hasToolBar() {
        return false;
    }

    public String getStrHotelId() {
        return strHotelId;
    }

    public String getStrHotelCaption() {
        return strHotelCaption;
    }
    public String getRoom_mode() {
        return room_mode;
    }
    @Override
    public void onClick(View v) {
        LinearLayout.LayoutParams lp;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        switch (v.getId()) {
            case R.id.id_tab_day:
                if (frgDayRoom.isAdded()) {
                    transaction.hide(frgHourRoom).show(frgDayRoom).commit();
                } else {
                    transaction.hide(frgHourRoom).add(R.id.FragmentContainer, frgDayRoom).commit();
                }
                lp = (android.widget.LinearLayout.LayoutParams) mTabLine
                        .getLayoutParams();
                lp.leftMargin = 0;
                mTabLine.setLayoutParams(lp);
                break;
            case R.id.id_tab_hour:
                if (frgHourRoom.isAdded()) {
                    transaction.hide(frgDayRoom).show(frgHourRoom).commit();
                } else {
                    transaction.hide(frgDayRoom).add(R.id.FragmentContainer, frgHourRoom).commit();
                }
                lp = (android.widget.LinearLayout.LayoutParams) mTabLine
                        .getLayoutParams();
                lp.leftMargin = (int) ((screenWidth * 1.0 / 2));
                mTabLine.setLayoutParams(lp);
                break;
            case R.id.goBack:
                finish();
                break;
        }
    }
}

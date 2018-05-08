package com.sht.smartlock.ui.activity.hourroom;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupWindow;
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
import com.sht.smartlock.widget.calendar2.CalendarViewSingle;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class HourRoomFragment extends Fragment implements View.OnClickListener {
    TextView tvStartDay, tvStartTime;
    String strHotelId, strHotelCaption;
    HotelRoomAdapter mHotelRoomAdater;
    List<HotelRoom> mListHotelRoom;
    PullToRefreshListView lvHotelRoom;

    View root;
    SearchHourRoomPopWindow popWindow;
    public final static String EXTRA_HOTEL_CAPTION = "extra_hotel_caption";
    public final static String EXTRA_HOTEL_ID = "extra_hotel_id";



    public static  HourRoomFragment newInstance(String HotelCaption, String HotelId) {
        HourRoomFragment myFragment = new HourRoomFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_HOTEL_CAPTION, HotelCaption);
        bundle.putString(EXTRA_HOTEL_ID, HotelId);
      //  this.strHotelId=strHotelId;

        myFragment.setArguments(bundle);
        return myFragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            strHotelCaption = args.getString(EXTRA_HOTEL_CAPTION);
            strHotelId = args.getString(EXTRA_HOTEL_ID);
        }
    }

    public HourRoomFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_hour_room, container, false);
        tvStartTime = (TextView) root.findViewById(R.id.tvStartTime);
        tvStartDay = (TextView) root.findViewById(R.id.tvStayTime);
        tvStartTime.setOnClickListener(this);
        tvStartDay.setOnClickListener(this);
        mListHotelRoom = new ArrayList<>();
        mHotelRoomAdater = new HotelRoomAdapter(mListHotelRoom, (BookingHourRoomActivity) getActivity());
        lvHotelRoom = (PullToRefreshListView) root.findViewById(R.id.lvHourRoom);
        lvHotelRoom.setAdapter(mHotelRoomAdater);
        lvHotelRoom.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                String strStartTime=tvStartTime.getText().toString();
                if(strStartTime.equals(AppContext.getProperty(getString(R.string.current_time)))){

                }
                HttpClient.instance().getHourRoomTypeList(AppContext.getProperty(Config.KEY_HOUR_CHECKIN), tvStartTime.getText().toString(), strHotelId, new HttpCallBack() {
                    @Override
                    public void onSuccess(ResponseBean responseBean) {
                        lvHotelRoom.onRefreshComplete();
                        mListHotelRoom.clear();
                        try {
                            List<HotelRoom> temp = responseBean.getListDataWithGson(HotelRoom.class);
                            mHotelRoomAdater.addAll(temp);
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "没有可预订的房型，换个酒店试试吧~~", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), R.string.load_fail, Toast.LENGTH_SHORT).show();
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
        setStartTime();
        String strStaytime = AppContext.getProperty(Config.KEY_HOUR_CHECKIN);
        HttpClient.instance().getHourRoomTypeList(strStaytime, tvStartTime.getText().toString(), strHotelId, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                mListHotelRoom.clear();
                try {
                    List<HotelRoom> temp = responseBean.getListDataWithGson(HotelRoom.class);
                    mHotelRoomAdater.addAll(temp);
                } catch (Exception e) {
                    if (!isHidden())
                        Toast.makeText(getActivity(), "没有可预订的房型，换个酒店试试吧~~", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                if (!isHidden())
                    ProgressDialog.show(getActivity(), R.string.on_loading);
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
                if (!isHidden())
                    Toast.makeText(getActivity(), R.string.load_fail, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setStartTime() {
        if (AppContext.getProperty(Config.KEY_HOUR_STARTTIME) != null) {
            tvStartTime.setText(AppContext.getProperty(Config.KEY_HOUR_STARTTIME));
        } else {
            AppContext.setProperty(Config.KEY_HOUR_STARTTIME, "11:00");
            tvStartTime.setText("11:00");
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
                    String showStart = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日" + " " + DateUtil.getWeekByDay(strCheckInDate);
                    tvStartDay.setText(showStart);

                } else {
                    checkInDate = new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(checkInDate);
                    String showStart = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日" + " " + DateUtil.getWeekByDay(checkInDate);
                    strCheckInDate = DateUtil.getDateString(c);
                    AppContext.setProperty(Config.KEY_HOUR_CHECKIN, strCheckInDate);
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    tvStartDay.setText(showStart);

                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            Date checkInDate = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(checkInDate);
            String showStart = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日" + " " + DateUtil.getWeekByDay(checkInDate);
            strCheckInDate = DateUtil.getDateString(c);
            AppContext.setProperty(Config.KEY_HOUR_CHECKIN, strCheckInDate);
            tvStartDay.setText(showStart);
        }

    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.tvStayTime:
                i = new Intent(getActivity(), CalendarViewSingle.class);
                startActivity(i);
                break;
            case R.id.tvStartTime:
                popWindow = new SearchHourRoomPopWindow(getActivity());
                popWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        setStartTime();
                    }
                });
                break;
        }
    }
}

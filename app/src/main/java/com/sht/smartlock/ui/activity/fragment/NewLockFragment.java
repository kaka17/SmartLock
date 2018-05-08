package com.sht.smartlock.ui.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.model.booking.City;
import com.sht.smartlock.ui.activity.MainActivity;
import com.sht.smartlock.ui.activity.booking.SearchListActivity;
import com.sht.smartlock.ui.activity.booking.SelectCityActivity;
import com.sht.smartlock.ui.activity.booking.SelectDestinationActivity;
import com.sht.smartlock.ui.activity.booking.SelectPricePopWindow;
import com.sht.smartlock.ui.activity.hourroom.SearchHourRoomActivity;
import com.sht.smartlock.util.DateUtil;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.widget.calendar2.CalendarView;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NewLockFragment extends Fragment implements View.OnClickListener{

    private ImageView ivNearby,ivHour;
    private TextView tvCity;
    private LinearLayout linTime;
    private TextView tvInDate,tvInTime,tvOutTime,tvSearch,tvMoney;
    private SelectPricePopWindow popWindow;
    private  View view;
    String[] price = new String[]{"0", "150", "300", "500", "不限"};
    private ImageView ivSearch;
    private View reContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_new_lock, container, false);
        initView(view);
        setOnClicktener();
        return view;


    }

    private void  initView(View view){
        ivNearby = (ImageView) view.findViewById(R.id.ivNearby);
        ivHour = (ImageView) view.findViewById(R.id.ivHour);
        tvCity = (TextView) view.findViewById(R.id.tvCity);
        linTime = (LinearLayout) view.findViewById(R.id.linTime);
        tvInDate = (TextView) view.findViewById(R.id.tvInDate);
        tvInTime = (TextView) view.findViewById(R.id.tvInTime);
        tvOutTime = (TextView) view.findViewById(R.id.tvOutTime);
        tvSearch = (TextView) view.findViewById(R.id.tvSearch);
        tvMoney = (TextView) view.findViewById(R.id.tvMoney);

        ivSearch = (ImageView) view.findViewById(R.id.ivSearch);
        reContainer = view.findViewById(R.id.reContainer);
    }

    private void setOnClicktener(){
        ivNearby.setOnClickListener(this);
        ivHour.setOnClickListener(this);
        tvCity.setOnClickListener(this);
        tvSearch.setOnClickListener(this);
        linTime.setOnClickListener(this);
        tvMoney.setOnClickListener(this);
        //
        ivSearch.setOnClickListener(this);
        reContainer.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()){
            case R.id.ivNearby:
                i = new Intent(getActivity(), SearchListActivity.class);
                i.putExtra(Config.HOTEL_TYPE, "nearby");
                startActivity(i);
                break;
            case R.id.ivHour:
                i=new Intent(getActivity(), SearchHourRoomActivity.class);
                startActivity(i);
                break;
            case R.id.tvCity:
                i = new Intent(getActivity(), SelectCityActivity.class);
                startActivity(i);
                break;
            case R.id.tvSearch:
                i = new Intent(getActivity(), SelectDestinationActivity.class);
                startActivity(i);
                break;

            case R.id.linTime:
                i = new Intent(getActivity(), CalendarView.class);
                startActivity(i);
                break;
            case R.id.tvMoney:
                popWindow = new SelectPricePopWindow(getActivity(), null);
                popWindow.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        setPrice();
                    }
                });
                break;

            case R.id.ivSearch:
                if(tvCity.getText().toString().equals("")||tvCity.getText().toString().equals("选择城市")){
                    Toast.makeText(getActivity(), getString(R.string.please_select_city), Toast.LENGTH_SHORT).show();
                    return;
                }
                i = new Intent(getActivity(), SearchListActivity.class);
                i.putExtra(Config.HOTEL_TYPE, "search");
                startActivity(i);
                break;
            case R.id.reContainer:
                MainActivity.idMenu.closeMenu();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //设定选择日期
        setCalendarDate();
        setPrice();
        setCity();
        setDestination();
    }

    public void setDestination() {
        if (AppContext.getProperty(getString(R.string.booking_destination)) != null) {
            tvSearch.setText(AppContext.getProperty(getString(R.string.booking_destination)));
            tvSearch.setTextColor(getResources().getColor(R.color.text_font));
        }
    }
    public void setPrice() {
        if (AppContext.getProperty(getString(R.string.price_range_start)) != null) {
            int left = Integer.parseInt(AppContext.getProperty(getString(R.string.price_range_start)));
            int right = Integer.parseInt(AppContext.getProperty(getString(R.string.price_range_end)));
            tvMoney.setText(price[left] + "-" + price[right]);
            tvMoney.setTextColor(getResources().getColor(R.color.text_font));

        } else {
        }
    }
    public void setCity() {
        if (AppContext.getProperty(getString(R.string.booking_cityname)) != null) {
            tvCity.setText(AppContext.getProperty(getString(R.string.booking_cityname)));
            tvCity.setTextColor(getResources().getColor(R.color.LOCKTEXTCOLOR));
        } else {
            //定位
//            AppContext.setProperty(getString(R.string.booking_cityname), "深圳");
//            AppContext.setProperty(getString(R.string.booking_cityid), "1");
        }
        if (AppContext.getProperty(getString(R.string.booking_cityname))==null) {
            getHotelCity();
        }
    }

    public void setCalendarDate() {
        String strCheckInDate = AppContext.getProperty(Config.KEY_CHECKIN_DATE);
        String strCheckOutDate = AppContext.getProperty(Config.KEY_CHECKOUT_DATE);

        if (strCheckInDate != null && strCheckOutDate != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.YEAR_MONTH_DAY);
            Date checkInDate;
            Date checkOutDate;
            try {
                checkInDate = simpleDateFormat.parse(strCheckInDate);
                checkOutDate = simpleDateFormat.parse(strCheckOutDate);
                if (checkInDate.after(Calendar.getInstance().getTime())||strCheckInDate.equals(DateUtil.getDateStr(Calendar.getInstance().getTimeInMillis(),DateUtil.YEAR_MONTH_DAY))) {   //当前时间是否大于缓存时间

                    Calendar c = Calendar.getInstance();
                    c.setTime(checkInDate);
                    String showStart = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日";
                    c.setTime(checkOutDate);
                    String showEnd = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日";
                    tvInTime.setText(showStart+"  "+DateUtil.getWeekByDay(strCheckInDate));
//                    tvCheckInWeek.setText(DateUtil.getWeekByDay(strCheckInDate));
                    tvOutTime.setText(showEnd+"  "+DateUtil.getWeekByDay(strCheckOutDate));
//                    tvCheckOutWeek.setText(DateUtil.getWeekByDay(strCheckOutDate));
                    tvInDate.setText(getString(R.string.total) + DateUtil.getDayBetweenDate(checkInDate, checkOutDate) + getString(R.string.night));
                } else {
                    checkInDate = new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(checkInDate);
                    String showStart = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日";
                    strCheckInDate = DateUtil.getDateString(c);
                    AppContext.setProperty(Config.KEY_CHECKIN_DATE, strCheckInDate);
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    checkOutDate = c.getTime();
                    strCheckOutDate = DateUtil.getDateString(c);
                    AppContext.setProperty(Config.KEY_CHECKOUT_DATE, strCheckOutDate);
                    String showEnd = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日";
                    tvInTime.setText(showStart+"  "+DateUtil.getWeekByDay(strCheckInDate));
//                    tvCheckInWeek.setText(DateUtil.getWeekByDay(strCheckInDate));
                    tvOutTime.setText(showEnd+"  "+DateUtil.getWeekByDay(strCheckOutDate));
//                    tvCheckOutWeek.setText(DateUtil.getWeekByDay(strCheckOutDate));
                    tvInDate.setText(getString(R.string.total) + DateUtil.getDayBetweenDate(checkInDate, checkOutDate) + getString(R.string.night));
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
            AppContext.setProperty(Config.KEY_CHECKIN_DATE, strCheckInDate);
            c.add(Calendar.DAY_OF_MONTH, 1);
            Date checkOutDate = c.getTime();
            strCheckOutDate = DateUtil.getDateString(c);
            AppContext.setProperty(Config.KEY_CHECKOUT_DATE, strCheckOutDate);
            String showEnd = (c.get(Calendar.MONTH) + 1) + "月" + (c.get(Calendar.DAY_OF_MONTH)) + "日";
            tvInTime.setText(showStart+"  "+DateUtil.getWeekByDay(strCheckInDate));
//                    tvCheckInWeek.setText(DateUtil.getWeekByDay(strCheckInDate));
            tvOutTime.setText(showEnd+"  "+DateUtil.getWeekByDay(strCheckOutDate));
//                    tvCheckOutWeek.setText(DateUtil.getWeekByDay(strCheckOutDate));
            tvInDate.setText(getString(R.string.total) + DateUtil.getDayBetweenDate(checkInDate, checkOutDate) + getString(R.string.night));
        }

    }


    private void getHotelCity() {
        HttpClient.instance().getHotelCity(new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                LogUtil.log("paramss re" + responseBean.toString());
                ProgressDialog.disMiss();
                List<City> cityList = responseBean.getListDataWithGson(City.class);
                if (cityList.size()>0) {
                    if (AppContext.getProperty(Config.LOCATIONCITY) != null) {
                        if (cityList.contains(AppContext.getProperty(Config.LOCATIONCITY))) {
                            for (int i=0;i<cityList.size();i++){
                                if (cityList.get(i).getCaption().equals(AppContext.getProperty(Config.LOCATIONCITY))){
                                    AppContext.setProperty(getString(R.string.booking_cityname), cityList.get(i).getCaption());
                                    tvCity.setText(AppContext.getProperty(getString(R.string.booking_cityname)));
                                    AppContext.setProperty(getString(R.string.booking_cityid), cityList.get(i).getId());
                                }
                            }
                        } else {
                            tvCity.setText(cityList.get(0).getCaption());
                            AppContext.setProperty(getString(R.string.booking_cityname), cityList.get(0).getCaption());
                            AppContext.setProperty(getString(R.string.booking_cityid), cityList.get(0).getId());
                            Log.e("TAG", "---------->默认选第一个地址01");

                        }
                    }else {
                        tvCity.setText(cityList.get(0).getCaption());
                        Log.e("TAG", "---------->默认选第一个地址02");
                        AppContext.setProperty(getString(R.string.booking_cityid), cityList.get(0).getId());
                        AppContext.setProperty(getString(R.string.booking_cityname), cityList.get(0).getCaption());
                    }
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                LogUtil.log("onstart");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                LogUtil.log("onfinish");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                LogUtil.log("paramss 2 " + error + "\n" + message);
                LogUtil.log("onfail");
            }
        });

    }
}

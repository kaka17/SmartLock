package com.sht.smartlock.ui.activity.booking;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.util.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2015/9/25.
 */
public class SelectArriveTimePopWindow extends PopupWindow implements View.OnClickListener {
    ListView lvArriveTime;
    View mView;
    ImageView ivPopClose;
    List<arriveTime> arriveTimeList = new ArrayList<>();

    public SelectArriveTimePopWindow(final Activity context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.activity_booking_bill_pop, null);
        int startTime=12;//开始时间默认从12点开始，具体从服务器读取。
        try {
           startTime = Integer.parseInt(AppContext.getProperty(Config.KEY_ARRIVE_STARTTIME).split(":")[0]);
        }catch(Exception e){
        }
        for (int i = startTime; i <= 19; i++) {
            arriveTime arriveTime = new arriveTime();
            arriveTime.setArriveTime(i + ":00-" + (i + 1) + ":00");
            if(AppContext.getProperty(Config.KEY_ARRIVE_TIME).equals(i + ":00-" + (i + 1) + ":00"))
            {
                arriveTime.setIsSelected(true);
            }else {
                arriveTime.setIsSelected(false);
            }
            arriveTimeList.add(arriveTime);
        }
        arriveTime tempArriveTime=new arriveTime();
        tempArriveTime.setArriveTime("20:00-23:59");
        if(AppContext.getProperty(Config.KEY_ARRIVE_TIME).equals("20:00-23:59"))
        {
            tempArriveTime.setIsSelected(true);
        }else {
            tempArriveTime.setIsSelected(false);
        }
        arriveTimeList.add(tempArriveTime);
        lvArriveTime = (ListView) mView.findViewById(R.id.lvArriveTime);
        //  lvArriveTime.setAdapter(new ArrayAdapter<String>());
        // ArrayAdapter<String> adapter=new ArrayAdapter<String>(context,android.R.layout.simple_expandable_list_item_1,strArriveTime);
        ArriveTimeAdapter arriveTimeAdapter = new ArriveTimeAdapter(context, arriveTimeList);
        lvArriveTime.setAdapter(arriveTimeAdapter);
        lvArriveTime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             //  =DateUtil.stringToDate(AppContext.getProperty(Config.KEY_CHECKIN_DATE),DateUtil.YEAR_MONTH_DAY);
                String strCheckIn=AppContext.getProperty(Config.KEY_CHECKIN_DATE);
                SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                Date date= null;
                try {
                    date = sf.parse(strCheckIn);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(DateUtil.isToday(date.getTime())){
                    String s=arriveTimeList.get((int) id).getArriveTime();
                    Calendar calendarNow=Calendar.getInstance(Locale.CHINA);
//                    Date now=new Date();
//                    calendarNow.setTime(now);
                    Calendar temp=Calendar.getInstance(Locale.CHINA);
                    temp.set(Calendar.HOUR_OF_DAY,Integer.parseInt(s.split("-")[1].split(":")[0]));
                    temp.set(Calendar.MINUTE,Integer.parseInt(s.split("-")[1].split(":")[1]));
                    if(calendarNow.before(temp)){
                        AppContext.setProperty(Config.KEY_ARRIVE_TIME, arriveTimeList.get((int) id).getArriveTime());
                        dismiss();
                    }else{
                        Toast.makeText(context, R.string.select_wrong_time,Toast.LENGTH_SHORT).show();
                    }
                }else {
                    AppContext.setProperty(Config.KEY_ARRIVE_TIME, arriveTimeList.get((int) id).getArriveTime());
                    dismiss();
                }
            }
        });
        ivPopClose= (ImageView) mView.findViewById(R.id.ivPopClose);
        ivPopClose.setOnClickListener(this);
        setContentView(mView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        //setAnimationStyle(R.style.Animation_AppCompat_Dialog);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        setBackgroundDrawable(dw);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivPopClose:
                dismiss();
                break;
        }
    }

    public class ArriveTimeAdapter extends BaseAdapter {
        Context context;
        List<arriveTime> arriveTime;

        public ArriveTimeAdapter(Context context, List<arriveTime> arriveTime) {
            this.context = context;
            this.arriveTime = arriveTime;
        }

        @Override
        public int getCount() {
            return arriveTime.size();
        }

        @Override
        public Object getItem(int position) {
            return arriveTime.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.activity_booking_bill_pop_cell, null);
                convertView.setTag(new ListCell((TextView) convertView.findViewById(R.id.tvArriveTimeCell), (ImageView) convertView.findViewById(R.id.ivIsSelected)));
            }
            ListCell lc = (ListCell) convertView.getTag();
            arriveTime mArriveTime = arriveTime.get(position);
            lc.getTvTime().setText(mArriveTime.getArriveTime());

            if (mArriveTime.isSelected() == false) {
                lc.getIvSelected().setVisibility(View.INVISIBLE);
            } else {
                lc.getIvSelected().setVisibility(View.VISIBLE);
            }
//            int hour=0,minute=0;
//            Calendar c=Calendar.getInstance();
//            if(c.)
            return convertView;
        }
    }

    class ListCell {
        TextView tvTime;
        ImageView ivSelected;

        public ListCell(TextView tvTime, ImageView ivSelected) {
            this.tvTime = tvTime;
            this.ivSelected = ivSelected;
        }

        public TextView getTvTime() {
            return tvTime;
        }

        public void setTvTime(TextView tvTime) {
            this.tvTime = tvTime;
        }

        public ImageView getIvSelected() {
            return ivSelected;
        }

        public void setIvSelected(ImageView ivSelected) {
            this.ivSelected = ivSelected;
        }
    }

    class arriveTime {
        String arriveTime;
        boolean isSelected;

        public arriveTime() {

        }

        public arriveTime(String arriveTime, boolean isSelected) {
            this.arriveTime = arriveTime;
            this.isSelected = isSelected;
        }

        public String getArriveTime() {
            return arriveTime;
        }

        public void setArriveTime(String arriveTime) {
            this.arriveTime = arriveTime;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setIsSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }
    }
}

package com.sht.smartlock.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.ui.activity.myinterface.MyItemClickListener;
import com.sht.smartlock.ui.entity.WakeTimeEntity;
import com.sht.smartlock.util.DateUtil;

import java.util.List;

/**
 * Created by Administrator on 2017/4/14.
 */
public class WakeAdapter extends BaseAdapter {
    private Context context;
    private List<WakeTimeEntity> list;
    private MyItemClickListener onClick;
    public WakeAdapter(Context context,List<WakeTimeEntity> list,MyItemClickListener onClick){
        this.context=context;
        this.list=list;
        this.onClick=onClick;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHoilder mhoilder;
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.wake_item,parent,false);
            mhoilder=new ViewHoilder(convertView);
            convertView.setTag(mhoilder);
        }else {
            mhoilder= (ViewHoilder) convertView.getTag();
        }
        String timeStr = DateUtil.formatTimeByFormat(list.get(position).getMorning_call_time(),DateUtil.PATTERNBYEND);
        String[] split = timeStr.split(" ");
        String dayBetweenDate = DateUtil.getDayBetweenDatebyStr(DateUtil.getNow(DateUtil.PATTERN), list.get(position).getMorning_call_time());
        Log.d("Time","---------->"+DateUtil.getNow(DateUtil.PATTERN));
        mhoilder.tvTimeOut.setText(dayBetweenDate);
        if (split.length>0){
            mhoilder.tvTime.setText(split[0]);
            mhoilder.tvDate.setText(split[1]);
        }else {
        }
        mhoilder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(v,position);
            }
        });

        return convertView;
    }

    class  ViewHoilder{
        private TextView tvTime,tvDate,tvTimeOut;
        private ImageView ivDelete;

        public ViewHoilder(View convertView) {
            tvTime= (TextView) convertView.findViewById(R.id.tvTime);
            tvDate= (TextView) convertView.findViewById(R.id.tvDate);
            tvTimeOut= (TextView) convertView.findViewById(R.id.tvTimeOut);
            ivDelete= (ImageView) convertView.findViewById(R.id.ivDelete);
        }
    }

}

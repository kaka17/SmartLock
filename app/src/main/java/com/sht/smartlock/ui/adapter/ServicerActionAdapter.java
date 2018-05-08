package com.sht.smartlock.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.ui.entity.ServicerActionEntity;
import com.sht.smartlock.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/21.
 */
public class ServicerActionAdapter extends BaseAdapter {
    private Context context;
    private List<ServicerActionEntity> list;
    String PATTERN = "HH:mm yyyy/MM/dd";
    public ServicerActionAdapter(Context context,List<ServicerActionEntity> list){
        this.context=context;
        this.list=list;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoilder mhoilder;
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.servicer_action_item,parent,false);
            mhoilder=new ViewHoilder(convertView);
            convertView.setTag(mhoilder);
        }else {
            mhoilder= (ViewHoilder) convertView.getTag();
        }

        mhoilder.tvReceive.setText(list.get(position).getAction());
        mhoilder.tvReceiveTime.setText(DateUtil.formatTimeByFormat(list.get(position).getAction_time(), PATTERN));
        if (list.get(position).getAttendant_name()==null||list.get(position).getAttendant_name().equals("")||list.get(position).getAttendant_name().equals("null")){
            mhoilder.tvServiceror.setVisibility(View.GONE);
        }else {
            mhoilder.tvServiceror.setVisibility(View.VISIBLE);
            mhoilder.tvServiceror.setText(context.getString(R.string.Serviceror) + " " + list.get(position).getAttendant_name());
        }
        if (list.size()-1==position){
            mhoilder.viewLine02.setVisibility(View.GONE);
        }else {
            mhoilder.viewLine02.setVisibility(View.VISIBLE);
        }
        if (position==0){
            mhoilder.ivReceive.setImageResource(R.drawable.pic_dot_green);
            mhoilder.tvReceive.setTextColor(context.getResources().getColor(R.color.TaskIsIng));
        }
        return convertView;
    }
    class ViewHoilder{

        private RelativeLayout  relReceive;
        private TextView tvReceive, tvReceiveTime, tvServiceror;
        private ImageView ivReceive;
        private View viewLine02;
        public ViewHoilder(View view) {
            relReceive = (RelativeLayout) view.findViewById(R.id.relReceive);
            ivReceive = (ImageView) view.findViewById(R.id.ivReceive);
            tvReceive = (TextView) view.findViewById(R.id.tvReceive);
            tvReceiveTime = (TextView) view.findViewById(R.id.tvReceiveTime);
            tvServiceror = (TextView) view.findViewById(R.id.tvServiceror);
            viewLine02=view.findViewById(R.id.viewLine02);

        }
    }
}

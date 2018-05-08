package com.sht.smartlock.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.ui.entity.OtherOderEntity;

import java.util.List;

/**
 * Created by Administrator on 2017/5/11.
 */
public class OtherOrderAdapter extends BaseAdapter {
    private Context context;
    private List<OtherOderEntity> list;
    public OtherOrderAdapter(Context context, List<OtherOderEntity> list){
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
        ViewHoilder holider;
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.ortherorder_item,parent,false);
            holider=new ViewHoilder(convertView);
            convertView.setTag(holider);
        }else {
            holider= (ViewHoilder) convertView.getTag();
        }
        holider.tvOrderType.setText("消费类型："+list.get(position).getCaption());
        holider.tvMoney01.setText("￥"+list.get(position).getTotal());

        return convertView;
    }
    class ViewHoilder{

        private TextView tvOrderType,tvMoney01;
        public ViewHoilder(View view) {
            tvOrderType = (TextView) view.findViewById(R.id.tvOrderType);
            tvMoney01 = (TextView) view.findViewById(R.id.tvMoney01);
        }
    }
}

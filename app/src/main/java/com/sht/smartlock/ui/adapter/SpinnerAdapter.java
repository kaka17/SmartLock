package com.sht.smartlock.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.ui.entity.MyLockEntity;

import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */
public class SpinnerAdapter extends BaseAdapter {
    private List<MyLockEntity> mlist;
    private Context context;
    public  SpinnerAdapter(List<MyLockEntity> mlist,Context context){
        this.context=context;
        this.mlist=mlist;
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SpViewHoilder mhoilder;
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.spinner_item,parent,false);
            mhoilder=new SpViewHoilder(convertView);
            convertView.setTag(mhoilder);
        }else {
            mhoilder= (SpViewHoilder) convertView.getTag();
        }
             mhoilder.tvName.setText(mlist.get(position).getHotel_caption());
        return convertView;
    }

    class SpViewHoilder{
        private TextView tvName;
        private SpViewHoilder(View view){
            tvName= (TextView) view.findViewById(R.id.tvName);
        }
    }
}

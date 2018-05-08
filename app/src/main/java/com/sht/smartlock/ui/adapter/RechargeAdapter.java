package com.sht.smartlock.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.model.booking.PayChannel;

import java.util.List;

/**
 * Created by Administrator on 2016/1/22.
 */
public class RechargeAdapter extends BaseAdapter {

    private Context context;
    private List<PayChannel> mListPayChannel;
    public RechargeAdapter(Context context,List<PayChannel> mListPayChannel){
        this.context=context;
        this.mListPayChannel=mListPayChannel;
    }

    @Override
    public int getCount() {
        return mListPayChannel.size();
    }

    @Override
    public Object getItem(int position) {
        return mListPayChannel.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mholder;
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.recharge_itempay,parent,false);
            mholder=new ViewHolder(convertView);
            convertView.setTag(mholder);
        }else {
            mholder= (ViewHolder) convertView.getTag();
        }
        return convertView;
    }
    class ViewHolder{
        ImageView ivChannelPic;
        TextView tvChannleName;
        TextView tvChannelIntroduction;
        ImageView ivIsSelected;
        private ViewHolder(View view){

        }
    }
}

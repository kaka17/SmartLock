package com.sht.smartlock.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.ui.entity.OtherOderInfoEntity;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Administrator on 2017/5/11.
 */
public class OtherOrderInfoAdapter extends BaseAdapter {

    private Context context;
    private List<OtherOderInfoEntity> list;
    private DecimalFormat formatOld01 = new DecimalFormat("#0.00");
    public OtherOrderInfoAdapter(Context context,List<OtherOderInfoEntity> list){
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
            convertView= LayoutInflater.from(context).inflate(R.layout.otherorderinfo_item,parent,false);
            mhoilder=new ViewHoilder(convertView);
            convertView.setTag(mhoilder);
        }else {
            mhoilder= (ViewHoilder) convertView.getTag();
        }
        mhoilder.tvNumber.setText(list.get(position).getCode()+"");
        mhoilder.tvName.setText(list.get(position).getCaption());
        mhoilder.tvNum.setText("数量 "+list.get(position).getQuantity());
        mhoilder.tvMoney.setText("￥"+formatOld01.format(list.get(position).getPrice()));
        return convertView;
    }
    class ViewHoilder{
        private TextView tvNumber,tvName,tvNum,tvMoney;

        public ViewHoilder(View view) {
            tvNumber= (TextView) view.findViewById(R.id.tvNumber);
            tvName= (TextView) view.findViewById(R.id.tvName);
            tvNum= (TextView) view.findViewById(R.id.tvNum);
            tvMoney= (TextView) view.findViewById(R.id.tvMoney);
        }
    }
}

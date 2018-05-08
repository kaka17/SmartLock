package com.sht.smartlock.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.model.BillInfo;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Administrator on 2016/8/22.
 */
public class MyBillAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private Context context;
    private List<BillInfo> billInfoList;


    public MyBillAdapter(Context context,List<BillInfo> billInfoList){
        this.context = context;
        this.billInfoList = billInfoList;
        layoutInflater =LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return billInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.bill_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tvBillPrice = (TextView)convertView.findViewById(R.id.tvBillPrice);
            viewHolder.tvBillHotelName = (TextView)convertView.findViewById(R.id.tvBillHotelName);
            viewHolder.tvBillState = (TextView)convertView.findViewById(R.id.tvBillState);
            viewHolder.tvBillTime = (TextView)convertView.findViewById(R.id.tvBillTime);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)convertView.getTag();
        viewHolder.tvBillPrice.setText("￥"+billInfoList.get(position).getTotal());
        viewHolder.tvBillHotelName.setText(billInfoList.get(position).getCaption());
//        viewHolder.tvBillState.setText();
        viewHolder.tvBillTime.setText(billInfoList.get(position).getTime().substring(0,10));
        return convertView;
    }

    class ViewHolder{
        private TextView tvBillPrice,tvBillHotelName;
        private TextView tvBillState,tvBillTime;
    }

}

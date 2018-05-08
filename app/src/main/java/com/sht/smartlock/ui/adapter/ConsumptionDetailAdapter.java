package com.sht.smartlock.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.util.DateUtil;
import com.sht.smartlock.util.LogUtil;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/8/24.
 */
public class ConsumptionDetailAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private Context context;
    private List<Map<String,String>> list;
    private boolean isDay=true;

    public ConsumptionDetailAdapter(Context context,List<Map<String,String>> list){
        this.context = context;
        this.list = list;
        layoutInflater =LayoutInflater.from(context);
    }

    public void isDayOrHour(boolean isDay){
        this.isDay=isDay;
    }

    @Override
    public int getCount() {
        return list.size();
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
            convertView = layoutInflater.inflate(R.layout.consumptiondetail_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tvConsumptionName = (TextView) convertView.findViewById(R.id.tvConsumptionName);
            viewHolder.tvConsumptionNum = (TextView) convertView.findViewById(R.id.tvConsumptionNum);
            viewHolder.tvConsumptionPrice = (TextView) convertView.findViewById(R.id.tvConsumptionPrice);
            viewHolder.tvConsumptionData = (TextView) convertView.findViewById(R.id.tvConsumptionData);
            viewHolder.tvConsumptionTime = (TextView) convertView.findViewById(R.id.tvConsumptionTime);
            viewHolder.vLineBill1Detail1 = (View) convertView.findViewById(R.id.vLineBill1Detail1);
            viewHolder.linTime = (LinearLayout) convertView.findViewById(R.id.linTime);
            //
            viewHolder.linPei = (LinearLayout) convertView.findViewById(R.id.linPei);
            viewHolder.tvPeiName = (TextView) convertView.findViewById(R.id.tvPeiName);
            viewHolder.tvPeiPrice = (TextView) convertView.findViewById(R.id.tvPeiPrice);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)convertView.getTag();
        viewHolder.tvConsumptionName.setText(list.get(position).get("caption"));
        Pattern pattern = Pattern.compile("^[-+]?[0-9]");
        if(pattern.matcher(list.get(position).get("quantity")).matches()){ //数字  不包含小数
            viewHolder.tvConsumptionNum.setText(list.get(position).get("quantity")+"份");
            viewHolder.tvConsumptionPrice.setText("￥"+Double.parseDouble(list.get(position).get("price"))*Integer.parseInt(list.get(position).get("quantity")));


        } else {//非数字
            viewHolder.tvConsumptionNum.setText(list.get(position).get("quantity"));
            viewHolder.tvConsumptionPrice.setText("￥"+list.get(position).get("price"));
        }
//        if(!list.get(position).get("quantity").equals("")){
//
//        }else {
//
//        }

        if (isDay) {
            viewHolder.tvConsumptionTime.setText(list.get(position).get("create_time"));
            viewHolder.linTime.setVisibility(View.VISIBLE);
        }else {
            viewHolder.tvConsumptionTime.setText("入：" + DateUtil.formatTime05(list.get(position).get("hour_start")) + "  离：" + DateUtil.formatTime05(list.get(position).get("hour_end")) + " 共："
                    + DateUtil.getHours(DateUtil.getLongTime(list.get(position).get("hour_end")), DateUtil.getLongTime(list.get(position).get("hour_start")))+"/h");
        }

        try {
            if (list.get(position).get("charge")!=null){
                String charge = list.get(position).get("charge");
                if (charge.equals("1")){
                    viewHolder.linTime.setVisibility(View.GONE);
                    viewHolder.vLineBill1Detail1.setVisibility(View.GONE);
                    viewHolder.tvConsumptionNum.setVisibility(View.VISIBLE);
                    Log.e("TAAG","------------111>");
                }else {
                    viewHolder.linTime.setVisibility(View.VISIBLE);

                }
            }
            Log.e("TAAG", "------------222>"+list.get(position).get("charge"));
            if (list.get(position).get("service_charge")!=null){
                String service_charge = list.get(position).get("service_charge");
                if (service_charge.equals("1")){
                    viewHolder.linTime.setVisibility(View.VISIBLE);
                    viewHolder.tvConsumptionNum.setVisibility(View.INVISIBLE);
                    viewHolder.vLineBill1Detail1.setVisibility(View.VISIBLE);
                    Log.e("TAAG", "------------222>");
                }
            }
        }catch (Exception e){
            Log.e("TAG","-------------h账单>"+e.toString());
        }


        return convertView;
    }

    class ViewHolder{
        private TextView tvConsumptionName;
        private TextView tvConsumptionNum;
        private TextView tvConsumptionPrice;
        private TextView tvConsumptionData;
        private TextView tvConsumptionTime;
        private View vLineBill1Detail1;
        private LinearLayout linTime;

        private TextView tvPeiName;
        private TextView tvPeiPrice;
        private LinearLayout linPei;

    }

}

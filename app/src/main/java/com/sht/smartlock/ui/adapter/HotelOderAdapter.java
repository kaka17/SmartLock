package com.sht.smartlock.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.model.HotelOrder;
import com.sht.smartlock.ui.entity.HetelListEntity;
import com.sht.smartlock.util.DateUtil;
import com.sht.smartlock.util.LogUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//import u.aly.ak;

/**
 * Created by Administrator on 2015/10/8.
 */
public class HotelOderAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private Context context;
    private List<HetelListEntity> hotelOrderList;

    public HotelOderAdapter(Context context,List<HetelListEntity> hotelOrderList){
        this.context = context;
        this.hotelOrderList = hotelOrderList;
        layoutInflater =LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return hotelOrderList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view == null){
            view = layoutInflater.inflate(R.layout.hotel_order_item,null);
            viewHolder = new ViewHolder();
            viewHolder.tv_Order_Status = (TextView)view.findViewById(R.id.tv_Order_Status);
//            viewHolder.tv_Front_Pay = (TextView)view.findViewById(R.id.tv_Front_Pay);
            viewHolder.tv_Hotel_Name = (TextView)view.findViewById(R.id.tv_Hotel_Name);
            viewHolder.tv_Room_Type = (TextView)view.findViewById(R.id.tv_Room_Type);
            viewHolder.tv_Checkin_Time = (TextView)view.findViewById(R.id.tv_Checkin_Time);
            viewHolder.tv_CheckinState = (TextView)view.findViewById(R.id.tv_CheckinState);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();

        //订单状态
//        if (hotelOrderList.get(i).getState().equals("1")) {
//                viewHolder.tv_Order_Status.setText("已提交");
//        }else if(hotelOrderList.get(i).getState().equals("2")||
//                hotelOrderList.get(i).getState().equals("3")){
//            viewHolder.tv_Order_Status.setText(R.string.hotel_state_already_check_in);
//        }else if(hotelOrderList.get(i).getState().equals("4")){
//            viewHolder.tv_Order_Status.setText(R.string.hotel_state_already_shop);
//        }else if(hotelOrderList.get(i).getState().equals("5")){
//            viewHolder.tv_Order_Status.setText(R.string.hotel_state_has_canceled);
//
//            if(!hotelOrderList.get(i).getPay_state().equals("-1")||
//                    hotelOrderList.get(i).getPay_type().equals("0")){
//                viewHolder.tv_Order_Status.setText("已退订");
//            }
//        }else if(hotelOrderList.get(i).getState().equals("6")){
//            viewHolder.tv_Order_Status.setText("已过期");
//        }else if(hotelOrderList.get(i).getState().equals("0")){
//            viewHolder.tv_Order_Status.setText(R.string.hotel_state_has_canceled);
//        }

        switch (hotelOrderList.get(i).getState()){
            case "-1":
                viewHolder.tv_CheckinState.setText("已删除");
                break;
            case "1":
                viewHolder.tv_CheckinState.setText("已提交");
                break;
            case "2":
                viewHolder.tv_CheckinState.setText("已入住");
                break;
            case "3":
                viewHolder.tv_CheckinState.setText("已退房");
                break;
            case "4":
                viewHolder.tv_CheckinState.setText("已完成");
                break;
            case "5":
                viewHolder.tv_CheckinState.setText("已取消");
                break;
            case "6":
                viewHolder.tv_CheckinState.setText("已过期");
                break;
            default:
                viewHolder.tv_CheckinState.setText("");
                break;

        }
        //支付状态
        viewHolder.tv_Order_Status.setVisibility(View.VISIBLE);
        switch (hotelOrderList.get(i).getPay_state()){
            case "0":
                viewHolder.tv_Order_Status.setText("未支付￥"+hotelOrderList.get(i).getPrice());
                if (hotelOrderList.get(i).getState().equals("4")){
                    viewHolder.tv_Order_Status.setVisibility(View.GONE);
                }
                break;
            case "1":
                viewHolder.tv_Order_Status.setText("￥"+hotelOrderList.get(i).getPrice());
                break;
            case "2":
                if (hotelOrderList.get(i).getState().equals("4")){
                    viewHolder.tv_Order_Status.setText("￥"+hotelOrderList.get(i).getPrice());
                }else {
                    viewHolder.tv_Order_Status.setText("退款成功￥"+hotelOrderList.get(i).getPrice());
                }
                break;
            case "3":
                viewHolder.tv_Order_Status.setText("未支付￥"+hotelOrderList.get(i).getPrice());
                if (hotelOrderList.get(i).getState().equals("4")){
                    viewHolder.tv_Order_Status.setVisibility(View.GONE);
                }
                break;
            case "4":
                viewHolder.tv_Order_Status.setText("未退款￥"+hotelOrderList.get(i).getPrice());
                break;
            case "5":
                viewHolder.tv_Order_Status.setText("退款中￥"+hotelOrderList.get(i).getPrice());
                break;

            default:
                viewHolder.tv_Order_Status.setText("");
                break;

        }
//        viewHolder.tv_Front_Pay.setText(hotelOrderList.get(i).getPrice());
        viewHolder.tv_Hotel_Name.setText(hotelOrderList.get(i).getHotel_caption());
        viewHolder.tv_Room_Type.setText(hotelOrderList.get(i).getCaption());

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try{
            Date d1 = df.parse(hotelOrderList.get(i).getEnd_date());
            Date d2 = df.parse(hotelOrderList.get(i).getStart_date());

            long diff = d1.getTime() - d2.getTime();//这样得到的差值是微秒级别
            long days = diff / (1000 * 60 * 60 * 24);

            long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
            long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
            if(hotelOrderList.get(i).getTerm_type().equals("1")){
                viewHolder.tv_Checkin_Time.setText("入:"+hotelOrderList.get(i).getStart_date()+"\t"+ DateUtil.getWeekByDay02(hotelOrderList.get(i).getStart_date())+
                        "\t离:"+hotelOrderList.get(i).getEnd_date()+DateUtil.getWeekByDay02(hotelOrderList.get(i).getEnd_date())+"\t"+"共"+days+"天");
            }else{
                viewHolder.tv_Checkin_Time.setText("入:"+hotelOrderList.get(i).getStart_date()+"\t"+ DateUtil.getWeekByDay02(hotelOrderList.get(i).getStart_date())+
                        "\t离:"+hotelOrderList.get(i).getEnd_date() + DateUtil.getWeekByDay02(hotelOrderList.get(i).getEnd_date())+"\t"+"共"+hotelOrderList.get(i).getHour_term()+"小时");
            }

            System.out.println("" + days + "天" + hours + "小时" + minutes + "分");
        }catch (Exception e){
            LogUtil.log(e.toString());
        }
        return view;
    }

    class ViewHolder{
        private TextView tv_Order_Status;
        private TextView tv_Front_Pay;
        private TextView tv_Hotel_Name;
        private TextView tv_Room_Type;
        private TextView tv_Checkin_Time;
        private TextView tv_CheckinState;
    }


}

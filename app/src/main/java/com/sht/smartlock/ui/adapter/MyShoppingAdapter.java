package com.sht.smartlock.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.model.ShoppingInfo;
import com.sht.smartlock.ui.entity.OrderListEntity;
import com.sht.smartlock.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2015/10/10.
 */
public class MyShoppingAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private Context context;
    private List<ShoppingInfo> shoppingInfos;
    private List<OrderListEntity> listItem;
    public MyShoppingAdapter(Context context,List<OrderListEntity> listItem){
        this.context = context;
//        this.shoppingInfos = orderingInfoList;
        this.listItem = listItem;
        layoutInflater =LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listItem.size();
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
            view = layoutInflater.inflate(R.layout.myordering_item,null);
            viewHolder = new ViewHolder();
            viewHolder.myOrderingName = (TextView)view.findViewById(R.id.myOrderingName);
            viewHolder.tvMyOrdering = (TextView)view.findViewById(R.id.tvMyOrdering);
            viewHolder.tvTotal = (TextView)view.findViewById(R.id.tvTotal);
            viewHolder.tvPaymentMethod = (TextView)view.findViewById(R.id.tvPaymentMethod);
            viewHolder.tvDateTime = (TextView)view.findViewById(R.id.tvDateTime);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        viewHolder.myOrderingName.setText(listItem.get(i).getCaption());
        viewHolder.tvTotal.setText("共计:"+listItem.get(i).getTotal());

        switch (listItem.get(i).getPay_channel()){
            case "1":
                switch (listItem.get(i).getService_pay_state()){
                    case "0":
                        viewHolder.tvPaymentMethod.setText("现金");
                        break;
                    case "1":
                        viewHolder.tvPaymentMethod.setText("现金");
                        break;
                    case "2":
                        viewHolder.tvPaymentMethod.setText("现金");
                        break;
                    case "3":
                        viewHolder.tvPaymentMethod.setText("现金");
                        break;
                    case "4":
                        viewHolder.tvPaymentMethod.setText("现金");
                        break;

                }
                break;
            case "2":
                switch (listItem.get(i).getService_pay_state()){
                    case "0":
                        viewHolder.tvPaymentMethod.setText("刷卡");
                        break;
                    case "1":
                        viewHolder.tvPaymentMethod.setText("刷卡");
                        break;
                    case "2":
                        viewHolder.tvPaymentMethod.setText("刷卡");
                        break;
                    case "3":
                        viewHolder.tvPaymentMethod.setText("刷卡");
                        break;
                    case "4":
                        viewHolder.tvPaymentMethod.setText("刷卡");
                        break;

                }
                break;
            case "3":
                switch (listItem.get(i).getService_pay_state()){
                    case "0":
                        viewHolder.tvPaymentMethod.setText("支付宝");
                        break;
                    case "1":
                        viewHolder.tvPaymentMethod.setText("支付宝");
                        break;
                    case "2":
                        viewHolder.tvPaymentMethod.setText("支付宝");
                        break;
                    case "3":
                        viewHolder.tvPaymentMethod.setText("支付宝");
                        break;
                    case "4":
                        viewHolder.tvPaymentMethod.setText("支付宝");
                        break;

                }
                break;
            case "4":
                switch (listItem.get(i).getService_pay_state()){
                    case "0":
                        viewHolder.tvPaymentMethod.setText("微信");
                        break;
                    case "1":
                        viewHolder.tvPaymentMethod.setText("微信");
                        break;
                    case "2":
                        viewHolder.tvPaymentMethod.setText("微信");
                        break;
                    case "3":
                        viewHolder.tvPaymentMethod.setText("微信");
                        break;
                    case "4":
                        viewHolder.tvPaymentMethod.setText("微信");
                        break;

                }
                break;
            case "5":
                switch (listItem.get(i).getService_pay_state()){
                    case "0":
                        viewHolder.tvPaymentMethod.setText("银联");
                        break;
                    case "1":
                        viewHolder.tvPaymentMethod.setText("银联");
                        break;
                    case "2":
                        viewHolder.tvPaymentMethod.setText("银联");
                        break;
                    case "3":
                        viewHolder.tvPaymentMethod.setText("银联");
                        break;
                    case "4":
                        viewHolder.tvPaymentMethod.setText("银联");
                        break;

                }
                break;
            case "6":
                switch (listItem.get(i).getService_pay_state()){
                    case "0":
                        viewHolder.tvPaymentMethod.setText("余额");
                        break;
                    case "1":
                        viewHolder.tvPaymentMethod.setText("余额");
                        break;
                    case "2":
                        viewHolder.tvPaymentMethod.setText("余额");
                        break;
                    case "3":
                        viewHolder.tvPaymentMethod.setText("余额");
                        break;
                    case "4":
                        viewHolder.tvPaymentMethod.setText("余额");
                        break;

                }
                break;
            case "7":
                switch (listItem.get(i).getService_pay_state()){
                    case "0":
                        viewHolder.tvPaymentMethod.setText("协议");
                        break;
                    case "1":
                        viewHolder.tvPaymentMethod.setText("协议");
                        break;
                    case "2":
                        viewHolder.tvPaymentMethod.setText("协议");
                        break;
                    case "3":
                        viewHolder.tvPaymentMethod.setText("协议");
                        break;
                    case "4":
                        viewHolder.tvPaymentMethod.setText("协议");
                        break;

                }
                break;
            case "null":
                switch (listItem.get(i).getService_pay_state()){
                    case "0":
                        viewHolder.tvPaymentMethod.setText("房费");
                        break;
                    case "1":
                        viewHolder.tvPaymentMethod.setText("房费");
                        break;
                    case "2":
                        viewHolder.tvPaymentMethod.setText("房费");
                        break;
                    case "3":
                        viewHolder.tvPaymentMethod.setText("房费");
                        break;
                    case "4":
                        viewHolder.tvPaymentMethod.setText("房费");
                        break;

                }
                break;

        }

        switch (listItem.get(i).getState()){
            case "-1":
                viewHolder.tvPaymentMethod.setText("订单已取消");
                break;
            case "1":
                if (listItem.get(i).getService_pay_state().equals("0")){
                    viewHolder.tvPaymentMethod.setText("房费");
                }
                break;
        }
//        viewHolder.tvPaymentMethod.setText(listItem.get(i).get("pay_type"));
        viewHolder.tvDateTime.setText(listItem.get(i).getCreate_time());
        try {
            JSONArray jsonArray=new JSONArray(listItem.get(i).getItem());
            String caption = jsonArray.getJSONObject(0).getString("caption");
            viewHolder.tvMyOrdering.setText(caption+"等"+jsonArray.length()+"件商品");
        } catch (JSONException e) {
            e.printStackTrace();
        }





//        if(listItem.get(i).get("state").equals("0")){
//            viewHolder.tvPaymentMethod.setText(R.string.not_paid);
//        }else if(listItem.get(i).get("state").equals("1")||
//                listItem.get(i).get("state").equals("2")||
//                listItem.get(i).get("state").equals("3")){
//            if(listItem.get(i).get("pay_state").equals("-1")){
//                viewHolder.tvPaymentMethod.setText(R.string.order_info_pay_01);
//            }else if(listItem.get(i).get("pay_state").equals("2")){
//                if(listItem.get(i).get("pay_channel").equals("1")){
//                    viewHolder.tvPaymentMethod.setText(R.string.Order_pay_type01);
//                }else if(listItem.get(i).get("pay_channel").equals("2")){
//                    viewHolder.tvPaymentMethod.setText(R.string.Order_pay_type02);
//                }else if(listItem.get(i).get("pay_channel").equals("3")||
//                        listItem.get(i).get("pay_channel").equals("4")||
//                        listItem.get(i).get("pay_channel").equals("5")){
//                    viewHolder.tvPaymentMethod.setText(R.string.order_info_pay_02);
//                }
//            }
//        }else if(listItem.get(i).get("state").equals("-1")){
//            viewHolder.tvPaymentMethod.setText(R.string.order_submit_Order_isInfo);
//        }


//        if(listItem.get(i).get("pay_channel").equals("1")){
//            viewHolder.tvPaymentMethod.setText("在线支付");
//        }else if(listItem.get(i).get("pay_channel").equals("2")){
//            viewHolder.tvPaymentMethod.setText("房费");
//        }

        return view;
    }

    class ViewHolder{
        private TextView myOrderingName;
        private TextView tvMyOrdering;
        private TextView tvTotal;
        private TextView tvPaymentMethod;
        private TextView tvDateTime;
    }

}

package com.sht.smartlock.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.ui.activity.myinterface.ListItemClickHelp;
import com.sht.smartlock.ui.ordering.entity.Product;
import com.sht.smartlock.ui.ordering.orderingutil.NumberUtils;

import java.util.List;

/**
 * Created by Administrator on 2015/10/8.
 */
public class Submit_Order_Adapter extends BaseAdapter {
    private List<Product> myShopList;
    private Context context;
    private ListItemClickHelp clickHelp;


    public Submit_Order_Adapter(List<Product> myShopList,Context context){
        this.myShopList=myShopList;
        this.context=context;


    }
    public void setOnAddAndSubClicklistenter(ListItemClickHelp clickHelp){
        this.clickHelp=clickHelp;
    }

    @Override
    public int getCount() {
        return myShopList==null ? 0 : myShopList.size();
    }

    @Override
    public Object getItem(int position) {
        return myShopList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder mholder;
        final Product product=myShopList.get(position);
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.submit_order_item,parent,false);
            mholder=new ViewHolder(convertView);
            convertView.setTag(mholder);
        }else {
            mholder= (ViewHolder) convertView.getTag();
        }
        mholder.tv_Name.setText(product.getName());
        mholder.tv_Num.setText("X"+product.getNum());
        double price= NumberUtils.toDouble(NumberUtils.format(product.getPrices()));
        mholder.tv_Money.setText("￥"+NumberUtils.format(price *product.getNum()));

        return convertView;
    }
    public class  ViewHolder{
        private TextView tv_Name,tv_Num,tv_Money;


        private  ViewHolder(View  v){
            tv_Name= (TextView) v.findViewById(R.id.tv_Name);
            tv_Num= (TextView) v.findViewById(R.id.tv_Num);
            tv_Money= (TextView) v.findViewById(R.id.tv_Money);
        }

    }
}

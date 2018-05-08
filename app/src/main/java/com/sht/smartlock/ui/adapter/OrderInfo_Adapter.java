package com.sht.smartlock.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.ui.activity.OrderFoodActivity;
import com.sht.smartlock.ui.activity.OrderingInfoActivity;
import com.sht.smartlock.ui.activity.myinterface.ListItemClickHelp;
import com.sht.smartlock.ui.ordering.entity.Product;
import com.sht.smartlock.ui.ordering.orderingutil.NumberUtils;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2015/9/23.
 */
public class OrderInfo_Adapter extends BaseAdapter {

    private List<Product> myShopList;
    private Context context;
    private ListItemClickHelp clickHelp;


    public OrderInfo_Adapter(List<Product> myShopList,Context context){
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
            convertView= LayoutInflater.from(context).inflate(R.layout.order_info_item,parent,false);
            mholder=new ViewHolder(convertView);
            convertView.setTag(mholder);
        }else {
            mholder= (ViewHolder) convertView.getTag();
        }
        mholder.tv_Name.setText(product.getName());
        mholder.tv_Num.setText("X "+product.getNum()+"");
        double price=NumberUtils.toDouble(NumberUtils.format(product.getPrices()));
        mholder.tv_Money.setText("￥"+NumberUtils.format(price *product.getNum()));
        final int pos = position;
        final View view = convertView;
        final int img_order_sub_id = mholder.img_order_sub.getId();
        final int img_order_add_id = mholder.img_order_add.getId();
//减少按钮
        mholder.img_order_sub.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (product.getNum()-1==0){
                    product.setNum(product.getNum() - 1);
                    myShopList.remove(position);
              }else {
                   product.setNum(product.getNum() - 1);
                }
                notifyDataSetChanged();
                if (clickHelp!=null){
                    clickHelp.onClick(view,parent,position,img_order_sub_id);
                }
            }
        });
        //增加
        mholder.img_order_add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                product.setNum(product.getNum() + 1);
                   notifyDataSetChanged();
                if (clickHelp!=null){
                  clickHelp.onClick(view, parent, position, img_order_add_id);
                }
            }
        });

        return convertView;
    }
    public class  ViewHolder{
       private  TextView tv_Name,tv_Num,tv_Money;
        private ImageView img_order_sub,img_order_add;

        private  ViewHolder(View  v){
            tv_Name= (TextView) v.findViewById(R.id.tv_Name);
            tv_Num= (TextView) v.findViewById(R.id.tv_Num);
            tv_Money= (TextView) v.findViewById(R.id.tv_Money);
            img_order_add= (ImageView) v.findViewById(R.id.img_order_add);
            img_order_sub= (ImageView) v.findViewById(R.id.img_order_sub);


        }

    }

}

package com.sht.smartlock.ui.ordering.apdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.helper.MySQLiteOpenHelper;
import com.sht.smartlock.ui.activity.OrderFoodActivity;
import com.sht.smartlock.ui.ordering.entity.Product;
import com.sht.smartlock.ui.ordering.entity.ProductType;
import com.sht.smartlock.ui.ordering.orderingutil.NumberUtils;

import org.apache.commons.collections.Bag;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2015/9/18.
 */
public class MyOrdering_Adapter extends BaseAdapter {

    private Context context;
    private Bag productList;
    private  List<Product> myShopList;
    private OrderFoodActivity orderActivity;

    private MySQLiteOpenHelper mydbHelper = null;
    private boolean flag = false;
    private PopupWindow upWindow;
    private String tab;

    public MyOrdering_Adapter(Context context,Bag productList,List<Product> myShopList,OrderFoodActivity orderActivity,MySQLiteOpenHelper mydbHelper,PopupWindow upWindow,String tab){
        this.context=context;
        this.productList=productList;
        this.myShopList=myShopList;
        this.orderActivity=orderActivity;

        this.mydbHelper=mydbHelper;
        this.upWindow=upWindow;
        this.tab=tab;

    }



    @Override
    public int getCount() {
        return myShopList.size() ;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
    final Product product=myShopList.get(position);
    final ViewHolder mholder;
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.ordering_item,parent,false);
            mholder=new ViewHolder(convertView);
            convertView.setTag(mholder);

        }else {
            mholder= (ViewHolder) convertView.getTag();
        }
        int num=myShopList.get(position).getNum();
        mholder.tv_name.setText(myShopList.get(position).getName());
        mholder.tv_Num.setText(num + "");

        String price=NumberUtils.format(myShopList.get(position).getPrices());
       double price_d= NumberUtils.toDouble(price)*num;

        mholder.tv_Money.setText("￥"+NumberUtils.format(price_d));

        //减少按钮
        mholder.iv_Des.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                //数据库存储
                if (product.getNum()-1==0){
                    //数据库删除
                    if (tab.equals("1")){//购物
//                        String sql="delete from shopping_words where name='"+product.getName()+"'";
                        String sql="delete from shopping_words where ID_item='"+product.getID()+"'";
                        boolean isdelete=mydbHelper.delete(sql);
                    }else {//订餐

//                    String sql="delete from tb_words where name='"+product.getName()+"'";
                    String sql="delete from tb_words where ID_item='"+product.getID()+"'";
                    boolean isdelete=mydbHelper.delete(sql);
                    }
//					BaseApplication.toast(isdelete+"删除缓存");
                }else {
                    getMySQLData(product.getName(), product.getPrices() + "", product.getNum()+"",product,0);
                }

                int num=product.getNum()-1;
                if (num==0){
                    product.setNum(num);
                    productList.remove(product);
                    myShopList.remove(position);
                    notifyDataSetChanged();
                    orderActivity.setNotifyDataSetChanged();
                    orderActivity.updateBottomStatus(getTotalPrice(), getTotalNumber());
                }else {
                    myShopList.get(position).setNum(num);
                    orderActivity.updateBottomStatus(getTotalPrice(), getTotalNumber());
                    notifyDataSetChanged();
                    orderActivity.setNotifyDataSetChanged();
                }
                //设置取消Popupwindow
                if (myShopList.size()==0){
                    upWindow.dismiss();
                }
            }
        });
        //增加
        mholder.iv_Add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //先查询数据库是否存储有数据
                getMySQLData(product.getName(), product.getPrices() + "", product.getNum() + "",product,1);
                product.setNum(product.getNum()+1);
//                int num=product.getNum()+1;
//                myShopList.get(position).setNum(num);
               // productList.add(product);
                orderActivity.updateBottomStatus(getTotalPrice(), getTotalNumber());
                notifyDataSetChanged();
                orderActivity.setNotifyDataSetChanged();

            }
        });






        return convertView;
    }

    class  ViewHolder{
        private TextView tv_name;
        private TextView tv_Money;
        private TextView tv_Num;
        private ImageView iv_Des;
        private ImageView iv_Add;


        private  ViewHolder(View v){
            tv_name= (TextView) v.findViewById(R.id.tv_name);
            tv_Money= (TextView) v.findViewById(R.id.tv_Money);
            tv_Num= (TextView) v.findViewById(R.id.tv_Num);
            iv_Des= (ImageView) v.findViewById(R.id.iv_Des);
            iv_Add= (ImageView) v.findViewById(R.id.iv_Add);

        }
    }


    /**
     * 获取总金额
     * @return
     */
    public double getTotalPrice(){
        double totalProce = 0;
        if(productList.size() == 0){
            return totalProce;
        }
        for(Iterator<?> itr = productList.uniqueSet().iterator();itr.hasNext();){
            Product product = (Product)itr.next();
            Double price=product.getPrices();
            totalProce += product.getPrices()*product.getNum();
//			totalProce+=price;
            totalProce=NumberUtils.toDouble(NumberUtils.format(totalProce));
        }
        return totalProce;
    }

    /**
     * 获取总数量
     * @return
     */
    public int getTotalNumber(){
        int totalNumber = 0;
        if(productList.size() == 0){
            return totalNumber;
        }
        for(Iterator<?> itr = productList.uniqueSet().iterator();itr.hasNext();){
            Product product = (Product)itr.next();


            totalNumber +=product.getNum();
        }
        return totalNumber;
    }

    private void getMySQLData(String name,String price,String num,Product product,int isadd){//isadd  1为添加0 为减少
        // // 把数据存储到数据库中

        if (tab.equals("1")){//购物-----------------------------------------------------
            String sqlString2 = "select * from shopping_words where name=? and price=? and num=? and ID_item=?";
            int count = mydbHelper.selectCount(
                    sqlString2,
                    new String[] {name,price,num,product.getID()});
            //数据库的数量跟着改变
            if (isadd==1){
                int n=Integer.parseInt(num)+1;
                num=n+"";
            }else {
                int n=Integer.parseInt(num)-1;
                num=n+"";
            }
            if (count > 0) {
                // "数据已经存在需要更改
                String sqlUpda="update shopping_words set num=? where ID_item='"+product.getID()+"'";
                boolean isfale=mydbHelper.updateData(sqlUpda,new String[] {num});
                //				BaseApplication.toast(isfale+"数据更改");
            } else {
                // 先插入数据再查询数据
                String sql = "insert into shopping_words (name ,price,num,ID_item) values (?,?,?,?)";
                flag = mydbHelper.execData(
                        sql,
                        new Object[] {name,price,num ,product.getID()});
                //				BaseApplication.toast(flag+"数据存储");
            }


        }else {//订餐---------------------------------------------------------------
            String sqlString2 = "select * from tb_words where name=? and price=? and num=? and ID_item=?";
            int count = mydbHelper.selectCount(
                    sqlString2,
                    new String[] {name,price,num,product.getID()});
    //			BaseApplication.toast(count+"数据是否已存在");
            //数据库的数量跟着改变
            if (isadd==1){
                int n=Integer.parseInt(num)+1;
                num=n+"";
            }else {
                int n=Integer.parseInt(num)-1;
                num=n+"";
            }
            if (count > 0) {
                // "数据已经存在需要更改
                String sqlUpda="update tb_words set num=? where ID_item='"+product.getID()+"'";
                boolean isfale=mydbHelper.updateData(sqlUpda,new String[] {num});
    //				BaseApplication.toast(isfale+"数据更改");
            } else {
                // 先插入数据再查询数据
                String sql = "insert into tb_words (name ,price,num,ID_item) values (?,?,?,?)";
                flag = mydbHelper.execData(
                        sql,
                        new Object[] {name,price,num ,product.getID()});
    //				BaseApplication.toast(flag+"数据存储");
            }
        }
    }
}

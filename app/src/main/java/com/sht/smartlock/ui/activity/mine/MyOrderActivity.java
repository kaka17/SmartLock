package com.sht.smartlock.ui.activity.mine;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.R;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.ui.activity.LoginByNameActivity;
import com.sht.smartlock.ui.activity.OrderByOthersActivity;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.adapter.OderFormListAdapter;

public class MyOrderActivity extends BaseActivity {
    private ImageView btn_cancle;
    private TextView tvTitlePanel;
//    private ListView oder_list_titlelocal;
    private LinearLayout linearBookingHotel;
    private LinearLayout linearOrdering;
    private LinearLayout linearShopping,linearOther;
    private String strname[] = {"订酒店","订餐","购物","其他"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findviewbyid();
        setClickLister();
    }

    private void findviewbyid(){
        btn_cancle = (ImageView)findViewById(R.id.btn_cancle);
        tvTitlePanel = (TextView)findViewById(R.id.tvTitlePanel);
        linearBookingHotel = (LinearLayout)findViewById(R.id.linearBookingHotel);
        linearOrdering = (LinearLayout)findViewById(R.id.linearOrdering);
        linearShopping = (LinearLayout)findViewById(R.id.linearShopping);
        linearOther = (LinearLayout)findViewById(R.id.linearOther);
//        oder_list_titlelocal = (ListView)findViewById(R.id.oder_list_titlelocal);
//
//        oder_list_titlelocal.setDivider(null);
//
//        OderFormListAdapter oderFormListAdapter = new OderFormListAdapter(MyOrderActivity.this,strname);
//        oder_list_titlelocal.setAdapter(oderFormListAdapter);
        tvTitlePanel.setText("我的订单");

    }

    private void setClickLister(){
        btn_cancle.setOnClickListener(listenerCancle);
        linearBookingHotel.setOnClickListener(listener);
        linearOrdering.setOnClickListener(listener);
        linearShopping.setOnClickListener(listener);
        linearOther.setOnClickListener(listener);
//        oder_list_titlelocal.setOnItemClickListener(itemClickListener);
    }

    View.OnClickListener listenerCancle = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.linearBookingHotel://酒店订单
                    if(AppContext.getShareUserSessinid()!=null){
                        startActivity(new Intent(MyOrderActivity.this,MyHotelOrderListActivity.class));
                    }else{
                        BaseApplication.toast("请先登录!");
                        startActivity(new Intent(MyOrderActivity.this,LoginByNameActivity.class));
                    }
                    break;
                case  R.id.linearOrdering://订餐
                    if(AppContext.getShareUserSessinid()!=null){
                        startActivity(new Intent(MyOrderActivity.this,OrderingActivity.class));
                    }else{
                        BaseApplication.toast("请先登录!");
                        startActivity(new Intent(MyOrderActivity.this,LoginByNameActivity.class));
                    }
                    break;
                case R.id.linearShopping://购物
                    if(AppContext.getShareUserSessinid()!=null) {
                        startActivity(new Intent(MyOrderActivity.this, GoShoppingActivity.class));
                    }else{
                        BaseApplication.toast("请先登录!");
                        startActivity(new Intent(MyOrderActivity.this,LoginByNameActivity.class));
                    }
                    break;
                case R.id.linearOther:
                    if(AppContext.getShareUserSessinid()!=null) {
                        startActivity(new Intent(getApplicationContext(), OrderByOthersActivity.class));
                    }else {
                        BaseApplication.toast("请先登录!");
                        startActivity(new Intent(MyOrderActivity.this,LoginByNameActivity.class));
                    }
                    break;
            }
        }
    };


    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_order;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }

    
}

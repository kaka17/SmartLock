package com.sht.smartlock.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.sht.smartlock.AppManager;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.ui.activity.base.BaseActivity;

public class OrderingSuccessActivity extends BaseActivity {

    private TextView tv_Money;
    private Intent intent;
    private double dbtotalprice;
    private String tab;
    private String billno;
    private String room_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_ordering_success);
        intent=getIntent();
        dbtotalprice=intent.getDoubleExtra(Config.KEY_HOTEL_ROOMTOTALPRICE, 0);
        tab=intent.getStringExtra(Config.ORDER_TAB);
        billno = intent.getStringExtra(Config.KEY_BOOKING_BILLNUM);//订单号
        room_id= intent.getStringExtra(Config.ROOM_ID);
        onBack();
        initView();
    }


    private void initView(){
        tv_Money = (TextView) findViewById(R.id.tv_Money);
        tv_Money.setText(dbtotalprice+"元");
    }








    //获取布局文件
    @Override
    protected int getLayoutId() {
        return R.layout.activity_ordering_success;
    }

    protected boolean hasToolBar() {
        return false;
    }
    private void onBack(){
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Submit_OrdersActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString(Config.ORDER_TAB, tab);
                bundle.putString(Config.ORDER_NUMVER, billno);//订单号
                bundle.putString(Config.ROOM_ID,room_id);
                intent.putExtras(bundle);
                startActivity(intent);


                AppManager.getAppManager().finishActivity(OrderingSuccessActivity.class);
            }
        });
    }

}

package com.sht.smartlock.ui.activity.mine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.model.BillInfo;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.adapter.MyBillAdapter;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import java.util.ArrayList;
import java.util.List;

public class MyBillActivity extends BaseActivity implements View.OnClickListener{
    private ImageView btnBillCancle;
    private TextView tvTitlePanelBill;
    private ImageView ivBillType1,ivBillType2,ivBillType3,ivBillType4;
    private TextView tvBillType1,tvBillType2,tvBillType3,tvBillType4;
    private ListView lvBill;

    private List<BillInfo> list = new ArrayList<BillInfo>();
    private MyBillAdapter billAdapter;
    private RelativeLayout reEnpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById();
        initData("1");
    }

    private void findViewById(){
        reEnpty = (RelativeLayout) findViewById(R.id.reEnpty);

        btnBillCancle = (ImageView)findViewById(R.id.btnBillCancle);
        tvTitlePanelBill = (TextView)findViewById(R.id.tvTitlePanelBill);
        ivBillType1 = (ImageView)findViewById(R.id.ivBillType1);
        ivBillType2 = (ImageView)findViewById(R.id.ivBillType2);
        ivBillType3 = (ImageView)findViewById(R.id.ivBillType3);
        ivBillType4 = (ImageView)findViewById(R.id.ivBillType4);
        tvBillType1 = (TextView)findViewById(R.id.tvBillType1);
        tvBillType2 = (TextView)findViewById(R.id.tvBillType2);
        tvBillType3 = (TextView)findViewById(R.id.tvBillType3);
        tvBillType4 = (TextView)findViewById(R.id.tvBillType4);
        lvBill = (ListView)findViewById(R.id.lvBill);

        tvTitlePanelBill.setText(R.string.myTitleBill);

        btnBillCancle.setOnClickListener(this);
        ivBillType1.setOnClickListener(this);
        ivBillType2.setOnClickListener(this);
        ivBillType3.setOnClickListener(this);
        ivBillType4.setOnClickListener(this);
        billAdapter = new MyBillAdapter(MyBillActivity.this,list);
        lvBill.setAdapter(billAdapter);
        lvBill.setOnItemClickListener(itemClickListener);
    }


    private void initData(String action){
        HttpClient.instance().my_bill_list(action, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(MyBillActivity.this,"正在加载...");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                ProgressDialog.disMiss();
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                LogUtil.log("resp=" + responseBean.toString());
                List<BillInfo> lists = responseBean.getListData(BillInfo.class);
                list.clear();
                if (lists.size()>0){
                    list.addAll(lists);
                    reEnpty.setVisibility(View.GONE);
                }else {
                    reEnpty.setVisibility(View.VISIBLE);
                }
                    billAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBillCancle:
                finish();
                break;
            case R.id.ivBillType1:
                ivBillType1.setImageResource(R.drawable.icon_time_highlight);
                ivBillType2.setImageResource(R.drawable.icon_time_dim);
                ivBillType3.setImageResource(R.drawable.icon_time_dim);
                ivBillType4.setImageResource(R.drawable.icon_time_dim);
                tvBillType1.setTextColor(getResources().getColor(R.color.LOCKHourText));
                tvBillType2.setTextColor(getResources().getColor(R.color.bill_def_color));
                tvBillType3.setTextColor(getResources().getColor(R.color.bill_def_color));
                tvBillType4.setTextColor(getResources().getColor(R.color.bill_def_color));
                initData("1");
                break;
            case R.id.ivBillType2:
                ivBillType1.setImageResource(R.drawable.icon_time_dim);
                ivBillType2.setImageResource(R.drawable.icon_time_highlight);
                ivBillType3.setImageResource(R.drawable.icon_time_dim);
                ivBillType4.setImageResource(R.drawable.icon_time_dim);
                tvBillType1.setTextColor(getResources().getColor(R.color.bill_def_color));
                tvBillType2.setTextColor(getResources().getColor(R.color.LOCKHourText));
                tvBillType3.setTextColor(getResources().getColor(R.color.bill_def_color));
                tvBillType4.setTextColor(getResources().getColor(R.color.bill_def_color));
                initData("2");
                break;
            case R.id.ivBillType3:
                ivBillType1.setImageResource(R.drawable.icon_time_dim);
                ivBillType2.setImageResource(R.drawable.icon_time_dim);
                ivBillType3.setImageResource(R.drawable.icon_time_highlight);
                ivBillType4.setImageResource(R.drawable.icon_time_dim);
                tvBillType1.setTextColor(getResources().getColor(R.color.bill_def_color));
                tvBillType2.setTextColor(getResources().getColor(R.color.bill_def_color));
                tvBillType3.setTextColor(getResources().getColor(R.color.LOCKHourText));
                tvBillType4.setTextColor(getResources().getColor(R.color.bill_def_color));
                initData("3");
                break;
            case R.id.ivBillType4:
                ivBillType1.setImageResource(R.drawable.icon_time_dim);
                ivBillType2.setImageResource(R.drawable.icon_time_dim);
                ivBillType3.setImageResource(R.drawable.icon_time_dim);
                ivBillType4.setImageResource(R.drawable.icon_time_highlight);
                tvBillType1.setTextColor(getResources().getColor(R.color.bill_def_color));
                tvBillType2.setTextColor(getResources().getColor(R.color.bill_def_color));
                tvBillType3.setTextColor(getResources().getColor(R.color.bill_def_color));
                tvBillType4.setTextColor(getResources().getColor(R.color.LOCKHourText));
                initData("4");
                break;
        }
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MyBillActivity.this, BillingDetailsActivity.class);
            intent.putExtra("booking_id",list.get(position).getBooking_id());
            intent.putExtra("user_service_id", list.get(position).getUser_service_id());
            intent.putExtra("caption", list.get(position).getCaption());
            intent.putExtra("room_no", list.get(position).getRoom_no());
            intent.putExtra("time", list.get(position).getTime());
            startActivity(intent);
        }
    };


    @Override
    protected boolean hasToolBar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_bill;
    }
}

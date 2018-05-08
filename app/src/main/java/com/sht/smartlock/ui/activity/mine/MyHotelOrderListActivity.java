package com.sht.smartlock.ui.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.model.HotelOrder;
import com.sht.smartlock.model.UserLoginInfo;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.adapter.HotelOderAdapter;
import com.sht.smartlock.ui.entity.HetelListEntity;
import com.sht.smartlock.util.LogUtil;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class MyHotelOrderListActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2 {
    private ImageView btn_cancle;
    private TextView tvTitlePanel;
    private PullToRefreshListView list_hoteloder;
    private UserLoginInfo userInfo;
    private HotelOderAdapter oderAdapter;
    private List<HetelListEntity> hotellist = new ArrayList<>();
    private List<HotelOrder> lists;

    private int pageid = 1;
    private boolean isResult = true;
    private RelativeLayout reEnpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findviewbyid();
        setClickLister();
//        initDate(1);
    }

    private void findviewbyid() {
        btn_cancle = (ImageView) findViewById(R.id.btn_cancle);
        tvTitlePanel = (TextView) findViewById(R.id.tvTitlePanel);
        list_hoteloder = (PullToRefreshListView) findViewById(R.id.list_hoteloder);
        userInfo = new UserLoginInfo();
        tvTitlePanel.setText("酒店订单");
        reEnpty = (RelativeLayout) findViewById(R.id.reEnpty);

        list_hoteloder.setMode(PullToRefreshBase.Mode.BOTH);
        list_hoteloder.setOnRefreshListener(this);
        oderAdapter = new HotelOderAdapter(MyHotelOrderListActivity.this, hotellist);
        list_hoteloder.setAdapter(oderAdapter);
    }

    private void setClickLister() {
        btn_cancle.setOnClickListener(onClickListener);
        list_hoteloder.setOnItemClickListener(itemClickListener);
    }

    private int isTranslation;

    //i 等于1是不清理数据
    private void initDate(final int i) {
//        isTranslation = i;
//        LogUtil.log("i==" + i);
//        HttpClient.instance().myhotel_order_list(pageid, new HttpCallBack() {
//            @Override
//            public void onSuccess(ResponseBean responseBean) {
//                LogUtil.log("responseBean=" + responseBean.toString());
//                if (pageid == 1) {
//                    hotellist.clear();
//                }
//                try {
//                    if (isTranslation == 0) {
//                        hotellist.clear();
//                        lists.clear();
//                    }
//                    lists = responseBean.getListDataWithGson(HotelOrder.class);
//
//                    LogUtil.log("lists=" + lists.size());
//                    hotellist.addAll(lists);
//                    oderAdapter.notifyDataSetChanged();
//                } catch (Exception e) {
//                    list_hoteloder.onRefreshComplete();
//                    BaseApplication.toast("没有更多数据");
//                }
//                list_hoteloder.onRefreshComplete();
//            }
//        });

        HttpClient.instance().roomOrderList( i, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                list_hoteloder.onRefreshComplete();
                List<HetelListEntity> listData = responseBean.getListData(HetelListEntity.class);
                if(i==1){
                    hotellist.clear();
                }
                hotellist.addAll(listData);
                oderAdapter.notifyDataSetChanged();
                if (hotellist.size()>0){
                    reEnpty.setVisibility(View.GONE);
                }else {
                    reEnpty.setVisibility(View.VISIBLE);
                }
            }
        });




    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_cancle:
                    finish();
                    break;
            }
        }
    };

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        pageid = 1;
        initDate(pageid);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        pageid++;
        initDate(pageid);
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//            BaseApplication.toast(hotellist.get(i).getID().toString());
//           startActivity(new Intent(MyHotelOrderListActivity.this,HotelStatusActivity.class));
            Intent intent = new Intent();
            intent.setClass(MyHotelOrderListActivity.this, HotelStatusActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("booking_id", hotellist.get(i - 1).getID().toString());
            bundle.putString("checkin_time", hotellist.get(i - 1).getStart_date() + "");
            bundle.putString("pay_state", hotellist.get(i - 1).getPay_state());
            bundle.putString("term_type", hotellist.get(i - 1).getTerm_type());
//            bundle.putDouble(Config.LONGITUDE,114.053608);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        initDate(1);
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_hotel__order__list;
    }
}

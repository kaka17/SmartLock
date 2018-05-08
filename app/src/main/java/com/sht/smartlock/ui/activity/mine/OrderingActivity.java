package com.sht.smartlock.ui.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.model.OrderingInfo;
import com.sht.smartlock.ui.activity.Submit_OrdersActivity;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.adapter.MyOrderingAdapter;
import com.sht.smartlock.ui.entity.OrderListEntity;
import com.sht.smartlock.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderingActivity extends BaseActivity implements AdapterView.OnItemClickListener,PullToRefreshBase.OnRefreshListener2{
    private ImageView btn_cancle;
    private TextView tvTitlePanel;
    private PullToRefreshListView listOrdering;
    private List<OrderingInfo> orderingInfoList = new ArrayList<OrderingInfo>();

//    private ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String,String>>();
//    private ArrayList<HashMap<String, String>> lists = new ArrayList<HashMap<String,String>>();
private List<OrderListEntity> list=new ArrayList<>();
    private MyOrderingAdapter orderingAdapter;
    private int pageid =1;
    private boolean isResult = true;
    private RelativeLayout reEnpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findviewbyid();
        setClickLister();
        initDate(1);
    }

    private void findviewbyid(){
        btn_cancle = (ImageView)findViewById(R.id.btn_cancle);
        tvTitlePanel = (TextView)findViewById(R.id.tvTitlePanel);
        listOrdering = (PullToRefreshListView)findViewById(R.id.listOrdering);
        reEnpty = (RelativeLayout) findViewById(R.id.reEnpty);
        tvTitlePanel.setText("点餐订单");
        listOrdering.setMode(PullToRefreshBase.Mode.BOTH);
        listOrdering.setOnRefreshListener(this);
        orderingAdapter = new MyOrderingAdapter(OrderingActivity.this, list);
        listOrdering.setAdapter(orderingAdapter);
    }

    private void setClickLister(){
        btn_cancle.setOnClickListener(onClickListener);
        listOrdering.setOnItemClickListener(this);
    }

    private String result;
    private int isClear;
    private void initDate(final int i){
//        isClear = i;
//        HttpClient.instance().mydinchan_list(pageid, new HttpCallBack() {
//            @Override
//            public void onSuccess(ResponseBean responseBean) {
//                if (!responseBean.toString().equals("")) {
//                    try {
//                        JSONTokener jsonTokener = new JSONTokener(responseBean.toString());
//                        JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
//                        result = jsonObject.getString("result");
//                            if(isClear == 0){
//                            lists.clear();
//                            listItem.clear();
//                            LogUtil.log("isClear=="+isClear);
//                        }else{
//                            lists.clear();
//                        }
//                        JSONArray jsonResultArray = new JSONArray(result);
//                        for (int i = 0; i < jsonResultArray.length(); i++) {
//                            JSONObject jsonResultData = jsonResultArray.getJSONObject(i);
//                            HashMap<String, String> map = new HashMap<String, String>();
//                            map.put("ID", jsonResultData.getString("ID"));
//                            map.put("hotel_id", jsonResultData.getString("hotel_id"));
//                            map.put("hotel_caption", jsonResultData.getString("hotel_caption"));
//                            map.put("create_time", jsonResultData.getString("create_time"));
//                            map.put("pay_channel", jsonResultData.getString("pay_channel"));
//                            map.put("total", jsonResultData.getString("total"));
//                            map.put("state", jsonResultData.getString("state"));
//                            map.put("pay_state", jsonResultData.getString("pay_state"));
//                            map.put("room_id", jsonResultData.getString("room_id"));
//                            map.put("items", jsonResultData.getString("items"));
//                            JSONArray jsonResultItemArray = new JSONArray(jsonResultData.getString("items"));
//                            JSONObject jsonObjectItem = jsonResultItemArray.getJSONObject(0);
//                            map.put("caption", jsonObjectItem.getString("caption"));
//                            map.put("quantity", jsonObjectItem.getString("quantity"));
//                            map.put("price", jsonObjectItem.getString("price"));
//                            map.put("discount", jsonObjectItem.getString("discount"));
//                            map.put("size", jsonResultItemArray.length() + "");
//                            lists.add(map);
//                        }
//                        listItem.addAll(lists);
////                        LogUtil.log("listItem=" +lists.size()+"---" + listItem.size());
//                        orderingAdapter.notifyDataSetChanged();
//
//                    } catch (Exception e) {
////                        listItem.clear();
////                        lists.clear();
//                        toastFail(getString(R.string.no_result_left));
//                    }
//                    listOrdering.onRefreshComplete();
//                }
//            }
//        });
        //---------------------------------
        HttpClient.instance().bookDinnerList(i, "1", new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                Log.e("SHOPPING", "-------------->" + responseBean.toString());
                List<OrderListEntity> listData = responseBean.getListData(OrderListEntity.class);
                if (i==1){
                    list.clear();
                }

                list.addAll(listData);
                orderingAdapter.notifyDataSetChanged();
                if (list.size()>0){
                    reEnpty.setVisibility(View.GONE);
                }else {
                    reEnpty.setVisibility(View.VISIBLE);
                }



            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent=new Intent();
        intent.setClass(OrderingActivity.this, Submit_OrdersActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString(Config.ORDER_TAB,"2");
        bundle.putString(Config.REMARKS,"1");
        bundle.putString(Config.MAINORORDER,"1");
        if (! list.get(i-1).getRoom_id().equals("null")){
            bundle.putString(Config.ROOM_ID,list.get(i-1).getRoom_id());
        }
        bundle.putString(Config.ORDER_NUMVER, list.get(i-1).getID());

        intent.putExtras(bundle);
        startActivity(intent);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_cancle:
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        initDate(1);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        pageid =1;
        initDate(pageid);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isResult =false;
        pageid ++;
        initDate(pageid);
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ordering;
    }
}

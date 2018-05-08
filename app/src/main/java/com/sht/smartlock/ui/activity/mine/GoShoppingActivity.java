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
import com.sht.smartlock.model.ShoppingInfo;
import com.sht.smartlock.ui.activity.Submit_OrdersActivity;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.adapter.MyShoppingAdapter;
import com.sht.smartlock.ui.entity.OrderListEntity;
import com.sht.smartlock.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GoShoppingActivity extends BaseActivity implements AdapterView.OnItemClickListener,PullToRefreshBase.OnRefreshListener2{
    private ImageView btn_cancle;
    private TextView tvTitlePanel;
    private PullToRefreshListView listMyShopping;

//    private ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
//    private ArrayList<HashMap<String, String>> lists = new ArrayList<HashMap<String, String>>();
    private List<OrderListEntity> list=new ArrayList<>();
    private MyShoppingAdapter myShoppingAdapter;

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
        listMyShopping = (PullToRefreshListView)findViewById(R.id.listMyShopping);
        tvTitlePanel.setText("购物订单");
        reEnpty = (RelativeLayout) findViewById(R.id.reEnpty);

        listMyShopping.setMode(PullToRefreshBase.Mode.BOTH);
        listMyShopping.setOnRefreshListener(this);
        myShoppingAdapter = new MyShoppingAdapter(GoShoppingActivity.this,list);
        listMyShopping.setAdapter(myShoppingAdapter);
    }

    private void setClickLister(){
        btn_cancle.setOnClickListener(onClickListener);
        listMyShopping.setOnItemClickListener(this);
    }

    private int isclear;
    private void initDate(final int i){
        isclear = i;
//
//        HttpClient.instance().myshopping_list(pageid, new HttpCallBack() {
//            @Override
//            public void onSuccess(ResponseBean responseBean) {
//                try {
//                    JSONObject jsonObject=new JSONObject(responseBean.toString());
//                    if(jsonObject.optString("error","")==""){
//                        if(!responseBean.toString().equals("")){
//                            try {
//                                JSONTokener jsonTokener = new JSONTokener(responseBean.toString());
//                                JSONObject jsonObject1 = (JSONObject)jsonTokener.nextValue();
//                                result = jsonObject1.getString("result");
////                                if (isResult) {//如果是刷新就清空
//                                    if(isclear == 0){
//                                                lists.clear();
//                                                listItem.clear();
//                                        LogUtil.log("isClear==" + isclear);
//                                    } else {
//                                        lists.clear();
//                                    }
////                                }
//                                JSONArray jsonSpArray = new JSONArray(result);
//                                for(int i=0;i<jsonSpArray.length();i++){
//                                    JSONObject jsonObjectSp = jsonSpArray.getJSONObject(i);
//                                    HashMap<String, String> map = new HashMap<String, String>();
//                                    map.put("ID",jsonObjectSp.getString("ID"));
//                                    map.put("hotel_id",jsonObjectSp.getString("hotel_id"));
//                                    map.put("hotel_caption",jsonObjectSp.getString("hotel_caption"));
//                                    map.put("create_time",jsonObjectSp.getString("create_time"));
//                                    map.put("pay_channel", jsonObjectSp.getString("pay_channel"));
//                                    map.put("total", jsonObjectSp.getString("total"));
//                                    map.put("state", jsonObjectSp.getString("state"));
//                                    map.put("pay_state", jsonObjectSp.getString("pay_state"));
//                                    map.put("room_id", jsonObjectSp.getString("room_id"));
//                                    map.put("items", jsonObjectSp.getString("items"));
//                                    JSONArray jsonResultItemArray = new JSONArray(jsonObjectSp.getString("items"));
//                                    JSONObject jsonObjectItem = jsonResultItemArray.getJSONObject(0);
//                                    map.put("caption",jsonObjectItem.optString("caption"));
//                                    map.put("quantity",jsonObjectItem.optString("quantity"));
//                                    map.put("price",jsonObjectItem.optString("price"));
//                                    map.put("discount",jsonObjectItem.optString("discount"));
//                                    map.put("size",jsonResultItemArray.length()+"");
//
//                                    lists.add(map);
//                                }
//                                listItem.addAll(lists);
//                                myShoppingAdapter.notifyDataSetChanged();
//
//                            }catch (Exception e){
//                                lists.clear();
//                            toastFail(getString(R.string.no_result_left));
//                            }
//                        }
//                    }else{
//                        toastFail(getString(R.string.no_result_left));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                listMyShopping.onRefreshComplete();
//            }
//        });


        //---------------------------------
        HttpClient.instance().bookDinnerList(i, "2", new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                Log.e("SHOPPING", "-------------->" + responseBean.toString());
                List<OrderListEntity> listData = responseBean.getListData(OrderListEntity.class);
                if (i==1){
                    list.clear();
                }
                list.addAll(listData);
                myShoppingAdapter.notifyDataSetChanged();
                if (list.size()>0){
                    reEnpty.setVisibility(View.GONE);
                }else {
                    reEnpty.setVisibility(View.VISIBLE);
                }


            }
        });


    }


    private String result;
    class  NetworkRequestLoginResult extends HttpCallBack {

        public void onStart() {
            super.onStart();
//            ProgressDialog.show(mContext, "开始");
        }

        @Override
        public void onFailure(String error, String message) {
            listMyShopping.onRefreshComplete();
            super.onFailure(error, message);
        }

        @Override
        public void onSuccess(ResponseBean responseBean) {

            listMyShopping.onRefreshComplete();


        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent=new Intent();
        intent.setClass(GoShoppingActivity.this, Submit_OrdersActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString(Config.ORDER_TAB,"1");
        bundle.putString(Config.REMARKS,"1");
        bundle.putString(Config.MAINORORDER,"1");
        if (! list.get(i-1).getRoom_id().equals("null")){
            bundle.putString(Config.ROOM_ID, list.get(i-1).getRoom_id());
        }
        bundle.putString(Config.ORDER_NUMVER, list.get(i-1).getID());
//            bundle.putDouble(Config.LONGITUDE,114.053608);
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
        pageid = 1;
        initDate(pageid);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        pageid ++;
        initDate(pageid);
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_go_shopping;
    }


}

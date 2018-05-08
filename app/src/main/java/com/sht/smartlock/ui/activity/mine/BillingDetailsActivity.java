package com.sht.smartlock.ui.activity.mine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.model.BillingDetailsInfo;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.adapter.ConsumptionDetailAdapter;
import com.sht.smartlock.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillingDetailsActivity extends BaseActivity implements View.OnClickListener{
    private ImageView goBack;
    private TextView tvTotalAmount,tvBillHotelName,tvBillTime,tvBillRoomNumber;
    private TextView tvRoomTotal,tvOrderingTotal,tvShoppingTotal,tvOtherToatal;
    private ListView lvlinDetailed;
    private ImageView ivRoomCost,ivOdering,ivShopping,ivOther;
    private TextView tvShowText;
    private ImageView ivFold;

    private BillingDetailsInfo billingDetailsInfo;
    private JSONArray jsonArray;
    private JSONObject jsonObject;

    private Map<String,String> mapRoom = new HashMap<>();
    private Map<String,String> mapOdering;
    private Map<String,String> mapShopping;
    private Map<String,String> mapRooms;
    private Map<String,String> mapOther = new HashMap<>();
    private List<Map<String,String>> listRoom = new ArrayList<Map<String,String>>();
    private List<Map<String,String>> mapRoomLists = new ArrayList<Map<String,String>>();
    private List<Map<String,String>> listOdering = new ArrayList<Map<String,String>>();
    private List<Map<String,String>> listShopping = new ArrayList<Map<String,String>>();
    private List<Map<String,String>> listOther = new ArrayList<Map<String,String>>();

    private ConsumptionDetailAdapter detailAdapter;
    List<Map<String,String>> lists=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById();
        initData();
    }

    private void findViewById(){
        goBack = (ImageView)findViewById(R.id.goBack);
        tvTotalAmount = (TextView)findViewById(R.id.tvTotalAmount);
        tvBillHotelName = (TextView)findViewById(R.id.tvBillHotelName);
        tvBillTime = (TextView)findViewById(R.id.tvBillTime);
        tvBillRoomNumber = (TextView)findViewById(R.id.tvBillRoomNumber);
        tvRoomTotal = (TextView)findViewById(R.id.tvRoomTotal);
        tvOrderingTotal = (TextView)findViewById(R.id.tvOrderingTotal);
        tvShoppingTotal = (TextView)findViewById(R.id.tvShoppingTotal);
        tvOtherToatal = (TextView)findViewById(R.id.tvOtherToatal);
        ivRoomCost = (ImageView)findViewById(R.id.ivRoomCost);
        ivOdering = (ImageView)findViewById(R.id.ivOdering);
        ivShopping = (ImageView)findViewById(R.id.ivShopping);
        ivOther = (ImageView)findViewById(R.id.ivOther);
        tvShowText = (TextView)findViewById(R.id.tvShowText);
        ivFold = (ImageView)findViewById(R.id.ivFold);

        lvlinDetailed = (ListView)findViewById(R.id.lvlinDetailed);
        detailAdapter = new ConsumptionDetailAdapter(this,lists);
        lvlinDetailed.setAdapter(detailAdapter);

        goBack.setOnClickListener(this);
        ivRoomCost.setOnClickListener(this);
        ivOdering.setOnClickListener(this);
        ivShopping.setOnClickListener(this);
        ivOther.setOnClickListener(this);
        ivFold.setOnClickListener(this);
    }
    Double billRoomPrice = 0.00;//房费总价格
    String strPrice = "";
    Double billOderingPrice = 0.00;//订餐总价格
    Double billShopingPrice = 0.00;//总价格
    Double billTotal = 0.00;//总价格
    private void initData(){
        Intent intent = getIntent();
        String booking_id = intent.getStringExtra("booking_id");//订单id
        String user_service_id = intent.getStringExtra("user_service_id");//第三方服务id
        String caption = intent.getStringExtra("caption");//酒店名称
        String room_no = intent.getStringExtra("room_no");//房间号
        String time = intent.getStringExtra("time");//房间号

        LogUtil.log("user_service_id=="+user_service_id);

        if(!caption.equals("")){
            tvBillHotelName.setText(caption);
        }
        if(!time.equals("")){
            tvBillTime.setText(time);
        }
        if(!room_no.equals("")){
            tvBillRoomNumber.setText(room_no);
        }

     HttpClient.instance().my_bill_detail(booking_id, user_service_id, new HttpCallBack() {


         @Override
         public void onSuccess(ResponseBean responseBean) {
             LogUtil.log("responseBean=" + responseBean.toString());
             billingDetailsInfo = responseBean.getData(BillingDetailsInfo.class);

             if (!billingDetailsInfo.getRoom_debt().equals("")) {

                 strPrice = billingDetailsInfo.getRoom_debt();
                 billRoomPrice = Double.parseDouble(strPrice);
//                 LogUtil.log("aa="+billRoomPrice + 200);
//                 billRoomPrice = Integer.parseInt(strPrice.substring(strPrice.length()-1,strPrice.length()));
                 mapRoom.put("caption", "房费");
                 mapRoom.put("quantity", "1");
                 mapRoom.put("price", billingDetailsInfo.getRoom_debt());
                 mapRoom.put("create_time", billingDetailsInfo.getCreate_time());
                 listRoom.add(mapRoom);
                 tvRoomTotal.setText("￥" + billRoomPrice);
             }

             if (billingDetailsInfo.getUnit_room_debt() !=null && ! billingDetailsInfo.getUnit_room_debt().equals("[]")) {//房费
                 try {
                     listOdering.clear();
                     jsonArray = new JSONArray(billingDetailsInfo.getUnit_room_debt());
                     for (int i = 0; i < jsonArray.length(); i++) {
                         mapRooms = new HashMap<String, String>();
                         try {
                             jsonObject = new JSONObject(jsonArray.get(i).toString());
                             if (billingDetailsInfo.getTerm_type().equals("1")){//全日房
                                 mapRooms.put("create_time", jsonObject.getString("charge_date"));//消费日期
                                 mapRooms.put("caption", jsonObject.getString("room_no")+"号房费");//消费房间
                                 mapRooms.put("quantity",Config.IDALLDAY);
                                 mapRooms.put("price", jsonObject.getString("price"));//消费价格
                                 mapRoomLists.add(mapRooms);
                             }else {// 2 钟点房
                                 mapRooms.put("hour_start", jsonObject.getString("hour_start"));//消费日期
                                 mapRooms.put("hour_end", jsonObject.getString("hour_end"));//消费日期
                                 mapRooms.put("caption", jsonObject.getString("room_no")+"号房费");//消费房间
                                 mapRooms.put("quantity", Config.IDHOUREDAY);
                                 mapRooms.put("price", billingDetailsInfo.getRoom_debt());//消费价格
                                 mapRoomLists.add(mapRooms);
                             }

                         } catch (JSONException e) {
                             e.printStackTrace();
                         }
                     }
//                     DecimalFormat dr = new DecimalFormat("#0.00");
//                     tvOrderingTotal.setText("￥" + dr.format(billOderingPrice));
                 } catch (JSONException e) {
                     e.printStackTrace();
                 }
             } else {
                 tvOrderingTotal.setText("暂无消费");
             }

             if (billingDetailsInfo.getCater_debt() != null) {//订餐
                 try {
                     listOdering.clear();
                     jsonArray = new JSONArray(billingDetailsInfo.getCater_debt());
                     for (int i = 0; i < jsonArray.length(); i++) {
                         mapOdering = new HashMap<String, String>();
                         try {

                             if (jsonArray.get(i).toString().equals("{}")){
                             }else {
                                 jsonObject = new JSONObject(jsonArray.get(i).toString());
                                 mapOdering.put("create_time", jsonObject.getString("create_time"));
                                 mapOdering.put("caption", jsonObject.getString("caption"));
                                 mapOdering.put("quantity", jsonObject.getString("quantity"));
                                 mapOdering.put("price", jsonObject.getString("price"));

                                 mapOdering.put("charge", jsonObject.optString("charge"));
                                 mapOdering.put("service_charge", jsonObject.optString("service_charge"));
                                 listOdering.add(mapOdering);
                                 DecimalFormat dr1 = new DecimalFormat("#0.");
                                 int numOdering = (int) Double.parseDouble(jsonObject.getString("quantity"));
                                 DecimalFormat dr = new DecimalFormat("#0.00");
                                 billOderingPrice = Double.parseDouble(dr.format(billOderingPrice)) + (numOdering * Double.parseDouble(dr.format(Double.parseDouble(listOdering.get(i).get("price")))));
                             }
                         } catch (JSONException e) {
                             e.printStackTrace();
                         }
                     }
                     DecimalFormat dr = new DecimalFormat("#0.00");
                     tvOrderingTotal.setText("￥" + dr.format(billOderingPrice));
                 } catch (JSONException e) {
                     e.printStackTrace();
                 }
             } else {
                 tvOrderingTotal.setText("暂无消费");
             }


             LogUtil.log("qwer==" + billingDetailsInfo.getShopping_debt());
             if (billingDetailsInfo.getShopping_debt() != null) {
                 listShopping.clear();
                 try {
                     jsonArray = new JSONArray(billingDetailsInfo.getShopping_debt());
                     for (int i = 0; i < jsonArray.length(); i++) {
                         mapShopping = new HashMap<>();
                         try {
                             if (jsonArray.get(i).toString().equals("{}")){
                             }else {
                                 jsonObject = new JSONObject(jsonArray.get(i).toString());
                                 mapShopping.put("create_time", jsonObject.getString("create_time"));
                                 mapShopping.put("caption", jsonObject.getString("caption"));
                                 mapShopping.put("quantity", jsonObject.getString("quantity"));
                                 mapShopping.put("price", jsonObject.getString("price"));
                                 mapShopping.put("charge", jsonObject.optString("charge"));
                                 mapShopping.put("service_charge", jsonObject.optString("service_charge"));
                                 listShopping.add(mapShopping);
                                 double numShopping = Double.parseDouble(jsonObject.getString("quantity"));
                                 billShopingPrice = billShopingPrice + (numShopping * Double.parseDouble(listShopping.get(i).get("price")));
                             }

                         } catch (JSONException e) {
                             e.printStackTrace();
                         }
                     }
                     DecimalFormat dr = new DecimalFormat("#0.00");
                     tvShoppingTotal.setText("￥" + dr.format(billShopingPrice));
//                     LogUtil.log("billShopingPrice=" + listShopping.get(0).get("caption"));
                 } catch (JSONException e) {
                     e.printStackTrace();
                 }
             } else {
                 tvShoppingTotal.setText("暂无消费");
             }


             if (billingDetailsInfo.getOther_debt() != null) {
                 try {
                     listOther.clear();
                     jsonArray = new JSONArray(billingDetailsInfo.getOther_debt());
                     for (int i = 0; i < jsonArray.length(); i++) {
                         mapOther = new HashMap<String, String>();
                         try {
                             jsonObject = new JSONObject(jsonArray.get(i).toString());
                             mapOther.put("price", jsonObject.getString("total"));
                             mapOther.put("quantity", "1");
                             mapOther.put("caption", jsonObject.getString("content"));
                             mapOther.put("create_time", jsonObject.getString("create_time"));
                             listOther.add(mapOther);
//                             double numOther = Double.parseDouble(jsonObject.getString("quantity"));
                             billTotal = billTotal + Double.parseDouble(listOther.get(i).get("price"));

                             LogUtil.log("billTotal=" + listOther.get(i).get("price"));
                         } catch (JSONException e) {
                             e.printStackTrace();
                         }
                     }
                     DecimalFormat dr = new DecimalFormat("#0.00");
                     tvOtherToatal.setText("￥" + dr.format(billTotal));
                 } catch (JSONException e) {
                     e.printStackTrace();
                 }
             } else {
                 tvOtherToatal.setText("暂无消费");
             }
             double totalAmount = billRoomPrice + billShopingPrice + billOderingPrice + billTotal;
             DecimalFormat dr = new DecimalFormat("#0.00");
             tvTotalAmount.setText("￥" + dr.format(totalAmount));

             tvShowText.setText("房费");
             if (billingDetailsInfo.getTerm_type().equals("1")) {
                 refresh(mapRoomLists);
             }else {
                 refresh(mapRoomLists);
                 detailAdapter.isDayOrHour(false);
             }
         }
     });


    }

    private void refresh(List<Map<String,String>> list){
        lists.clear();
        lists.addAll(list);
        detailAdapter.notifyDataSetChanged();
    }


    @Override
    protected boolean hasToolBar() {
        return false;
    }

    boolean flag;
    @Override
    public void onClick(View v) {
     switch (v.getId()){
         case R.id.goBack:
             finish();
             break;
         case R.id.ivRoomCost:
             ivRoomCost.setImageResource(R.drawable.btn_cost_house);
             ivOdering.setImageResource(R.drawable.btn_cost_diner_tarch);
             ivShopping.setImageResource(R.drawable.btn_cost_shopping_tarch);
             ivOther.setImageResource(R.drawable.icon_cost_product_tarch);
             tvShowText.setText("房费");
//             refresh(listRoom);
//             refresh(mapRoomLists);
             if (billingDetailsInfo.getTerm_type().equals("1")) {
                 refresh(mapRoomLists);
             }else {
                 refresh(mapRoomLists);
                 detailAdapter.isDayOrHour(false);
             }
             break;
         case R.id.ivOdering:
             ivRoomCost.setImageResource(R.drawable.btn_cost_house_tarch);
             ivOdering.setImageResource(R.drawable.btn_cost_diner);
             ivShopping.setImageResource(R.drawable.btn_cost_shopping_tarch);
             ivOther.setImageResource(R.drawable.icon_cost_product_tarch);
             tvShowText.setText("订餐");
             if(listOdering.size() < 1){
                 BaseApplication.toast("暂无订餐明细");
             }
             refresh(listOdering);
             break;
         case R.id.ivShopping:
             ivRoomCost.setImageResource(R.drawable.btn_cost_house_tarch);
             ivOdering.setImageResource(R.drawable.btn_cost_diner_tarch);
             ivShopping.setImageResource(R.drawable.btn_cost_shopping);
             ivOther.setImageResource(R.drawable.icon_cost_product_tarch);
             tvShowText.setText("购物");
             if(listShopping.size() < 1){
                 BaseApplication.toast("暂无购物明细");
             }
             refresh(listShopping);
             break;
         case R.id.ivOther:
             ivRoomCost.setImageResource(R.drawable.btn_cost_house_tarch);
             ivOdering.setImageResource(R.drawable.btn_cost_diner_tarch);
             ivShopping.setImageResource(R.drawable.btn_cost_shopping_tarch);
             ivOther.setImageResource(R.drawable.icon_cost_product);
             tvShowText.setText("其他");
             if(listOther.size() < 1){
                 BaseApplication.toast("暂无其他明细");
             }
             refresh(listOther);
             break;
         case R.id.ivFold:
             if(flag == false){
                 flag = true;
                 ivFold.setImageResource(R.drawable.safari_up);
                 lvlinDetailed.setVisibility(View.GONE);
             }else {
                 flag = false;
                 ivFold.setImageResource(R.drawable.safari_down);
                 lvlinDetailed.setVisibility(View.VISIBLE);
                 detailAdapter.notifyDataSetChanged();
             }
             break;
     }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_billing_details;
    }
}

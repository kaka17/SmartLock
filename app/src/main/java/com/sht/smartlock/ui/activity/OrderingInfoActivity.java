package com.sht.smartlock.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.AppManager;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.helper.MySQLiteOpenHelper;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.booking.PayBillActivity;
import com.sht.smartlock.ui.activity.fragment.NewDoorFragment;
import com.sht.smartlock.ui.activity.mine.GoShoppingActivity;
import com.sht.smartlock.ui.activity.mine.OrderingActivity;
import com.sht.smartlock.ui.activity.myinterface.ListItemClickHelp;
import com.sht.smartlock.ui.adapter.OrderInfo_Adapter;
import com.sht.smartlock.ui.entity.Entitys;
import com.sht.smartlock.ui.entity.MyInfoEntity;
import com.sht.smartlock.ui.entity.OrderEntity;
import com.sht.smartlock.ui.ordering.entity.Product;
import com.sht.smartlock.ui.ordering.orderingutil.NumberUtils;
import com.sht.smartlock.util.DoubleUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderingInfoActivity extends BaseActivity implements View.OnClickListener, ListItemClickHelp {

    private TextView tv_Tile, tv_Name, tv_Phone, tv_Mylockna, tv_Price, tv_Make_Order;
    private ListView lv_Ordering_Info;
    private EditText et_Remarks;
    private TextView tv_Remarks_writer;
    //    private RadioButton rb_Hole_Pay;
//    private RadioButton rb_Ordering_PayOnOline;
    private TextView tv_Total_Price;
    ;
    private List<Product> myShopList;
    private OrderInfo_Adapter adapter;
    //  private RadioGroup rg_Pay;
    private boolean isHolePay = true;
    private String tab;
    private OrderEntity entity;
    private ImageView iv_Hole_Pay;
    private ImageView iv_Ordering_PayOnOline;
    private TextView tv_Num;
    private MySQLiteOpenHelper mydbHelper = null;
    private MyInfoEntity myinfo_entity;
    private String pay_type = "2";
    private ImageView iv_Ordering_PayOnMoney;
    private boolean isMoreServicer=false;
    private List<String> listProviders=new ArrayList<>();
    private TextView tvPeiMoney,tvPeiName;
    private double peiMoney=0.0;
    private RelativeLayout relayPei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_ordering_info);
        Bundle bundle = getIntent().getExtras();
        entity = (OrderEntity) bundle.getSerializable("OrderEntity");
        myShopList = entity.getMyShopList();
        tab = bundle.getString(Config.ORDER_TAB);
        mydbHelper = new MySQLiteOpenHelper(getApplicationContext());
        initView();
        if (tab.equals("1")) {//购物
            tv_Tile.setText(R.string.order_shopping_info_title);
        } else {//订餐
            tv_Tile.setText(R.string.order_info_title);
        }
        onBack();
        setOnClickListener();
        getMyinfo();
        getNumPrio();
    }


    //获取布局文件
    @Override
    protected int getLayoutId() {
        return R.layout.activity_ordering_info;
    }

    protected boolean hasToolBar() {
        return false;
    }

    private void onBack() {
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.getAppManager().finishActivity(OrderingInfoActivity.class);
            }
        });
    }

    private void initView() {
        tv_Tile = (TextView) findViewById(R.id.tv_tile);
        tv_Name = (TextView) findViewById(R.id.tv_name);
        tv_Phone = (TextView) findViewById(R.id.tv_phone);
        tv_Num = (TextView) findViewById(R.id.tv_Num);
        //酒店名
        tv_Mylockna = (TextView) findViewById(R.id.tv_Mylockna);
        //
        lv_Ordering_Info = (ListView) findViewById(R.id.lv_Ordering_Info);
        //总价格
        tv_Price = (TextView) findViewById(R.id.tv_price);
        //填写备注
        et_Remarks = (EditText) findViewById(R.id.et_Remarks);
        //点击填写备注
        tv_Remarks_writer = (TextView) findViewById(R.id.tv_Remarks_writer);

        //显示总价格
        tv_Total_Price = (TextView) findViewById(R.id.tv_Total_Price);
        //下单
        tv_Make_Order = (TextView) findViewById(R.id.tv_Make_Order);

        //选择计入房费或者是在线支付
        iv_Hole_Pay = (ImageView) findViewById(R.id.iv_Hole_Pay);
        iv_Ordering_PayOnOline = (ImageView) findViewById(R.id.iv_Ordering_PayOnOline);
        iv_Ordering_PayOnMoney = (ImageView) findViewById(R.id.iv_Ordering_PayOnMoney);
        //配送费
        tvPeiMoney = (TextView) findViewById(R.id.tvPeiMoney);
        tvPeiName = (TextView) findViewById(R.id.tvPeiName);
        relayPei = (RelativeLayout) findViewById(R.id.relayPei);


        //
        adapter = new OrderInfo_Adapter(myShopList, getApplicationContext());
        lv_Ordering_Info.setAdapter(adapter);
        adapter.setOnAddAndSubClicklistenter(this);
        adapter.notifyDataSetChanged();

        double totalPrice = getTotalPrice();
        tv_Price.setText(" 共￥" + totalPrice);
        tv_Total_Price.setText(" 共￥" + totalPrice);

        if (NewDoorFragment.list.size() > 0) {
            String hotel_caption = NewDoorFragment.list.get(NewDoorFragment.pos).getHotel_caption();
            tv_Mylockna.setText(" " + hotel_caption);
        }

    }

    private void setOnClickListener() {
        //填写备注
        tv_Remarks_writer.setOnClickListener(this);
        //下单
        tv_Make_Order.setOnClickListener(this);
        iv_Hole_Pay.setOnClickListener(this);
        iv_Ordering_PayOnOline.setOnClickListener(this);
        iv_Ordering_PayOnMoney.setOnClickListener(this);

    }
    private void getNumPrio(){
        for (int i=0;i<myShopList.size();i++){
            String provider_id = myShopList.get(i).getProvider_id();
            listProviders.add(provider_id);
        }

        List<String> list = new ArrayList<>();
        list.add(listProviders.get(0));
        for(int i=1;i<listProviders.size();i++){
            if(list.toString().indexOf(listProviders.get(i)) == -1){
                list.add(listProviders.get(i));
            }
        }

        HttpClient.instance().servicerCharge(NewDoorFragment.list.get(NewDoorFragment.pos).getID(), list.size()+"", getTotalPrice()+"", new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(OrderingInfoActivity.this,"正在加载中...");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                Entitys entitys = responseBean.getData(Entitys.class);
                if (entitys.getCode()==1){
                    if (entitys.getResult()!=null&& ! entitys.getResult().equals("null") && ! entitys.getResult().equals("[]")){
                        try {
                            JSONObject jsonArray=new JSONObject(entitys.getResult());
                            String caption = jsonArray.getString("caption");
                            String charge = jsonArray.getString("charge");
                            tvPeiName.setText(caption);
                            tvPeiMoney.setText("￥"+charge);
                            peiMoney=Double.parseDouble(charge);
                            peiMoney= Double.parseDouble(DoubleUtil.getDoubleDecimal2(peiMoney));
                            double totalPrice = getTotalPrice();
                            tv_Price.setText(" 共￥" + DoubleUtil.getDoubleDecimal2(totalPrice + peiMoney));
                            tv_Total_Price.setText(" 共￥" + DoubleUtil.getDoubleDecimal2(totalPrice+peiMoney));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        relayPei.setVisibility(View.GONE);
                        tvPeiMoney.setText("￥0");
                    }
                }else {
                    AppContext.toast(entitys.getMsg());
                }
            }
        });

    }

    /**
     * 获取总金额
     *
     * @return
     */
    public double getTotalPrice() {
        double totalProce = 0;
        int num = 0;
        if (myShopList.size() == 0) {
            return totalProce;
        }
        for (int i = 0; i < myShopList.size(); i++) {
            Product product = myShopList.get(i);
            Double price = product.getPrices();
            totalProce += product.getPrices() * product.getNum();
            num += product.getNum();
//			totalProce+=price;
            totalProce = NumberUtils.toDouble(NumberUtils.format(totalProce));
        }
        if (num != 0) {
            tv_Num.setVisibility(View.VISIBLE);
            tv_Num.setText(num + "");
        }
        return totalProce;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_Remarks_writer://填写备注
                tv_Remarks_writer.setVisibility(View.GONE);
                et_Remarks.setVisibility(View.VISIBLE);
                BaseApplication.toast(R.string.Toast18);
                break;
            case R.id.iv_Hole_Pay:
                iv_Ordering_PayOnOline.setImageResource(R.drawable.chenked_out);
                iv_Hole_Pay.setImageResource(R.drawable.iocn_choosed_book);
                iv_Ordering_PayOnMoney.setImageResource(R.drawable.chenked_out);
                isHolePay = true;
                pay_type = "2";
                break;
            case R.id.iv_Ordering_PayOnOline:
                iv_Ordering_PayOnOline.setImageResource(R.drawable.iocn_choosed_book);
                iv_Hole_Pay.setImageResource(R.drawable.chenked_out);
                iv_Ordering_PayOnMoney.setImageResource(R.drawable.chenked_out);
                isHolePay = false;
                pay_type = "4";
                break;
            case R.id.iv_Ordering_PayOnMoney:
                iv_Ordering_PayOnOline.setImageResource(R.drawable.chenked_out);
                iv_Hole_Pay.setImageResource(R.drawable.chenked_out);
                iv_Ordering_PayOnMoney.setImageResource(R.drawable.iocn_choosed_book);
                pay_type = "1";
                isHolePay = true;
                break;


            case R.id.tv_Make_Order://下单
                //备注信息

//                if (isHolePay) {//酒店支付
////                   Intent intent=new Intent();
////                   intent.setClass(getApplicationContext(), Submit_OrdersActivity.class);
////                   Bundle bundle=new Bundle();
////                   bundle.putSerializable("OrderEntity", entity);
////                   bundle.putString("tab", tab);
////                   bundle.putString("Remarks",remarks);
////                   intent.putExtras(bundle);
////                   startActivity(intent);
//                    isHolePay = true;
//                } else {//在线支付
//                    isHolePay = false;
//                }
                for (int i = 0; i < myShopList.size(); i++) {
                    //  如果有任意一个服务商的id和第一个不相同就属于多服务商
                        Log.e("Pri", "----------->" + myShopList.get(i).getProvider_id());
                    if ( ! myShopList.get(0).getProvider_id().equals(myShopList.get(i).getProvider_id())){
                        isMoreServicer=true;
                        showMyDialog();
                        return;
                    }
                }
                isMoreServicer=false;
                initSubmitByNew();
//                initSubmit();
                break;

        }
    }
    private void showMyDialog(){
        final Dialog dialog = new Dialog(mContext, R.style.OrderDialog);
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View view = mInflater.inflate(R.layout.buss_dialog, null);
        dialog.setContentView(view);
        //
        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        TextView tvMessage = (TextView) view.findViewById(R.id.tvMessage);

//        tvTitle.setText(title);
//        tvMessage.setText(message);

        view.findViewById(R.id.tvCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.tvSure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                initSubmit();
                initSubmitByNew();

            }
        });
        dialog.show();
    }


    private void initSubmitByNew(){
        final List<JSONObject> list_json = new ArrayList<JSONObject>();
        for (int i = 0; i < myShopList.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("service_id", myShopList.get(i).getID());// 物品名称
                jsonObject.put("count", myShopList.get(i).getNum());
                list_json.add(jsonObject);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        final String finalPay_type = pay_type;
        String remarks = et_Remarks.getText().toString().trim();

        HttpClient.instance().addServicerOrderByNew(NewDoorFragment.list.get(NewDoorFragment.pos).getBook_id(),pay_type,remarks,list_json.toString(), new HttpCallBack() {

            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(OrderingInfoActivity.this,"正在下单中...");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
                AppContext.toast("下单失败");

            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                Log.e("NEWORder","------------------------>"+responseBean.toString());
                ProgressDialog.disMiss();
                Entitys data = responseBean.getData(Entitys.class);
                if (data.getCode()==1){//成功
                    //说明是订单号，成功下单，
                    SQLiteDatabase db = mydbHelper
                            .getSQLiteDatabase();
                    if (db != null) {
                        if (tab.equals("1")) {//购物
                            mydbHelper.onUpgrade_DefaultDB(db, 1, 2);
                        } else {//订餐
                            mydbHelper.onUpgrade(db, 1, 2);
                        }
                    } else {
                        BaseApplication.toast(R.string.Toast16);
                    }

                    try {
                        JSONObject jsonObject=new JSONObject(data.getResult());
                        String order_id= jsonObject.getString("order_id");
                        String total_price= jsonObject.getString("total_price");
                        String price= jsonObject.getString("price");
//                        Double totalPrices=Double.parseDouble(total_price);
                        if (isHolePay) {
                            if (!isMoreServicer){
                                String remarks = et_Remarks.getText().toString();
                                Intent intent = new Intent();
                                intent.setClass(getApplicationContext(), Submit_OrdersActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("OrderEntity", entity);//订单信息
    //                            bundle.putSerializable("myinfo_entity", myinfo_entity);// 我的信息
                                bundle.putString(Config.ORDER_TAB, tab);
                                bundle.putString(Config.REMARKS, remarks);
                                bundle.putString(Config.ORDER_NUMVER, order_id);//订单号
                                bundle.putString(Config.PAY_TYPE, finalPay_type);
                                bundle.putString(Config.MAINORORDER, "0");
                                bundle.putString(Config.ROOM_ID, NewDoorFragment.list.get(NewDoorFragment.pos).getRoom_id());
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                            }else {
                                //-------------------------------
                                Intent intentList = new Intent();
                                if (tab.equals("1")) {//购物
                                    intentList.setClass(getApplicationContext(), GoShoppingActivity.class);
                                } else {
                                    intentList.setClass(getApplicationContext(), OrderingActivity.class);
                                }
                                startActivity(intentList);
                                AppManager.getAppManager().finishActivity(OrderingInfoActivity.class);
                            }
                        } else {
                            Intent i = new Intent(getApplicationContext(), PayBillActivity.class);
                            //总价格
                            i.putExtra(Config.KEY_HOTEL_ROOMTOTALPRICE, Double.parseDouble(price));
                            //订单号
                            i.putExtra(Config.KEY_BOOKING_BILLNUM, order_id);
                            i.putExtra(Config.PAYACTION, Config.ORDERING);
                            i.putExtra(Config.ORDER_TAB, tab);
                            i.putExtra(Config.KEY_BILL_TYPE, Config.BILL_TYPE_SERVER);
                            i.putExtra(Config.MoreServicerOrderPrice, total_price);
                            i.putExtra(Config.MoreServicerOrderId, isMoreServicer);
                            i.putExtra(Config.ROOM_ID, NewDoorFragment.list.get(NewDoorFragment.pos).getRoom_id());
                            startActivity(i);
                            AppManager.getAppManager().finishActivity(OrderingInfoActivity.class);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }else if (data.getCode()==0){//失败
                    AppContext.toast(data.getMsg());
                }else {//

                    AppContext.toast("错误代码"+data.getCode()+"+"+data.getMsg());
                }
            }
        });






    }


    private void initSubmit() {
        final List<JSONObject> list_json = new ArrayList<JSONObject>();
        for (int i = 0; i < myShopList.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("service_id", myShopList.get(i).getID());// 物品名称
                jsonObject.put("quantity", myShopList.get(i).getNum());
                jsonObject.put("price", myShopList.get(i).getHotel_price());
                jsonObject.put("discount", 1);

                list_json.add(jsonObject);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        final String finalPay_type = pay_type;
        String remarks = et_Remarks.getText().toString().trim();



        //---------------------------------------------------------
        /*
        *   第一参数：备注
        *   第二参数 酒店id
        *   第三参数 总价格
        *   第四参数 折扣
        *   第五参数 房费或者在线支付
        *   第六参数 购物json
        *   第七参数
        *
        *   MainTabIndexFragment.list.get(MainTabIndexFragment.pos).getRoom_id() ,新添加数据为：房间id
        *   remarks 备注
        *
        *
        * */
        HttpClient.instance().addService(NewDoorFragment.list.get(NewDoorFragment.pos).getRoom_id(), remarks, NewDoorFragment.list.get(NewDoorFragment.pos).getID(), getTotalPrice() + "", "1", pay_type, list_json.toString(), NewDoorFragment.list.get(NewDoorFragment.pos).getBook_id(), new HttpCallBack() {

            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(mContext, R.string.Progress_Lo);
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                AppContext.toLog("xiadan" + responseBean.toString());
                try {
                    JSONObject jsonObject = new JSONObject(responseBean.toString());
                    String result = jsonObject.getString("result");
//                    BaseApplication.toast(result+"");
                    if (!result.equals("false")) {
                        if (isNumeric(result)) {
                            //说明是订单号，成功下单，
                            SQLiteDatabase db = mydbHelper
                                    .getSQLiteDatabase();
                            if (db != null) {
                                if (tab.equals("1")) {//购物
                                    mydbHelper.onUpgrade_DefaultDB(db, 1, 2);
                                } else {//订餐
                                    mydbHelper.onUpgrade(db, 1, 2);
                                }
                            } else {
                                BaseApplication.toast(R.string.Toast16);
                            }
                            if (isHolePay) {
                                String remarks = et_Remarks.getText().toString();
                                Intent intent = new Intent();
                                intent.setClass(getApplicationContext(), Submit_OrdersActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("OrderEntity", entity);//订单信息
//                            bundle.putSerializable("myinfo_entity", myinfo_entity);// 我的信息
                                bundle.putString(Config.ORDER_TAB, tab);
                                bundle.putString(Config.REMARKS, remarks);
                                bundle.putString(Config.ORDER_NUMVER, result);//订单号
                                bundle.putString(Config.PAY_TYPE, finalPay_type);
                                bundle.putString(Config.MAINORORDER, "0");
                                bundle.putString(Config.ROOM_ID, NewDoorFragment.list.get(NewDoorFragment.pos).getRoom_id());
                                intent.putExtras(bundle);
                                startActivity(intent);
                                AppManager.getAppManager().finishActivity(OrderingInfoActivity.class);
                            } else {
                                Intent i = new Intent(getApplicationContext(), PayBillActivity.class);
                                //总价格
                                i.putExtra(Config.KEY_HOTEL_ROOMTOTALPRICE, getTotalPrice());
                                //订单号
                                i.putExtra(Config.KEY_BOOKING_BILLNUM, result);
                                i.putExtra(Config.PAYACTION, Config.ORDERING);
                                i.putExtra(Config.ORDER_TAB, tab);
                                i.putExtra(Config.KEY_BILL_TYPE, Config.BILL_TYPE_SERVER);
                                i.putExtra(Config.ROOM_ID, NewDoorFragment.list.get(NewDoorFragment.pos).getRoom_id());
                                startActivity(i);
                                AppManager.getAppManager().finishActivity(OrderingInfoActivity.class);
                            }
                        }else {//下单失败 ，提示原因
                            JSONObject jsonObject1=jsonObject.getJSONObject("result");
                            AppContext.toast(jsonObject1.getString("msg"));
                        }
                    } else if (result.equals("false")) {
                        AppContext.toast("余额不支持下单或押金不足，请重新下单");
                    } else {
                        AppContext.toast("下单失败，请重新下单");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
            }

            @Override
            public void onFinish() {
                ProgressDialog.disMiss();
            }
        });

    }

    @Override
    public void onClick(View item, View widget, int position, int which) {
        double totalPrice = getTotalPrice();
        tv_Price.setText(" 共￥" + totalPrice);
        tv_Total_Price.setText(" 共￥" + totalPrice);
    }


    private void getMyinfo() {
        HttpClient.instance().getUser_Myinfo(new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                AppContext.toLog(responseBean.toString());
                myinfo_entity = responseBean.getData(MyInfoEntity.class);
                tv_Name.setText("  " + myinfo_entity.getName());
                tv_Phone.setText("  " + myinfo_entity.getPhone_no());
            }
        });
    }

    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }


}

package com.sht.smartlock.ui.activity.booking;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.MySSLSocketFactory;
import com.pingplusplus.android.PaymentActivity;
import com.pingplusplus.android.Pingpp;
import com.pingplusplus.android.PingppLog;
import com.sht.smartlock.api.base.BaseHttpClient;
import com.sht.smartlock.helper.HttpURLConnHelper;
import com.sht.smartlock.ui.activity.OrderFoodActivity;
import com.sht.smartlock.ui.activity.mine.GoShoppingActivity;
import com.sht.smartlock.ui.activity.mine.OrderingActivity;
import com.sht.smartlock.ui.entity.AuthResult;
import com.sht.smartlock.ui.entity.PayResult;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.AppManager;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.api.base.ResponseHandler;
import com.sht.smartlock.model.booking.PayChannel;
import com.sht.smartlock.ui.activity.mine.MyHotelOrderListActivity;
import com.sht.smartlock.ui.activity.Submit_OrdersActivity;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PayBillActivity extends BaseActivity implements View.OnClickListener {
    TextView tvBillnum, tvTotalPrice, tvNeedToPay;
    ImageView tvGoBack;
    Button btnEnsureToPay;
    ListView lvPayChannel;
    String strBillnum;
    String strSelectedChannel;
    double dbTotalPrice;
    List<PayChannel> mListPayChannel;
    PayChannelAdapter mPayChannelAdapter;
    String[] strChannelId = {"wx", "alipay"};
    String[] strChannelName = {"微信支付", "支付宝支付"};
    String[] strChannelIntroduction = {"推荐安装微信5.0及以上版本的使用", "推荐有支付宝账号的用户使用"};
    int[] drPicture = {R.drawable.weixin, R.drawable.alipay};
    private static final int REQUEST_CODE_PAYMENT = 1;
    DecimalFormat df = new java.text.DecimalFormat("#0.00");
    String payAction = Config.BOOKING; //支付成功跳转界面使用 默认为订酒店
    String room_mode;
    int bill_type;
    double avaliableBalance = 0;
    private String tab;
    private String room_id;
    private String hotelId;
    private IWXAPI api;
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    private boolean isMoreServicer=false;
    private String isMoreServicerPrice;

    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//                        Toast.makeText(PayBillActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        Intent i;
                        switch (payAction) {
                            case Config.BOOKING:
                                i = new Intent(mContext, PayResultActivity.class);
                                i.putExtra(Config.KEY_BOOKING_BILLNUM, strBillnum);
                                i.putExtra(Config.KEY_SHOW_ROOM_MODE, room_mode);
                                startActivity(i);
                                break;
                            case Config.ORDERING://购物，订餐支付
                                if (!isMoreServicer) {
                                    Intent intent = new Intent(getApplicationContext(), Submit_OrdersActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString(Config.ORDER_TAB, tab);
                                    bundle.putString(Config.ORDER_NUMVER, strBillnum);//订单号
                                    bundle.putString(Config.ROOM_ID, room_id);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }else {
                                    Intent intentList=new Intent();
                                    if (tab.equals("1")){//购物
                                        intentList.setClass(getApplicationContext(), GoShoppingActivity.class);
                                    }else {
                                        intentList.setClass(getApplicationContext(), OrderingActivity.class);
                                    }
                                    startActivity(intentList);
                                }
                                AppManager.getAppManager().finishActivity(PayBillActivity.class);
                                break;
                        }


                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(PayBillActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        Toast.makeText(PayBillActivity.this,
                                "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        // 其他状态值则为授权失败
                        Toast.makeText(PayBillActivity.this,
                                "授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();

                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;


    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitView();
        Intent i = getIntent();
        api = WXAPIFactory.createWXAPI(this, null);
        api.registerApp("wx58fb96f5bcbbb8d4");
        strBillnum = i.getStringExtra(Config.KEY_BOOKING_BILLNUM);
        dbTotalPrice = i.getDoubleExtra(Config.KEY_HOTEL_ROOMTOTALPRICE, 0);
        room_mode = i.getStringExtra(Config.KEY_SHOW_ROOM_MODE);
        bill_type = i.getIntExtra(Config.KEY_BILL_TYPE, 1);
        payAction = i.getStringExtra(Config.PAYACTION);
        hotelId = i.getStringExtra(Config.KEY_HOTEL_ID);
        tab = i.getStringExtra(Config.ORDER_TAB);
        room_id = i.getStringExtra(Config.ROOM_ID);

        if (payAction == null) {
            payAction = Config.BOOKING;
        }

        tvBillnum.setText(strBillnum);
        tvTotalPrice.setText(getString(R.string.currency_sign) + df.format(dbTotalPrice));
        tvNeedToPay.setText(getString(R.string.currency_sign) + df.format(dbTotalPrice));
        if (payAction.equals(Config.BOOKING)) {
            getMyBalance();
        }else if(payAction.equals(Config.ORDERING)){//服务订单
            //
            isMoreServicer=i.getBooleanExtra(Config.MoreServicerOrderId,false);
            if (isMoreServicer){//多服务订单
                isMoreServicerPrice= i.getStringExtra(Config.MoreServicerOrderPrice);//每个订单的价格[223,112]分为单位
                Log.e("TAGG","----------->"+isMoreServicerPrice);
                String s = strBillnum.substring(1, strBillnum.length() - 1);
                tvBillnum.setText(s);
            }

        }
        PingppLog.DEBUG = true;


    }

    private void getMyBalance() {
        HttpClient.instance().getMyAvaliableBalance(hotelId, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(mContext, "正在加载余额...");
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                String avaBalance = responseBean.getData();
                try {
                    avaliableBalance = Double.parseDouble(avaBalance);
                    //  avaliableBalance=1000;
                    //  avaBalance="1000";
                } catch (Exception e) {
                    toastFail("余额加载失败！");
                    avaliableBalance = -1;
                    avaBalance = "--";
                }
                avaBalance = "可用余额:" + avaBalance + "元";
                if (mListPayChannel.size() == 4) {
                    PayChannel payChannel = mListPayChannel.get(3);
                    payChannel.setIntroduction(avaBalance);
                    mPayChannelAdapter.notifyDataSetChanged();
                } else {
                    PayChannel payChannel = new PayChannel("balance", "余额支付", avaBalance, R.drawable.balance, false);
                    mListPayChannel.add(payChannel);
                    mPayChannelAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
                toastFail("加载余额失败，请稍候再试！");
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pay_bill;
    }

    protected boolean hasToolBar() {
        return false;
    }

    void InitView() {
        tvBillnum = (TextView) findViewById(R.id.tvBillnum);
        tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);
        tvNeedToPay = (TextView) findViewById(R.id.tvNeedToPay);
        tvGoBack = (ImageView) findViewById(R.id.goBack);
        btnEnsureToPay = (Button) findViewById(R.id.btnEnsureToPay);
        lvPayChannel = (ListView) findViewById(R.id.lvPayChannel);
        tvGoBack.setOnClickListener(this);
        btnEnsureToPay.setOnClickListener(this);
        mListPayChannel = new ArrayList<>();
        for (int i = 0; i < strChannelId.length; i++) {
            PayChannel payChannel = new PayChannel(strChannelId[i],
                    strChannelName[i],
                    strChannelIntroduction[i],
                    drPicture[i],
                    false);
            mListPayChannel.add(payChannel);
        }
        mPayChannelAdapter = new PayChannelAdapter(mContext, mListPayChannel);
        lvPayChannel.setAdapter(mPayChannelAdapter);
        lvPayChannel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PayChannel payChannel = (PayChannel) mPayChannelAdapter.getItem((int) id);

                //  LogUtil.log("strSelectedChannel=="+strSelectedChannel);
                if (payChannel.getId().equals(getString(R.string.balance))) {
                    if (avaliableBalance == -1) {
                        getMyBalance();
                        return;
                    } else if (avaliableBalance < dbTotalPrice) {
                        toastFail("可用余额不足以支付！");
                        //  strSelectedChannel=null;
                        return;
                    }
                }
                strSelectedChannel = payChannel.getId();
                mPayChannelAdapter.setSelect((int) id);
                mPayChannelAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goBack:
                // finish();
                if (payAction.equals(Config.ORDERING)) {
                    Intent intent = new Intent(getApplicationContext(), OrderFoodActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(Config.ORDER_TAB, tab);
//                    bundle.putString(Config.ORDER_NUMVER, strBillnum);//订单号
//                    bundle.putString(Config.ROOM_ID, room_id);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    AppManager.getAppManager().finishActivity(PayBillActivity.class);

                } else {
                    AppManager.getAppManager().finishToActivity(ShowHotelDetail02Activity.class, MyHotelOrderListActivity.class);
                }
                break;
            case R.id.btnEnsureToPay:
                if (strSelectedChannel == null) {
                    toastFail(getString(R.string.please_choose_pay_channel));
                    return;
                }

                int tempAmount = (int) (dbTotalPrice * 100);
//                AsyncHttpClient client = new AsyncHttpClient();
//                client.setTimeout(BaseHttpClient.HTTP_TIMEOUT);
//                client.addHeader(HTTP.CONTENT_TYPE,
//                        "application/x-www-form-urlencoded;charset=UTF-8");
//                client.setSSLSocketFactory(getSocketFactory());


                try {
                    Map<String, Object> params = new HashMap<>();
                    if (isMoreServicer){//多服务订单
                        params.put("amount", isMoreServicerPrice);
                    }else {
                        params.put("amount", String.valueOf(tempAmount));
                    }

                    params.put("billnum", strBillnum);
                    params.put("channel", strSelectedChannel);
                    params.put("billtype", bill_type);
                    //if(payAction.equals(""))

                    String method = "";
                    if (payAction.equals(Config.BOOKING)) {
//                        method = "book.getCharge_before";
                        method = HttpClient.instance().NEWONLINE_PAY;
                        params.put("hotel_id", hotelId);
                    } else {
//                        method = "pingpp.newPay";
//                        params.put("hotel_id", hotelId);
                        method = HttpClient.instance().SERVICERNEWONLINE_PAY;
                    }

                    JSONRPC2Request reqOut = new JSONRPC2Request(method, (Map<String, Object>) params, "123");
                    reqOut.appendNonStdAttribute("auth", AppContext.getShareUserSessinid());
//                    String myurl = "{\"id\":\"123\",\"method\":\"book.create_online_pay\",\"params\":{\"amount\":\"100\",\"billtype\":1,\"billnum\":\"6404\",\"hotel_id\":\"24\",\"channel\":\"wx\"},\"jsonrpc\":\"2.0\",\"auth\":\"5aefcfd4cd2b7f7b582051e4b4390a3c\"}";
                    //HttpURl
                    Log.e("paramss", reqOut.toString());
                    doHttpUrl(HttpClient.BASEPATH, reqOut.toString());


//                    StringEntity entity = new StringEntity(reqOut.toString(), HTTP.UTF_8);
//                    String url = getHttpsPath(HttpClient.BASEPATH);
//                    //  String url=getHttpsPath( "http://www.dreamboxs.cn:8088/smartlock/server.php");
//                    System.out.println("paramss=== " + reqOut.toString());
//                    Log.e("Tagg", "---------->url" + url + "entity==" + entity.toString());
//                    client.post(mContext, url, entity, "application/json-rpc", new ResponseHandler(mContext, new HttpCallBack() {
//                        @Override
//                        public void onSuccess(final ResponseBean responseBean) {
//                            //System.out.println(r);
//
//                            Log.e("Pay", "--------->" + responseBean.toString());
//                            ProgressDialog.disMiss();
//                            String data = responseBean.getData();
//                            if (data.equals("true")) {
//                                Intent i = new Intent(mContext, PayResultActivity.class);
//                                i.putExtra(Config.KEY_BOOKING_BILLNUM, strBillnum);
//                                i.putExtra(Config.KEY_SHOW_ROOM_MODE, room_mode);
//                                startActivity(i);
//                                return;
//                            } else if (data.equals("false")) {
//                                toastFail("支付失败！");
//                                return;
//                            }
//                            //原生支付接口
//                            if (strSelectedChannel.equals(strChannelId[0])) {//微信支付
//                                try {
//                                    //微信支付成功之后需要使用
//
//                                    AppContext.setProperty(Config.PAYDATAstrBillnum, strBillnum);
//                                    AppContext.setProperty(Config.PAYDATAroom_mode, room_mode);
//                                    AppContext.setProperty(Config.PAYDATAtab, tab);
//                                    AppContext.setProperty(Config.PAYDATAroom_id, room_id);
//                                    AppContext.setProperty(Config.PAYDATApayAction, payAction);
//
//                                    JSONObject jsonobject = new JSONObject(responseBean.toString());
//                                    JSONObject result = jsonobject.getJSONObject("result");
//                                    JSONObject json = result.getJSONObject("data");
//                                    PayReq req = new PayReq();
//                                    //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
//                                    req.appId = json.getString("appid");
//                                    req.partnerId = json.getString("partnerid");
//                                    req.prepayId = json.getString("prepayid");
//                                    req.nonceStr = json.getString("noncestr");
//                                    req.timeStamp = json.getString("timestamp");
//                                    req.packageValue = json.getString("package");
//                                    req.sign = json.getString("sign");
//                                    req.extData = "app data"; // optional
////                                    Toast.makeText(PayBillActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
//                                    Log.e("TAG", "------->PayReq" + req.toString());
//                                    // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
//                                    api.sendReq(req);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                    Log.e("TAG", "---------->" + e.toString());
//                                }
//                            } else if (strSelectedChannel.equals(strChannelId[1])) {//支付宝
//                                new Thread(new Runnable() {
//                                    @Override
//                                    public void run() {//子线程中调起支付宝
//                                        try {
//                                            JSONObject jsonObject = new JSONObject(responseBean.toString());
//                                            String result1 = jsonObject.getString("result");
//                                            PayTask alipay = new PayTask(PayBillActivity.this);
//                                            Map<String, String> result = alipay.payV2(result1, true);
//                                            Log.e("msp", result.toString());
//                                            Message msg = new Message();
//                                            msg.what = SDK_PAY_FLAG;
//                                            msg.obj = result;
//                                            mHandler.sendMessage(msg);
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                }).start();
//                            }
//                            //Pay++支付接口
////                            Pingpp.createPayment(PayBillActivity.this, data, "app_Ka9eHOSCW58GzTub");
//                        }
//
//                        @Override
//                        public void onFailure(String error, String message) {
//                            super.onFailure(error, message);
//                            //    System.out.println("paramss error"+error+message);
//                            ProgressDialog.disMiss();
//                            toastFail(getString(R.string.system_busy));
//                        }
//
//                        @Override
//                        public void onStart() {
//                            super.onStart();
//                            ProgressDialog.show(mContext, R.string.on_loading);
//                        }
//                    }));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    private void doHttpUrl(final String url, final String par) {
        ProgressDialog.show(mContext, R.string.on_loading);
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] bytes = HttpURLConnHelper.doPostSubmit(url, par);
                if (bytes == null) {
                    Log.e("PAY", "------------>null");
                } else {
                    final String str = new String(bytes);
                    Log.e("PAY", "------------>" + str);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ProgressDialog.disMiss();
                                final ResponseBean responseBean = new ResponseBean(str);
                                Log.e("Http","---------->>>>"+responseBean);
                                Log.d("paramss", responseBean.toString());
                                String data = responseBean.getData();
                                if (data.equals("true")) {
                                    Intent i = new Intent(mContext, PayResultActivity.class);
                                    i.putExtra(Config.KEY_BOOKING_BILLNUM, strBillnum);
                                    i.putExtra(Config.KEY_SHOW_ROOM_MODE, room_mode);
                                    startActivity(i);
                                    return;
                                } else if (data.equals("false")) {
                                    toastFail("支付失败！");
                                    return;
                                }
                                //原生支付接口
                                if (strSelectedChannel.equals(strChannelId[0])) {//微信支付
                                    try {
                                        //微信支付成功之后需要使用
                                        AppContext.setProperty(Config.ISMORESERVICER,isMoreServicer+"");
                                        AppContext.setProperty(Config.PAYDATAstrBillnum, strBillnum);
                                        AppContext.setProperty(Config.PAYDATAroom_mode, room_mode);
                                        AppContext.setProperty(Config.PAYDATAtab, tab);
                                        AppContext.setProperty(Config.PAYDATAroom_id, room_id);
                                        AppContext.setProperty(Config.PAYDATApayAction, payAction);

                                        JSONObject jsonobject = new JSONObject(responseBean.toString());
                                        JSONObject result = jsonobject.getJSONObject("result");
                                        JSONObject json = result.getJSONObject("data");
                                        PayReq req = new PayReq();
                                        //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
                                        req.appId = json.getString("appid");
                                        req.partnerId = json.getString("partnerid");
                                        req.prepayId = json.getString("prepayid");
                                        req.nonceStr = json.getString("noncestr");
                                        req.timeStamp = json.getString("timestamp");
                                        req.packageValue = json.getString("package");
                                        req.sign = json.getString("sign");
                                        req.extData = "app data"; // optional
//                                    Toast.makeText(PayBillActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
                                        Log.e("TAG", "------->PayReq" + req.toString());
                                        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                                        api.sendReq(req);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Log.e("TAG", "---------->" + e.toString());
                                    }
                                } else if (strSelectedChannel.equals(strChannelId[1])) {//支付宝
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {//子线程中调起支付宝
                                            try {
                                                JSONObject jsonObject = new JSONObject(responseBean.toString());
                                                String result1 = jsonObject.getString("result");
                                                PayTask alipay = new PayTask(PayBillActivity.this);
                                                Map<String, String> result = alipay.payV2(result1, true);
                                                Log.e("msp", result.toString());
                                                Message msg = new Message();
                                                msg.what = SDK_PAY_FLAG;
                                                msg.obj = result;
                                                mHandler.sendMessage(msg);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }).start();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //支付页面返回处理
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
//            if (resultCode == Activity.RESULT_OK) {
//                String result = data.getExtras().getString("pay_result");
//                /* 处理返回值
//                 * "success" - payment succeed
//                 * "fail"    - payment failed
//                 * "cancel"  - user canceld
//                 * "invalid" - payment plugin not installed
//                 */
//                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
//                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
//                //    showMsg(result, errorMsg, extraMsg);
//                if (result.equals("success")) {
//                    Intent i;
//                    switch (payAction) {
//                        case "booking":
//                            i = new Intent(mContext, PayResultActivity.class);
//                            i.putExtra(Config.KEY_BOOKING_BILLNUM, strBillnum);
//                            i.putExtra(Config.KEY_SHOW_ROOM_MODE, room_mode);
//                            startActivity(i);
//                            break;
//                        case Config.ORDERING://购物，订餐支付
////                            i=new Intent(mContext, OrderingSuccessActivity.class);
////                            i.putExtra(Config.KEY_BOOKING_BILLNUM, strBillnum);
////                            i.putExtra(Config.KEY_SHOW_ROOM_MODE, room_mode);
////                            i.putExtra(Config.KEY_HOTEL_ROOMTOTALPRICE, dbTotalPrice);
////                            i.putExtra(Config.ORDER_TAB,tab);
////                            i.putExtra(Config.ROOM_ID,room_id);
////                            startActivity(i);
////                            finish();
//                            Intent intent = new Intent(getApplicationContext(), Submit_OrdersActivity.class);
//                            Bundle bundle = new Bundle();
//                            bundle.putString(Config.ORDER_TAB, tab);
//                            bundle.putString(Config.ORDER_NUMVER, strBillnum);//订单号
//                            bundle.putString(Config.ROOM_ID, room_id);
//                            intent.putExtras(bundle);
//                            startActivity(intent);
//                            AppManager.getAppManager().finishActivity(PayBillActivity.class);
//
//                            break;
//                    }
//                } else {
//                    showMsg(result, errorMsg, extraMsg);
//                }
//            }
        }
    }

    public void showMsg(String title, String msg1, String msg2) {
        String str = title;
        if (null != msg1 && msg1.length() != 0) {
            str += "\n" + msg1;
        }
        if (null != msg2 && msg2.length() != 0) {
            str += "\n" + msg2;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(str);
        builder.setTitle("提示");
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }

    public class PayChannelAdapter extends BaseAdapter {

        Context mContext;
        List<PayChannel> mPayChannel;

        public PayChannelAdapter(Context context, List<PayChannel> payChannels) {
            mContext = context;
            mPayChannel = payChannels;
        }

        public void setSelect(int position) {
            for (int i = 0; i < mPayChannel.size(); i++) {
                if (i == position) {
                    mPayChannel.get(i).setIsSelected(true);
                } else {
                    mPayChannel.get(i).setIsSelected(false);
                }
            }
        }

        @Override
        public int getCount() {
            return mPayChannel.size();
        }

        @Override
        public Object getItem(int position) {
            return mPayChannel.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_pay_bill_channel_item, null);
                convertView.setTag(new ListCell((ImageView) convertView.findViewById(R.id.ivChannelPic),
                        (TextView) convertView.findViewById(R.id.tvChannelName),
                        (TextView) convertView.findViewById(R.id.tvChannelIntro),
                        (ImageView) convertView.findViewById(R.id.ivIsSelected)));
            }
            ListCell lc = (ListCell) convertView.getTag();
            PayChannel payChannel = mPayChannel.get(position);
            lc.getIvChannelPic().setBackgroundResource(payChannel.getIvPicture());
            lc.getTvChannleName().setText(payChannel.getName());
            lc.getTvChannelIntroduction().setText(payChannel.getIntroduction());
            lc.getIvIsSelected().setBackgroundResource(R.drawable.isselected);
            if (payChannel.isSelected() == false) {
                lc.getIvIsSelected().setBackgroundResource(R.drawable.unselector);
            } else {
                lc.getIvIsSelected().setBackgroundResource(R.drawable.iocn_choosed_book);
            }
            return convertView;
        }

        public class ListCell {
            ImageView ivChannelPic;
            TextView tvChannleName;
            TextView tvChannelIntroduction;
            ImageView ivIsSelected;

            public ListCell(ImageView ivChannelPic, TextView tvChannleName, TextView tvChannelIntroduction, ImageView ivIsSelected) {
                this.ivChannelPic = ivChannelPic;
                this.tvChannleName = tvChannleName;
                this.tvChannelIntroduction = tvChannelIntroduction;
                this.ivIsSelected = ivIsSelected;
            }

            public ImageView getIvChannelPic() {
                return ivChannelPic;
            }

            public void setIvChannelPic(ImageView ivChannelPic) {
                this.ivChannelPic = ivChannelPic;
            }

            public TextView getTvChannleName() {
                return tvChannleName;
            }

            public void setTvChannleName(TextView tvChannleName) {
                this.tvChannleName = tvChannleName;
            }

            public TextView getTvChannelIntroduction() {
                return tvChannelIntroduction;
            }

            public void setTvChannelIntroduction(TextView tvChannelIntroduction) {
                this.tvChannelIntroduction = tvChannelIntroduction;
            }

            public ImageView getIvIsSelected() {
                return ivIsSelected;
            }

            public void setIvIsSelected(ImageView ivIsSelected) {
                this.ivIsSelected = ivIsSelected;
            }
        }
    }

    public String getHttpsPath(String url) {
        String URLhttps = url.replace("http", "https");
        int portIndex = URLhttps.indexOf(":", 6);
        if (portIndex != -1) {
            int endIndex = URLhttps.indexOf("/", portIndex);
            String temp = URLhttps.substring(portIndex, endIndex);
            URLhttps = URLhttps.replace(temp, "");
        }
        return URLhttps;
    }

    private SSLSocketFactory getSocketFactory() {
        // TODO Auto-generated method stub
        SSLSocketFactory sslFactory = null;
        try {
            KeyStore keyStore = KeyStore.getInstance("BKS");
            InputStream instream = this.getResources().openRawResource(
                    R.raw.client);
            keyStore.load(instream, "wangkewang".toCharArray());
            sslFactory = new MySSLSocketFactory(keyStore);
        } catch (KeyStoreException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (NoSuchAlgorithmException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (CertificateException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (UnrecoverableKeyException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (KeyManagementException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sslFactory;
    }

    public static SchemeRegistry getSchemeRegistry(Context context) {
        try {
            InputStream input = null;

            input = context.getResources().openRawResource(R.raw.client);

            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(input, "wangkewang".toCharArray());
            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            //   sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//            HttpParams params = new BasicHttpParams();
//            HttpConnectionParams.setConnectionTimeout(params, 10000);
//            HttpConnectionParams.setSoTimeout(params, 10000);2
//            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            SchemeRegistry registry = new SchemeRegistry();
            //  registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));
            return registry;
        } catch (Exception e) {
            return null;
        }
    }
}

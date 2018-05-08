package com.sht.smartlock.ui.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.MySSLSocketFactory;
import com.pingplusplus.android.PaymentActivity;
import com.pingplusplus.android.Pingpp;
import com.sht.smartlock.AppManager;
import com.sht.smartlock.api.base.BaseHttpClient;
import com.sht.smartlock.helper.HttpURLConnHelper;
import com.sht.smartlock.ui.activity.booking.PayResultActivity;
import com.sht.smartlock.ui.entity.AuthResult;
import com.sht.smartlock.ui.entity.PayResult;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.api.base.ResponseHandler;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.model.booking.PayChannel;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.adapter.RechargeAdapter;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RechargeActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_AliPay,iv_WinXinPay,iv_BankPay;
    private ImageView tv_Recharge;
    private int n=1;
    private ListView lv_PayMede;
    private RechargeAdapter mRechargeAdapter;
    List<PayChannel> mListPayChannel;
    String[] strChannelId = {"alipay","wx", "upacp"};
//    String[] strChannelName = {"微信支付", "银行卡支付", "支付宝支付"};
//    String[] strChannelIntroduction = {"推荐安装微信5.0及以上版本的使用", "支持储蓄卡信用卡，无需开通网银", "推荐有支付宝账号的用户使用"};
    int[] drPicture = {R.drawable.weixin, R.drawable.bankcard, R.drawable.alipay};
    private TextView tv_Lock;
    private EditText et_Money;
    private TextView tv_Gift;
    private double dbTotalPrice=0.1;
    private  int bill_type=3;
    private static final int REQUEST_CODE_PAYMENT = 1;
    private String holderID="";
    private String holderName="";
    private String holderGift="";
    private LinearLayout lin_Alipay,lin_Weixin,lin_Bankcard;
    private String recharge_account_user_id;

    private IWXAPI api;
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

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
                        Intent intent=new Intent(getApplicationContext(),RechargeSuccessActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(RechargeActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(RechargeActivity.this,
                                "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        // 其他状态值则为授权失败
                        Toast.makeText(RechargeActivity.this,
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
//        setContentView(R.layout.activity_recharge);
        Intent intent=getIntent();
        api = WXAPIFactory.createWXAPI(this, null);
        api.registerApp("wx58fb96f5bcbbb8d4");
        holderID= intent.getStringExtra(Config.AMOUNT_ID);
        holderName= intent.getStringExtra(Config.HOTELNAME);
        holderGift= intent.getStringExtra(Config.COMMENT);
        recharge_account_user_id=intent.getStringExtra(Config.BALANCE_ID);
        onBack();
        initView();
        setOnClicListener();
//        CanTrue();
    }

    //获取布局文件
    @Override
    protected int getLayoutId() {
        return R.layout.activity_recharge;
    }

    protected boolean hasToolBar() {
        return false;
    }
    private void onBack(){
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView(){

        tv_Lock = (TextView) findViewById(R.id.tv_Lock);
        et_Money = (EditText) findViewById(R.id.et_Money);
        tv_Gift = (TextView) findViewById(R.id.tv_Gift);
        iv_AliPay = (ImageView) findViewById(R.id.iv_AliPay);
        iv_WinXinPay = (ImageView) findViewById(R.id.iv_WinXinPay);
        iv_BankPay = (ImageView) findViewById(R.id.iv_BankPay);
        tv_Recharge = (ImageView) findViewById(R.id.tv_Recharge);
        lin_Alipay = (LinearLayout) findViewById(R.id.lin_Alipay);
        lin_Weixin = (LinearLayout) findViewById(R.id.lin_Weixin);
        lin_Bankcard = (LinearLayout) findViewById(R.id.lin_Bankcard);

        tv_Lock.setText(holderName);
        tv_Gift.setText(holderGift);

//        lv_PayMede = (ListView) findViewById(R.id.lv_PayMede);
//        mListPayChannel = new ArrayList<>();
//        for (int i = 0; i < strChannelId.length; i++) {
//            PayChannel payChannel = new PayChannel(strChannelId[i],
//                    strChannelName[i],
//                    strChannelIntroduction[i],
//                    drPicture[i],
//                    false);
//            mListPayChannel.add(payChannel);
//        }
//
//        mRechargeAdapter=new RechargeAdapter(getApplicationContext(),mListPayChannel);

    }

    private void setOnClicListener(){
        iv_AliPay.setOnClickListener(this);
        iv_WinXinPay.setOnClickListener(this);
        iv_BankPay.setOnClickListener(this);
        tv_Recharge.setOnClickListener(this);
        lin_Alipay.setOnClickListener(this);
        lin_Weixin.setOnClickListener(this);
        lin_Bankcard.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_AliPay://支付宝
            case R.id.lin_Alipay:
                n=1;
                iv_AliPay.setImageResource(R.drawable.true_01);
                iv_WinXinPay.setImageResource(R.drawable.fase_01);
                iv_BankPay.setImageResource(R.drawable.fase_01);
                break;
            case R.id.iv_WinXinPay://微信
            case R.id.lin_Weixin:
                n=2;
                iv_AliPay.setImageResource(R.drawable.fase_01);
                iv_WinXinPay.setImageResource(R.drawable.true_01);
                iv_BankPay.setImageResource(R.drawable.fase_01);
                break;
            case R.id.iv_BankPay://银联
            case R.id.lin_Bankcard:
                n=3;
                iv_AliPay.setImageResource(R.drawable.fase_01);
                iv_WinXinPay.setImageResource(R.drawable.fase_01);
                iv_BankPay.setImageResource(R.drawable.true_01);
                break;
            case R.id.tv_Recharge://去充值
                if (!holderID.equals("")) {
                    String money =et_Money.getText().toString().trim();
                    if (money.equals("")){
                        BaseApplication.toast("请输入金额");
                        return;
                    }
                    double mon=Double.parseDouble(money);
                    getRecharge_account_id(holderID,mon);
                }
//                CanTrue();
//                startActivity(new Intent(getApplicationContext(),RechargeSuccessActivity.class));

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //支付页面返回处理
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                /* 处理返回值
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceld
                 * "invalid" - payment plugin not installed
                 */
                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
                //    showMsg(result, errorMsg, extraMsg);
                AppContext.toLog("result=="+result+"errorMsg=="+errorMsg+"extraMsg=="+extraMsg);
                if (result.equals("success")) {
                    Intent intent=new Intent(getApplicationContext(),RechargeSuccessActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }

    }

    private void getRecharge_account_id(String holderID, final double dbTotalPrice){
        HttpClient.instance().get_or_create_recharge_user(holderID, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                try {
                    JSONObject jsonObject = new JSONObject(responseBean.toString());
                    AppContext.toLog(responseBean.toString());
                    String result = jsonObject.getString("result");
                    if (result.equals("0")) {
                        AppContext.toast("该酒店不支持充值");
//                        BaseApplication.toast("充值失败请刷新");
                    } else {
                        if (n == 1) {
                            toPayByOnline(strChannelId[0], dbTotalPrice, result);
                        } else if (n == 2) {
                            toPayByOnline(strChannelId[1], dbTotalPrice, result);
                        } else if (n == 3) {
                            toPayByOnline(strChannelId[2], dbTotalPrice, result);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    BaseApplication.toast("异常：" + e.toString());
                }
            }
        });
    }

    private void toPayByOnline(final String strSelectedChannel,double dbTotalPrice,String strBillnum){
        int tempAmount = (int) (dbTotalPrice * 100);
//        AsyncHttpClient client = new AsyncHttpClient();
//        client.setTimeout(BaseHttpClient.HTTP_TIMEOUT);
//        client.addHeader(HTTP.CONTENT_TYPE,
//                "application/x-www-form-urlencoded;charset=UTF-8");
//        client.setSSLSocketFactory(getSocketFactory());
        try {
            Map<String,Object> params = new HashMap<>();

            params.put("billnum", strBillnum);
            params.put("amount", String.valueOf(tempAmount));
            params.put("channel", strSelectedChannel);
            params.put("billtype", bill_type);
//            String method = "pingpp.newPay";
            String method = HttpClient.instance().SERVICERNEWONLINE_PAY;
            JSONRPC2Request reqOut = new JSONRPC2Request(method, (Map<String, Object>) params, "123");
            reqOut.appendNonStdAttribute("auth", AppContext.getShareUserSessinid());

            StringEntity entity = new StringEntity(reqOut.toString(), HTTP.UTF_8);
            String url=getHttpsPath(HttpClient.BASEPATH);
            Log.e("TAG","===----------->url"+url);
            //  String url=getHttpsPath( "http://www.dreamboxs.cn:8088/smartlock/server.php");
            System.out.println("paramss "+reqOut.toString());

            doHttpUrl(HttpClient.BASEPATH,reqOut.toString(),strSelectedChannel);

//            client.post(mContext, url, entity, "application/json-rpc", new ResponseHandler(mContext, new HttpCallBack() {
//                @Override
//                public void onSuccess(final ResponseBean responseBean) {
//                    //System.out.println(r);
//                    ProgressDialog.disMiss();
//                    String data = responseBean.getData();
//                    //Pay++支付调用
////                    Pingpp.createPayment(RechargeActivity.this, data, "app_Ka9eHOSCW58GzTub");
//
//                    //原生的支付调用
//                    switch (strSelectedChannel) {
//                        case "alipay"://支付宝
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {//子线程中调起支付宝
//                                    try {
//                                        JSONObject jsonObject = new JSONObject(responseBean.toString());
//                                        String result1 = jsonObject.getString("result");
//                                        PayTask alipay = new PayTask(RechargeActivity.this);
//                                        Map<String, String> result = alipay.payV2(result1, true);
//                                        Log.e("msp", result.toString());
//                                        Message msg = new Message();
//                                        msg.what = SDK_PAY_FLAG;
//                                        msg.obj = result;
//                                        mHandler.sendMessage(msg);
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }).start();
//
//                            break;
//                        case "wx"://微信
//                            try {
//                                //微信支付成功之后需要使用
//                                AppContext.setProperty(Config.PAYDATApayAction,Config.PAYDATARechargeActivity);
//                                JSONObject jsonobject = new JSONObject(responseBean.toString());
//                                JSONObject result = jsonobject.getJSONObject("result");
//                                JSONObject json = result.getJSONObject("data");
//                                PayReq req = new PayReq();
//                                //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
//                                req.appId = json.getString("appid");
//                                req.partnerId = json.getString("partnerid");
//                                req.prepayId = json.getString("prepayid");
//                                req.nonceStr = json.getString("noncestr");
//                                req.timeStamp = json.getString("timestamp");
//                                req.packageValue = json.getString("package");
//                                req.sign = json.getString("sign");
//                                req.extData = "app data"; // optional
////                                    Toast.makeText(PayBillActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
//                                Log.e("TAG", "------->PayReq" + req.toString());
//                                // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
//                                api.sendReq(req);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                                Log.e("TAG", "---------->" + e.toString());
//                            }
//                            break;
//                    }
//
//
//                }
//
//                @Override
//                public void onFailure(String error, String message) {
//                    super.onFailure(error, message);
//                    //    System.out.println("paramss error"+error+message);
//                    ProgressDialog.disMiss();
//                    toastFail(getString(R.string.system_busy));
//                }
//
//                @Override
//                public void onStart() {
//                    super.onStart();
//                    ProgressDialog.show(mContext, R.string.on_loading);
//                }
//            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public String getHttpsPath(String url){
        String URLhttps=url.replace("http","https");
        int portIndex=URLhttps.indexOf(":",6);
        if(portIndex!=-1){
            int endIndex=URLhttps.indexOf("/",portIndex);
            String temp=URLhttps.substring(portIndex,endIndex);
            URLhttps=URLhttps.replace(temp,"");
        }
        Log.e("TAG","===----------->"+URLhttps);
        return URLhttps;
    }
    //判断该账号是否可以充值
    private void CanTrue(){
        HttpClient.instance().rechare_before(recharge_account_user_id, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                Log.e("TAA", "---------------------->" + responseBean.toString() + "");
                try {
                    JSONObject jsonObject = new JSONObject(responseBean.toString());
                    String result = jsonObject.getString("result");
                    if (result.equals("1")) {//可以充值
//                        if (!holderID.equals("")) {
//                            String money =et_Money.getText().toString().trim();
//                            if (money.equals("")){
//                                BaseApplication.toast("请输入金额");
//                                return;
//                            }
//                            double mon=Double.parseDouble(money);
//                            getRecharge_account_id(holderID,mon);
//                        }
                    } else {
                        //不可以充值
                        AppContext.toast("该账号不允许充值");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    AppContext.toast("该酒店一次，暂不支持充值");
                }
            }
        });
    }

    private void doHttpUrl(final String url, final String par,final String strSelectedChannel) {
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
                                Log.e("Http", "---------->>>>" + responseBean);

                                String data = responseBean.getData();
                                //Pay++支付调用

                                //原生的支付调用
                                switch (strSelectedChannel) {
                                    case "alipay"://支付宝
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {//子线程中调起支付宝
                                                try {
                                                    JSONObject jsonObject = new JSONObject(responseBean.toString());
                                                    String result1 = jsonObject.getString("result");
                                                    PayTask alipay = new PayTask(RechargeActivity.this);
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

                                        break;
                                    case "wx"://微信
                                        try {
                                            //微信支付成功之后需要使用
                                            AppContext.setProperty(Config.PAYDATApayAction,Config.PAYDATARechargeActivity);
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
                                        break;
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




}

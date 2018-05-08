package com.sht.smartlock.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.myview.CountDownTimerUtils;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class NewPhoneActivity extends BaseActivity implements View.OnClickListener{
    private EditText edPhone,etCode;
    private TextView tvGetCode,tvGoTo;
    private CountDownTimerUtils mCountDownTimerUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_new_phone);
        initView();
        setOnClisktenter();
        onBack();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_phone;
    }

    @Override
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
        edPhone = (EditText) findViewById(R.id.edPhone);
        etCode = (EditText) findViewById(R.id.etCode);
        tvGetCode= (TextView) findViewById(R.id.tvGetCode);
        tvGoTo= (TextView) findViewById(R.id.tvGoTo);
    }

    private void setOnClisktenter(){
        tvGetCode.setOnClickListener(this);
        tvGoTo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvGetCode:
                String phone1 = edPhone.getText().toString().trim();
                if (phone1.isEmpty()){
                    AppContext.toast("请输入手机号码");
                    return;
                }
                mCountDownTimerUtils = new CountDownTimerUtils(tvGetCode, 60000, 1000);
                mCountDownTimerUtils.start();
                isbind(phone1);
                break;
            case R.id.tvGoTo:
                String phone = edPhone.getText().toString().trim();
                String code = etCode.getText().toString().trim();
                if (phone.isEmpty()||code.isEmpty()){
                    AppContext.toast("请输入手机号码和验证码");
                    return;
                }
                initData(phone,code );
                break;
        }
    }

    private void isbind(final String phone){
        HttpClient.instance().isbindphoneNo(phone, new HttpCallBack() {
            @Override
            public void onStart() {
                ProgressDialog.show(NewPhoneActivity.this, "正在发送验证码，请稍等");
//                ProgressDialog.show(NewPhoneActivity.this,"");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
                mCountDownTimerUtils.cancel();
                mCountDownTimerUtils.onFinish();
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                String data = responseBean.getData();
                if (data.equals("true")){
                    getCode(phone);
                }else {
                    AppContext.toast("该号码已经绑定其他账号，请换其他号码");
                    mCountDownTimerUtils.cancel();
                    mCountDownTimerUtils.onFinish();
                    ProgressDialog.disMiss();
                }
            }
        });
    }

    private void getCode(String phone){
        HttpClient.instance().firstGetPhoneCode(phone, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
            }
            @Override
            public void onSuccess(ResponseBean responseBean) {
                try {
                    ProgressDialog.disMiss();
                    JSONObject jsonObject = new JSONObject(responseBean.toString());
                    String result = jsonObject.getString("result");
                    if (result.equals("true")) {
                        AppContext.toast("验证码已发送，请查收");
                    } else {
                        AppContext.toast("验证码发送失败，请重新获取");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    private void  initData(final String phone,String code){
        HttpClient.instance().firstBindPhoneNo(phone, code, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(NewPhoneActivity.this, "正在绑定手机号码，请稍等");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
            }
            @Override
            public void onSuccess(ResponseBean responseBean) {
                try {
                    ProgressDialog.disMiss();
                    JSONObject jsonObject=new JSONObject(responseBean.toString());
                    String result= jsonObject.getString("result");
                    if (result.equals("true")){
                        AppContext.toast("手机号码绑定成功");
                        AppContext.setProperty(Config.USERPHONE, phone);
                        finish();
                    }else if(result.equals("-1")) {
                        AppContext.toast("验证码错误");
                    }else if(result.equals("-2")) {
                        AppContext.toast("该手机号已经绑定其他账号，不能绑定该账号");
                    }else  {
                        AppContext.toast("手机号码绑定失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}

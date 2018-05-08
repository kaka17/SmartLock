package com.sht.smartlock.ui.activity;

import android.content.Intent;
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

public class OldPhoneActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvPhone;
    private EditText etCode;
    private TextView tvGetCode,tvGoTo;
    private String phone;
    private  CountDownTimerUtils mCountDownTimerUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_old_phone);
        phone= getIntent().getExtras().getString(Config.OLDPHONE);
        initView();
        setOnClisktenter();
        onBack();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_old_phone;
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
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        etCode = (EditText) findViewById(R.id.etCode);
        tvGetCode= (TextView) findViewById(R.id.tvGetCode);
        tvGoTo= (TextView) findViewById(R.id.tvGoTo);
        tvPhone.setText(phone);
    }

    private void setOnClisktenter(){
        tvGetCode.setOnClickListener(this);
        tvGoTo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvGetCode:
                getCode();
                mCountDownTimerUtils = new CountDownTimerUtils(tvGetCode, 60000, 1000);
                mCountDownTimerUtils.start();
                break;
            case R.id.tvGoTo:
                String code = etCode.getText().toString().trim();
                if (code.isEmpty()){
                    AppContext.toast("请输入验证码");
                    return;
                }
                goToIsTrue(code);
                break;
        }

    }
    private void getCode(){
        HttpClient.instance().PhoneNogetCode(new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(OldPhoneActivity.this, "正在获取验证码，请稍等");
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
                try {
                    ProgressDialog.disMiss();
                    JSONObject jsonObject=new JSONObject(responseBean.toString());
                    String result= jsonObject.getString("result");
                    if (result.equals("true")){
                        AppContext.toast("验证码已发送，请查收");
                    }else {
                        AppContext.toast("验证码发送失败，请重新获取");
                        mCountDownTimerUtils.cancel();
                        mCountDownTimerUtils.onFinish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void goToIsTrue(String code){
        HttpClient.instance().OldPhoneCodeIsTrue(code, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(OldPhoneActivity.this,"正在验证手机，请稍等");
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
                       Intent intent=new Intent(getApplicationContext(),NewPhoneActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        AppContext.toast("验证码错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}

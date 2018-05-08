package com.sht.smartlock.ui.activity.mine;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.Base64Utils;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.model.UserLoginInfo;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private ImageView regist_image_logo;
    private Button sensmsButton, verificationButton, countryButton;
    private Button btnLogin;
    private TextView tvTitlePanel;
    private ImageView btn_Cancle;
    private EditText etPassWord;
    private EditText etPhone, etVerCode;
    String afterencrypt = "";
    public String phString;
    private String pwdString;
    int retrySeconds = 180;
    int recLen = retrySeconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitView();
    }

    private void InitView() {
        regist_image_logo = (ImageView)findViewById(R.id.regist_image_logo);
        btn_Cancle = (ImageView) findViewById(R.id.btn_cancle);
        sensmsButton = (Button) findViewById(R.id.btnGetVerificationCode);
        verificationButton = (Button) findViewById(R.id.btnRegister);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        etPhone = (EditText) findViewById(R.id.editPhoneNumber);
        etVerCode = (EditText) findViewById(R.id.editVerificationCode);
        etPassWord = (EditText) findViewById(R.id.editPassWord);
        tvTitlePanel = (TextView) findViewById(R.id.tvTitlePanel);
        tvTitlePanel.setText(getString(R.string.register));
        sensmsButton.setOnClickListener(this);
        verificationButton.setOnClickListener(this);
        btn_Cancle.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }

    private boolean valiatePhoneNum(String num) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,7,8,9][0-9]{9}$"); // 验证手机号
        m = p.matcher(num);
        b = m.matches();
        if (!b) {
            toastFail(getString(R.string.please_enter_correct_phonenum));
        }
        return b;
    }

    private boolean valiate() {
        if (!valiatePhoneNum(etPhone.getText().toString())) {
            return false;
        }
        if (etVerCode.getText().length() == 0) {
            toastFail(getString(R.string.ver_code_can_not_empty));
            return false;
        }
        if (etPassWord.getText().toString().trim().length() == 0) {
            toastFail(getString(R.string.password_can_not_be_empty));
            return false;
        }
        if (etPassWord.getText().toString().length() < 6) {
            toastFail(getString(R.string.password_length_must_six_bit_at_least));
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btnGetVerificationCode://获取验证码

                if (!TextUtils.isEmpty(etPhone.getText().toString())) {
                    if (!valiatePhoneNum(etPhone.getText().toString())) return;

                    phString = etPhone.getText().toString();
                    HttpClient.instance().getRegisterVerCode(etPhone.getText().toString(), new HttpCallBack() {
                        @Override
                        public void onStart() {
                            super.onStart();
                            countHander.postDelayed(runnable, 1000);
                            sensmsButton.setEnabled(false);
                        }

                        @Override
                        public void onFailure(String error, String message) {
                            super.onFailure(error, message);
                            toastFail(getString(R.string.get_ver_code_fail));
                            sensmsButton.setEnabled(true);
                            recLen=0;
                        }

                        @Override
                        public void onSuccess(ResponseBean responseBean) {
                            if (responseBean.getData().equals("true")) {
                              //  toastSuccess(getString(R.string.get_ver_code_success));
                             //   recLen = retrySeconds;
                            }

                           else if (responseBean.getData().equals("false")) {
//                                toastFail(getString(R.string.get_ver_code_fail));
//                                sensmsButton.setEnabled(true);
//                                recLen=0;
                            } else {
                                toastFail(getString(R.string.user_was_been_register));
                                sensmsButton.setEnabled(true);
                                recLen=0;
                            }
                        }
                    });

                } else {
                    Toast.makeText(this, R.string.phone_can_not_be_empty, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btnRegister://注册
                if (!valiate()) return;
                ProgressDialog.show(mContext, R.string.on_loading);
                pwdString = etPassWord.getText().toString();
                afterencrypt = Base64Utils.encodeStr(pwdString);
                if (!pwdString.equals("") || !phString.equals("")) {
                    HttpClient.instance().register(phString, pwdString, etVerCode.getText().toString(), new NetworkRequestRegistResult());
                }
                break;
            case R.id.btnLogin:
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                RegisterActivity.this.finish();
                break;
            case R.id.btn_cancle:
                RegisterActivity.this.finish();
                break;
            default:
                break;
        }
    }

    Handler countHander = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            recLen--;
            sensmsButton.setText(getString(R.string.retry)+"(" + recLen + ")");
            if (recLen > 0) {
                countHander.postDelayed(this, 1000);
            } else {
                sensmsButton.setEnabled(true);
                sensmsButton.setText(R.string.retry_to_send);
                recLen=retrySeconds;
            }
        }
    };

    class NetworkRequestRegistResult extends HttpCallBack {
        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onSuccess(ResponseBean responseBean) {
            ProgressDialog.disMiss();
            LogUtil.log("regist=="+responseBean.toString());
            if (responseBean.isFailure()) {
                BaseApplication.toast(responseBean.toString());
                return;
            }
            try {
                UserLoginInfo userLogin = responseBean.getData(UserLoginInfo.class);

                AppContext.setProperty(Config.SHARE_USER_ACCOUNT, userLogin.getUser_name());
                AppContext.setProperty(Config.SHARE_USERSESSIONID, userLogin.getSessionid());
                //环信账号
                AppContext.setProperty(Config.EMID,userLogin.getEmid());
                AppContext.setProperty(Config.SHARE_VOIP_ACCOUNT, userLogin.getVoip_account());
                AppContext.setProperty(Config.SHARE_VOIP_PED, userLogin.getVoip_pwd());
              //  Toast.makeText(BaseApplication._context, "注册成功,已为您自动跳转！", Toast.LENGTH_LONG).show();
                Toast.makeText(BaseApplication._context, R.string.jump_for_you_after_register_success, Toast.LENGTH_LONG).show();
                AppContext.setProperty(Config.SHARE_USERPWD, afterencrypt);
                Timer timer = new Timer();
                TimerTask tast = new TimerTask() {
                    @Override
                    public void run() {
                        finish();
                    }
                };
                timer.schedule(tast, 2000);

            } catch (Exception e) {
                BaseApplication.toastFail(responseBean.getData());
                etPassWord.setText("");
                etVerCode.setText("");
            }
        }

        @Override
        public void onFailure(String error, String message) {
            super.onFailure(error, message);
            toastFail(getString(R.string.try_after_later_when_register_fail));
        }

        @Override
        public void onFinish() {
            super.onFinish();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }
}

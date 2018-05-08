package com.sht.smartlock.ui.activity.mine;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.Base64Utils;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.RSAUtils;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.model.UserLoginInfo;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.myview.CountDownTimerUtils;
import com.sht.smartlock.ui.entity.Entitys;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;
import org.json.JSONObject;
import org.json.JSONTokener;


import java.security.PublicKey;


public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener{
    private ImageView btn_cancle;
    private TextView tvTitlePanel;

    private EditText editRestVerificationCode;
    private EditText editRestNewPassword;
    private EditText editRepeatNewPassword;
    private Button btnRepeatPwdSave;
    private TextView btnResetPassword;

    private boolean btnVStatus;
    private boolean btnNStatus;
    private boolean btnVNStatus;
    private String strPhone;
    private String veriFicationCode;
    private String newPassword;
    private String repeatPassword;

    int retrySeconds = 180;
    int recLen = retrySeconds;
    private CountDownTimerUtils mCountDownTimerUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findviewbyid();
        setClickLister();

        Bundle bundle = this.getIntent().getExtras();
        if(!bundle.getString("ForPhoneNumber").equals("")||
                !bundle.getString("ForPhoneNumber").equals("null")){
            strPhone = bundle.getString("ForPhoneNumber");
        }

//        LogUtil.log("bundle="+bundle.getString("ForPhoneNumber"));
    }


    private void findviewbyid() {
        btn_cancle = (ImageView) findViewById(R.id.btn_cancle);
        tvTitlePanel = (TextView) findViewById(R.id.tvTitlePanel);

        editRestVerificationCode = (EditText)findViewById(R.id.editRestVerificationCode);
        editRestNewPassword = (EditText)findViewById(R.id.editRestNewPassword);
        editRepeatNewPassword = (EditText)findViewById(R.id.editRepeatNewPassword);
        btnRepeatPwdSave = (Button)findViewById(R.id.btnRepeatPwdSave);
        btnResetPassword = (TextView)findViewById(R.id.btnResetPassword);
        tvTitlePanel.setText("重置密码");
        btnRepeatPwdSave.setEnabled(true);
        mCountDownTimerUtils = new CountDownTimerUtils(btnResetPassword, 60000, 1000);
        mCountDownTimerUtils.start();
//        HttpClient.instance().reset_user_login_pwd("322","",new NetworkRequestLoginResult());

//
    }

    @Override
    public void onClick(View view) {
       finish();
    }

    private void setClickLister(){
        btn_cancle.setOnClickListener(this);
        btnRepeatPwdSave.setOnClickListener(listener);
        btnResetPassword.setOnClickListener(listener);
    }





    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_cancel:
                    finish();
                    break;
                case R.id.btnRepeatPwdSave://修改密码
                    if(!valiate())return;
                    veriFicationCode = editRestVerificationCode.getText().toString();
                    newPassword = editRestNewPassword.getText().toString();
                    repeatPassword = editRepeatNewPassword.getText().toString();
                    if(!newPassword.equals(repeatPassword)){
                        editRestNewPassword.setText("");
                        editRepeatNewPassword.setText("");
                        BaseApplication.toast("密码不一致，请重新输入！");
                        return;
                    }
                    HttpClient.instance().reset_user_login_pwd(strPhone,repeatPassword,veriFicationCode,new NetworkRequestRegistResult());
                    break;
                case R.id.btnResetPassword:
                    HttpClient.instance().request_verify_code(strPhone, new HttpCallBack() {

                        @Override
                        public void onStart() {
                            super.onStart();
                            mCountDownTimerUtils = new CountDownTimerUtils(btnResetPassword, 60000, 1000);
                            mCountDownTimerUtils.start();
                        }

                        @Override
                        public void onFailure(String error, String message) {
                            super.onFailure(error, message);
                            mCountDownTimerUtils.cancel();
                            mCountDownTimerUtils.onFinish();
                        }

                        @Override
                        public void onSuccess(ResponseBean responseBean) {
                            if (responseBean.getData().equals("true")) {
                                  AppContext.toast("验证码获取成功");
                                //   recLen = retrySeconds;
                            }

                            else if (responseBean.getData().equals("false")) {
//                                toastFail(getString(R.string.get_ver_code_fail));
//                                sensmsButton.setEnabled(true);
//                                recLen=0;
                                mCountDownTimerUtils.cancel();
                                mCountDownTimerUtils.onFinish();
                            } else {
                                toastFail("获取失败");
                                btnResetPassword.setEnabled(true);
                                recLen=0;
                            }
                        }
                    });
                    break;
            }
        }
    };

    class NetworkRequestRegistResult extends HttpCallBack{
        @Override
        public void onFailure(String error, String message) {
            super.onFailure(error, message);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            ProgressDialog.disMiss();
        }

        @Override
        public void onStart() {
            super.onStart();
            ProgressDialog.show(ResetPasswordActivity.this,"正在提交");
        }

        @Override
        public void onSuccess(ResponseBean responseBean) {
            ProgressDialog.disMiss();
            LogUtil.log(repeatPassword.toString());
            try{
                JSONTokener jsonParser = new JSONTokener(responseBean.toString());
                JSONObject jsonresult = (JSONObject) jsonParser.nextValue();
                if(jsonresult.getString("result").equals("true")){
                    BaseApplication.toast("修改成功！");
                    HttpClient.instance().login(strPhone, repeatPassword, new NetworkRequestRegistLogin());
                    if (AppContext.getProperty(Config.GTCID)!=null){
                        bangGT(AppContext.getProperty(Config.GTCID));
                    }
                }else{
                    LogUtil.log("密码修改失败");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    String afterencrypt;
    private void bangGT(String cid){
        HttpClient.instance().bandGT(cid,new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                Entitys data = responseBean.getData(Entitys.class);
                if (data.getCode()==1){
                    AppContext.setProperty(Config.ISBINDGTCID,"true");
                }else {
                    AppContext.setProperty(Config.ISBINDGTCID,"false");
                }
            }
        });
    }
    class NetworkRequestRegistLogin extends HttpCallBack{
        @Override
        public void onFailure(String error, String message) {
            super.onFailure(error, message);
        }

        @Override
        public void onFinish() {
            super.onFinish();
        }

        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onSuccess(ResponseBean responseBean) {
            LogUtil.log("reset=" + responseBean.toString());

               final UserLoginInfo userLogin = responseBean.getData(UserLoginInfo.class);
               //加密操作
               try{
                   // 从字符串中得到公钥
                   PublicKey publicKey = RSAUtils.loadPublicKey(Config.PUCLIC_KEY);
                   // 从文件中得到公钥
//                InputStream inPublic = getResources().getAssets().open("rsa_public_key.pem");
//                PublicKey publicKey = RSAUtils.loadPublicKey(inPublic);
                   // 加密
                   byte[] encryptByte = RSAUtils.encryptData(repeatPassword.getBytes(), publicKey);
                   // 为了方便观察吧加密后的数据用base64加密转一下，要不然看起来是乱码,所以解密是也是要用Base64先转换
                   afterencrypt = Base64Utils.encode(encryptByte);
                   //   BaseApplication.toast(afterencrypt);
//                et2.setText(afterencrypt);
               } catch (Exception e) {
                   e.printStackTrace();
               }

               AppContext.setProperty(Config.SHARE_USER_ACCOUNT, userLogin.getNick_name());
               AppContext.setProperty(Config.SHARE_USERSESSIONID, userLogin.getSessionid());
//               AppContext.setProperty(Config.SHARE_USERPWD, afterencrypt);
               AppContext.setProperty(Config.SHARE_USERPWD, afterencrypt);
               AppContext.setProperty(Config.OPENDOOR_PWD_SAVE, String.valueOf(userLogin.isSet_unlock_pwd()));
            finish();
           }
        }


    private boolean valiate() {
        if (editRestVerificationCode.getText().length() == 0) {
            toastFail("验证码不能为空！");
            return false;
        }
        if (editRestNewPassword.getText().toString().trim().length() == 0) {
            toastFail("密码不能为空！");
            return false;
        }
        if (editRepeatNewPassword.getText().toString().trim().length() == 0) {
            toastFail("确认密码不能为空！");
            return false;
        }
        if (editRestNewPassword.getText().toString().length() < 6) {
            toastFail("密码不能低于六位！");
            return false;
        }
        if (editRestNewPassword.getText().toString().length() >18) {
            toastFail("密码不能大于18位！");
            return false;
        }

        if (editRepeatNewPassword.getText().toString().length() < 6) {
            toastFail("确认密码不能低于六位！");
            return false;
        }
        return true;
    }

    class  NetworkRequestLoginResult extends HttpCallBack {

        public void onStart() {
            super.onStart();
//            ProgressDialog.show(mContext, "开始");
        }

        @Override
        public void onFailure(String error, String message) {
//            ProgressDialog.disMiss();
            super.onFailure(error, message);
        }

        @Override
        public void onSuccess(ResponseBean responseBean) {
            ProgressDialog.disMiss();
            if(responseBean.isFailure()){
                BaseApplication.toast(responseBean.toString());
            }
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_reset_password;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }
}

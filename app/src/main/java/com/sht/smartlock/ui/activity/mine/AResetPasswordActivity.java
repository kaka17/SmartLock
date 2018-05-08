package com.sht.smartlock.ui.activity.mine;

import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import org.json.JSONObject;
import org.json.JSONTokener;


public class AResetPasswordActivity extends BaseActivity implements View.OnClickListener{
    private ImageView btn_cancle;
    private TextView tvTitlePanel;

    private EditText editChangePasswordCode;
    private EditText editARestNewChangePassword;
    private EditText editARepeatChangeNewPassword;
    private Button btnARepeatChangePasswordSave;
    private Button btnUpdataAresetPassword;

    private boolean btnAVStatus;
    private boolean btnANStatus;
    private boolean btnAVNStatus;
    private String strAPhone;
    private String veriAFicationCode;
    private String newAPassword;
    private String repeatAPassword;

    int retrySeconds = 180;
    int recLen = retrySeconds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findviewbyid();
        setClickLister();
        initData();
    }

   private void initData(){
       Bundle bundle = getIntent().getExtras();
       strAPhone = bundle.getString("ForPhoneNumber");
   }

    private void findviewbyid() {
        btn_cancle = (ImageView) findViewById(R.id.btn_cancle);
        tvTitlePanel = (TextView) findViewById(R.id.tvTitlePanel);

        editChangePasswordCode = (EditText)findViewById(R.id.editChangePasswordCode);
        editARestNewChangePassword = (EditText)findViewById(R.id.editARestNewChangePassword);
        editARepeatChangeNewPassword = (EditText)findViewById(R.id.editARepeatChangeNewPassword);
        btnARepeatChangePasswordSave = (Button)findViewById(R.id.btnARepeatChangePasswordSave);
        btnUpdataAresetPassword = (Button)findViewById(R.id.btnUpdataAresetPassword);
        tvTitlePanel.setText("修改登录密码");
        btnARepeatChangePasswordSave.setEnabled(false);
//        HttpClient.instance().reset_user_login_pwd("322","",new NetworkRequestLoginResult());

        editChangePasswordCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(i2 == 4){
                    btnAVStatus = true;
                }else{
                    btnAVStatus = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(i2 == 4){
                    btnAVStatus = true;
                }else{
                    btnAVStatus = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() == 4){
                    btnAVStatus = true;
                }else{
                    btnAVStatus = false;
                }
//                BaseApplication.toast(btnVStatus+"");

                if(btnAVStatus == true&&btnANStatus == true&&btnAVNStatus == true){
                    btnARepeatChangePasswordSave.setEnabled(true);
                }else{
                    btnARepeatChangePasswordSave.setEnabled(false);
                }
            }
        });

        editARestNewChangePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(i2 >= 6){
                    btnANStatus = true;
                }else{
                    btnANStatus = false;
                }

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(i2 >= 6){
                    btnANStatus = true;
                }else{
                    btnANStatus = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {


                if(editable.length() >= 6){
                    btnANStatus = true;
                }else{
                    btnANStatus = false;
                }
//                BaseApplication.toast(btnNStatus+"");

                if(btnAVStatus == true&&btnANStatus == true&&btnAVNStatus == true){
                    btnARepeatChangePasswordSave.setEnabled(true);
                }else{
                    btnARepeatChangePasswordSave.setEnabled(false);
                }
            }
        });

        editARepeatChangeNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(i2 >= 6){
                    btnAVNStatus = true;
                }else{
                    btnAVNStatus = false;
                }
            }
            //15816409436
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(i2 >= 6){
                    btnAVNStatus = true;
                }else{
                    btnAVNStatus = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() >= 6){
                    btnAVNStatus = true;
                }else{
                    btnAVNStatus = false;
                }
//                BaseApplication.toast(btnVNStatus+"");

                if(btnAVStatus == true&&btnANStatus == true&&btnAVNStatus == true){
                    btnARepeatChangePasswordSave.setEnabled(true);
                }else{
                    btnARepeatChangePasswordSave.setEnabled(false);
                }
            }
        });
    }

    Handler countHander = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            recLen--;
            btnUpdataAresetPassword.setText(getString(R.string.retry_to_please)+"(" + recLen + ")");
            if (recLen > 0) {
                countHander.postDelayed(this, 1000);
            } else {
                btnUpdataAresetPassword.setEnabled(true);
                btnUpdataAresetPassword.setText(R.string.retry_to_send);
                recLen=retrySeconds;
            }
        }
    };


    @Override
    public void onClick(View view) {
        finish();
    }

    private void setClickLister(){
        btn_cancle.setOnClickListener(this);
        btnARepeatChangePasswordSave.setOnClickListener(listener);
        btnUpdataAresetPassword.setOnClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_cancel:
                    finish();
                    break;
                case R.id.btnARepeatChangePasswordSave://修改密码
                    if(!valiate())return;
                    veriAFicationCode = editChangePasswordCode.getText().toString();
                    newAPassword = editARestNewChangePassword.getText().toString();
                    repeatAPassword = editARepeatChangeNewPassword.getText().toString();

                    if(newAPassword.length() < 6 || repeatAPassword.length() < 6){
                        BaseApplication.toast("密码长度不能小于6位");
                        return;
                    }
                    if(!newAPassword.equals(repeatAPassword)){
                        editARestNewChangePassword.setText("");
                        editARepeatChangeNewPassword.setText("");

                        BaseApplication.toast("密码不一致，请重新输入！");
                        return;
                    }
                    HttpClient.instance().modify_user_login_pwd(repeatAPassword,veriAFicationCode, new NetworkRequestChangrResult());
                    break;
                case R.id.btnUpdataAresetPassword:
                    if(!strAPhone.equals("")&&!strAPhone.equals("null")){
                        HttpClient.instance().request_verify_code(strAPhone, new HttpCallBack() {
                            @Override
                            public void onStart() {
                                super.onStart();
                                countHander.postDelayed(runnable, 1000);
                                btnUpdataAresetPassword.setEnabled(false);
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
                                    toastFail("获取失败");
                                    btnUpdataAresetPassword.setEnabled(true);
                                    recLen=0;
                                }
                            }
                            @Override
                            public void onFailure(String error, String message) {
                                super.onFailure(error, message);
                                toastFail(getString(R.string.get_ver_code_fail));
                                btnUpdataAresetPassword.setEnabled(true);
                                recLen=0;
                            }
                        });
                    }

                    break;
            }
        }
    };

    class NetworkRequestChangrResult extends HttpCallBack {
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
            JSONTokener jsonParser;
            JSONObject jsonresult;
            try{
                jsonParser = new JSONTokener(responseBean.toString());
                jsonresult = (JSONObject) jsonParser.nextValue();
//                LogUtil.log("reset1=" + jsonresult.getString("result").toString());
                if(jsonresult.getString("result").equals("true")){
                    BaseApplication.toast("修改成功");
                }else{
                    BaseApplication.toast("修改失败");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            finish();
        }
    }

    private boolean valiate() {
        if (editChangePasswordCode.getText().length() == 0) {
            toastFail("验证码不能为空！");
            return false;
        }
        if (editARestNewChangePassword.getText().toString().trim().length() == 0) {
            toastFail("密码不能为空！");
            return false;
        }
        if (editARepeatChangeNewPassword.getText().toString().trim().length() == 0) {
            toastFail("确认密码不能为空！");
            return false;
        }
        if (editARestNewChangePassword.getText().toString().length() < 6) {
            toastFail("密码不能低于六位！");
            return false;
        }

        if (editARepeatChangeNewPassword.getText().toString().length() < 6) {
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
        return R.layout.activity_areset_password;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }
}

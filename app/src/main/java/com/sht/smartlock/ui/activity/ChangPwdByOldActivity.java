package com.sht.smartlock.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.ui.activity.badgeview.BadgeUtils;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.mine.AForgetPasswordActivity;
import com.sht.smartlock.ui.entity.Entitys;
import com.sht.smartlock.widget.dialog.ProgressDialog;

public class ChangPwdByOldActivity extends BaseActivity  implements View.OnClickListener {

    private EditText editOldPassword,etNewChangePassword,etARepeatChangeNewPassword;
    private boolean isFirst=false;
    private TextView tvForgetPwd;
    private Button btnPasswordSave;
    private boolean btnANStatus;
    private boolean btnAVNStatus;
    private String phone="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chang_pwd_by_old);
        Intent intent=getIntent();
        isFirst=intent.getBooleanExtra(Config.ISChANGPWDByFIRST,false);
        phone=getIntent().getStringExtra(Config.CHANGPWDPHONE);
        onBack();
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chang_pwd_by_old;
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
        editOldPassword = (EditText) findViewById(R.id.editOldPassword);
        etNewChangePassword = (EditText) findViewById(R.id.etNewChangePassword);
        etARepeatChangeNewPassword = (EditText) findViewById(R.id.etARepeatChangeNewPassword);

        tvForgetPwd = (TextView) findViewById(R.id.tvForgetPwd);
        btnPasswordSave = (Button) findViewById(R.id.btnPasswordSave);

        tvForgetPwd.setOnClickListener(this);
        btnPasswordSave.setOnClickListener(this);

        if (isFirst){
            editOldPassword.setVisibility(View.GONE);
            tvForgetPwd.setVisibility(View.GONE);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvForgetPwd:
                Intent intent1=new Intent(getApplicationContext(),ChangPwdByPhoneActivity.class);
                intent1.putExtra(Config.CHANGPWDPHONE,phone);
                startActivity(intent1);
                finish();
                break;
            case R.id.btnPasswordSave:

                if (!valiate()){
                    return;
                }
                String oldPwd=editOldPassword.getText().toString().trim();
                String newPwd=etNewChangePassword.getText().toString().trim();
                String newPwd2=etARepeatChangeNewPassword.getText().toString().trim();

                if (isFirst){
                    if (newPwd.equals(newPwd2)){
                        changPwd(newPwd,"",true);
                    }else {
                        AppContext.toast("新密和确认密码不一致");
                    }
                }else {
                    if (newPwd.equals(newPwd2)){
                        changPwd(newPwd,oldPwd,false);
                    }else {
                        AppContext.toast("新密和确认密码不一致");
                    }
                }
                break;

        }
    }

    private boolean valiate() {

        if (etNewChangePassword.getText().toString().trim().length() == 0) {
            toastFail("密码不能为空！");
            return false;
        }
        if (etARepeatChangeNewPassword.getText().toString().trim().length() == 0) {
            toastFail("确认密码不能为空！");
            return false;
        }
        if (etNewChangePassword.getText().toString().length() < 6) {
            toastFail("密码不能低于六位！");
            return false;
        }
        if (etNewChangePassword.getText().toString().length() > 18) {
            toastFail("密码不能大于六位！");
            return false;
        }

        if (etARepeatChangeNewPassword.getText().toString().length() < 6) {
            toastFail("确认密码不能低于六位！");
            return false;
        }
        return true;
    }


    private void changPwd(String newPwd,String oldPwd,boolean isFirst){
        HttpClient.instance().changPwdNew(newPwd, oldPwd, isFirst, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(ChangPwdByOldActivity.this,"正在修改密码中...");
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
                    AppContext.toast(entitys.getMsg());
                    finish();
                }else {
                    AppContext.toast(entitys.getMsg());
                }
            }
        });
    }

}

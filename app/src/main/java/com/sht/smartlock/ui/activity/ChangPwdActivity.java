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
import com.sht.smartlock.widget.dialog.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangPwdActivity extends BaseActivity {

    private EditText etNewPwd,etSurepwd;
    private TextView tvOldPwd,tvSure;
    private String randompwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chang_pwd);
        randompwd = getIntent().getStringExtra(Config.RANDOMPWD);
        onBack();
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chang_pwd;
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

    private void  initView(){
        tvOldPwd = (TextView) findViewById(R.id.tvOldPwd);
        etNewPwd = (EditText) findViewById(R.id.etNewPwd);
        etSurepwd = (EditText) findViewById(R.id.etSurepwd);
        tvSure = (TextView) findViewById(R.id.tvSure);
        tvOldPwd.setText(randompwd);
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
    }


    private void initData(){

        String trim = etNewPwd.getText().toString().trim();
        String trim2 = etSurepwd.getText().toString().trim();
        if (!trim.equals(trim2)||trim.isEmpty()){
            AppContext.toast("新密码不一致，请重新修改");
            return;
        }
        HttpClient.instance().changpwd(randompwd, trim, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(ChangPwdActivity.this,"正在修改密码。。。");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                try {
                    JSONObject jsonObject=new JSONObject(responseBean.toString());
                    String result = jsonObject.getString("result");
                    if (result.equals("true")){
                        AppContext.toast("密码修改成功");
                        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        AppContext.toast("密码修改失败，请重试");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}

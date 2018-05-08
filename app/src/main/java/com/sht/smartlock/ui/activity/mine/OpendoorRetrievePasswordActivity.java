package com.sht.smartlock.ui.activity.mine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.util.LogUtil;

import org.json.JSONObject;

public class OpendoorRetrievePasswordActivity extends BaseActivity {
    private ImageView btn_cancle;
    private TextView tvTitlePanel;
    private EditText editRetrievePassword;
    private Button btnRetrievePassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findviewByid();
        setClickLister();
    }

    private void findviewByid(){
        btn_cancle = (ImageView)findViewById(R.id.btn_cancle);
        tvTitlePanel = (TextView)findViewById(R.id.tvTitlePanel);
        btnRetrievePassword = (Button)findViewById(R.id.btnRetrievePassword);
        editRetrievePassword = (EditText)findViewById(R.id.editRetrievePassword);
        tvTitlePanel.setText("重置密码");
    }

    private void setClickLister(){
        btn_cancle.setOnClickListener(listener);
        btnRetrievePassword.setOnClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          switch (view.getId()){
              case  R.id.btn_cancle:
                  finish();
                  break;
              case R.id.btnRetrievePassword:
                  String updateOpendoorPWD = editRetrievePassword.getText().toString();
                  if(!updateOpendoorPWD.equals("")&&!updateOpendoorPWD.equals("null")){
                      HttpClient.instance().reset_user_unlock_pwd(updateOpendoorPWD, new HttpCallBack() {
                          @Override
                          public void onSuccess(ResponseBean responseBean) {
                              try {
                                  JSONObject jsonObject = new JSONObject(responseBean.toString());
                                  if (jsonObject.getString("result").equals("-1")) {
                                      BaseApplication.toast("登录密码错误");
                                  } else if (jsonObject.getString("result").equals("0")) {
                                      BaseApplication.toast("密码已经为空");
                                  } else if (jsonObject.getString("result").equals("1")) {
                                      BaseApplication.toast("重置成功");
                                      startActivity(new Intent(OpendoorRetrievePasswordActivity.this, OpenDoorPasswordActivity.class));
                                      AppContext.setProperty(Config.OPENDOOR_PWD_SAVE, "false");
                                      finish();
                                  }

                              } catch (Exception e) {
                                  LogUtil.log(e.toString());
                              }
                          }
                      });
                  }else{
                      BaseApplication.toast("请输入密码");
                  }
                  break;
          }
        }
    };


    @Override
    protected boolean hasToolBar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_opendoor_retrieve_password;
    }
}

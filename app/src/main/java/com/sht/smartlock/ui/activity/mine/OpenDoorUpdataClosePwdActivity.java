package com.sht.smartlock.ui.activity.mine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.AppManager;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.ui.activity.base.BaseActivity;

public class OpenDoorUpdataClosePwdActivity extends BaseActivity implements View.OnClickListener{
    private ImageView btn_cancle;
    private TextView tvTitlePanel;
    private Button btnOpendoorpwdRetrievePassword;
    private RelativeLayout linearOpenDoorUpdata;
    private RelativeLayout linearOpenDoorClose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findviewbyid();
        setClickLister();
    }

    private void findviewbyid(){
        btn_cancle = (ImageView)findViewById(R.id.btn_cancle);
        tvTitlePanel = (TextView)findViewById(R.id.tvTitlePanel);
        btnOpendoorpwdRetrievePassword = (Button)findViewById(R.id.btnOpendoorpwdRetrievePassword);
        linearOpenDoorUpdata = (RelativeLayout)findViewById(R.id.linearOpenDoorUpdata);
        linearOpenDoorClose = (RelativeLayout)findViewById(R.id.linearOpenDoorClose);
        tvTitlePanel.setText("更改开门密码");
    }


    private void setClickLister(){
        btn_cancle.setOnClickListener(this);
        linearOpenDoorUpdata.setOnClickListener(this);
        linearOpenDoorClose.setOnClickListener(this);
        btnOpendoorpwdRetrievePassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_cancle:
                finish();
                break;
            case R.id.linearOpenDoorUpdata:
                startActivity(new Intent(OpenDoorUpdataClosePwdActivity.this,UpdataOpenDoorPwdActivity.class));
                break;
            case R.id.linearOpenDoorClose:
                startActivity(new Intent(OpenDoorUpdataClosePwdActivity.this,CloseOpenDoorActivity.class));
                finish();
                break;
            case R.id.btnOpendoorpwdRetrievePassword:
                startActivity(new Intent(OpenDoorUpdataClosePwdActivity.this,OpendoorRetrievePasswordActivity.class));
//                AppContext.setProperty(Config.OPENDOOR_PWD_SAVE, "false");
                finish();
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_open_door_updata_close_pwd;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }
}

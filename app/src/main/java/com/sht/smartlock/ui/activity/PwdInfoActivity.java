package com.sht.smartlock.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.mine.AForgetPasswordActivity;
import com.sht.smartlock.ui.activity.mine.OpenDoorPasswordActivity;
import com.sht.smartlock.ui.activity.mine.OpenDoorUpdataClosePwdActivity;
import com.sht.smartlock.util.LogUtil;

public class PwdInfoActivity extends BaseActivity implements View.OnClickListener{

    private LinearLayout linOpenDoorPwd,linLogPwd;
    private String phone="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_pwd_info);
        phone=getIntent().getStringExtra(Config.CHANGPWDPHONE);
        onBack();
        initView();
        setOnClickListener();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pwd_info;
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
        linOpenDoorPwd = (LinearLayout) findViewById(R.id.linOpenDoorPwd);
        linLogPwd = (LinearLayout) findViewById(R.id.linLogPwd);
    }
    private  void setOnClickListener(){
        linOpenDoorPwd.setOnClickListener(this);
        linLogPwd.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.linOpenDoorPwd:
                String str_opendoor = AppContext.getProperty(Config.OPENDOOR_PWD_SAVE);
                LogUtil.log("opendoor =" + str_opendoor);
                if (!str_opendoor.equals("")) {
                    if (str_opendoor.equals("true")) {
                        startActivity(new Intent(PwdInfoActivity.this, OpenDoorUpdataClosePwdActivity.class));
                    } else {
                        startActivity(new Intent(PwdInfoActivity.this, OpenDoorPasswordActivity.class));
                    }
                }
                break;
            case R.id.linLogPwd:
                if (AppContext.getProperty(Config.ISBINDPHONE)!=null && AppContext.getProperty(Config.ISBINDPHONE).equals("true")){
//                    startActivity(new Intent(PwdInfoActivity.this, AForgetPasswordActivity.class));
                    Intent intent=new Intent(getApplicationContext(),ChangPwdByOldActivity.class);
                    intent.putExtra(Config.ISChANGPWDByFIRST, false);
                    intent.putExtra(Config.CHANGPWDPHONE,phone);
                    startActivity(intent);
                }else {
//                    AppContext.toast("请先绑定手机号码");
                    Intent intent=new Intent(getApplicationContext(), BindingPhoneActivity.class);
//                    Bundle bundle=new Bundle();
//                    bundle.putString(Config.OLDPHONE,phone);
//                    intent.putExtras(bundle);
                    intent.putExtra(Config.ISChANGPWD,true);
                    startActivity(intent);
                }

                break;
        }
    }

    /*
    *   取消订单的Dialog
    * */
    private void getCancelDialog() {
        final Dialog dialog = new Dialog(PwdInfoActivity.this, R.style.OrderDialog);
        LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());
        View view = mInflater.inflate(R.layout.bindphone_dialog, null);
        dialog.setContentView(view);
        //
        view.findViewById(R.id.tv_No).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_Sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                makeOrderChang();
            }
        });
        dialog.show();
    }

}

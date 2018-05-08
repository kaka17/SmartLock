package com.sht.smartlock.ui.activity.mine;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.AppManager;
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

public class CloseOpenDoorActivity extends BaseActivity implements View.OnClickListener{
    private ImageView btn_cancle;
    private TextView tvTitlePanel;

    private EditText password_text;
    private ImageButton btnOpenDoorColseGetPassWord;
    private ImageButton btnColseOpenDoorCancle;
    private TextView tvColseOpenDoorShow;

    int flag = 0;//定义标记变量
    private String str_password;

    private SharedPreferences share;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findviewbyid();
        setClickLister();
    }


    private void findviewbyid(){
        btn_cancle = (ImageView)findViewById(R.id.btn_cancle);
        tvTitlePanel = (TextView)findViewById(R.id.tvTitlePanel);
        password_text = (EditText)findViewById(R.id.password_text2);
        btnOpenDoorColseGetPassWord = (ImageButton)findViewById(R.id.btnOpenDoorColseGetPassWord);
        btnColseOpenDoorCancle = (ImageButton)findViewById(R.id.btnColseOpenDoorCancle);
        tvColseOpenDoorShow = (TextView)findViewById(R.id.tvColseOpenDoorShow);
        tvTitlePanel.setText("关闭开门密码");

        share = this.getSharedPreferences(Config.OPENDOOR_PWD, Context.MODE_PRIVATE);
        password_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //s:变化前的所有字符； start:字符开始的位置； count:变化前的总字节数；after:变化后的字节数
//                Toast.makeText(getApplicationContext(), "变化前:"+charSequence+";"+i+";"+i1+";"+i2, Toast.LENGTH_SHORT).show();
                if (charSequence.length() == 4) {
                    btnOpenDoorColseGetPassWord.setEnabled(true);
                } else {
                    btnOpenDoorColseGetPassWord.setEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //S：变化后的所有字符；start：字符起始的位置；before: 变化之前的总字节数；count:变化后的字节数
//                Toast.makeText(getApplicationContext(), "变化后:"+charSequence+";"+i+";"+i1+";"+i2, Toast.LENGTH_SHORT).show();
                if (charSequence.length() == 4) {
                    btnOpenDoorColseGetPassWord.setEnabled(true);
                    btnOpenDoorColseGetPassWord.setImageResource(R.drawable.opendoor_determine1);
                } else {
                    btnOpenDoorColseGetPassWord.setEnabled(false);
                    btnOpenDoorColseGetPassWord.setBackgroundResource(R.drawable.opendoor_determine);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //s:变化后的所有字符
//                Toast.makeText(getApplicationContext(), "变化:" + editable, Toast.LENGTH_SHORT).show();
                if (editable.length() == 4) {
                    btnOpenDoorColseGetPassWord.setEnabled(true);
                    btnOpenDoorColseGetPassWord.setImageResource(R.drawable.opendoor_determine1);
                } else {
                    btnOpenDoorColseGetPassWord.setEnabled(false);
                    btnOpenDoorColseGetPassWord.setBackgroundResource(R.drawable.opendoor_determine);
                }
            }
        });
    }

    private void setClickLister(){
        btn_cancle.setOnClickListener(this);
        btnOpenDoorColseGetPassWord.setOnClickListener(this);
        btnColseOpenDoorCancle.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_cancle:
                AppManager.getAppManager().finishActivity();
                break;
            case R.id.btnOpenDoorColseGetPassWord:
                if(flag==0){
                    //执行方法1（对应功能1)
                    str_password = password_text.getText().toString();
                    HttpClient.instance().modify_user_unlock_pwd(str_password,"", new NetworkRequestLoginResult());
                    flag ++;
                }
            case R.id.btnColseOpenDoorCancle:
              finish();
                break;
        }
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

            try{
                JSONTokener jsonTokener = new JSONTokener(responseBean.toString());
                JSONObject jsonObject  = (JSONObject)jsonTokener.nextValue();
                if(jsonObject.getString("result").equals("true")){
                    BaseApplication.toast("密码关闭成功！");
//                    share.edit().putBoolean(Config.OPENDOOR_PWD_SAVE, false).commit();
                    AppContext.setProperty(Config.OPENDOOR_PWD_SAVE, "false");
                    finish();
                }else{
                    BaseApplication.toast("密码关闭失败！");
//                    AppContext.setProperty(Config.OPENDOOR_PWD_SAVE, "false");
                    finish();
                }

            }catch (Exception e){
                LogUtil.log(e.toString());
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_close_open_door;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }
}

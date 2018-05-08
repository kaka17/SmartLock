package com.sht.smartlock.ui.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
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
import com.sht.smartlock.model.HotelOrder;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.adapter.HotelOderAdapter;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import org.json.JSONObject;
import org.json.JSONTokener;

public class OpenDoorPasswordActivity extends BaseActivity implements View.OnClickListener{
    private ImageView btn_cancle;
    private TextView tvTitlePanel;

    private EditText password_text;
    private ImageButton btnOpenDoorGetPassWord;
    private TextView tvOpenDoorShow;
    private ImageButton btnOpenDoorCancle;
//    private SharedPreferences share;
    private SharedPreferences share;
    private SharedPreferences.Editor editor = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findviewbyid();
        setClickLister();

    }

    int flag = 0;//定义标记变量
    private String str_password;
    private String str_again_again;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnOpenDoorGetPassWord:
                if(flag==0){
                    //执行方法1（对应功能1)
                    str_password = password_text.getText().toString();
//                    BaseApplication.toast("密码1:"+str_password);
                    password_text.setText("");
                    tvOpenDoorShow.setText("请再输入一遍");
                    flag ++;
                }else if(flag==1){
                    //执行方法2（对应功能2)
                    str_again_again = password_text.getText().toString();
                    if(str_again_again.equals(str_password)){
                        HttpClient.instance().modify_user_unlock_pwd("", str_again_again, new NetworkRequestLoginResult());
                    }else{
                        password_text.setText("");
                        tvOpenDoorShow.setText("请再输入一遍");
                        flag --;
                    }
                    flag ++;
                }
//                flag=(flag+1)%2;//其余得到循环执行上面3个不同的功能
                break;
            case R.id.btn_cancle:
                finish();
                break;
            case R.id.btnOpenDoorCancle:
                finish();
                break;
        }
    }

    private void findviewbyid(){
        btn_cancle = (ImageView)findViewById(R.id.btn_cancle);
        tvTitlePanel = (TextView)findViewById(R.id.tvTitlePanel);
        password_text = (EditText)findViewById(R.id.password_text);
        btnOpenDoorGetPassWord = (ImageButton)findViewById(R.id.btnOpenDoorGetPassWord);
        tvOpenDoorShow = (TextView)findViewById(R.id.tvOpenDoorShow);
        btnOpenDoorCancle = (ImageButton)findViewById(R.id.btnOpenDoorCancle);
        tvTitlePanel.setText("密码设置");

        share = this.getSharedPreferences(Config.OPENDOOR_PWD, Context.MODE_PRIVATE);
        btnOpenDoorGetPassWord.setClickable(false);
        password_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //s:变化前的所有字符； start:字符开始的位置； count:变化前的总字节数；after:变化后的字节数
//                Toast.makeText(getApplicationContext(), "变化前:"+charSequence+";"+i+";"+i1+";"+i2, Toast.LENGTH_SHORT).show();
                if (charSequence.length() == 4) {
                    btnOpenDoorGetPassWord.setEnabled(true);
                    btnOpenDoorGetPassWord.setBackgroundResource(R.drawable.opendoor_determine1);
                } else {
                    btnOpenDoorGetPassWord.setEnabled(false);
                    btnOpenDoorGetPassWord.setBackgroundResource(R.drawable.opendoor_determine);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //S：变化后的所有字符；start：字符起始的位置；before: 变化之前的总字节数；count:变化后的字节数
//                Toast.makeText(getApplicationContext(), "变化后:"+charSequence+";"+i+";"+i1+";"+i2, Toast.LENGTH_SHORT).show();
                if (charSequence.length() == 4) {
                    btnOpenDoorGetPassWord.setEnabled(true);
                    btnOpenDoorGetPassWord.setBackgroundResource(R.drawable.opendoor_determine1);
                } else {
                    btnOpenDoorGetPassWord.setEnabled(false);
                    btnOpenDoorGetPassWord.setBackgroundResource(R.drawable.opendoor_determine);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //s:变化后的所有字符
//                Toast.makeText(getApplicationContext(), "变化:" + editable, Toast.LENGTH_SHORT).show();
                if (editable.length() == 4) {
                    btnOpenDoorGetPassWord.setEnabled(true);
                    btnOpenDoorGetPassWord.setBackgroundResource(R.drawable.opendoor_determine1);
                } else {
                    btnOpenDoorGetPassWord.setEnabled(false);
                    btnOpenDoorGetPassWord.setBackgroundResource(R.drawable.opendoor_determine);
                }
            }
        });
    }

    private void setClickLister(){
        btnOpenDoorGetPassWord.setOnClickListener(this);
        btn_cancle.setOnClickListener(this);
        btnOpenDoorCancle.setOnClickListener(this);
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
                JSONObject jsonObject = (JSONObject)jsonTokener.nextValue();
                if(!jsonObject.getString("result").equals("true")){
                    BaseApplication.toast("密码设置失败！");
                    AppContext.setProperty(Config.OPENDOOR_PWD_SAVE, "true");
                    finish();
//                    startActivity(new Intent(OpenDoorPasswordActivity.this, CloseOpenDoorActivity.class));

                }else{
                    BaseApplication.toast("密码设置成功！");
                    startActivity(new Intent(OpenDoorPasswordActivity.this, OpenDoorUpdataClosePwdActivity.class));
//                    share.edit().putBoolean(Config.OPENDOOR_PWD_SAVE, true).commit();
                    AppContext.setProperty(Config.OPENDOOR_PWD_SAVE, "true");
                    finish();
                }
            }catch (Exception e){
                LogUtil.log(e.toString());
            }
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_open_door_password;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }
}

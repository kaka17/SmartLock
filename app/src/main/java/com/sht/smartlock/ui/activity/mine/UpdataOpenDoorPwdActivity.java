package com.sht.smartlock.ui.activity.mine;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sht.smartlock.AppManager;
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

public class UpdataOpenDoorPwdActivity extends BaseActivity implements View.OnClickListener{
    private ImageView btn_cancle;
    private TextView tvTitlePanel;

    private EditText password_text;
    private ImageButton btnOpenDoorUpdataGetPassWord;
    private ImageButton btnUpdataOpenDoorCancle;
    private TextView tvUpdateOpenDoorShow;

    int flag = 0;//定义标记变量
    private String str_password;
    private String str_again_again;
    private String str_again_agains;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findviewbyid();
        setClickListter();
    }

    private void findviewbyid(){
        btn_cancle = (ImageView)findViewById(R.id.btn_cancle);
        tvTitlePanel = (TextView)findViewById(R.id.tvTitlePanel);
        password_text = (EditText)findViewById(R.id.password_text2);
        btnOpenDoorUpdataGetPassWord = (ImageButton)findViewById(R.id.btnOpenDoorUpdataGetPassWord);
        btnUpdataOpenDoorCancle = (ImageButton)findViewById(R.id.btnUpdataOpenDoorCancle);
        tvUpdateOpenDoorShow = (TextView)findViewById(R.id.tvUpdateOpenDoorShow);
        tvTitlePanel.setText("修改开门密码");
        password_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //s:变化前的所有字符； start:字符开始的位置； count:变化前的总字节数；after:变化后的字节数
//                Toast.makeText(getApplicationContext(), "变化前:"+charSequence+";"+i+";"+i1+";"+i2, Toast.LENGTH_SHORT).show();
                if (charSequence.length() == 4) {
                    btnOpenDoorUpdataGetPassWord.setEnabled(true);
                    btnOpenDoorUpdataGetPassWord.setBackgroundResource(R.drawable.opendoor_determine1);
                } else {
                    btnOpenDoorUpdataGetPassWord.setEnabled(false);
                    btnOpenDoorUpdataGetPassWord.setBackgroundResource(R.drawable.opendoor_determine);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //S：变化后的所有字符；start：字符起始的位置；before: 变化之前的总字节数；count:变化后的字节数
//                Toast.makeText(getApplicationContext(), "变化后:"+charSequence+";"+i+";"+i1+";"+i2, Toast.LENGTH_SHORT).show();
                if (charSequence.length() == 4) {
                    btnOpenDoorUpdataGetPassWord.setEnabled(true);
                    btnOpenDoorUpdataGetPassWord.setBackgroundResource(R.drawable.opendoor_determine1);
                } else {
                    btnOpenDoorUpdataGetPassWord.setEnabled(false);
                    btnOpenDoorUpdataGetPassWord.setBackgroundResource(R.drawable.opendoor_determine);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //s:变化后的所有字符
//                Toast.makeText(getApplicationContext(), "变化:" + editable, Toast.LENGTH_SHORT).show();
                if (editable.length() == 4) {
                    btnOpenDoorUpdataGetPassWord.setEnabled(true);
                    btnOpenDoorUpdataGetPassWord.setBackgroundResource(R.drawable.opendoor_determine1);
                } else {
                    btnOpenDoorUpdataGetPassWord.setEnabled(false);
                    btnOpenDoorUpdataGetPassWord.setBackgroundResource(R.drawable.opendoor_determine);
                }
            }
        });
    }

    private void setClickListter(){
        btn_cancle.setOnClickListener(this);
        btnOpenDoorUpdataGetPassWord.setOnClickListener(this);
        btnUpdataOpenDoorCancle.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_cancle:
                finish();
                break;
            case R.id.btnOpenDoorUpdataGetPassWord:
                if(flag==0){
                    //执行方法1（对应功能1)
                    str_password = password_text.getText().toString();
//                    BaseApplication.toast("密码1:"+str_password);
                    password_text.setText("");
                    tvUpdateOpenDoorShow.setText("请输入修改密码");
                    flag ++;
                }else if(flag==1){
                    //执行方法2（对应功能2)
                    str_again_again = password_text.getText().toString();
                    password_text.setText("");
                    tvUpdateOpenDoorShow.setText("请再次输入修改密码");
//                    if(str_again_again.equals(str_password)){
//                        HttpClient.instance().modify_user_unlock_pwd(str_password, str_again_again, new NetworkRequestLoginResult());
//                        startActivity(new Intent(UpdataOpenDoorPwdActivity.this,OpenDoorUpdataClosePwdActivity.class));
//                    }else{
//                        password_text.setText("");
//                        tvOpenDoorShow.setText("请再输入一遍");
//                        flag --;
//                    }
                    flag ++;
                }else if(flag==2){
                    str_again_agains = password_text.getText().toString();
                    if(str_again_agains.equals(str_again_again)) {
                        HttpClient.instance().modify_user_unlock_pwd(str_password,str_again_agains , new NetworkRequestLoginResult());
//                        startActivity(new Intent(UpdataOpenDoorPwdActivity.this,OpenDoorUpdataClosePwdActivity.class));
                    }else{
                        password_text.setText("");
//                        tvUpdateOpenDoorShow.setText("两次密码不一致，请重新输入");
                        BaseApplication.toast("两次密码不一致，请重新输入");
                        flag --;
                    }
                    flag ++;
                }
                break;
            case R.id.btnUpdataOpenDoorCancle:
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

            finish();

             if(!responseBean.toString().equals("")){
                 try {
                     JSONTokener jsonTokener = new JSONTokener(responseBean.toString());
                     JSONObject jsonObject = (JSONObject)jsonTokener.nextValue();
                     if(jsonObject.getString("result").equals("true")){
                         BaseApplication.toast("密码修改成功！");
                         finish();
                     }else {
                         BaseApplication.toast("密码修改失败！");
                         finish();
                     }
                 }catch (Exception e){
                     LogUtil.log(e.toString());
                 }
             }

        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_updata_open_door_pwd;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }
}

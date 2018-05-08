package com.sht.smartlock.ui.activity.mine;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener{
    private TextView tvBtnTitlePanel;
    private Button btnPanelSave;
    private ImageView btn_btncancle;
    private EditText editPhone;//获取验证码的手机号码

    private String phoneStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findviewbyid();
        setOnClickLister();
    }


    private void tZhuan(){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        intent.setClass(ForgetPasswordActivity.this, ResetPasswordActivity.class);
        bundle.putString("ForPhoneNumber", editPhone.getText().toString());
        intent.putExtras(bundle);
        startActivity(intent);

        AppManager.getAppManager().finishActivity();
    }

    private void findviewbyid(){
        tvBtnTitlePanel = (TextView)findViewById(R.id.tvBtnTitlePanel);
        btnPanelSave = (Button)findViewById(R.id.btnPanelSave);
        btn_btncancle = (ImageView)findViewById(R.id.btn_btncancle);
        editPhone = (EditText)findViewById(R.id.editPhone);

        tvBtnTitlePanel.setText("重设密码");
        btnPanelSave.setText("下一步");
    }

    private void setOnClickLister(){
        btn_btncancle.setOnClickListener(listener);
        btnPanelSave.setOnClickListener(this);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnPanelSave:
                String verPhoneNumber = editPhone.getText().toString();
                if (!valiate()) return;
                if(verPhoneNumber.length()<0||verPhoneNumber.equals("")){
                    BaseApplication.toast("请输入手机号码！");
                }else{
                    getDialog("验证码发送至手机:", verPhoneNumber);
                }
                break;
        }
    }

    private String strtype;
    private String phString;
    int recLen = 20;
    private void getDialog(String strInfo,String distrub){
        strtype = distrub;
        final Dialog dialog=new Dialog(ForgetPasswordActivity.this,R.style.OrderDialog);
        LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());
        View view = mInflater.inflate(R.layout.forgetpassworddialog, null);
        TextView forgetTv_Order =  (TextView)view.findViewById(R.id.forgetTv_Order);
        Button btnforgetCancle =  (Button)view.findViewById(R.id.btnforgetCancle);
        Button btnforgetSave =  (Button)view.findViewById(R.id.btnforgetSave);
        dialog.setContentView(view);

        forgetTv_Order.setText(strInfo +"\n"+ distrub);
        view.findViewById(R.id.btnforgetCancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.log("type=" + strtype);
//                HttpClient.instance().setroomtrouble("322", strtype, new NetworkRequestLoginResult());
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.btnforgetSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//获取验证码
                 String str_editPhone = editPhone.getText().toString();
                if (!TextUtils.isEmpty(str_editPhone)) {
                    if (!valiatePhoneNum(str_editPhone)) return;
                    tZhuan();
                    HttpClient.instance().getVerCode(str_editPhone, new HttpCallBack() {
                        @Override
                        public void onSuccess(ResponseBean responseBean) {
                            if(responseBean.getData().equals("true")){
                                toastSuccess("获取验证码成功！");

                            }else {
                                toastFail("获取验证码失败！");
                            }
                        }
                    });
                } else {
                    Toast.makeText(ForgetPasswordActivity.this, "电话不能为空", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private boolean valiatePhoneNum(String num) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,7,8,9][0-9]{9}$"); // 验证手机号
        m = p.matcher(num);
        b = m.matches();
        if (!b) {
            toastFail(getString(R.string.please_enter_correct_phonenum));
        }
        return b;
    }

    private boolean valiate() {
        if (!valiatePhoneNum(editPhone.getText().toString())) {
            return false;
        }
//        if (verEditText.getText().length() == 0) {
//            toastFail("验证码不能为空！");
//            return false;
//        }
//        if (editPassWord.getText().toString().trim().length() == 0) {
//            toastFail("密码不能为空！");
//            return false;
//        }
//        if (editPassWord.getText().toString().length() < 6) {
//            toastFail("密码不能低于六位！");
//            return false;
//        }
        return true;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_forget_password;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}

package com.sht.smartlock.ui.activity.mine;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

public class EditIDNumberActivity extends BaseActivity implements View.OnClickListener{
    private TextView tvBtnTitlePanel;
    private ImageView btn_btncancle;
    private Button btnPanelSave;
    private EditText editIDNum;

    private String streditIDNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findviewbyid();
        setOnClickLister();
        initData();
    }

    private void findviewbyid(){
        tvBtnTitlePanel = (TextView)findViewById(R.id.tvBtnTitlePanel);
        btn_btncancle = (ImageView)findViewById(R.id.btn_btncancle);
        btnPanelSave = (Button)findViewById(R.id.btnPanelSave);
        editIDNum = (EditText)findViewById(R.id.editIDNum);
        tvBtnTitlePanel.setText("修改身份证号码");
    }

    private void setOnClickLister(){
        btn_btncancle.setOnClickListener(listener);
        btnPanelSave.setOnClickListener(this);
    }

    private void initData(){
        Bundle bundle = this.getIntent().getExtras();
        streditIDNumber = bundle.getString("tvID_number");
        LogUtil.log(streditIDNumber + "");
        if(!streditIDNumber.equals("")||!streditIDNumber.equals("null")){
            editIDNum.setText(streditIDNumber);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnPanelSave:
                String strIDNumber = editIDNum.getText().toString();
//                if(!strIDNumber.equals("")||!strIDNumber.equals("null")){
                    valiateIDNum(strIDNumber);


//                }
                break;
        }
    }

    private boolean valiateIDNum(String IDnum) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("(^\\d{15}$)|(^\\d{17}([0-9]|X)$)"); // 验证身份证号码
        m = p.matcher(IDnum);
        b = m.matches();
        if (!b) {
            toastFail(getString(R.string.please_enter_correct_idnum));
        }else{
            HttpClient.instance().set_user_id_no(IDnum,new NetworkRequestLoginResult());
        }
        return b;
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    class  NetworkRequestLoginResult extends HttpCallBack {
        public void onStart() {
            super.onStart();
            ProgressDialog.show(mContext, "正在加载..");
        }

        @Override
        public void onFailure(String error, String message) {
            ProgressDialog.disMiss();
            super.onFailure(error, message);
        }

        @Override
        public void onSuccess(ResponseBean responseBean) {
            ProgressDialog.disMiss();
            if(responseBean.isFailure()){
                BaseApplication.toast(responseBean.toString());
            }
            BaseApplication.toast("修改成功");
            finish();
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_idnumber;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }


}

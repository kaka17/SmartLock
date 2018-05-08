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
import com.sht.smartlock.widget.dialog.ProgressDialog;

public class EditUserNmaeActivity extends BaseActivity implements View.OnClickListener{
    private TextView tvBtnTitlePanel;
    private ImageView btn_btncancle;
    private Button btnPanelSave;
    private EditText editUserName;

    private String str_editUserName;
    private String strUsedrName;
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
        editUserName = (EditText)findViewById(R.id.editUserName);
        tvBtnTitlePanel.setText("修改用户名");
    }

     private void setOnClickLister(){
         btn_btncancle.setOnClickListener(listener);
         btnPanelSave.setOnClickListener(this);
     }

    private void initData(){
        Bundle bundle = this.getIntent().getExtras();
        str_editUserName = bundle.getString("tvFull_name");
        if(!str_editUserName.equals("")||!str_editUserName.equals("null")){
            editUserName.setText(str_editUserName);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnPanelSave:
               strUsedrName = editUserName.getText().toString();
                if(!strUsedrName.equals("")||!strUsedrName.equals("null")){
                    HttpClient.instance().set_user_true_name(strUsedrName,new NetworkRequestLoginResult());
                }
                break;
        }
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
//            ProgressDialog.disMiss();
            if(responseBean.isFailure()){
                BaseApplication.toast(responseBean.toString());
            }
            AppContext.setProperty(Config.KEY_USER_NAME,strUsedrName);
            BaseApplication.toast("修改成功");
            finish();
        }
    }



    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_user_nmae;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }
}

package com.sht.smartlock.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class ScanOKActivity extends BaseActivity {

    private TextView tvSure;
    private String url;
    private LinearLayout linTure;
    private ImageView ivIsTrue;
    private TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_scan_ok);
        Bundle extras = getIntent().getExtras();
        url=extras.getString(Config.URLFORREANDIN);
        onBack();
        initView();
        initScanOrder(url);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_scan_ok;
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
    private  void  initView(){
        tvSure = (TextView) findViewById(R.id.tvSure);

        linTure = (LinearLayout) findViewById(R.id.linTure);
        ivIsTrue = (ImageView) findViewById(R.id.ivIsTrue);
        tvInfo = (TextView) findViewById(R.id.tvInfo);


        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();

            }
        });


    }

    private void initScanOrder(String url){
        HttpClient.instance().scanUserRegister(url, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(ScanOKActivity.this, "正在提交前台下单...");
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

                    JSONObject jsonObject = new JSONObject(responseBean.toString());
                    String result = jsonObject.getString("result");
                    if (result.equals("true")) {
                        AppContext.toast("已提交等待酒店处理");
                    } else {
                        ivIsTrue.setImageResource(R.drawable.pic_failed);
                        tvInfo.setText("提交失败，请重新扫描二维码...");
//                        AppContext.toast("提交失败，请重新扫码");
                    }
                    linTure.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

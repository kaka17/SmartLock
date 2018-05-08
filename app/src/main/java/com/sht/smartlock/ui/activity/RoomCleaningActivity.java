package com.sht.smartlock.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sht.smartlock.AppManager;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.ui.activity.base.BaseActivity;

public class RoomCleaningActivity extends BaseActivity {
    private ImageView btn_cancle;
    private TextView tvTitlePanel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findviewbyid();
        setClickLister();
    }


    private void findviewbyid() {
        btn_cancle = (ImageView) findViewById(R.id.btn_cancle);
        tvTitlePanel = (TextView) findViewById(R.id.tvTitlePanel);

        tvTitlePanel.setText("打扫房间");

//        HttpClient.instance().addhotelService("1", "1", "aa", "2", new NetworkRequestLoginResult());
    }

    private void setClickLister(){
        btn_cancle.setOnClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AppManager.getAppManager().finishActivity();
        }
    };


    class  NetworkRequestLoginResult extends HttpCallBack {
        @Override
        public void onFailure(String error, String message) {
            super.onFailure(error, message);
        }

        @Override
        public void onFinish() {
            super.onFinish();
        }

        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onSuccess(ResponseBean responseBean) {
            BaseApplication.toast(responseBean.toString());
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_room_cleaning;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }
}

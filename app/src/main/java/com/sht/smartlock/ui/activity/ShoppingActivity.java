package com.sht.smartlock.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sht.smartlock.R;
import com.sht.smartlock.ui.activity.base.BaseActivity;

public class ShoppingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_shopping);
        onBack();
    }
    //获取布局文件
    @Override
    protected int getLayoutId() {
        return R.layout.activity_shopping;
    }

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
}

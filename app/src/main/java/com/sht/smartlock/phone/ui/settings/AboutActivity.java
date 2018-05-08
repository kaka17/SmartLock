package com.sht.smartlock.phone.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.phone.ui.ECSuperActivity;


public class AboutActivity extends ECSuperActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
        getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt,
                -1, null,
                null,
                getString(R.string.app_company), null, this);

    }
    private  TextView tv;

    private void initViews() {

        tv=(TextView)findViewById(R.id.tv_open_web);
        tv.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btn_left:
                hideSoftKeyboard();
                finish();
                break;
            case R.id.tv_open_web:
                Intent intent=new Intent(this,WebAboutActivity.class);
                startActivity(intent);


        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }


}

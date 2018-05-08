package com.sht.smartlock.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.ui.activity.base.BaseActivity;

import org.w3c.dom.Text;

public class BusinessCentreActivity extends BaseActivity {
    private ImageView btn_cancle;
    private TextView tvTitlePanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finviewbyid();
        setOnClickLister();
    }

    private void finviewbyid(){
        btn_cancle = (ImageView)findViewById(R.id.btn_cancle);
        tvTitlePanel = (TextView)findViewById(R.id.tvTitlePanel);

        tvTitlePanel.setText("商务中心");
    }

    private void setOnClickLister(){
        btn_cancle.setOnClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_cancle:
                    finish();
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_business_centre;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }
}

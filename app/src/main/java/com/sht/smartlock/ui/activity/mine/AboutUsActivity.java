package com.sht.smartlock.ui.activity.mine;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sht.smartlock.AppManager;
import com.sht.smartlock.R;
import com.sht.smartlock.ui.activity.base.BaseActivity;

public class AboutUsActivity extends BaseActivity {
    private ImageView btn_cancle;
    private TextView tvTitlePanel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findviewbyid();
        setOnClickLister();
    }


    private void findviewbyid(){
        btn_cancle = (ImageView)findViewById(R.id.btn_cancle);
        tvTitlePanel = (TextView)findViewById(R.id.tvTitlePanel);

        tvTitlePanel.setText("关于我们");
    }


    private void setOnClickLister(){
        btn_cancle.setOnClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
                    finish();
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about_us;
    }


    @Override
    protected boolean hasToolBar() {
        return false;
    }
}

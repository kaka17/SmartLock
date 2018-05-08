package com.sht.smartlock.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sht.smartlock.AppManager;
import com.sht.smartlock.R;
import com.sht.smartlock.ui.activity.base.BaseActivity;

public class FoodInfoActivity extends BaseActivity {

    private TextView tv_FoodInfo,tv_title;
    private ImageView iv_Food;
    private String caption,imgUrl,content,thumbnail,brief;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_food_info);
        caption= getIntent().getExtras().getString("caption");
//        imgUrl= getIntent().getExtras().getString("imgUrl");
        content= getIntent().getExtras().getString("content");
        thumbnail= getIntent().getExtras().getString("thumbnail");
        brief= getIntent().getExtras().getString("brief");
        initView();
        onBack();
    }
    @Override
    protected boolean hasToolBar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_food_info;
    }
    private void onBack() {
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AppManager.getAppManager().finishActivity(Submit_OrdersActivity.class);
                finish();
            }
        });
    }

    private void initView(){
        iv_Food = (ImageView) findViewById(R.id.iv_Food);
        tv_title= (TextView) findViewById(R.id.tv_title);
        tv_FoodInfo = (TextView) findViewById(R.id.tv_FoodInfo);
        tv_title.setText(caption);
        tv_FoodInfo.setText(brief);
        ImageLoader.getInstance().displayImage(thumbnail, iv_Food);
    }

}

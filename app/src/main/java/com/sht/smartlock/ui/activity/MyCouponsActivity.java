package com.sht.smartlock.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sht.smartlock.AppContext;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.ui.activity.myview.CouponsPopWindow;
import com.sht.smartlock.ui.adapter.CouponsAdapter;
import com.sht.smartlock.ui.entity.CouponsEntity;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import java.util.ArrayList;
import java.util.List;

public class MyCouponsActivity extends BaseActivity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    private TextView tvCoupons;
    private ListView lvCoupons;
    private CouponsAdapter adapter;
    private List<CouponsEntity> list = new ArrayList<>();
    private CouponsPopWindow popWindow;
    private PullToRefreshListView pullCoupons;
    private int pageid = 1;
    private String type = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_my_coupons);
        onBack();
        initView();
        setOnClickListenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_coupons;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }

    private void onBack() {
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        tvCoupons = (TextView) findViewById(R.id.tvCoupons);
        pullCoupons = (PullToRefreshListView) findViewById(R.id.pullCoupons);
        lvCoupons = pullCoupons.getRefreshableView();
        adapter = new CouponsAdapter(list, getApplicationContext());
        lvCoupons.addFooterView(getBottomView());
        lvCoupons.setAdapter(adapter);
    }

    private void setOnClickListenter() {
        tvCoupons.setOnClickListener(this);
        pullCoupons.setMode(PullToRefreshBase.Mode.BOTH);
        pullCoupons.setOnRefreshListener(this);
    }

    private View getBottomView() {
        View view = LayoutInflater.from(MyCouponsActivity.this).inflate(R.layout.coupons_bottom, null);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCoupons:
                toPop(v);
                break;
        }

    }

    private void toPop(View view) {
        if (popWindow == null || !popWindow.isShowing()) {
            popWindow = new CouponsPopWindow(MyCouponsActivity.this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popWindow.dismiss();
                    pageid = 1;
                    switch (v.getId()) {
                        case R.id.btnNewCoupons:

                            type = "1";
                            initData(pageid + "", type);
                            tvCoupons.setText(R.string.CouponsList01);
                            break;
                        case R.id.btnOld:
                            type = "2";
                            tvCoupons.setText(R.string.CouponsList02);
                            initData(pageid + "", type);
                            break;
                        case R.id.btnUsedOld:
                            type = "3";
                            tvCoupons.setText(R.string.CouponsList03);
                            initData(pageid + "", type);
                            break;
                        case R.id.btnAll:
                            type = "4";
                            tvCoupons.setText(R.string.CouponsList04);
                            initData(pageid + "", type);
                            break;
                    }
                }
            });
            // 加了下面这行，onItemClick才好用
            popWindow.setFocusable(true);
            /*
            * 	设置宽度等于上面控件的宽度
			* */
            popWindow.showAsDropDown(view);
        } else {
            popWindow.dismiss();
        }
    }

    private void initData(final String pageid, String search) {
        HttpClient.instance().redPackgelist(pageid, search, new HttpCallBack() {
            @Override
            public void onStart() {
                super.onStart();
                ProgressDialog.show(MyCouponsActivity.this, "正在加载红包列表...");
            }

            @Override
            public void onFailure(String error, String message) {
                super.onFailure(error, message);
                ProgressDialog.disMiss();
                pullCoupons.onRefreshComplete();
            }

            @Override
            public void onSuccess(ResponseBean responseBean) {
                ProgressDialog.disMiss();
                pullCoupons.onRefreshComplete();
                List<CouponsEntity> listData = responseBean.getListData(CouponsEntity.class);
                if (pageid.equals("1")) {
                    list.clear();
                }
                if (listData != null && listData.size() > 0) {
                    list.addAll(listData);
                } else {
                    switch (type){
                        case "1":
                            AppContext.toast(getResources().getString(R.string.MyCoupone01));
                            break;
                        case "2":
                            AppContext.toast(getResources().getString(R.string.MyCoupone02));
                            break;
                        case "3":
                            AppContext.toast(getResources().getString(R.string.MyCoupone03));
                            break;
                        case "4":
                            AppContext.toast(getResources().getString(R.string.MyCoupone04));
                            break;

                    }
                    AppContext.toast("暂无更多红包");
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData(pageid + "", type);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        pageid = 1;
        initData(pageid + "", type);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        pageid++;
        initData(pageid + "", type);
    }
}

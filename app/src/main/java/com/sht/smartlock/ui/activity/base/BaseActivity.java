package com.sht.smartlock.ui.activity.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.TextView;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.AppManager;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.UIHelper;
import com.sht.smartlock.ui.activity.sysbg.SystemBarTintManager;
import com.sht.smartlock.ui.chat.applib.controller.HXSDKHelper;
import com.sht.smartlock.util.DeviceUtil;
import com.sht.smartlock.util.VersionUpdateUtil;
import com.sht.smartlock.util.cache.DataCache;
import com.sht.smartlock.widget.dialog.CommonDialog;
import com.sht.smartlock.widget.dialog.DialogHelper;
import com.sht.smartlock.widget.dialog.ProgressDialog;
import com.umeng.analytics.MobclickAgent;

//import butterknife.ButterKnife;


public class BaseActivity extends AppCompatActivity {
    protected LayoutInflater mInflater;
    protected static final int STATE_NONE = 0;
    protected static final int STATE_INIT = 1;
    protected static final int STATE_REFRESH = 2;
    protected static final int STATE_LOADMORE = 3;
    protected int mState = STATE_NONE;
    protected Context mContext;
    protected DataCache mDataCache;
    public Toolbar mToolbar;
    TextView mToolbarTitle;
    private InputMethodManager mManager;
    private View mContentView;
    private boolean isCheckVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//设置系统标题栏和app主题一致
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.drawable.main_state_bar);
        mContext = this;
        mInflater = getLayoutInflater();
        mDataCache = DataCache.get(this);
        AppManager.getAppManager().addActivity(this);
        mContentView = inflateView(getLayoutId());
        setContentView(mContentView);
       // ButterKnife.inject(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        isCheckVersion = getIntent().getBooleanExtra("isCheckVersion", false);
        if (isCheckVersion) {
            VersionUpdateUtil updateUtil = new VersionUpdateUtil(this);
            updateUtil.check(false);
        }
        if (hasToolBar()) {
//            mToolbarTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
            setSupportActionBar(mToolbar);
            if (getToolBarTitle() != 0) {
                mToolbarTitle.setText(getToolBarTitle());
            }
            getSupportActionBar().setTitle("");
            if (hasBackBtn()) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                if (getNavigationIcon() != 0) {
                    mToolbar.setNavigationIcon(getNavigationIcon());
                } else {
                    mToolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
                }
            }
        } else {
            if (mToolbar != null) {
                mToolbar.setVisibility(View.GONE);
            }
        }

        mManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
    protected int getNavigationIcon() {
        return 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                mManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }


    protected boolean hasBackBtn() {
        return true;
    }

    public void setToolBarTitle(int title) {
        mToolbarTitle.setText(title);
    }

    public void setToolBarTitle(String title) {
        mToolbarTitle.setText(title);
    }

    protected boolean hasToolBar() {
        return true;
    }

    protected boolean hasFeatureTitle() { return true;  }

    protected int getToolBarTitle() {
        return R.string.app_name;
    }

    protected int getLayoutId() {
        return 0;
    }

    protected View inflateView(int resId) {
        return mInflater.inflate(resId, null);
    }

    protected void toastFail(String msg) {
        AppContext.toastFail(msg);
    }

    protected void toastFail(int msg) {
        AppContext.toastFail(getString(msg));
    }

    protected void toastSuccess(String msg) {
        AppContext.toastSuccess(msg);
    }

    protected void toastSuccess(int msg) {
        AppContext.toastSuccess(getString(msg));
    }

    public void showOnLoading(){
        ProgressDialog.show(this,R.string.on_loading);
    }
    public void dismissOnLoading(){
        ProgressDialog.disMiss();
    }
    public void showListItemDialog(Integer id, String title, String[] array) {
        CommonDialog dialog = DialogHelper.getPinterestDialogCancelable(this);
        dialog.setTitle(title);
        dialog.setNegativeButton(R.string.cancel, null);
        dialog.setItemsWithoutChk(array, new OnListItemClickListener(id, dialog));
        dialog.show();
    }

    public void showDialog(String title, String message, DialogInterface.OnClickListener positiveClickListener, DialogInterface.OnClickListener negativeClickListener) {
        CommonDialog dialog = DialogHelper.getPinterestDialogCancelable(this);
        if (!title.equals("")) {
            dialog.setTitle(title);
        }
        dialog.setMessage(message);
        dialog.setPositiveButton(R.string.ok, positiveClickListener);
        dialog.setNegativeButton(R.string.cancel, negativeClickListener);
        dialog.show();
    }

    public void showListItemDialog(String title, String[] array) {
        CommonDialog dialog = DialogHelper.getPinterestDialogCancelable(this);
        dialog.setTitle(title);
        dialog.setNegativeButton(R.string.cancel, null);
        dialog.setItemsWithoutChk(array, new OnListItemClickListener(Config.DEFAULT_LISTDIALOG_ID, dialog));
        dialog.show();
    }

    public void injectRipple(View... view) {
        UIHelper.injectRipple(this, view);
    }

    public class OnListItemClickListener implements AdapterView.OnItemClickListener {
        private int id;
        private CommonDialog dialog;

        public OnListItemClickListener(int id, CommonDialog dialog) {
            this.id = id;
            this.dialog = dialog;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (this.id == Config.DEFAULT_LISTDIALOG_ID) {
                onListItemSelected(parent.getAdapter().getItem(position).toString(), position);
            } else {
                onListItemSelected(this.id, parent.getAdapter().getItem(position).toString(), position);
            }
            dialog.dismiss();
        }
    }

    protected void onListItemSelected(String content, int position) {
    }

    protected void onListItemSelected(Integer id, String content, int position) {
    }

    @Override
    public void onBackPressed() {
        DeviceUtil.hideSoftKeyboard(getWindow().getDecorView());
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    public View getContentView() {
        return mContentView;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  JPushInterface.onResume(this);
//        HXSDKHelper.getInstance().getNotifier().reset();

    }


    @Override
    protected void onPause() {
        super.onPause();
        //    JPushInterface.onPause(this);
    }

    public boolean isCheckVersion() {
        return isCheckVersion;
    }

    public void setIsCheckVersion(boolean isCheckVersion) {
        this.isCheckVersion = isCheckVersion;
    }
}

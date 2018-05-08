package com.sht.smartlock.phone.ui;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

import com.umeng.analytics.MobclickAgent;
import com.sht.smartlock.R;
import com.sht.smartlock.phone.common.ActivityTaskUtils;
import com.sht.smartlock.phone.common.dialog.ECProgressDialog;
import com.sht.smartlock.phone.common.utils.CrashHandler;
import com.sht.smartlock.phone.common.utils.LogUtil;
import com.sht.smartlock.phone.common.view.TopBarView;


import java.util.Set;

/**
 * Created by Jorstin on 2015/3/17.
 */
@ActivityTransition(0)
public abstract class ECSuperActivity extends ECFragmentActivity implements GestureDetector.OnGestureListener{

    private static final String TAG = ECSuperActivity.class.getSimpleName();
    /**
     * 初始化应用ActionBar
     */
    private CCPActivityBase mBaseActivity = new CCPActivityImpl(this);
    /**
     * 初始化广播接收器
     */
    private InternalReceiver internalReceiver;
    private GestureDetector mGestureDetector = null;
    private boolean mIsHorizontalScrolling = false;
    private int mScrollLimit = 0;
    private boolean mIsChildScrolling = false;
    private int mMinExitScrollX = 0;

    /**屏幕资源*/
    private KeyguardManager.KeyguardLock mKeyguardLock = null;
    private KeyguardManager mKeyguardManager = null;
    private PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseActivity.init(getBaseContext(), this);
        onActivityInit();
        LogUtil.d(TAG, "checktask onCreate:" + super.getClass().getSimpleName() + "#0x"
                + super.hashCode() + ", taskid:" + getTaskId() + ", task:" + new ActivityTaskUtils(this));
        abstracrRegist();

        onActivityCreate();
    }


    /**
     * 唤醒屏幕资源
     */
    protected void enterIncallMode() {
        if (!(mWakeLock.isHeld())) {
            // wake up screen
            // BUG java.lang.RuntimeException: WakeLock under-locked
            mWakeLock.setReferenceCounted(false);
            mWakeLock.acquire();
        }
        mKeyguardLock = this.mKeyguardManager.newKeyguardLock("");
        mKeyguardLock.disableKeyguard();
    }

    /**
     * 初始化资源
     */
    protected void initProwerManager() {
        mWakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP , "CALL_ACTIVITY#" + super.getClass().getName());
        mKeyguardManager = ((KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE));
    }

    /**
     * 释放资源
     */
    protected void releaseWakeLock() {
        try {
            if (this.mWakeLock.isHeld()) {
                if (this.mKeyguardLock != null) {
                    this.mKeyguardLock.reenableKeyguard();
                    this.mKeyguardLock = null;
                }
                this.mWakeLock.release();
            }
            return;
        } catch (Exception e) {
            LogUtil.e(TAG, e.toString());
        }
    }

    /********************************************************/


    protected final void registerReceiver(String[] actionArray) {
        if (actionArray == null) {
            return;
        }
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(SDKCoreHelper.ACTION_KICK_OFF);
        for (String action : actionArray) {
            intentfilter.addAction(action);
        }
        if (internalReceiver == null) {
            internalReceiver = new InternalReceiver();
        }
        registerReceiver(internalReceiver, intentfilter);
    }

   

    /**
     * The sub Activity implement, set the Ui Layout
     * @return
     */
    protected abstract int getLayoutId();
    /**
     * 子类重载该方法自定义标题布局文件
     * @return
     */
    public int getTitleLayout() {
        return R.layout.ec_title_view_base;
    }

    protected void onActivityInit() {
    }

    /**
     * 如果子界面需要拦截处理注册的广播
     * 需要实现该方法
     * @param context
     * @param intent
     */
    protected void handleReceiver(Context context, Intent intent) {
        // 广播处理
        if(intent == null ) {
            return ;
        }
        if(SDKCoreHelper.ACTION_KICK_OFF.equals(intent.getAction())) {
            finish();
        }
    }

    public void onBaseContentViewAttach(View contentView) {
        setContentView(contentView);
    }

    public FragmentActivity getActionBarActivityContext() {
        return mBaseActivity.getFragmentActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBaseActivity.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        // HSCoreService
        super.onResume();
        CrashHandler.getInstance().setContext(this);
        mBaseActivity.onResume();
        MobclickAgent.onResume(this);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        LogUtil.d(TAG, "checktask onCreate:" + super.getClass().getSimpleName() + "#0x"
                + super.hashCode() + ", taskid:" + getTaskId() + ", task:" + new ActivityTaskUtils(this));
        super.onDestroy();
        mBaseActivity.onDestroy();
        try {
            unregisterReceiver(internalReceiver);
        } catch (Exception e) {
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(mBaseActivity.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(mBaseActivity.onKeyUp(keyCode, event)) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }


    public void toggleSoftInput() {
        mBaseActivity.toggleSoftInput();
    }

    public void hideSoftKeyboard() {
        mBaseActivity.hideSoftKeyboard();
    }


    /**
     * 跳转
     * @param clazz
     * @param intent
     */
    protected void startCCPActivity(Class<? extends Activity> clazz , Intent intent) {
        intent.setClass(this, clazz);
        startActivity(intent);
    }


    // Internal calss.
    private class InternalReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent == null || intent.getAction() == null ) {
                return ;
            }
            handleReceiver(context, intent);
        }
    }

    public Activity getActivitContext() {
        if(getParent() != null) {
            return getParent();
        }
        return null;
    }

    public TopBarView getTopBarView() {
        return mBaseActivity.getTopBarView();
    }

    /**
     * 设置ActionBar标题
     * @param resid
     */
    public void setActionBarTitle(int resid) {
        mBaseActivity.setActionBarTitle(getString(resid));
    }

    /**
     * 设置ActionBar标题
     * @param text
     */
    public void setActionBarTitle(CharSequence text) {
        mBaseActivity.setActionBarTitle(text);
    }

    /**
     * 返回ActionBar 标题
     * @return
     */
    public final CharSequence getActionBarTitle() {
        return mBaseActivity.getActionBarTitle();
    }

    /**
     * #getLayoutId()
     * @return
     */
    public View getActivityLayoutView() {
        return mBaseActivity.getActivityLayoutView();
    }

    /**
     *
     */
    public final void showTitleView() {
        mBaseActivity.showTitleView();
    }

    /**
     *
     */
    public final void hideTitleView() {
        mBaseActivity.hideTitleView();
    }

    public boolean isEnableRightSlideGesture() {
        return true;
    }

    protected Set<View> getReturnInvalidAreaView() {
        return null;
    }

    private Rect getReturnInvalidArea(View view) {
        if (view == null)
            return null;
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        Rect rect = new Rect();
        rect.left = location[0];
        rect.top = location[1];
        rect.right = (rect.left + view.getRight() - view.getLeft());
        rect.bottom = (rect.top + view.getBottom() - view.getTop());
        return rect;
    }

    private void reset() {
        this.mIsHorizontalScrolling = false;
        this.mScrollLimit = 0;
    }

    private boolean isCannotHorizontalScroll() {
        return (this.mScrollLimit >= 5);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!(isEnableRightSlideGesture())) {
            return false;
        }
        if (isCannotHorizontalScroll()) {
            return false;
        }
        if ((!(this.mIsHorizontalScrolling)) && (Math.abs(2.0F * distanceY) > Math.abs(distanceX))) {
            this.mScrollLimit = (1 + this.mScrollLimit);
            return false;
        }
        this.mIsHorizontalScrolling = true;
        if(e1 == null || e2 == null) {
            return false;
        }
        float f1 = 0.0F;
        if (!(this.mIsChildScrolling)) {
            if(e1 != null) {
                f1 = e1.getX();
            }
            float f2 = 0.0F;
            if (e2 != null) {
                f2 = e2.getX();
            }
            if (f1 - f2 < getMinExitScrollX())
            {
                this.mScrollLimit = 5;
                close();
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param mute
     */
    public SpannableString setNewMessageMute(boolean mute) {
        mBaseActivity.setMute(mute);
        return mBaseActivity.buildActionTitle();
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    public void abstracrRegist() {
        registerReceiver(new String[]{SDKCoreHelper.ACTION_KICK_OFF});
    }

    private int getMinExitScrollX() {
        if (this.mMinExitScrollX == 0) {
            this.mMinExitScrollX = (int) (getResources().getInteger(R.integer.min_exit_scroll_factor) * getWidthPixels() / 100.0F);
            this.mMinExitScrollX = (-this.mMinExitScrollX);
        }
        return this.mMinExitScrollX;
    }

    public int getWidthPixels() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    public void close() {
        finish();
    }
    private ECProgressDialog mPostingdialog;
    public void showCommonProcessDialog(String tips) {
		mPostingdialog = new ECProgressDialog(this,
				R.string.progress_common);
		mPostingdialog.show();
	}

	/**
	 * 关闭对话框
	 */
	public void dismissCommonPostingDialog() {
		if (mPostingdialog == null || !mPostingdialog.isShowing()) {
			return;
		}
		mPostingdialog.dismiss();
		mPostingdialog = null;
	}

}

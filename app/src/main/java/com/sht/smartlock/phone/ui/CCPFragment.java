/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.sht.smartlock.phone.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;
import com.sht.smartlock.R;
import com.sht.smartlock.phone.common.utils.LogUtil;
import com.sht.smartlock.phone.common.view.SwipeBackLayout;
import com.sht.smartlock.phone.common.view.TopBarView;


/**
 * 应用页面View基类，每个View 必须要继承与该类并实现相应的方法
 * Created by Jorstin on 2015/3/18.
 */
public abstract class CCPFragment extends Fragment {

    private static final String TAG = "ECSDK_Demo.CCPFragment";

    /**广播拦截器*/
    private InternalReceiver internalReceiver;
    /**当前页面是否可以销毁*/
    private boolean isFinish = false;
    /**
     * 初始化应用ActionBar
     */
    private CCPActivityBase mBaseActivity = new CCPFragmentImpl(this);
    public SwipeBackLayout mSwipeBackLayout;

    final Handler mSupperHandler = new Handler() {

        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseActivity.init(getActivity().getBaseContext(), getActivity());
        onFragmentInit();
        abstracrRegist();
    }

    public void abstracrRegist() {
        registerReceiver(new String[]{SDKCoreHelper.ACTION_KICK_OFF});
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mBaseActivity.setRootConsumeWatcher(null);
        return mBaseActivity.mBaseLayoutView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            // 注销广播监听器
            getActivity().unregisterReceiver(internalReceiver);
        } catch (Exception e) {
        }
    }

    public void onDestroy() {
        super.onDestroy();
        this.mBaseActivity.onDestroy();
    }

    /**
     * 处理按钮按下事件
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(mBaseActivity.onKeyDown(keyCode, event)) {
            return true;
        }
        if((event.getKeyCode() == KeyEvent.KEYCODE_BACK)
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
            return true;
        }
        return false;
    }

    /**
     * 注册广播Action，子类如果需要监听广播可以调用
     * 该方法传入相应事件的Action
     * @param actionArray
     */
    protected final void registerReceiver(String[] actionArray) {
        if (actionArray == null) {
            return;
        }
        IntentFilter intentfilter = new IntentFilter();
        for (String action : actionArray) {
            intentfilter.addAction(action);
        }
        //intentfilter.addAction(CASIntent.ACTION_SERVICE_DESTORY);
        //intentfilter.addAction(CASIntent.ACTION_FORCE_DEACTIVE);
        if (internalReceiver == null) {
            internalReceiver = new InternalReceiver();
        }
        getActivity().registerReceiver(internalReceiver, intentfilter);
    }

    /**
     * 返回一个Handler 主线程
     * @return
     */
    public Handler getSupperHandler() {
        return mSupperHandler;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setIsFinish(boolean isFinish) {
        this.isFinish = isFinish;
    }

    /**
     * 重载页面关闭方法
     */
    public void finish() {
        if(getActivity() == null) {
            return;
        }
        if(isFinish) {
            getActivity().finish();
            return;
        }

        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onResume() {
        long currentTimeMillis = System.currentTimeMillis();
        super.onResume();
        MobclickAgent.onPageStart("MainScreen"); //统计页面
        this.mBaseActivity.onResume();
        LogUtil.d(TAG, "KEVIN MMActivity onPause:" + (System.currentTimeMillis() - currentTimeMillis));
    }

    public void onStart() {
        this.mBaseActivity.onStart();
        super.onStart();
    }

    public final void setScreenEnable(boolean screenEnable) {
        this.mBaseActivity.setScreenEnable(screenEnable);
    }


    @Override
    public void onPause() {
        long currentTimeMillis = System.currentTimeMillis();
        super.onPause();
        MobclickAgent.onPageEnd("MainScreen");
        this.mBaseActivity.onPause();
        LogUtil.d(TAG, "KEVIN MMActivity onPause:" + (System.currentTimeMillis() - currentTimeMillis));
    }

    /**
     * 查找	View
     * @param paramInt
     * @return
     */
    public final View findViewById(int paramInt) {
        return getView().findViewById(paramInt);
    }

    /**
     * 如果子界面需要拦截处理注册的广播
     * 需要实现该方法
     * @param context
     * @param intent
     */
    protected void handleReceiver(Context context, Intent intent) {
        // 广播处理
    }

    /**
     * 每个页面需要实现该方法返回一个该页面所对应的资源ID
     * @return 页面资源ID
     */
    protected abstract int getLayoutId();

    /**
     * 如果需要自定义页面标题，则需要重载该方法
     * @return
     */
    public int getTitleLayoutId() {
        return R.layout.ec_title_view_base;
    }

    protected void onFragmentInit() {

    }

    public void onBaseContentViewAttach(View contentView) {

    }

    public TopBarView getTopBarView() {
        return mBaseActivity.getTopBarView();
    }
    /**
     *
     * @param mute
     */
    public SpannableString setNewMessageMute(boolean mute) {
        mBaseActivity.setMute(mute);
        return mBaseActivity.buildActionTitle();
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


    public void toggleSoftInput() {
        mBaseActivity.toggleSoftInput();
    }

    public void hideSoftKeyboard() {
        mBaseActivity.hideSoftKeyboard();
    }

    /**
     * 自定义应用全局广播处理器，方便全局拦截广播并进行分发
     * @author 容联•云通讯
     * @date 2014-12-4
     * @version 4.0
     */
    private class InternalReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent == null || intent.getAction() == null) {
                return ;
            }
            handleReceiver(context, intent);
        }

    }
}

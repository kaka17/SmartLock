package com.sht.smartlock.phone.ui;

import android.view.View;

/**
 * com.yuntongxun.ecdemo.ui in ECDemo_Android
 * Created by Jorstin on 2015/6/16.
 */
public class CCPFragmentImpl extends CCPActivityBase {
    final private CCPFragment mFragment;

    public CCPFragmentImpl(CCPFragment fragment) {
        mFragment  = fragment;
    }

    @Override
    protected void onInit() {
        mFragment.onFragmentInit();
    }

    @Override
    protected int getLayoutId() {
        return mFragment.getLayoutId();
    }

    @Override
    protected View getContentLayoutView() {
        return null;
    }

    @Override
    protected String getClassName() {
        return mFragment.getClass().getName();
    }

    @Override
    protected void dealContentView(View contentView) {
        mFragment.onBaseContentViewAttach(contentView);
    }

    @Override
    public int getTitleLayout() {
        return mFragment.getTitleLayoutId();
    }

}

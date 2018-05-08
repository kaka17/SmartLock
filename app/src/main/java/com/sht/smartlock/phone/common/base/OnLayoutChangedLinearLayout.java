package com.sht.smartlock.phone.common.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * com.yuntongxun.ecdemo.common.base in ECDemo_Android
 * Created by Jorstin on 2015/6/16.
 */
public class OnLayoutChangedLinearLayout extends LinearLayout {

    public OnLayoutChangedListener mListener;
    public OnLayoutChangedLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public OnLayoutChangedLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(mListener == null) {
            return ;
        }
        mListener.onLayoutChanged();
    }

    public void setOnChattingLayoutChangedListener(OnLayoutChangedListener listener) {
        mListener = listener;
    }

    public interface OnLayoutChangedListener {
        void onLayoutChanged();
    }
}
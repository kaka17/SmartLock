package com.sht.smartlock.phone.ui.voip;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.sht.smartlock.R;


/**
 * com.yuntongxun.ecdemo.ui.voip in ECDemo_Android
 * Created by Jorstin on 2015/7/9.
 */
public class ECCallShowSmallTalker extends LinearLayout {

    public ECCallShowSmallTalker(Context context) {
        this(context, null);
    }

    public ECCallShowSmallTalker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ECCallShowSmallTalker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);

        initView();
    }

    private void initView() {
        View.inflate(getContext(), R.layout.ec_voip_call_show_small_talker, this);
    }
}

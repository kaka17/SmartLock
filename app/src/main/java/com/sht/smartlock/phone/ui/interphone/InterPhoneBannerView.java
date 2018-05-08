package com.sht.smartlock.phone.ui.interphone;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sht.smartlock.R;

/**
 * 实时对讲状态通知显示
 * com.yuntongxun.ecdemo.ui.interphone in ECDemo_Android
 * Created by Jorstin on 2015/7/16.
 */
public class InterPhoneBannerView extends LinearLayout {

    /**实时对讲状态通知显示*/
    private TextView mTipsView;
    /**实时对讲在线人数/总人数*/
    private TextView mCountView;
    /**实时对讲图标*/
    private ImageView mPersonView;

    /**实时对讲参与总数*/
    private int mCount;
    /**实时对讲在线总数*/
    private int mOnlineCount;

    public InterPhoneBannerView(Context context) {
        this(context, null);
    }

    public InterPhoneBannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InterPhoneBannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);

        initBannerView();
    }

    private void initBannerView() {
        View.inflate(getContext(), R.layout.ec_inter_phone_banner, this);
        mTipsView = (TextView) findViewById(R.id.notice_tips);
        mCountView = (TextView) findViewById(R.id.count_tv);
        mPersonView = (ImageView) findViewById(R.id.ic_person);
    }

    /**
     * 设置参与实时对讲人数
     * @param count 参与实时对讲人数
     */
    public void setCount(int count) {
        if(count < 0) {
            count = 0;
        }
        mCount = count;
        invalidateCountView();
    }

    /**
     * 设置当前在线总数
     * @param onlineCount 在线总数
     */
    public void setOnlineCount(int onlineCount) {
        if(onlineCount < 0) {
            onlineCount = 0;
        }
        mOnlineCount = onlineCount;
        invalidateCountView();
    }

    /**
     * 设置实时对讲状态
     * @param id
     */
    public void setTips(int id) {
        if(id > 0) {
            setTips(getResources().getString(id));
        }
    }

    /**
     * 设置实时对讲状态
     * @param text
     */
    public void setTips(String text) {
        mTipsView.setText(text);
    }

    /**
     * 设置界面显示人数对比
     */
    private void invalidateCountView() {
        mCountView.setText(mOnlineCount + "/" + mCount);
    }

    public void setOnLineCount(int online,int all){
        mCountView.setText(online + "/" + all);

    }
}

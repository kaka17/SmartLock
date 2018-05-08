package com.sht.smartlock.phone.ui.meeting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sht.smartlock.R;
import com.sht.smartlock.phone.common.utils.LogUtil;
import com.sht.smartlock.phone.ui.chatting.base.emoji.Objects;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECVoIPSetupManager;
import com.yuntongxun.ecsdk.platformtools.ECHandlerHelper;

import java.util.Random;

/**
 * com.yuntongxun.ecdemo.ui.meeting in ECDemo_Android
 * Created by Jorstin on 2015/7/17.
 */
public class VoiceMeetingMicAnim extends LinearLayout implements Runnable {
    private static final String TAG = "ECSDK_Demo.VoiceMeetingMicAnim";
    /**底部左部声音振幅*/
    private LinearLayout mLeftAmplitude;
    /**底部右部声音振幅*/
    private LinearLayout mRightAmplitude;
    /**静音按钮*/
    private ImageView mMikeView;
    /**是否开始*/
    private boolean mStart = false;
    /**是否静音*/
    private boolean isMikeEnable;
    private Objects mLock = new Objects();
    private OnMeetingMicEnableListener mCallback;

    private OnClickListener mMicClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                mMikeView.setEnabled(false);
                ECVoIPSetupManager setupManager = ECDevice.getECVoIPSetupManager();
                if(setupManager != null) {
                    setupManager.setMute(!isMikeEnable);
                    isMikeEnable = setupManager.getMuteStatus();
                }
                if(isMikeEnable) {
                    initBottomStatus(0);
                } else {
                    synchronized (mLock) {
                        //new Thread(mikeAnimRunnable).start();
                        mLock.notify();

                    }

                }
                if(mCallback != null) {
                    mCallback.onMeetingMicEnable(isMikeEnable);
                }
                mMikeView.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public VoiceMeetingMicAnim(Context context) {
        this(context, null);
    }

    public VoiceMeetingMicAnim(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public VoiceMeetingMicAnim(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
    }

    /**
     * 当前Mic按钮的状态
     * @param isMikeEnable
     */
    public void setMikeEnable(boolean isMikeEnable) {
        this.isMikeEnable = isMikeEnable;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLeftAmplitude = (LinearLayout) findViewById(R.id.chatroom_l_status);
        mRightAmplitude = (LinearLayout) findViewById(R.id.chatroom_r_status);
        mMikeView = (ImageView) findViewById(R.id.chatroom_mike);
        mMikeView.setOnClickListener(mMicClickListener);

        initBottomStatus(0);
    }

    /**
     * 开始显示振幅
     */
    public void startMicAmpl() {
        mStart = true;
        new Thread(mikeAnimRunnable).start();
    }

    public void stopMicAmpl() {
        mStart = false;
    }

    public void setOnMeetingMicEnableListener(OnMeetingMicEnableListener listener) {
        mCallback = listener;
    }

    /**
     * 计算随机数
     * @param num
     * @return 随机数
     */
    private int randomNum(int num) {
        Random rand = new Random();
        return Math.abs(rand.nextInt() % num);
    }


    synchronized void  initBottomStatus(int num){//4
        if(mLeftAmplitude == null || mRightAmplitude == null) {
            LogUtil.w(TAG, "mLeftAmplitude: " + mLeftAmplitude + " ,mRightAmplitude: " + mRightAmplitude);
            return ;
        }
        mLeftAmplitude.removeAllViews();
        mRightAmplitude.removeAllViews();
        for (int i = 0; i < 6; i++) {
            ImageView imageViewl_i = new ImageView(getContext());
            ImageView imageViewR_i = new ImageView(getContext());
            if(i > (6 - num - 1)) {//1
                imageViewl_i.setImageResource(R.drawable.chatroom_speaker);
            } else {
                imageViewl_i.setImageResource(R.drawable.chatroom_unspeaker);

            }
            if(i >= num) {//4
                imageViewR_i.setImageResource(R.drawable.chatroom_unspeaker);
            } else {
                imageViewR_i.setImageResource(R.drawable.chatroom_speaker);
            }
            mLeftAmplitude.addView(imageViewl_i ,new LayoutParams(LayoutParams.WRAP_CONTENT , LayoutParams.WRAP_CONTENT , 1.0f));
            mRightAmplitude.addView(imageViewR_i, new LayoutParams(LayoutParams.WRAP_CONTENT , LayoutParams.WRAP_CONTENT , 1.0f));
        }
    }


    Runnable mikeAnimRunnable = new Runnable() {

        @Override
        public void run() {
            while(mStart) {
                LogUtil.d(TAG, "1mikeAnimRunnable isJion : "  + mStart + " , isMikeEnable :" + isMikeEnable);
                if(isMikeEnable) {
                    synchronized (mLock) {
                        try {
                            mLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                ECHandlerHelper.postRunnOnUI(VoiceMeetingMicAnim.this);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            LogUtil.d(TAG, "1mikeAnimRunnable isJion : "  + mStart + " , isMikeEnable :" + isMikeEnable);
        }
    };

    @Override
    public void run() {
        int abs = randomNum(6);
        initBottomStatus(abs);
    }


    public interface OnMeetingMicEnableListener {
        void onMeetingMicEnable(boolean enable);
    }
}

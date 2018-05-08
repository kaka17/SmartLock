package com.sht.smartlock.phone.ui.interphone;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;

import com.sht.smartlock.R;


/**
 * 实时对讲抢麦控制
 * Created by Jorstin on 2015/7/27.
 */
public class InterPhoneMicController extends FrameLayout implements View.OnTouchListener {

    private static final String TAG = "ECSDK_Demo.InterPhoneMicController";

    public static final int WHAT_ON_REQUEST_MIC_CONTROL = 0x2020;
    public static final int WHAT_ON_RELESE_MIC_CONTROL = 0x2021;
    public static final int WHAT_ON_PLAY_MUSIC = 0x2022;
    public static final int WHAT_ON_STOP_MUSIC = 0x2023;

    private static final long INTER_PHONE_TIME_INTERVAL = 500;

    /**实时对讲抢麦按钮*/
    private Button mInterSpeak;
    /**实时对讲控麦计时*/
    private Chronometer mChronometer;
    /**是否按下抢麦按钮*/
    private boolean isDownEvent;
    /**判断按钮事件是否有效*/
    private long downTime = 0;
    /**实时对讲Mic按钮事件*/
    private OnInterPhoneMicListener mOnInterPhoneMicListener;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_ON_REQUEST_MIC_CONTROL:
                    if(isDownEvent && mOnInterPhoneMicListener != null) {
                        mOnInterPhoneMicListener.onControlMic();
                    }
                    break;
            }
        }
    };

    public InterPhoneMicController(Context context) {
        this(context, null);
    }

    public InterPhoneMicController(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InterPhoneMicController(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initView();
    }

    private void initView() {
        mInterSpeak = (Button) findViewById(R.id.inter_phone_speak);
        mChronometer = (Chronometer) findViewById(R.id.chronometer);

        if(mInterSpeak != null) {
            mInterSpeak.setEnabled(false);
            mInterSpeak.setOnTouchListener(this) ;
        }
    }

    /**
     * 控麦按钮是否可用
     * @param enable
     */
    public void setInterSpeakEnabled(boolean enable) {
        if(mInterSpeak != null) {
            mInterSpeak.setEnabled(enable);
            mInterSpeak.setOnTouchListener(this) ;
        }
    }
    /**
     * 是否显示控麦时间
     * @param type
     */
    public void setControlMicType(MicType type) {
        if(mChronometer == null) {
            return ;
        }
        if(type == MicType.CONTROL) {
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.setVisibility(View.VISIBLE);
            mChronometer.start();
            mChronometer.setVisibility(View.VISIBLE);
            mInterSpeak.setBackgroundResource(R.drawable.voice_intephone_connect) ;
            return ;
        }

        if(type == MicType.IDLE) {
            mChronometer.stop();
            mChronometer.setVisibility(View.GONE);
            return ;
        }

        if(isDownEvent) {
            mInterSpeak.setBackgroundResource(R.drawable.voice_intephone_failed) ;
        } else {
            mInterSpeak.setBackgroundResource(R.drawable.voice_intephone_normal) ;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Message obtainMessage;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(mOnInterPhoneMicListener != null) {
                    mOnInterPhoneMicListener.onPrepareControlMic();
                }
                if (mHandler != null ) {
                    obtainMessage = mHandler.obtainMessage(WHAT_ON_PLAY_MUSIC);
                    mHandler.sendMessage(obtainMessage);
                }
                isDownEvent = true;
                downTime = event.getDownTime();
                mInterSpeak.setBackgroundResource(R.drawable.voice_intephone_pressed);
                if (mHandler != null ) {
                    obtainMessage = mHandler.obtainMessage(WHAT_ON_REQUEST_MIC_CONTROL);
                    mHandler.sendMessageDelayed(obtainMessage, INTER_PHONE_TIME_INTERVAL);
                }
                break;
            case MotionEvent.ACTION_UP:
                isDownEvent = false;
                if (mHandler != null ) {
                    mHandler.removeMessages(WHAT_ON_REQUEST_MIC_CONTROL);
                }
                mInterSpeak.setBackgroundResource(R.drawable.voice_intephone_normal);
                if((event.getEventTime() - downTime) >= INTER_PHONE_TIME_INTERVAL) {
                    if(mOnInterPhoneMicListener != null) {
                        mOnInterPhoneMicListener.onReleaseMic();
                        downTime = 0;
                    }
                }
                break;
        }
        return false;
    }

    /**
     * 设置实时对讲按钮通知
     * @param listener
     */
    public void setOnInterPhoneMicListener(OnInterPhoneMicListener listener) {
        mOnInterPhoneMicListener = listener;
    }

    public enum MicType {
        CONTROL , IDLE , ERROR;
    }


    public interface OnInterPhoneMicListener {
        void onPrepareControlMic();
        void onControlMic();
        void onReleaseMic();
    }
}

package com.sht.smartlock.phone.ui.meeting;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sht.smartlock.R;
import com.yuntongxun.ecsdk.platformtools.ECHandlerHelper;

import java.util.Random;

/**
 * com.yuntongxun.ecdemo.ui.meeting in ECDemo_Android
 * Created by Jorstin on 2015/7/17.
 */
public class VoiceMeetingCenter extends FrameLayout {

    private static int[] STATUS_ICON = new int[] {
            R.drawable.animation_box01
            , R.drawable.animation_box02
            ,R.drawable.animation_box03
            ,R.drawable.animation_box04
    };

    private LinearLayout mCenterAmplitude;
    /**是否启用动画*/
    private boolean mStart = false;
    public VoiceMeetingCenter(Context context) {
        this(context, null);
    }

    public VoiceMeetingCenter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceMeetingCenter(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        mCenterAmplitude = (LinearLayout) findViewById(R.id.chatroom_center_status);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    /**
     * 开始
     */
    public void startAmplitude() {
        mStart = true;
        new Thread(CenterAnimRunnable).start();
    }

    public void stopAmplitude() {
        mStart = false;
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


    synchronized void  initCenterStatus(int num){//4
        mCenterAmplitude.removeAllViews();
        for (int i = 0; i < num; i++) {
            ImageView imageView = new ImageView(getContext());
            if(STATUS_ICON != null ) {
                imageView.setImageResource(STATUS_ICON[randomNum(4)]);
                mCenterAmplitude.addView(imageView, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT , LayoutParams.WRAP_CONTENT , 1.0f));
            }
        }
    }


    Runnable CenterAnimRunnable = new Runnable() {

        @Override
        public void run() {
            while(mStart) {
                ECHandlerHelper.postRunnOnUI(new Runnable() {
                    @Override
                    public void run() {
                        initCenterStatus(15);
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}

package com.sht.smartlock.helper;

import android.widget.ImageView;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.base.BaseApplication;

public class SceneAnimation {
    private ImageView mImageView;
    private int[] mFrameRess;
    private int[] mDurations;
    private int mDuration;

    private int mLastFrameNo;
    private long mBreakDelay;
    private Runnable run;


    public SceneAnimation(ImageView pImageView, int[] pFrameRess,
                          int[] pDurations) {
        mImageView = pImageView;
        mFrameRess = pFrameRess;
        mDurations = pDurations;
        mLastFrameNo = pFrameRess.length - 1;

        mImageView.setBackgroundResource(mFrameRess[0]);
//        play(1);
    }

    public SceneAnimation(ImageView pImageView, int[] pFrameRess, int pDuration) {
        mImageView = pImageView;
        mFrameRess = pFrameRess;
        mDuration = pDuration;
        mLastFrameNo = pFrameRess.length - 1;

        mImageView.setBackgroundResource(mFrameRess[0]);
//        playConstant(1);
    }

    public SceneAnimation(ImageView pImageView, int[] pFrameRess,
                          int pDuration, long pBreakDelay) {
        mImageView = pImageView;
        mFrameRess = pFrameRess;
        mDuration = pDuration;
        mLastFrameNo = pFrameRess.length - 1;
        mBreakDelay = pBreakDelay;

        mImageView.setBackgroundResource(mFrameRess[0]);
        playConstant(1);
    }

    public void play(final int pFrameNo) {
        mImageView.postDelayed(new Runnable() {
            public void run() {
                mImageView.setBackgroundResource(mFrameRess[pFrameNo]);
                if (pFrameNo == mLastFrameNo)
                    play(0);
                else
                    play(pFrameNo + 1);
            }
        }, mDurations[pFrameNo]);
    }

    public void playConstant(final int pFrameNo) {
        run=new Runnable() {
            @Override
            public void run() {
                mImageView.setBackgroundResource(mFrameRess[pFrameNo]);

                if (pFrameNo == mLastFrameNo) {
                    playConstant(0);
                }
                else {
                    playConstant(pFrameNo + 1);
                }
            }
        };
        mImageView.postDelayed(run, 50);
//        mImageView.postDelayed(run, pFrameNo == mLastFrameNo && mBreakDelay > 0 ? mBreakDelay
//                : mDuration);


        //
//        mImageView.postDelayed(new Runnable() {
//            public void run() {
//                mImageView.setBackgroundResource(mFrameRess[pFrameNo]);
//
//                if (pFrameNo == mLastFrameNo) {
//                    playConstant(0);
//                }
//                else {
//                    playConstant(pFrameNo + 1);
//                }
//
//            }
//        }, pFrameNo == mLastFrameNo && mBreakDelay > 0 ? mBreakDelay
//                : mDuration);


    }

    public void stopAnimation(){
        if (run!=null) {
            mImageView.setBackgroundResource(mFrameRess[0]);
            mImageView.removeCallbacks(run);
        }
    }


}
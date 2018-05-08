package com.sht.smartlock.phone.common;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;

import com.sht.smartlock.R;

/**
 * com.yuntongxun.ecdemo.common in ECDemo_Android
 * Created by Jorstin on 2015/6/23.
 */
public class AnimatorUtils {

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void updateViewAnimation(View arcView , long duration , float translationX , final OnAnimationListener listener) {
        if(arcView == null || SDKVersionUtils.isSmallerorEqual(Build.VERSION_CODES.ICE_CREAM_SANDWICH)) {
            return ;
        }
        Animator animator = (Animator) arcView.getTag(R.anim.property_anim);
        if (animator != null) {
            animator.cancel();
        }
        arcView.animate().cancel();
        if (listener == null) {
            arcView.animate().setDuration(duration).translationX(translationX).translationY(0.0F);
            return;
        }
        arcView.animate().setDuration(duration).translationX(translationX).translationY(0.0F).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                listener.onAnimationEnd();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                listener.onAnimationCancel();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void startViewAnimation(View arcView , float translationX) {
        if(arcView == null || SDKVersionUtils.isSmallerorEqual(Build.VERSION_CODES.ICE_CREAM_SANDWICH)) {
            return ;
        }
        Animator animator = (Animator) arcView.getTag(R.anim.property_anim);
        if(animator != null) {
            animator.cancel();
        }
        arcView.animate().cancel();
        arcView.setTranslationX(translationX);
        arcView.setTranslationY(0.0F);
    }


    public interface OnAnimationListener {

        void onAnimationCancel();
        void onAnimationEnd();
    }
}

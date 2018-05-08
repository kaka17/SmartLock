package com.sht.smartlock.ui.activity.myview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sht.smartlock.R;


public class HomeButtonView extends FrameLayout {
	private ScaleAnimation animation_down;
	private ScaleAnimation animation_up;
	private boolean attri_doAnim;// 是否执行动画，在xml属性中可配置，默认为ture；
	private TextView textview;
	private HomeBtnOnClickListener homeBtnOnClickListener;

	public interface HomeBtnOnClickListener {
		void onClickDown(HomeButtonView homebtn);

		void onClickUp(HomeButtonView homebtn);
	}

	public void setHomeBtbOnClickListener(
			HomeBtnOnClickListener homeBtnOnClickListener) {
		this.homeBtnOnClickListener = homeBtnOnClickListener;
	}

	public HomeButtonView(Context paramContext) {
		this(paramContext, null);
	}

	public HomeButtonView(Context paramContext, AttributeSet paramAttributeSet) {
		this(paramContext, paramAttributeSet, 0);
	}

	public HomeButtonView(Context paramContext, AttributeSet paramAttributeSet,
			int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		TypedArray localTypedArray = paramContext.obtainStyledAttributes(
				paramAttributeSet, R.styleable.home_button);
		if (localTypedArray != null) {
			// 得到xml中设置的属性值
			this.attri_doAnim = localTypedArray.getBoolean(
					R.styleable.home_button_doAnim, true);
			localTypedArray.recycle();
		}

		loadAnim();
	}

	// 设置动画
	private void loadAnim() {
		ScaleAnimation localScaleAnimation1 = new ScaleAnimation(1.0F, 0.95F,
				1.0F, 0.95F, 1, 0.5F, 1, 0.5F);
		localScaleAnimation1.setFillAfter(true);
		localScaleAnimation1.setDuration(200L);
		this.animation_down = localScaleAnimation1;
		ScaleAnimation localScaleAnimation2 = new ScaleAnimation(0.95F, 1.0F,
				0.95F, 1.0F, 1, 0.5F, 1, 0.5F);
		localScaleAnimation2.setFillAfter(true);
		localScaleAnimation2.setDuration(200L);
		this.animation_up = localScaleAnimation2;
		this.animation_up.setAnimationListener(new AnimaListen(this));
	}

	private void start_AnimDown() {
		invalidate();
		startAnimation(this.animation_down);
	}

	private void start_AnimUp() {
		invalidate();
		startAnimation(this.animation_up);
	}

	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
		switch (paramMotionEvent.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.d("cc", "HomeBtn==>ACTION_DOWN");
			if (attri_doAnim) {
				if (homeBtnOnClickListener != null) {
					homeBtnOnClickListener.onClickDown(this);
				}
				start_AnimDown();
				return true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d("cc", "HomeBtn==>ACTION_MOVE");
			break;
		case MotionEvent.ACTION_UP:
			if (homeBtnOnClickListener != null) {
				homeBtnOnClickListener.onClickUp(this);
			}
			Log.d("cc", "HomeBtn==>ACTION_UP");
			if (attri_doAnim) {
				start_AnimUp();
				return true;
			}
			break;
		default:
			break;
		}
		return true;
	}

	class AnimaListen implements Animation.AnimationListener {

		AnimaListen(HomeButtonView paramHomeButtonView) {

		}

		public void onAnimationEnd(Animation arg0) {

		}

		public void onAnimationRepeat(Animation paramAnimation) {
		}

		public void onAnimationStart(Animation paramAnimation) {
		}

	}

}
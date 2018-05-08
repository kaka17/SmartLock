/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.cloopen.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.sht.smartlock.phone.ui.videomeeting;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.phone.common.CCPAppManager;
import com.yuntongxun.ecsdk.voip.video.ECCaptureView;

import java.util.HashMap;

/**
 * 视频会议主界面显示控件
 * @author Jorstin Chan
 * @date 2013-11-11
 * @version 3.5
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("UseSparseArrays")
public class CCPMulitVideoUI extends LinearLayout {

	public static final int LAYOUT_KEY_MAIN_SURFACEVIEW = 0X1;
	public static final int LAYOUT_KEY_SUB_SURFACEVIEW = 0X2;
	public static final int LAYOUT_KEY_SUB_VIEW_1 = 0X3;
	public static final int LAYOUT_KEY_SUB_VIEW_2 = 0X4;
	public static final int LAYOUT_KEY_SUB_VIEW_3 = 0X5;
	public static final int LAYOUT_KEY_SUB_VIEW_4 = 0X6;

	public HashMap<Integer, SubVideoSurfaceView> mSubViews = new HashMap<Integer, SubVideoSurfaceView>();

	private Context mContext;

	private ECCaptureView mCaptureView;
	private SubVideoSurfaceView mSubFrameLayout;


	private OnVideoUIItemClickListener mVideoUIItemClickListener;

	private int mVideoUIPadding = 0;
	private int mVideoUIMainKey = -1;

	public CCPMulitVideoUI(Context context) {
		super(context);
		initVideoUILayout(context);
	}

	public CCPMulitVideoUI(Context context, AttributeSet attrs) {
		super(context, attrs);
		initVideoUILayout(context);
	}

	public CCPMulitVideoUI(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		initVideoUILayout(context);

	}

	private void initVideoUILayout(Context context) {
		mContext = context;
		int space = Math.round(6 * getResources().getDisplayMetrics().densityDpi / 160.0F);

		mVideoUIPadding = Math.round(2 * getResources().getDisplayMetrics().densityDpi / 160.0F);

		LinearLayout topLayout = new LinearLayout(context);
		LayoutParams topLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT
				, LayoutParams.MATCH_PARENT);
		topLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
		topLayoutParams.weight = 1;
		topLayoutParams.bottomMargin = space;
		topLayout.setLayoutParams(topLayoutParams);

		// add main surfaceView;
		SubVideoSurfaceView mainSurfaceView = getSurfaceViewLayout(space , LAYOUT_KEY_MAIN_SURFACEVIEW);
		topLayout.addView(mainSurfaceView);

		LinearLayout rTopLinearLayout = new LinearLayout(context);
		LayoutParams rfLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT
				, LayoutParams.MATCH_PARENT);
		rfLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
		rfLayoutParams.weight = 2;
		rfLayoutParams.leftMargin = space;
		rTopLinearLayout.setLayoutParams(rfLayoutParams);
		rTopLinearLayout.setOrientation(LinearLayout.VERTICAL);


		// --------------------------right 2 start -------------------------- 1
		// add right top two view (surfaceView and imageView)
		SubVideoSurfaceView subSurfaceView = getSurfaceViewLayout(space , LAYOUT_KEY_SUB_SURFACEVIEW);
		mSubFrameLayout = subSurfaceView;
		if(mCaptureView == null) {
			mCaptureView = new ECCaptureView(getContext());
			mSubFrameLayout.addView(mCaptureView,0);
		}
		subSurfaceView.setIndex(LAYOUT_KEY_SUB_SURFACEVIEW);
		mSubViews.put(LAYOUT_KEY_SUB_SURFACEVIEW, subSurfaceView);
		rTopLinearLayout.addView(subSurfaceView);
		// ---------------------------------------------------- 2
		FrameLayout rImageView = getSubViewLayout(space , LAYOUT_KEY_SUB_VIEW_1);
		rTopLinearLayout.addView(rImageView);
		// -----------------------------right 2 end --------------------

		topLayout.addView(rTopLinearLayout);



		// -------------------------------------bottom -------------------
		// add bottom three ImageView
		LinearLayout bottomLayout = new LinearLayout(context);
		LayoutParams bottomLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT
				, LayoutParams.MATCH_PARENT);
		bottomLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
		bottomLayoutParams.weight = 2;
		bottomLayoutParams.topMargin = space;
		bottomLayoutParams.bottomMargin = space;
		bottomLayout.setLayoutParams(bottomLayoutParams);
		// ---------------------------------------------------- 1
		FrameLayout bImageView2 = getSubViewLayout(space , LAYOUT_KEY_SUB_VIEW_2);
		bottomLayout.addView(bImageView2);
		// ---------------------------------------------------- 1
		FrameLayout bImageView3 = getSubViewLayout(space , LAYOUT_KEY_SUB_VIEW_3);
		bottomLayout.addView(bImageView3);
		// ---------------------------------------------------- 1
		FrameLayout bImageView4 = getSubViewLayout(space , LAYOUT_KEY_SUB_VIEW_4);
		bottomLayout.addView(bImageView4);


		// add all view
		addView(topLayout);
		addView(bottomLayout);
	}

	private SubVideoSurfaceView getSubViewLayout(int space , final int layoutKye) {
		SubVideoSurfaceView fLayout = new SubVideoSurfaceView(mContext);
		fLayout.setIndex(layoutKye);
		LayoutParams rfLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT
				, LayoutParams.MATCH_PARENT);
		rfLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
		rfLayoutParams.weight = 1;

		SurfaceView surfaceView = (SurfaceView) fLayout.getVideoSurfaceView();
		if(surfaceView != null) {
			surfaceView.setVisibility(View.GONE);
			surfaceView.setZOrderOnTop(false);
		}
		TextView textView = (TextView) fLayout.getDisplayTextView();
		if(layoutKye == LAYOUT_KEY_SUB_VIEW_1) {
			rfLayoutParams.topMargin = space;
		} else if (layoutKye == LAYOUT_KEY_SUB_VIEW_2) {
			rfLayoutParams.rightMargin = space;
		} else if (layoutKye == LAYOUT_KEY_SUB_VIEW_3) {
			rfLayoutParams.rightMargin = space;
			rfLayoutParams.leftMargin = space;
		} else if (layoutKye == LAYOUT_KEY_SUB_VIEW_4) {
			rfLayoutParams.leftMargin = space;
		}

		mSubViews.put(layoutKye, fLayout);

		fLayout.setLayoutParams(rfLayoutParams);
		fLayout.setBackgroundResource(R.color.black);
		fLayout.getBackground().setAlpha(55);

		textView.getBackground().setAlpha(55);

		return fLayout;

	}

	private SubVideoSurfaceView getSurfaceViewLayout(int space , final int layoutKye) {
		SubVideoSurfaceView lframeLayout = new SubVideoSurfaceView(mContext);
		lframeLayout.setIndex(layoutKye);
		LayoutParams fLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT
				, LayoutParams.MATCH_PARENT);
		fLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
		fLayoutParams.weight = 1;

		SurfaceView surfaceView = (SurfaceView) lframeLayout.getVideoSurfaceView();
		lframeLayout.findViewById(R.id.status).setVisibility(View.GONE);
		if(surfaceView != null) {
			surfaceView.setVisibility(View.VISIBLE);
		}
		TextView textView = (TextView) lframeLayout.getDisplayTextView();

		if(surfaceView != null) surfaceView.setZOrderOnTop(false);

		if(layoutKye == LAYOUT_KEY_MAIN_SURFACEVIEW) {
			fLayoutParams.rightMargin = space;
		} else if (layoutKye == LAYOUT_KEY_SUB_SURFACEVIEW) {
			fLayoutParams.bottomMargin = space;
		}

		// The video item stored in the cache
		mSubViews.put(layoutKye, lframeLayout);
		lframeLayout.setLayoutParams(fLayoutParams);
		lframeLayout.setBackgroundResource(R.color.black);
		lframeLayout.getBackground().setAlpha(55);

		textView.getBackground().setAlpha(55);

		return lframeLayout;

	}

	/**
	 *
	 * @param index
	 * @param member
	 */
	public void setVideoMember(int index , MultiVideoMember member) {
		if(index >= mSubViews.size()) {
			return ;
		}

		SubVideoSurfaceView subVideoSurfaceView = mSubViews.get(index);
		if(subVideoSurfaceView == null) {
			return ;
		}

//		subVideoSurfaceView.setOnVideoUIItemClickListener(mVideoUIItemClickListener);
//		subVideoSurfaceView.setVideoUIMember(member);
	}

	/**
	 *
	 * @return
	 */
	public SurfaceView getMainSurfaceView() {
		return getSurfaceView(LAYOUT_KEY_MAIN_SURFACEVIEW , false);
	}

	public void setVideoUIMainScreen(int index) {

		if(index <= 1) {
			return;
		}

		if(mVideoUIMainKey != -1) {
			// src.
			SubVideoSurfaceView subVideoSurfaceViewSrc = mSubViews.get(mVideoUIMainKey);
			if(subVideoSurfaceViewSrc != null) {
				subVideoSurfaceViewSrc.setBackgroundColor(mContext.getResources().getColor(R.color.black));
				subVideoSurfaceViewSrc.setPadding(0, 0, 0, 0);
				subVideoSurfaceViewSrc.getBackground().setAlpha(55);
			}
		}

		// dest
		SubVideoSurfaceView subVideoSurfaceViewDest = mSubViews.get(index);
		if(subVideoSurfaceViewDest != null ) {
			mVideoUIMainKey = index;
			subVideoSurfaceViewDest.setBackgroundColor(Color.WHITE);
			subVideoSurfaceViewDest.getBackground().setAlpha(255);
			subVideoSurfaceViewDest.setPadding(mVideoUIPadding, mVideoUIPadding, mVideoUIPadding, mVideoUIPadding);
		}
	}


	public synchronized SurfaceView getSurfaceView(int index , boolean remove) {
		SubVideoSurfaceView subVideoSurfaceView = mSubViews.remove(index);
		if(subVideoSurfaceView == null) {
			return null;
		}
		
		if(remove) {
			subVideoSurfaceView.removeSurfaceView();
		}
		mSubViews.put(index, subVideoSurfaceView);
		return subVideoSurfaceView.getVideoSurfaceView();
	}
	
	
	public void onResume(int mCameraCapbilityIndex) {
		if(mSubFrameLayout != null) {
			if(mCaptureView != null) {
				mCaptureView.setCaptureParams(android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT, mCameraCapbilityIndex);
				mCaptureView.setZOrderOnTop(true);
				mCaptureView.onResume();
			}
			mSubFrameLayout.setVideoUIText(CCPAppManager.getUserId());
		}
	}

	public SurfaceView getCaptureView() {
		return mCaptureView;
	}

	public void switchCamera() {
		if(mCaptureView != null) {
			mCaptureView.switchCamera();
		}
	}


	/**
	 *
	 */
	public void release() {

	}

	public void setOnVideoUIItemClickListener(OnVideoUIItemClickListener l) {
		mVideoUIItemClickListener = l;
	}


	/**
	 *
	 * <p>Title: CCPVideoConUI.java</p>
	 * <p>Description:The interface is used to manage the members of the conference
	 *  If you want to know the results of the implementation of click each member
	 *  You must set the monitor through {@link CCPMulitVideoUI#setOnVideoUIItemClickListener(OnVideoUIItemClickListener)}</p>
	 * @version 3.5
	 *
	 * @see CCPMulitVideoUI#setOnVideoUIItemClickListener(OnVideoUIItemClickListener)
	 */
	public interface OnVideoUIItemClickListener {

		/**
		 * Callback method to be invoked when an item in this VideoUI has
		 * been clicked.
		 * <p>
		 * You must set the monitor through
		 * {@link CCPMulitVideoUI#setOnVideoUIItemClickListener(OnVideoUIItemClickListener)}</p>
		 *
		 *
		 */
		void onVideoUIItemClick(int key);
	}
}

package com.sht.smartlock.ui.activity.layoutmanager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.support.v7.widget.RecyclerView.Recycler;
import android.support.v7.widget.RecyclerView.State;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;

public  class MyLayoutManager extends LinearLayoutManager {

	public MyLayoutManager(Context context) {
		super(context);
	}

	private int[] mMeasuredDimension = new int[2];

	@Override
	public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state,
						  int widthSpec, int heightSpec) {
		View view = recycler.getViewForPosition(0);
		if(view != null){
			measureChild(view, widthSpec, heightSpec);
			int measuredWidth = MeasureSpec.getSize(widthSpec);
			int measuredHeight = view.getMeasuredHeight();
			setMeasuredDimension(measuredWidth, measuredHeight);
		}
//		final int widthMode = View.MeasureSpec.getMode(widthSpec);
//		final int heightMode = View.MeasureSpec.getMode(heightSpec);
//		final int widthSize = View.MeasureSpec.getSize(widthSpec);
//		final int heightSize = View.MeasureSpec.getSize(heightSpec);
//
//		measureScrapChild(recycler, 0,
//				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//				mMeasuredDimension);
//
//		int width = mMeasuredDimension[0];
//		int height = mMeasuredDimension[1];
//
//		switch (widthMode) {
//			case View.MeasureSpec.EXACTLY:
//			case View.MeasureSpec.AT_MOST:
//				width = widthSize;
//				break;
//			case View.MeasureSpec.UNSPECIFIED:
//		}
//
//		switch (heightMode) {
//			case View.MeasureSpec.EXACTLY:
//			case View.MeasureSpec.AT_MOST:
//				height = heightSize;
//				break;
//			case View.MeasureSpec.UNSPECIFIED:
//		}
//
//		setMeasuredDimension(width, height);
	}

//	private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec,
//								   int heightSpec, int[] measuredDimension) {
//		View view = recycler.getViewForPosition(position);
//		if (view != null) {
//			RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
//			int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec,
//					getPaddingLeft() + getPaddingRight(), p.width);
//			int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec,
//					getPaddingTop() + getPaddingBottom(), p.height);
//			view.measure(childWidthSpec, childHeightSpec);
//			measuredDimension[0] = view.getMeasuredWidth();
//			measuredDimension[1] = view.getMeasuredHeight();
//			recycler.recycleView(view);
//		}
//	}
}
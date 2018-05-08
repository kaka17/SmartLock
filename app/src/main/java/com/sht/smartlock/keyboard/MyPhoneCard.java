package com.sht.smartlock.keyboard;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class MyPhoneCard extends ViewGroup{

	 private static final int COLUMNS = 3;  
	    private static final int ROWS = 4;  
	    private static final int NUM_BUTTON = COLUMNS*ROWS;  
	      
	    private View[] mButtons = new View[NUM_BUTTON];  
	      
	    private int mButtonWidth;  
	    private int mButtonHeight;  
	    private int mPaddingLeft;  
	    private int mPaddingRight;  
	    private int mPaddingTop;  
	    private int mPaddingBottom;  
	    private int mWidthInc;  
	    private int mHeightInc;  
	    private int mWidth;  
	    private int mHeight;  
	  
	    public MyPhoneCard(Context context) {  
	        super(context);  
	    }  
	      
	    public MyPhoneCard(Context context, AttributeSet attrs){  
	        super(context,attrs);  
	    }  
	      
	    public MyPhoneCard(Context context, AttributeSet attrs, int defStyle){  
	        super(context,attrs,defStyle);  
	    }  
	      
	    /** 
	     * 当从xml将所有的控件都调入内存后，触发的动作 
	     * 在这里获取控件的大小，并计算整个ViewGroup需要的总的宽和高 
	     */  
	    @Override  
	    protected void onFinishInflate(){  
	        super.onFinishInflate();  
	        final View[] btns = mButtons;  
	          
	        for(int i=0; i<NUM_BUTTON; i++){  
	            btns[i] = this.getChildAt(i);  
	            btns[i].measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);  
	        }  
	          
	        //缓存大小   
	        final View child = btns[0];  
	        mButtonWidth = child.getMeasuredWidth();  
	        mButtonHeight = child.getMeasuredHeight();  
	        mPaddingLeft = this.getPaddingLeft();  
	        mPaddingRight = this.getPaddingRight();  
	        mPaddingTop = this.getPaddingTop();  
	        mPaddingBottom = this.getPaddingBottom();  
	        mWidthInc = mButtonWidth + mPaddingLeft + mPaddingRight;  
	        mHeightInc = mButtonHeight + mPaddingTop + mPaddingBottom;  
	          
	        mWidth = mWidthInc*COLUMNS;  
	        mHeight = mHeightInc*ROWS;  
	          
	        Log.v("Finish Inflate:", "btnWidth="+mButtonWidth+",btnHeight="+mButtonHeight+",padding:"+mPaddingLeft+","+mPaddingTop+","+mPaddingRight+","+mPaddingBottom);  
	  
	          
	          
	    }  
	      
	    /** 
	     * 这个方法在onFinishInflate之后，onLayout之前调用。这个方面调用两次 
	     */  
	    @Override  
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){  
	        super.onMeasure(widthMeasureSpec, heightMeasureSpec);  
	        Log.v("ViewGroup SIZE：width=", mWidth+"");  
	        Log.v("ViewGroup SIZE: height=",mHeight+"");  
	        final int width = resolveSize(mWidth, widthMeasureSpec);//传入我们希望得到的宽度，得到测量后的宽度   
	        final int height = resolveSize(mHeight,heightMeasureSpec);//传入我们希望得到的高度，得到测量后的高度   
//	        Log.v("ViewGroup Measured SIZE: width=", width+"");
//	        Log.v("ViewGroup Measured SIZE: height=", height+"");
	        //重新计算后的结果，需要设置。下面这个方法必须调用   
	        setMeasuredDimension(width, height);  
	    }  
	  
	    /** 
	     * 这个方法在onMeasure之后执行，这个自定义控件中含有12个子控件（每个小键），所以，重写这个方法， 
	     * 调用每个键的layout，将他们一个一个布局好 
	     * 就是4*3的放置，很简单，一个嵌套循环搞定 
	     */  
	    @Override  
	    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {  
	        final View[] buttons = mButtons;  
	        int i = 0;  
	        Log.v("BOTTOM:", bottom+"");  
	        Log.v("TOP", top+"");  
	          
	        int y = (bottom - top) - mHeight + mPaddingTop;//这里其实bottom-top=mHeight,所以y=mPaddingTop   
	        Log.v("Y=", y+"");  
	        for(int row=0; row<ROWS; row++){  
	            int x = mPaddingLeft;  
	            for(int col = 0; col < COLUMNS; col++){  
	                buttons[i].layout(x, y, x+mButtonWidth, y+mButtonHeight);  
	                x = x + mWidthInc;  
	                i++;  
	            }  
	            y = y + mHeightInc;  
	        }  
	    }  
	
}

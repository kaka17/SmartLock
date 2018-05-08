package com.sht.smartlock.ui.activity.myview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2015/9/11.
 */
public class MyViewPager extends ViewPager {
    private boolean isScrollable = true;

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean onTouchEvent(MotionEvent arg0) {

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return false;
    }
//     在listView中嵌套listView不需要这个
     @Override
     public boolean dispatchTouchEvent(MotionEvent ev) {
     // 只需要这句话，让父类不拦截触摸事件就可以了。
         getParent().requestDisallowInterceptTouchEvent(false);
         return super.dispatchTouchEvent(ev);
     }

}
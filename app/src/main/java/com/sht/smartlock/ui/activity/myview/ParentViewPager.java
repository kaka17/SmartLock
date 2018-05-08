package com.sht.smartlock.ui.activity.myview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.WindowManager;

/**
 * Created by Administrator on 2015/10/17.
 */
public class ParentViewPager extends ViewPager {
    private int childVPHeight=0;
     public ParentViewPager(Context context) {
        super(context);
         // TODO Auto-generated constructor stub
         init(context);
         }
    public ParentViewPager(Context context, AttributeSet attrs) {
         super(context, attrs);
         // TODO Auto-generated constructor stub
         init(context);
        }
    private void init(Context context) {
         // TODO Auto-generated method stub
         // 获取屏幕宽高
         WindowManager windowManager = (WindowManager)
                context.getSystemService(context.WINDOW_SERVICE);
         int disWidth = windowManager.getDefaultDisplay().getWidth();
        //根据屏幕的密度来过去dp值相应的px值
       childVPHeight=(int) (context.getResources().getDisplayMetrics().density
                * disWidth + 0.5f);
        }
    @Override
   public boolean onInterceptTouchEvent(MotionEvent arg0) {
       // TODO Auto-generated method stub
        //触摸在子ViewPager所在的页面和子ViewPager控件高度之内时
       //返回false，此时将会将触摸的动作传给子ViewPager
        if(getCurrentItem()==1 && arg0.getY()==1){
            return true;
        }
   return super.onInterceptTouchEvent(arg0);
   }



}

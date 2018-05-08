package com.sht.smartlock.ui.activity.myview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.handmark.pulltorefresh.library.PullToRefreshBase;

/**
 * Created by aaa on 15-3-14.
 */
public class MyRefresh extends PullToRefreshBase<RecyclerView> {
    public MyRefresh(Context context) {
        super(context);
    }

    public MyRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRefresh(Context context, Mode mode) {
        super(context, mode);
    }

    public MyRefresh(Context context, Mode mode, AnimationStyle animStyle) {
        super(context, mode, animStyle);
    }

    @Override
    public Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected RecyclerView createRefreshableView(Context context, AttributeSet attrs) {
        RecyclerView view = new RecyclerView(context, attrs);
        return view;
    }

    @Override
    protected boolean isReadyForPullEnd() {
        try {
            RecyclerView recycle = getRefreshableView();
            View view = recycle.getChildAt(recycle.getChildCount() - 1);
            int count = recycle.getAdapter().getItemCount();
            if (recycle.getChildPosition(view) == count - 1) {
                return view.getBottom() == recycle.getHeight();
            }
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    protected boolean isReadyForPullStart() {
        try {
            RecyclerView recycle = getRefreshableView();
            View view = recycle.getChildAt(0);
            //显示的第一个View中显示的第一条数据
            if (recycle.getChildPosition(view) == 0) {
                //第一条数据是否完整显示
                return view.getTop() == 0;
            }
        } catch (Exception e) {

        }
        return false;
    }
}

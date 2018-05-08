package com.sht.smartlock.ui.activity.myview;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2016/11/3.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        //设置左右的间隔如果想设置的话自行设置，我这用不到就注释掉了
/*          outRect.left = space;
            outRect.right = space;*/

        //       System.out.println("position"+parent.getChildPosition(view));
        //       System.out.println("count"+parent.getChildCount());

        //         if(parent.getChildPosition(view) != parent.getChildCount() - 1)
        //         outRect.bottom = space;

        //改成使用上面的间隔来设置
        if(parent.getChildPosition(view) != 0)
            outRect.top = space;
    }
}

package com.sht.smartlock.ui.activity.badgeview;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;


/**
 * Created by Administrator on 2017/1/17 0017.
 */

public class BadgeUtils {

    public static BadgeView newLeftBadgeView (Context context,View view,int badgeCount) {
        BadgeView badgeView = BadgeFactory.create(context)
                .setTextColor(Color.WHITE)
                .setWidthAndHeight(18,18)
                .setBadgeBackground(Color.parseColor("#FF6600"))
                .setTextSize(10)
                .setBadgeGravity(Gravity.LEFT| Gravity.TOP)
                .setBadgeCount(badgeCount)
                .setShape(BadgeView.SHAPE_CIRCLE)
//                .setMargin(0,0,5,0)

                .bind(view);
        return badgeView;
    }
    public static BadgeView newRightBadgeView (Context context,View view,int badgeCount) {
        BadgeView badgeView = BadgeFactory.create(context)
                .setTextColor(Color.WHITE)
                .setWidthAndHeight(18,18)
                .setBadgeBackground(Color.parseColor("#FF0000"))
                .setTextSize(12)
                .setBadgeGravity(Gravity.RIGHT| Gravity.TOP)
                .setBadgeCount(badgeCount)
                .setShape(BadgeView.SHAPE_CIRCLE)
//                .setMargin(0,0,5,0)

                .bind(view);
        return badgeView;
    }

}

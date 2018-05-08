package com.sht.smartlock.util;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by Jerry on 2015/6/27 0027.
 */
public class GuideUtil {
    public static void setGuide(Activity activity, int res) {
        final WindowManager windowManager = activity.getWindowManager();
        final ImageView imageView = new ImageView(activity);
        imageView.setLayoutParams(new WindowManager.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(res);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        // 设置显示的类型，TYPE_PHONE指的是来电话的时候会被覆盖，其他时候会在最前端，显示位置在stateBar下面，其他更多的值请查阅文档
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        // 设置显示格式
        params.format = PixelFormat.RGBA_8888;
        // 设置对齐方式
        params.gravity = Gravity.LEFT | Gravity.TOP;
        // 设置宽高
        params.width = (int) DeviceUtil.getScreenWidth();
        params.height = (int) DeviceUtil.getScreenHeight();

        // 添加到当前的窗口上
        windowManager.addView(imageView, params);

        // 点击图层之后，将图层移除
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                windowManager.removeView(imageView);
            }
        });
    }
}

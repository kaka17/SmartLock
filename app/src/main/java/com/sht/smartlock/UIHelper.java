package com.sht.smartlock;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;

import com.sht.smartlock.widget.MaterialRippleLayout;


public class UIHelper {
    public final static float ITEM_ALPHA = 0.7f;

    public static int[] color = new int[] { 0xffF6AE33, 0xff27B086, 0xff157BC0,
            0xff7DB53A, 0xffA16BAC, 0xffB5462B, 0xffE0DB6A, 0xff646BAC,
            0xffC8462B, 0xff1EDB6A, 0xffF6AE33, 0xff27B086, 0xff157BC0,
            0xff7DB53A, 0xffA16BAC, 0xffB5462B, 0xffE0DB6A, 0xff646BAC,
            0xffC8462B, 0xff1EDB6A, 0xffF6AE33, 0xff27B086, 0xff157BC0,
            0xff7DB53A, 0xffA16BAC, 0xffB5462B, 0xffE0DB6A, 0xff646BAC,
            0xffC8462B, 0xff1EDB6A, 0xffF6AE33, 0xff27B086, 0xff157BC0,
            0xff7DB53A, 0xffA16BAC, 0xffB5462B, 0xffE0DB6A, 0xff646BAC,
            0xffC8462B, 0xff1EDB6A, 0xffF6AE33, 0xff27B086, 0xff157BC0,
            0xff7DB53A, 0xffA16BAC, 0xffB5462B, 0xffE0DB6A, 0xff646BAC,
            0xffC8462B, 0xff1EDB6A, 0xffF6AE33, 0xff27B086, 0xff157BC0,
            0xff7DB53A, 0xffA16BAC, 0xffB5462B, 0xffE0DB6A, 0xff646BAC,
            0xffC8462B, 0xff1EDB6A, 0xffF6AE33, 0xff27B086, 0xff157BC0,
            0xff7DB53A, 0xffA16BAC, 0xffB5462B, 0xffE0DB6A, 0xff646BAC,
            0xffC8462B, 0xff1EDB6A };

    public static void injectRipple(Context context, View... view) {
        injectRipple(context, context.getResources().getColor(R.color.toolbar_background), view);
    }

    public static void injectRipple(Context context, int backgroundColor, View... view) {
        for (int i = 0; i < view.length; i++) {
            MaterialRippleLayout.on(view[i])
                    .rippleBackground(backgroundColor)
                    .rippleColor(context.getResources().getColor(R.color.ripple_color))
                    .rippleAlpha(0.2f)
                    .rippleHover(true)
                    .create();
        }
    }

    public static ArrayAdapter getSpinnerAdapter(Context context, String[] arrays) {
        return new ArrayAdapter(context, R.layout.item_spinner, arrays);
    }

    public static int getColor(int position) {
        if (position >= color.length) {
            position = 0;
        }
        return color[position];
    }
}

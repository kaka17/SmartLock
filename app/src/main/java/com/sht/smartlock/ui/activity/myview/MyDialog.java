package com.sht.smartlock.ui.activity.myview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

/**
 * Created by Administrator on 2016/11/25.
 */
public class MyDialog extends Dialog {
    public MyDialog(Context context) {
        super(context);
        setOwnerActivity((Activity) context);
    }

    public MyDialog(Context context, int themeResId) {
        super(context, themeResId);
        setOwnerActivity((Activity) context);
    }

    protected MyDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        setOwnerActivity((Activity) context);
    }
}

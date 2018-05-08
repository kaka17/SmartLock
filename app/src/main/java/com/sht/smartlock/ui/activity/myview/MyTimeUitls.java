package com.sht.smartlock.ui.activity.myview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.sht.smartlock.R;

/**
 * Created by Administrator on 2017/4/7.
 */
public class MyTimeUitls extends PopupWindow {
    private Context context;
    View mView;
    public MyTimeUitls(Context context){
        this.context=context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.common_datetime, null);
    }
}

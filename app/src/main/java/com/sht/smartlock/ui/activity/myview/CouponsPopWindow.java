package com.sht.smartlock.ui.activity.myview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.sht.smartlock.AppContext;
import com.sht.smartlock.R;
import com.sht.smartlock.util.LogUtil;


/**
 * Created by Administrator on 2015/10/10.
 */
public class CouponsPopWindow extends PopupWindow  {
    View mView;
    Button btnNewCoupons,btnOld,btnUsedOld,btnAll;
    View.OnClickListener mClickListener;
    public CouponsPopWindow(Context context, View.OnClickListener onClickListener) {
        super(context);
        mClickListener=onClickListener;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.coupons_pop, null);
        btnNewCoupons= (Button) mView.findViewById(R.id.btnNewCoupons);
        btnOld= (Button) mView.findViewById(R.id.btnOld);
        btnUsedOld= (Button) mView.findViewById(R.id.btnUsedOld);
        btnAll= (Button) mView.findViewById(R.id.btnAll);
        btnNewCoupons.setOnClickListener(onClickListener);
        btnOld.setOnClickListener(onClickListener);
        btnUsedOld.setOnClickListener(onClickListener);
        btnAll.setOnClickListener(onClickListener);

        setContentView(mView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
//        ColorDrawable dw = new ColorDrawable(0x90000000);
//        setBackgroundDrawable(dw);
        setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")
        ));
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int left = mView.findViewById(R.id.llPopMenu).getLeft();
                int bottom=mView.findViewById(R.id.llPopMenu).getBottom();
                int y = (int) event.getY();
                int x= (int) event.getX();
                LogUtil.log("paramss left:"+left+" x:"+x+" bottom:"+bottom +" y"+y);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y > bottom) {
                        dismiss();
                    }
                    if(x>left){
                        dismiss();
                    }
                }
                return true;
            }
        });
    }



}

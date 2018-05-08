package com.sht.smartlock.ui.activity.myview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.ui.entity.MyLockEntity;
import com.sht.smartlock.util.LogUtil;

import java.util.List;

/**
 * Created by Administrator on 2015/11/11.
 */
public class BlackPopWindow extends PopupWindow {


    private final TextView tv_Add_BlackList;
    private final TextView tv_SubBlackList;
    View mView;

    private Context context;
   private View.OnClickListener onClickListener;

    public BlackPopWindow(Context context,boolean isBlack,View.OnClickListener onClickListener ) {
        super(context);
        this.context=context;
        this.onClickListener=onClickListener;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.black_pop, null);

        tv_Add_BlackList = (TextView) mView.findViewById(R.id.tv_Add_BlackList);
        tv_SubBlackList = (TextView) mView.findViewById(R.id.tv_SubBlackList);

        if (isBlack){
            tv_SubBlackList.setVisibility(View.VISIBLE);
            tv_Add_BlackList.setVisibility(View.GONE);
        }else {
            tv_SubBlackList.setVisibility(View.GONE);
            tv_Add_BlackList.setVisibility(View.VISIBLE);
        }
        setContentView(mView);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")
        ));
//        setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")
//        ));
        tv_Add_BlackList.setOnClickListener(onClickListener);
        tv_SubBlackList.setOnClickListener(onClickListener);


        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int left = mView.findViewById(R.id.lin_Pop).getLeft();
                int bottom=mView.findViewById(R.id.lin_Pop).getBottom();
                int y = (int) event.getY();
                int x= (int) event.getX();
                LogUtil.log("paramss left:" + left + " x:" + x + " bottom:" + bottom + " y" + y);
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

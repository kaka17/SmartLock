package com.sht.smartlock.ui.activity.myview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.sht.smartlock.R;
import com.sht.smartlock.ui.entity.MyLockEntity;
import com.sht.smartlock.util.LogUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/11/24.
 */
public class SharePopwindow extends PopupWindow {
    private final ImageView ivWinXin,ivWinXinFriend,ivQQ,ivQQSpace;
    View mView;
    private Context context;
    private View.OnClickListener onClickListener;

    public SharePopwindow(Context context, View.OnClickListener onClickListener) {
        super(context);

        this.context = context;
        this.onClickListener = onClickListener;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.myshare, null);
        ivWinXin = (ImageView) mView.findViewById(R.id.ivWinXin);
        ivWinXinFriend = (ImageView) mView.findViewById(R.id.ivWinXinFriend);
        ivQQ = (ImageView) mView.findViewById(R.id.ivQQ);
        ivQQSpace = (ImageView) mView.findViewById(R.id.ivQQSpace);
        setContentView(mView);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        ivWinXin.setOnClickListener(onClickListener);
        ivWinXinFriend.setOnClickListener(onClickListener);
        ivQQ.setOnClickListener(onClickListener);
        ivQQSpace.setOnClickListener(onClickListener);
        setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00FFFFFF")
        ));
//        setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")
//        ));
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int left = mView.findViewById(R.id.lin_Pop).getLeft();
                int bottom = mView.findViewById(R.id.lin_Pop).getBottom();
                int y = (int) event.getY();
                int x = (int) event.getX();
                LogUtil.log("paramss left:" + left + " x:" + x + " bottom:" + bottom + " y" + y);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y > bottom) {
                        dismiss();
                    }
                    if (x > left) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }
}

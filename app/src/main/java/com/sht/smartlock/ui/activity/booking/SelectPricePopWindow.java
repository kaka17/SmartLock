package com.sht.smartlock.ui.activity.booking;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.sht.smartlock.AppContext;
import com.sht.smartlock.R;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.widget.rangebar.RangeBar;

/**
 * Created by Administrator on 2015/9/16.
 */
public class SelectPricePopWindow extends PopupWindow  {
    private View mMenuView;
    private TextView tvEnsure;
    private RangeBar rangeBar;
    ImageView    Dot0, Dot150, Dot300, Dot500, DotUnlimit;
    TextView     Price0, Price150, Price300, Price500, PriceUnlimit;
    ImageView[] tvDot = new ImageView[]{Dot0, Dot150, Dot300, Dot500, DotUnlimit};
    TextView[] tvPrice = new TextView[]{Price0, Price150, Price300, Price500, PriceUnlimit};
    int[] dotId = new int[]{R.id.dot0, R.id.dot150, R.id.dot300, R.id.dot500, R.id.dotUnLimit};
    int[] priceId = new int[]{R.id.Price0, R.id.Price150, R.id.price300, R.id.price500, R.id.priceUnLimit};

    public SelectPricePopWindow( final Activity context, RangeBar.OnRangeBarChangeListener rangebarListener) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.main_tab_booking_price_pop, null);
        tvEnsure = (TextView) mMenuView.findViewById(R.id.tvEnsure);
        rangeBar = (RangeBar) mMenuView.findViewById(R.id.priceRange);
        InitTv(mMenuView);
        tvEnsure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppContext.setProperty(context.getString(R.string.price_range_start), String.valueOf(rangeBar.getLeftIndex()));
                AppContext.setProperty(context.getString(R.string.price_range_end), String.valueOf(rangeBar.getRightIndex()));
                dismiss();
            }
        });
        setContentView(mMenuView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        //setAnimationStyle(R.style.Animation_AppCompat_Dialog);
        ColorDrawable dw = new ColorDrawable(0x90000000);
        setBackgroundDrawable(dw);

        rangeBar.setThumbImageNormal(R.drawable.btn_slider_price);
        rangeBar.setThumbImagePressed(R.drawable.btn_slider_price);
        mMenuView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.llprice_pop).getTop();
                int y = (int) event.getY();
                LogUtil.log("aaaaa "+height+" "+y);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });


        rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int i, int i1) {

                ChangeTvPrice(i, i1);
                LogUtil.log("rangbar " + i + "    " + i1);

            }
        });
        if (AppContext.getProperty(context.getString(R.string.price_range_start)) != null) {
            int left = Integer.parseInt(AppContext.getProperty(context.getString(R.string.price_range_start)));
            int right = Integer.parseInt(AppContext.getProperty(context.getString(R.string.price_range_end)));
            rangeBar.setThumbIndices(left, right);
            ChangeTvPrice(left, right);
        }
    }
    private void InitTv(View v) {
        for (int i = 0; i < dotId.length; i++) {
         //   tvDot[i] = (TextView) v.findViewById(dotId[i]);
            tvDot[i]=(ImageView)v.findViewById(dotId[i]);
            tvPrice[i] = (TextView) v.findViewById(priceId[i]);
        }
    }

    private void ChangeTvPrice(int left, int right) {
        for (int i = 0; i < tvDot.length; i++) {
            if (i < left || i > right) {
                tvDot[i].setImageResource(R.drawable.dot_normal);
                tvPrice[i].setTextColor(Color.parseColor("#353535"));
            } else {
                tvDot[i].setImageResource(R.drawable.dot_selected);
                tvPrice[i].setTextColor(AppContext.context().getResources().getColor(R.color.LOCKPICE));
            }
        }
    }
}

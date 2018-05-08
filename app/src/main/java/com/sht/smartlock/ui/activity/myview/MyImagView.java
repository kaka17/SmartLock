package com.sht.smartlock.ui.activity.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Administrator on 2017/4/13.
 */
public class MyImagView extends ImageView {

    /**
     * 图片宽高比
     *
     */
    private float ratio = 2.63f;

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    public MyImagView(Context context) {
        super(context);
    }

    public MyImagView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // 宽度方向上的测量模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        // 高度方向上的测量模式
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        // paddingLeft 类似于 iOS中的 UIEdgeInsert
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();

        /**
         * 判断依据
         * 宽度为Exactly,也就是填充父窗体或者指定宽度
         * 且高度不为Exactly,代表设置的既不是fill_parent也不是具体的值，需要具体测量
         * 且图片宽度比已经赋值完毕，不再是0.0f
         * 表示宽度确定，需要确定高度
         * MeasureSpec类似于ios中的CGSize,比CGSize多了一个mode属性
         */
        if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY && ratio != 0.0f) {
            height = (int) (width / ratio + 0.5f); // 高度实际值(px)
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        } else if (widthMode != MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY && ratio != 0.0f) {
            // 判断依据与上面相反,表示高度确定,需要测量宽度
            width = (int) (height * ratio + 0.5f);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}

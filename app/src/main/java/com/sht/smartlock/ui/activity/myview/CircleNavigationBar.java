package com.sht.smartlock.ui.activity.myview;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.sht.smartlock.R;


public class CircleNavigationBar extends LinearLayout {

	private ViewPager pager = null;
	private Paint paint = null;
	private float offset;
	private int position;
	private boolean showAnimation; // 是否显示移动动画
	private int circleRadius; // 绘制圆半
	private int defaultColor; // 默认圆心颜色
	private int checkColor; // 选中圆心颜色

	public CircleNavigationBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWillNotDraw(false);
		paint = new Paint();
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleNavigationBar);
		showAnimation = typedArray.getBoolean(R.styleable.CircleNavigationBar_showAnimation, false);
		// 设置半径
		int radius = typedArray.getInt(R.styleable.CircleNavigationBar_circleRadius, -1);
		circleRadius = (-1 != radius) ? dip2px(context, radius) : dip2px(context, 8);

		// 设置选中圆心颜色
		int defaultColorRes = typedArray.getResourceId(R.styleable.CircleNavigationBar_defaultCircle, -1);
//		defaultColor = (-1 != defaultColorRes) ? getResources().getColor(defaultColorRes) : getResources().getColor(Color.BLACK);
		defaultColor = (-1 != defaultColorRes) ? getResources().getColor(defaultColorRes) : Color.parseColor("#FFFFFF");
		// 设置默认圆心颜色
		int checkColorRes = typedArray.getResourceId(R.styleable.CircleNavigationBar_checkCircle, -1);
		checkColor = (-1 != checkColorRes) ? getResources().getColor(checkColorRes) : Color.parseColor("#CCCCCC");
		typedArray.recycle();

	}

	public CircleNavigationBar(Context context) {
		this(context, null);
	}

	public void setContentViewPager(ViewPager pager) {
		this.pager = pager;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (null != pager) {
			int count = pager.getAdapter().getCount();
			int circleWidth = getWidth() / count;
			int circleHeight = getHeight() / 2;
			paint.setAntiAlias(true);
			for (int i = 0; i < count; i++) {
				paint.setColor(!showAnimation ? ((i == position) ? defaultColor : checkColor) : defaultColor);
				canvas.drawCircle(i * circleWidth + circleWidth / 2, circleHeight, circleRadius, paint);
			}
			// 绘制移动圆心
			if (showAnimation) {
				paint.setColor(checkColor);
				canvas.drawCircle(position * circleWidth + circleWidth / 2 + offset * (circleWidth), circleHeight, circleRadius, paint);
			}
			paint.reset();
		}
	}

	public void scrollToPosition(int position, float positionOffset) {
		this.position = position;
		this.offset = positionOffset;
		invalidate();
	}
	public int getCircleRadius(){
		return circleRadius;
	}
	/**
	 * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		int value = (int) dpValue;
		if (null != context) {
			final float scale = context.getResources().getDisplayMetrics().density;
			value = (int) (dpValue * scale + 0.5f);
		}
		return value;
	}

}

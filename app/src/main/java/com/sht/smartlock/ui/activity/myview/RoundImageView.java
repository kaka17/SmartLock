package com.sht.smartlock.ui.activity.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.sht.smartlock.AppContext;

/**
 * 圆角ImageView
 *
 * @author skg
 *
 */
public class RoundImageView extends ImageView {
 
    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
 
    public RoundImageView(Context context) {
        super(context);
        init();
    }
 
    private final RectF roundRect = new RectF();
    private float rect_adius = 10;
    private final Paint maskPaint = new Paint();
    private final Paint zonePaint = new Paint();
 
    private void init() {
        maskPaint.setAntiAlias(true);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //
        zonePaint.setAntiAlias(true);
        zonePaint.setColor(Color.WHITE);
    }
 
    public void setRectAdius(float adius) {
        rect_adius = adius;
        invalidate();
    }
 
    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int w = getWidth();
        int h = getHeight();
        roundRect.set(0, 0, w, h);
    }
 
    @Override
    public void draw(Canvas canvas) {
        canvas.saveLayer(roundRect, zonePaint, Canvas.ALL_SAVE_FLAG);
        canvas.drawRoundRect(roundRect, dip2px(AppContext.instance().getApplicationContext(), rect_adius), dip2px(AppContext.instance().getApplicationContext(), rect_adius), zonePaint);
        canvas.saveLayer(roundRect, maskPaint, Canvas.ALL_SAVE_FLAG);
        super.draw(canvas);
        canvas.restore();
    }
    @Override
    public void setOnClickListener(OnClickListener l) {
    	super.setOnClickListener(l);
    }
    
    public static int dip2px(Context context, float dpValue) {
		int value = (int) dpValue;
		if (null != context) {
			final float scale = context.getResources().getDisplayMetrics().density;
			value = (int) (dpValue * scale + 0.5f);
		}
		return value;
	}
}

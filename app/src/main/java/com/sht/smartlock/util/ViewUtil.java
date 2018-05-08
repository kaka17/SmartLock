package com.sht.smartlock.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public final class ViewUtil {

	public static void setViewSize(View view, int width, int height) {
		view.setLayoutParams(new LinearLayout.LayoutParams(width, height));
	}

	public static float a(TextView tv) {
		float f = 0.0F;
		if (tv != null) {
			float f1 = 0.0f;
			CharSequence text = tv.getText();
			if (!TextUtils.isEmpty(text)) {
				f1 = tv.getPaint().measureText(text.toString());
			}
			int i = tv.getPaddingLeft();
			int j = tv.getPaddingRight();
			f = f1 + (float) (j + i);
		}
		return f;
	}

	public static int a(Context context) {
		return (int) TypedValue.applyDimension(1, 40F, context.getResources()
				.getDisplayMetrics());
	}

	public static void a(Activity activity, int[] ai) {
		if (ai != null && activity != null) {
			int size = ai.length;
			for (int i = 0; i < size; i++) {
				View view = activity.findViewById(ai[i]);
				if (view != null)
					view.setOnClickListener((OnClickListener) activity);
			}
		}
	}

	public static void a(Dialog dialog, int[] ai) {
		if (ai != null && dialog != null) {
			int size = ai.length;
			for (int j = 0; j < size; j++) {
				View view = dialog.findViewById(ai[j]);
				if (view != null)
					view.setOnClickListener((OnClickListener) dialog);
			}
		}
	}

	public static void a(View view) {
		if (view != null && (view.getParent() instanceof ViewGroup))
			((ViewGroup) view.getParent()).removeView(view);
	}

	public static void a(View view, int i) {
		if (view != null)
			view.setVisibility(i);
	}

	public static void a(View view, OnClickListener listener, int[] ai) {
		if (ai != null && view != null) {
			int size = ai.length;
			for (int j = 0; j < size; j++) {
				View view1 = view.findViewById(ai[j]);
				if (view1 != null)
					view1.setOnClickListener(listener);
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static Bitmap b(View view) {
		Bitmap bitmap = null;
		view.measure(MeasureSpec.makeMeasureSpec(0, 0),
				MeasureSpec.makeMeasureSpec(0, 0));
		int i = view.getMeasuredWidth();
		int j = view.getMeasuredHeight();
		view.layout(0, 0, i, j);
		view.setDrawingCacheEnabled(false);
		view.setWillNotCacheDrawing(true);
		view.setDrawingCacheEnabled(false);
		view.setWillNotCacheDrawing(true);
		if (i > 0 && j > 0) {
			bitmap = Bitmap.createBitmap(i, j,
					Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			if (Build.VERSION.SDK_INT >= 11)
				view.setLayerType(1, null);
			view.draw(canvas);
		}
		return bitmap;
	}

    public static void setListViewHeight(ListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

	public static void setTextColor(int color, TextView... views){
		for (int i = 0; i < views.length; i++) {
			TextView view = views[i];
			view.setTextColor(color);
		}
	}

    public static int getListViewHeight(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        int listviewHeight = 0;
        listView.measure(MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        listviewHeight = listView.getMeasuredHeight() * adapter.getCount() + (adapter.getCount() * listView.getDividerHeight());
        return listviewHeight;
    }
}

package com.sht.smartlock.widget.dialog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sht.smartlock.R;


public class DialogTitleView extends FrameLayout {

	public static final int MODE_REGULAR = 0;
	public static final int MODE_SMALL = 1;
	public LinearLayout buttonWell;
	public TextView subTitleTv;
	public View titleDivider;
	public TextView titleTv;
	
	public DialogTitleView(Context context) {
		super(context);
		init();
	}

	public DialogTitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DialogTitleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		inflate(getContext(), R.layout.view_dialog_header, this);
		titleTv = (TextView) findViewById(R.id.title_tv);
		subTitleTv = (TextView) findViewById(R.id.subtitle_tv);
		buttonWell = (LinearLayout) findViewById(R.id.button_well);
		titleDivider = findViewById(R.id.title_divder);
	}

//	public static final Button makeButton(Context context, int i) {
//		return makeButton(context, context.getString(i));
//	}

//	public static final Button makeButton(Context context, String s) {
//		return makeButton(context, s, (int) context.getResources()
//				.getDimension(R.dimen.button_height));
//	}
//
//	public static final Button makeButton(Context context, String s, int i) {
//		Button button_orange = (Button) ViewHelper.viewById(context, R.layout.view_btn_red);
//		android.view.ViewGroup.LayoutParams layoutparams = button_orange
//				.getLayoutParams();
//		if (layoutparams == null)
//			layoutparams = new android.view.ViewGroup.LayoutParams(-2, i);
//		else
//			layoutparams.height = i;
//		button_orange.setLayoutParams(layoutparams);
//		button_orange.setText(s);
//		return button_orange;
//	}

//	public void addAction(int i,
//			android.view.View.OnClickListener listener) {
//		ImageView imageview = new ImageView(getContext(), null, R.style.dialog_well_button);
//		imageview.setOnClickListener(listener);
//		imageview.setImageResource(i);
//		buttonWell.addView(imageview);
//	}

	public void addAction(View view,
			OnClickListener listener) {
		view.setOnClickListener(listener);
		buttonWell.addView(view);
	}

	public void setMode(int mode) {
		int padding = (int) getContext().getResources().getDimension(R.dimen.global_dialog_padding);
		if (mode == MODE_SMALL) {
			buttonWell.removeAllViews();
			buttonWell.setVisibility(View.VISIBLE);
			titleTv.setTextSize(1, 16F);
			padding /= 2;
		} else {
			titleTv.setTextSize(1, 22F);
		}
		titleTv.setPadding(padding, padding, padding, padding);
	}
}

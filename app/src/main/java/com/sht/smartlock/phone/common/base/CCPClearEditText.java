/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.cloopen.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.sht.smartlock.phone.common.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.sht.smartlock.R;


/**
 * <p>Title: CCPClearEditText</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: Beijing Speedtong Information Technology Co.,Ltd</p>
 * @author Jorstin Chan
 * @date 2014-4-6
 * @version 1.0
 */
public class CCPClearEditText extends EditText implements View.OnTouchListener , View.OnFocusChangeListener{

	final Drawable mClear = getResources().getDrawable(R.drawable.search_clear);
	/**
	 * @param context
	 */
	public CCPClearEditText(Context context) {
		super(context);
		initCCPClearEditTextRef();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public CCPClearEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initCCPClearEditTextRef();
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public CCPClearEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initCCPClearEditTextRef();
	}

	private void initCCPClearEditTextRef() {
		
		mClear.setBounds(0, 0, mClear.getIntrinsicHeight(), mClear.getIntrinsicHeight());
		
		doClearDrawable();
		setHeight(mClear.getIntrinsicHeight() + 5 * getResources().getDimensionPixelSize(R.dimen.OneDPPadding));
		setOnTouchListener(this);
		addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				doClearDrawable();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		setOnFocusChangeListener(this);
	}

	/**
	 * 
	 */
	private void doClearDrawable() {
		if("".equals(getText().toString()) || !isFocused()) {
			setClearDrawableNull();
			return;
		}
		setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], mClear, getCompoundDrawables()[3]);
	}

	/**
	 * 
	 */
	private void setClearDrawableNull() {
		setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], null, getCompoundDrawables()[3]);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		if(getCompoundDrawables()[2] == null) {
			return false;
		}
		
		if((event.getAction() != MotionEvent.ACTION_UP)
				|| event.getX() <= (getWidth() - getPaddingRight() - mClear.getIntrinsicWidth())) {
			return false;
			
		}
		getText().clear();
		
		return false;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		doClearDrawable();
	}
}

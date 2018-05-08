package com.sht.smartlock.phone.common.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class CCPDrawableUtils {

	/**
	 * 
	 * @param context
	 * @param id
	 * @return
	 */
	public static Drawable getDrawables(Context context , int id) {
		Drawable drawable = getResources(context).getDrawable(id);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		
		return drawable;
	}
	
	/**
	 * 
	 * @Title: getResource 
	 * @Description: TODO 
	 * @param context
	 * @return Resources 
	 */
	public static Resources getResources(Context context) {
		return context.getResources();
	}
}

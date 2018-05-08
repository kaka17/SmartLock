/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.sht.smartlock.phone.common.utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.provider.Settings;

import com.sht.smartlock.R;

/**
 * <p>Title: NotificationUtil.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: Beijing Speedtong Information Technology Co.,Ltd</p>
 * @author Jorstin Chan@容联•云通讯
 * @date 2015-1-4
 * @version 4.0
 */
public class NotificationUtil {
	
	public static final String TAG = LogUtil.getLogUtilsTag(Notification.class);

	@TargetApi(VERSION_CODES.HONEYCOMB)
	public static Notification buildNotification(Context context, int icon,
			int defaults, boolean onlyVibrate, String tickerText,
			String contentTitle, String contentText, Bitmap largeIcon,
			PendingIntent intent) {
		
		if(Build.VERSION.SDK_INT > VERSION_CODES.HONEYCOMB) {
			Notification.Builder builder = new Notification.Builder(context);
			builder
//			.setLights(-16711936, 300, 1000)
			.setSmallIcon(icon)
			.setTicker(tickerText)
			.setContentTitle(contentTitle)
			.setContentText(contentText)
			.setContentIntent(intent);
			
			if(onlyVibrate) {
				defaults &= Notification.DEFAULT_VIBRATE;
			} 
			
			LogUtil.d(TAG, "defaults flag " + defaults);
			builder.setDefaults(defaults);
			if(largeIcon != null) {
				builder.setLargeIcon(largeIcon);
			}
			return builder.getNotification();
		}
		
//		Notification notification = new Notification();
//		notification.ledARGB = -16711936;
//		notification.ledOnMS = 300;
//		notification.ledOffMS = 1000;
//		notification.flags = (Notification.FLAG_SHOW_LIGHTS | notification.flags);
//		notification.icon = icon;
//		notification.tickerText = tickerText;
//		LogUtil.d(TAG, "defaults flag " + defaults);
//		if(onlyVibrate) {
//			defaults &= Notification.DEFAULT_VIBRATE;
//		}
//		notification.defaults = defaults;
//		notification.setLatestEventInfo(context, contentTitle, contentText, intent);

		Notification noti = new Notification.Builder(context)//实例化Builder
				.setTicker(tickerText)//在状态栏显示的标题                         .setWhen(java.lang.System.currentTimeMillis())//设置显示的时间，默认就是currentTimeMillis()
				.setContentTitle(tickerText)//设置标题
				.setContentText(tickerText)//设置内容
				.setSmallIcon(R.drawable.ic_launcher)//设置状态栏显示时的图标      .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))//设置显示的大图标    .setContentIntent(PendingIntent.getActivity(MainActivity.this, 0, new Intent(Settings.ACTION_SETTINGS), 0))//设置点击时的意图
				.setDeleteIntent(PendingIntent.getActivity(context, 0, new Intent(Settings.ACTION_SETTINGS), 0))//设置删除时的意图             .setFullScreenIntent(PendingIntent.getActivity(MainActivity.this, 0, new Intent(Settings.ACTION_SETTINGS), 0), true)//这个将直接打开意图，而不经过状态栏显示再按下
				.setAutoCancel(false)//设置是否自动按下过后取消
				.setOngoing(false)//设置为true时就不能删除  除非使用notificationManager.cancel(1)方法
//				.setContentIntent(pendingIntent)
				.build();//创建Notification




		return noti;
	}
	
}

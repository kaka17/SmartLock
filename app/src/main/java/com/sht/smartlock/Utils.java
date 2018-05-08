package com.sht.smartlock;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

public class Utils {

	/**
	 * 获取本地的图片资源，并转化为bitmap
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getLoacalBitmap(String url)
			throws NullPointerException {
		Options options = new Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(url, options);
		options.outWidth = 400;
		options.outHeight = 400;
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;// 默认为ARGB_8888.
		options.inPurgeable = true;// 产生的位图将得到像素空间，如果系统gc，那么将被清空。当像素再次被访问，如果Bitmap已经decode，那么将被自动重新解码
		options.inInputShareable = true;// 位图可以共享一个参考输入数据(inputstream、阵列等)
		options.inSampleSize = 4;// decode 原图1/4
		bitmap = BitmapFactory.decodeFile(url, options);
		return bitmap;
	}

}

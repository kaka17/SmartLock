package com.sht.smartlock.phone.common.utils;

import java.util.regex.Pattern;

public class PatternUtils {
	
	public static boolean isZhongYing(String str){
		
      String zhongyin="^[A-Za-z\u4e00-\u9fa5]+$";//中英文
		
		Pattern p = Pattern.compile(zhongyin);
		
		return p.matcher(str).matches();
		
	}
	public static boolean isShuZiYing(String str){
		
		String yinshuzi="^[A-Za-z0-9_@]+$";//字母数字_ @
		
		Pattern p = Pattern.compile(yinshuzi);
		
		return p.matcher(str).matches();
		
	}

}

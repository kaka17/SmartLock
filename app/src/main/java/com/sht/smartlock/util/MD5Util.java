package com.sht.smartlock.util;

import java.security.MessageDigest;

public final class MD5Util {
	public final static String UTF8 = "utf-8";
	public final static String GBK = "gbk";
	public final static String GB18030 = "GB18030";
	public final static String ISO8859 = "iso8859-1";

	public final static String encrptMD5(String plainText, String charEncode) {
		if (plainText == null) {
			return null;
		}
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] strTemp = plainText.getBytes(charEncode);
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

	public static String joinString(String... params){
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < params.length; i++){
			stringBuffer.append(params[i]);
		}
		return encrptMD5(stringBuffer.toString(), MD5Util.UTF8);
	}
}

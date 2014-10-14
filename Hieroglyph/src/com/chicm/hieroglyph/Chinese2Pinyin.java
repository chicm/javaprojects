package com.chicm.hieroglyph;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

public class Chinese2Pinyin {

	public static boolean DEBUG = false;
	
	public static boolean isChinese (char c) {
		
		char[] arr = new char[1];
		arr[0] = c;
		int codepoint = Character.codePointAt(arr, 0);
		
		return (codepoint >= 0x4e00 && codepoint <=0x9FA5);
		
	}
	
	public static String convert (char charChinese) {
				
		String[] strPinyin;
		HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
		outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		
		if(!isChinese(charChinese))
			return ""+ charChinese;
		
		try {
			strPinyin = PinyinHelper.toHanyuPinyinStringArray(charChinese, outputFormat);
		} catch (Exception e) {
			e.printStackTrace(System.out);
			return null;
		}
		if(DEBUG) {
			for (String s : strPinyin) {
				System.out.println(s);
			}
		}
		return strPinyin[0];
	}
	
	public static String convert (String strChinese) {
		if(strChinese == null ||strChinese.length() < 1)
			return "";
		String pinyin = "";
		for ( int i = 0; i < strChinese.length(); i++) {
			pinyin += Chinese2Pinyin.convert(strChinese.charAt(i));
			if(isChinese(strChinese.charAt(i))) {
				pinyin += " ";
			}
		}
		return pinyin;
	}
	
	public static void main(String[] args) {
		System.out.println(Chinese2Pinyin.convert("ÈýËÄabcd"));
		//System.out.println(Chinese2Pinyin.isChinese('¹ú'));
	}

}

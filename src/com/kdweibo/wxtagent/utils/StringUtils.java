package com.kdweibo.wxtagent.utils;


public class StringUtils {

	public static boolean hasText(String str){
		if(str!=null && str.trim().length()>0){
			return true;
		}
		return false;
	}
}

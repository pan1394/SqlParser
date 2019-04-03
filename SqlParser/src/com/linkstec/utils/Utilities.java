package com.linkstec.utils;

import org.apache.commons.lang3.StringUtils;

public class Utilities {

	public static String tab2Space(String rawString) {
		return rawString.replaceAll("\\t+", " ");
	}
	
	public static String[] crop(String partString, String regexp) {
		String[] array = partString.split(regexp);

		String temp = StringUtils.trimToEmpty(array[1]);
		int spaceIndex = temp.indexOf(" ");
		String col = "";
		if (spaceIndex != -1) {
			col = StringUtils.substring(array[1], 0, spaceIndex);
		} else {
			col = temp;
		}
		String[] res = new String[2];
		res[0] = StringUtils.trimToEmpty(array[0]);
		res[1] = col;
		return res;
	}
	
	public static boolean contains(String[] keys, String compared) {
		for (String key : keys) {
			if (compared.contains(key))
				return true;
		}
		return false;
	}
	
	public static String fetch(String[] keys, String compared) {
		for (String key : keys) {
			if (compared.contains(key))
				return key;
		}
		return "";
	}
	
	public static String[] split(String string, String regex) {
		String[] items = string.split(regex);
		for(int i=0; i<items.length; i++) {
			items[i] = StringUtils.trimToEmpty(items[i]);
		}
		return items;
	}
	
	public static String abstractStringRange(String string, String start, String end) {
		int idx1 = -1;
		int idx2 = -1;
		idx1 = string.indexOf(start);
		idx2 = string.lastIndexOf(end);
		if(idx1 != idx2 && idx1>0 && idx2>0) {
			return string.substring(idx1, idx2-end.length());
		}
		return string;
				
				
	}
}

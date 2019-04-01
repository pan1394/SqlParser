package com.linkstec.utils;

import org.apache.commons.lang3.StringUtils;

public class Utlities {

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
}

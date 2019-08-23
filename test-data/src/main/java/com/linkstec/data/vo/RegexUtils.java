package com.linkstec.data.vo;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

	
	public static final String PREFIX = "FREFIX";
	
	public static final String SUFFIX = "SUFFIX";
	
	/**
	 * 分割为字母汉字 前缀，数字后缀
	 * 
	 * @param string
	 * @param content
	 */
	public static void split2parts(String string, Map<String, Object> content) {
		Pattern p = Pattern.compile("([0A-Za-z\\u4e00-\\u9fa5]+)(\\d+)");
		Matcher m = p.matcher(string);
		if(m.find()) {
			String pre = m.group(1);
			long suf = Long.valueOf(m.group(2));
			content.put(PREFIX, pre);
			content.put(SUFFIX, suf);
		}else {
			content.put(PREFIX, null);
			content.put(SUFFIX, null);
		}
	}
}

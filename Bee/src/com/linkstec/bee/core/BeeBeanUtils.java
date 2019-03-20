package com.linkstec.bee.core;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

public class BeeBeanUtils {
	public static Hashtable<String, String> makeData(Object obj) {
		Hashtable<String, String> values = new Hashtable<String, String>();
		Field[] fs = obj.getClass().getDeclaredFields();
		for (Field f : fs) {
			BeeProperty anno = f.getAnnotation(BeeProperty.class);
			if (anno != null) {
				String title = anno.value();
				try {
					Object value = f.get(obj);
					if (value instanceof Long) {
						long l = (long) value;
						if (l < 1000) {
							value = l + "B";
						} else if (l < 1000 * 1000) {
							value = l / 1000 + "K";
						} else {
							value = 1 / 1000 / 1000 + "M";
						}
					} else if (value instanceof Date) {
						SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd ss:mm");
						value = format.format(value);
					}
					values.put(title, value.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return values;
	}
}

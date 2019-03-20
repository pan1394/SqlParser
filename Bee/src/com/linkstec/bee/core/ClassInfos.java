package com.linkstec.bee.core;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import com.linkstec.bee.core.fw.editor.BProject;

public class ClassInfos {

	public static String getPrimeryClass(String name) {
		if (name == null) {
			return null;
		}
		if (name.equals("double")) {
			return name;
		} else if (name.equals("int")) {
			return name;
		} else if (name.equals("float")) {
			return name;
		} else if (name.equals("long")) {
			return name;
		} else if (name.equals("short")) {
			return name;
		} else if (name.equals("bute")) {
			return name;
		} else if (name.equals("void")) {
			return name;
		} else if (name.equals("boolean")) {
			return name;
		} else if (name.equals("char")) {
			return name;
		}

		return null;
	}

	public static List<String> lookupClass(String text) throws InterruptedException {
		List<String> list = new ArrayList<String>();
		String p = getPrimeryClass(text);
		if (p != null) {
			list.add(p);
			return list;
		}
		BProject project = Application.getInstance().getCurrentProject();
		if (project == null) {
			return list;
		}
		if (text == null || text.trim().equals("")) {
			return list;
		}
		Hashtable<String, URL> paths = LocalClassPathes.getPathes();
		ClassInfos.makeList(paths, text, list);

		// if (list.size() == 0) {

		paths = ProjectClassLoader.getClassLoader(project).getPathes();
		ClassInfos.makeList(paths, text, list);
		// }

		Collections.sort(list, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				String s1 = o1;
				String s2 = o2;

				if (s1.indexOf(".") > 0) {
					s1 = o1.substring(o1.lastIndexOf('.') + 1);
				}
				if (s2.indexOf(".") > 0) {
					s2 = o2.substring(o2.lastIndexOf('.') + 1);
				}

				if (s1.length() == s2.length()) {
					int len = s1.length();
					for (int i = 0; i < len; i++) {
						int o = s1.charAt(i) - s2.charAt(i);
						if (o != 0) {
							return o > 0 ? 1 : -1;
						}
					}
				} else {
					return s1.length() > s2.length() ? 1 : -1;
				}

				return 0;
			}

		});

		return list;
	}

	private static void makeList(Hashtable<String, URL> paths, String text, List<String> list) throws InterruptedException {
		Enumeration<String> keys = paths.keys();

		while (keys.hasMoreElements()) {
			if (Thread.currentThread().isInterrupted()) {
				throw new InterruptedException();
			}
			String key = keys.nextElement();
			String name = key;
			if (name.indexOf(".") > 0) {
				name = name.substring(key.lastIndexOf(".") + 1);
			}
			if (key.startsWith("jp.")) {
				Debug.a();
			}
			if (name.equals(text)) {
				list.add(key);
			}
		}
	}
}

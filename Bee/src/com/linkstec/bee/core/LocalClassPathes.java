package com.linkstec.bee.core;

import java.io.File;
import java.net.URL;
import java.util.Hashtable;

import com.link.test.ClassPathesLooker;

public class LocalClassPathes {
	private static Hashtable<String, URL> pathes = new Hashtable<String, URL>();

	public static void pushAll() {
		String path = System.getProperty("sun.boot.library.path");

		File file = new File(path);
		file = file.getParentFile();
		file = new File(file.getAbsolutePath() + File.separator + "lib");
		if (file.exists() && file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				String s = f.getAbsolutePath();
				// String[] ps = path.split(";");
				// for (String s : ps) {
				if (s.endsWith("dt.jar") || s.endsWith("rt.jar") || s.endsWith("jsse.jar") || s.endsWith("jce.jar")
						|| s.endsWith("jfr.jar")) {
					ClassPathesLooker.makeZipUrl(new File(s), pathes);
				}
				// }
			}
		}
	}

	public static Hashtable<String, URL> getPathes() {
		return pathes;
	}
}

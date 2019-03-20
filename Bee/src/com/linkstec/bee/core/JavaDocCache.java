package com.linkstec.bee.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class JavaDocCache {
	private static Hashtable<String, URL> pathes = new Hashtable<String, URL>();

	public static void put(String key, URL value) {
		pathes.put(key, value);
	}

	public static boolean containsKey(String key) {
		return pathes.containsKey(key);
	}

	public static URL getURL(String key) {
		return pathes.get(key);
	}

	public static void pushAll() {

		Properties p = System.getProperties();
		String path = (String) p.get("java.class.path");
		if (path != null) {
			if (path.indexOf(";") > 0) {
				String[] ps = path.split(";");
				for (String lib : ps) {
					if (lib.endsWith("Java1.8API.zip")) {
						makeZipUrl(new File(lib));
					}
				}
			}
		}

	}

	private static void makeZipUrl(File f) {
		try {
			ZipFile zip = new ZipFile(f);
			String zipUrl = "jar:file:/" + f.getAbsolutePath() + "!";
			Enumeration<? extends ZipEntry> entries = zip.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				String name = entry.getName();

				if (entry.isDirectory()) {
					continue;
				}

				if (name.endsWith(".d")) {
					String className = name.substring(0, name.lastIndexOf("."));
					className = className.replace('/', '.');
					if (!JavaDocCache.containsKey(className)) {
						JavaDocCache.put(className, new URL(zipUrl + "/" + name));
					}
				}
			}
			zip.close();
		} catch (ZipException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

}

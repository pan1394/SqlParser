package com.link.test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ClassPathesLooker {

	public static void makeLibraryUrl(String path, Hashtable<String, URL> pathes) {

		if (path != null && !path.equals("")) {
			if (path.indexOf(File.pathSeparator) > -1) {
				String[] paths = path.split(File.pathSeparator);
				for (String p : paths) {
					File f = new File(p);
					if (f.exists() && f.isDirectory()) {
						makeNormalFileUrl(f.getAbsolutePath(), f, pathes);
					} else if (f.exists() && f.isFile()) {
						String fp = f.getAbsolutePath();
						if (fp.endsWith(".class") || fp.endsWith(".propeties")) {
							makeNormalFileUrl(fp, f, pathes);
						} else {
							makeZipUrl(f, pathes);
						}
					}
				}
			} else {
				File f = new File(path);
				if (f.exists() && f.isDirectory()) {
					makeNormalFileUrl(f.getAbsolutePath(), f, pathes);
				} else if (f.exists() && f.isFile()) {
					String fp = f.getAbsolutePath();
					if (fp.endsWith(".class") || fp.endsWith(".propeties")) {
						makeNormalFileUrl(fp, f, pathes);
					} else {
						makeZipUrl(f, pathes);
					}
				}
			}
		}
	}

	public static void makeZipUrl(File f, Hashtable<String, URL> pathes) {
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

				if (name.endsWith(".class") || name.endsWith("properties.")) {
					String className = name.substring(0, name.lastIndexOf("."));
					className = className.replace('/', '.');
					if (!pathes.containsKey(className)) {
						pathes.put(className, new URL(zipUrl + "/" + name));
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

	public static void makeNormalFileUrl(String root, File f, Hashtable<String, URL> pathes) {
		String path = f.getAbsolutePath();
		if (!f.isDirectory()) {
			if (f.getName().endsWith(".class") || f.getName().endsWith(".propeties")) {
				if (path.startsWith(root)) {
					String name = path.substring(root.length() + 1, path.lastIndexOf("."));
					name = name.replace(File.separatorChar, '.');
					try {
						// class which has same class name located at later position will not be added
						if (!pathes.containsKey(name)) {
							pathes.put(name, new URL("file:/" + path));
						}
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
			} else if (f.getName().endsWith("zip") || f.getName().endsWith("jar")) {
				makeZipUrl(f, pathes);
			}
		} else {
			File[] files = f.listFiles();
			for (File file : files) {
				makeNormalFileUrl(root, file, pathes);
			}
		}
	}

}

package com.linkstec.bee.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.editor.BProject;

public class ClassCache implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5590648031192081658L;
	private static Hashtable<BProject, List<Class<?>>> stores = new Hashtable<BProject, List<Class<?>>>();

	public static synchronized void addClass(BProject project, Class<?> clazz) {
		List<Class<?>> list = stores.get(project);
		if (list == null) {
			list = new ArrayList<Class<?>>();
			stores.put(project, list);
		}
		boolean contained = false;
		for (Class<?> sc : list) {
			if (sc.getName().equals(clazz.getName())) {
				contained = true;
			}
		}
		if (!contained) {

			list.add(clazz);
		}
	}

	public static Hashtable<BProject, List<Class<?>>> getStore() {
		return stores;
	}

	public synchronized static Class<?> getCachedClass(BProject project, String className) {

		List<Class<?>> list = stores.get(project);
		synchronized (stores) {
			if (list != null) {
				for (Class<?> sc : list) {
					if (sc.getName().equals(className)) {
						return sc;
					}
				}
			}
		}

		try {
			return ProjectClassLoader.getClassLoader(project).loadClass(className);

		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
		return null;
	}

	public synchronized static Class<?> getLoadedClass(BProject project, String className) {

		List<Class<?>> list = stores.get(project);
		synchronized (stores) {
			if (list != null) {
				for (Class<?> sc : list) {
					if (sc.getName().equals(className)) {
						return sc;
					}
				}
			}
		}
		return null;
	}

	public static void write() {
		Enumeration<BProject> enu = stores.keys();
		while (enu.hasMoreElements()) {
			BProject p = enu.nextElement();
			String root = p.getRootPath();
			String path = root + File.separator + ".classCache";
			File file = new File(path);
			try {
				if (!file.exists()) {
					file.createNewFile();
				}
				List<String> clsses = new ArrayList<String>();
				List<Class<?>> list = stores.get(p);
				for (Class<?> cls : list) {
					clsses.add(cls.getName());
				}

				FileOutputStream fos = new FileOutputStream(file);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(clsses);
				oos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void read(BProject project) {
		try {
			String root = project.getRootPath();
			String path = root + File.separator + ".classCache";
			File file = new File(path);

			if (!file.exists()) {
				return;
			}
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			ObjectInputStream ois = new ObjectInputStream(bis);
			Object obj = ois.readObject();
			ois.close();
			bis.close();

			List<String> clss = (List<String>) obj;
			for (String name : clss) {
				Class cls = CodecUtils.getClassByName(name, project);
				if (cls != null) {
					ClassCache.addClass(project, cls);
				}
			}

			Application.log("project " + project.getName() + " class cache read completed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

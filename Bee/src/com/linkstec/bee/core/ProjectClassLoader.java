package com.linkstec.bee.core;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.core.fw.editor.BProject;

public class ProjectClassLoader {
	private static List<BeeClassLoader> list = new ArrayList<BeeClassLoader>();

	public static void addClassLoder(BeeClassLoader calssloader) {
		try {
			for (BeeClassLoader loader : list) {
				if (loader.getProject().getName().equals(calssloader.getProject().getName())) {
					list.remove(loader);
				}
			}
			list.add(calssloader);
		} catch (Exception e) {

		}
	}

	public static BeeClassLoader getClassLoader(String projectName) {
		for (BeeClassLoader loader : list) {
			if (loader.getProject().getName().equals(projectName)) {
				return loader;
			}
		}
		return null;
	}

	public static void removeClassLoader(String projectName) {
		for (BeeClassLoader loader : list) {
			if (loader.getProject().getName().equals(projectName)) {
				list.remove(loader);
				break;
			}
		}
	}

	public static BeeClassLoader getClassLoader(BProject project) {
		for (BeeClassLoader loader : list) {
			if (project == null) {
				System.out.println("null");
			}
			if (loader.getProject().getName().equals(project.getName())) {
				return loader;
			}
		}
		return null;
	}

	public static void clear() {
		list.clear();
	}
}

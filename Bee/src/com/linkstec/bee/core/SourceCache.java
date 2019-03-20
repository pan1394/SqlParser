package com.linkstec.bee.core;

import java.io.File;
import java.util.Date;
import java.util.Hashtable;

import com.linkstec.bee.UI.spective.code.BeeSource;
import com.linkstec.bee.core.fw.editor.BProject;

public class SourceCache {
	private static Hashtable<BProject, Hashtable<String, BeeSource>> stores = new Hashtable<BProject, Hashtable<String, BeeSource>>();

	public static void clear(BProject project) {
		if (project == null) {
			return;
		}

		Hashtable<String, BeeSource> list = stores.get(project);
		if (list != null) {
			list.clear();
		}
	}

	public static void addSource(File f, BProject project) {
		if (project == null) {
			return;
		}
		String name = getJavaName(f, project);
		if (name != null) {
			Hashtable<String, BeeSource> list = stores.get(project);
			if (list == null) {
				list = new Hashtable<String, BeeSource>();
				stores.put(project, list);
			}

			if (!list.containsKey(name)) {
				BeeSource source = new BeeSource();
				source.setPath(f.getAbsolutePath());
				source.setName(name);
				Date date = new Date();
				date.setTime(f.lastModified());
				source.setLastModified(date);
				source.setSize(f.length());
				source.setProjectName(project.getName());
				list.put(name, source);
			}
		}
	}

	private static String getJavaName(File f, BProject project) {
		if (project == null) {
			return null;
		}
		String name = f.getAbsolutePath();
		if (name.endsWith(".java")) {

			String path = project.getSourcePath();
			name = name.substring(path.length() + 1);
			name = name.substring(0, name.length() - 5);
			name = name.replace(File.separatorChar, '.');
			return name;
		}
		return null;
	}

	public static void removeSource(File f, BProject project) {
		String name = getJavaName(f, project);
		if (name != null) {
			Hashtable<String, BeeSource> list = stores.get(project);
			if (list != null) {
				list.remove(name);
			}
		}
	}

	public static BeeSource getSource(File f, BProject project) {
		if (project == null) {
			return null;
		}
		String name = getJavaName(f, project);
		if (name != null) {
			Hashtable<String, BeeSource> list = stores.get(project);
			if (list != null) {
				return list.get(name);
			}
		}
		return null;
	}

	public static BeeSource getSource(String name, BProject project) {
		if (project == null) {
			return null;
		}
		if (name != null) {
			Hashtable<String, BeeSource> list = stores.get(project);
			if (list != null) {
				return list.get(name);
			}
		}
		return null;
	}

}

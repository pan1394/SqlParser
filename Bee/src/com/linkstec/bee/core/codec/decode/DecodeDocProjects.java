package com.linkstec.bee.core.codec.decode;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.core.fw.editor.BProject;

public class DecodeDocProjects {

	private static List<DecodeDoc> list = new ArrayList<DecodeDoc>();

	public static void addDoc(DecodeDoc d) {
		try {
			for (DecodeDoc doc : list) {
				if (doc.getProject().getName().equals(d.getProject().getName())) {
					list.remove(doc);
				}
			}
			list.add(d);
		} catch (Exception e) {

		}
	}

	public static DecodeDoc getDoc(String projectName) {
		for (DecodeDoc doc : list) {
			if (doc.getProject().getName().equals(projectName)) {
				return doc;
			}
		}
		return null;
	}

	public static DecodeDoc getDoc(BProject project) {
		if (project == null) {
			return null;
		}
		for (DecodeDoc doc : list) {
			if (doc.getProject() != null && doc.getProject().getName().equals(project.getName())) {
				return doc;
			}
		}
		return null;
	}

	public static void clear() {
		list.clear();
	}
}

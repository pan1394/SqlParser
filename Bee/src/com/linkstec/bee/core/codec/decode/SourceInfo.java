package com.linkstec.bee.core.codec.decode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.editor.BProject;

public class SourceInfo {
	private String source = "";
	private String[] lines;
	private String path;
	private BProject project;

	public SourceInfo(String path, BProject project) {
		this.project = project;
		this.path = path;

		File file = new File(path);
		byte[] buffer = new byte[1024];
		try {
			FileInputStream in = new FileInputStream(file);
			int len = 0;

			while ((len = in.read(buffer)) != -1) {
				source = source + new String(buffer, 0, len);
			}

			in.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		lines = source.split("\n");
	}

	public String getLine(int number) {
		if (number > lines.length) {
			return "";
		}
		return lines[number - 1];
	}

	public Class<?> getTargetClass() {

		String root = project.getSourcePath();
		if (path.startsWith(root)) {
			String p = path.substring(root.length() + 1, path.length());
			p = p.substring(0, p.lastIndexOf('.'));
			p = p.replace(File.separatorChar, '.');
			try {
				Class<?> cls = Application.loadClass(project, p);
				return cls;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

}

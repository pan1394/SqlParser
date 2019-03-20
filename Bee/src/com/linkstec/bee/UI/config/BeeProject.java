package com.linkstec.bee.UI.config;

import java.io.File;
import java.util.Hashtable;

import com.linkstec.bee.core.ProjectClassLoader;
import com.linkstec.bee.core.codec.decode.BeeCompiler;
import com.linkstec.bee.core.fw.editor.BProject;

public class BeeProject implements BProject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6578985232414650534L;
	private String name;
	private String rootPath;
	private String sourcePath;
	private String classPath;
	private String designPath;
	private String libPath;
	private Hashtable<String, String> propertes = new Hashtable<String, String>();
	private String id;

	public BeeProject(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getClassPath() {
		return classPath;
	}

	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	public String getDesignPath() {
		return designPath;
	}

	public void setDesignPath(String designPath) {
		this.designPath = designPath;
	}

	public String getLibPath() {
		return libPath;
	}

	public void setLibPath(String libPath) {
		// clean it
		String[] ss = libPath.split(File.pathSeparator);
		int i = 0;
		for (String s : ss) {
			if (!s.trim().equals("")) {
				if (i == 0) {
					libPath = s;
				} else {
					libPath = libPath + File.pathSeparator + s;
				}
				i++;
			}
		}
		this.libPath = libPath;
		ProjectClassLoader.getClassLoader(name).updateClassUrls();
		BeeCompiler.comileAllWithThread(this, null);
	}

	public Hashtable<String, String> getPropertes() {
		return propertes;
	}

	public void setPropertes(Hashtable<String, String> propertes) {
		this.propertes = propertes;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof BeeProject) {
			BeeProject p = (BeeProject) obj;
			if (p.id == null) {
				return super.equals(obj);
			}
			if (p.id.equals(this.id)) {
				return true;
			}
		}
		return false;
	}

	public String toString() {
		return this.name;
	}
}

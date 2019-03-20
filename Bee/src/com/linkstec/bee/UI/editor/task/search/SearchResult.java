package com.linkstec.bee.UI.editor.task.search;

import com.linkstec.bee.core.fw.editor.BProject;

public class SearchResult {
	public static final String TYPE_SOURCE = "TYPE_SOURCE";
	public static final String TYPE_DESIGN = "TYPE_DESIGN";

	private String type;
	private BProject project;
	private String keyword;
	private String path;
	private Object userObject;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	public BProject getProject() {
		return project;
	}

	public void setProject(BProject project) {
		this.project = project;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}

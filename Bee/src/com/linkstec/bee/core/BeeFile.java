package com.linkstec.bee.core;

import java.io.Serializable;
import java.util.Date;

public class BeeFile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2959438521957610706L;

	/**
	 * 
	 */
	@BeeProperty("場所")
	private String path;
	@BeeProperty("更新時刻")
	private Date lastModified;
	@BeeProperty("サイズ")
	private long size;
	private String projectName;

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
}

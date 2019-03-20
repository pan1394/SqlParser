package com.linkstec.bee.UI.spective.basic.logic;

import java.io.Serializable;

public class LogicMenuMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 672659071281057123L;
	private String id;
	private String value;
	private String fileName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}

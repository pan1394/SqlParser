package com.link.test;

import java.io.Serializable;

public class BStaticInfoObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6313885871769449618L;
	private String packageName;
	private String className;
	private String name;
	private String logicName;
	private String type;
	private String kubunParent;
	private String value;

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogicName() {
		return logicName;
	}

	public void setLogicName(String logicName) {
		this.logicName = logicName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKubunParent() {
		return kubunParent;
	}

	public void setKubunParent(String kubunParent) {
		this.kubunParent = kubunParent;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}

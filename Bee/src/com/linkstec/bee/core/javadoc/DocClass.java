package com.linkstec.bee.core.javadoc;

import java.io.Serializable;
import java.util.List;

public class DocClass implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String comment;
	private List<DocMethod> methodList;
	private List<DocMethod> constructorList;
	private List<DocField> fieldList;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<DocMethod> getMethodList() {
		return methodList;
	}

	public void setMethodList(List<DocMethod> methodList) {
		this.methodList = methodList;
	}

	public List<DocMethod> getConstructorList() {
		return constructorList;
	}

	public void setConstructorList(List<DocMethod> constructorList) {
		this.constructorList = constructorList;
	}

	public List<DocField> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<DocField> fieldList) {
		this.fieldList = fieldList;
	}
}

package com.linkstec.bee.core.javadoc;

import java.io.Serializable;
import java.util.List;

public class DocMethod implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String comment;
	private List<DocType> parameter;
	private DocType returnStr;
	private List<DocType> exceptionStr;

	public List<DocType> getExceptionStr() {
		return exceptionStr;
	}

	public void setExceptionStr(List<DocType> exceptionStr) {
		this.exceptionStr = exceptionStr;
	}

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

	public List<DocType> getParameter() {
		return parameter;
	}

	public void setParameter(List<DocType> parameter) {
		this.parameter = parameter;
	}

	public DocType getReturnStr() {
		return returnStr;
	}

	public void setReturnStr(DocType returnStr) {
		this.returnStr = returnStr;
	}
}

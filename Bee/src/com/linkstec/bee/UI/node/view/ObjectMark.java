package com.linkstec.bee.UI.node.view;

import java.io.Serializable;

import com.linkstec.bee.UI.node.BasicNode;

public class ObjectMark implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7016338810452771831L;
	private String id;
	private String className;

	public ObjectMark(BasicNode node) {
		this.id = node.getId();
		this.className = node.getClass().getName();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClassName() {
		return className;
	}

	public String toString() {
		return this.id + "";
	}

}

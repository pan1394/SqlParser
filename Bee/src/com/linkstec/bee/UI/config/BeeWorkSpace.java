package com.linkstec.bee.UI.config;

import java.io.Serializable;

public class BeeWorkSpace implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5999599467276273705L;
	private String root;
	private Object userObject;

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

}

package com.linkstec.bee.UI.spective.basic.logic;

import javax.swing.JMenuItem;

public class BasicCellListeItem extends JMenuItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4906398436616206777L;
	private Object userObject;
	private String displayName;

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String toString() {
		if (this.displayName != null) {
			return displayName;
		} else {
			return super.toString();
		}
	}

}

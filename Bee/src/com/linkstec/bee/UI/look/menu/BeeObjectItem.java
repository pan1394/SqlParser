package com.linkstec.bee.UI.look.menu;

import javax.swing.JLabel;

public class BeeObjectItem extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8861102827053900828L;
	private Object userObject;

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	public String toString() {
		return this.getText();
	}
}

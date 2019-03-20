package com.linkstec.bee.UI.look.button;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JRadioButton;

public class BeeRadio extends JRadioButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8433780695408997415L;
	private Object userObject;

	public BeeRadio() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BeeRadio(Action a) {
		super(a);
		// TODO Auto-generated constructor stub
	}

	public BeeRadio(Icon icon, boolean selected) {
		super(icon, selected);
		// TODO Auto-generated constructor stub
	}

	public BeeRadio(Icon icon) {
		super(icon);
		// TODO Auto-generated constructor stub
	}

	public BeeRadio(String text, boolean selected) {
		super(text, selected);
		// TODO Auto-generated constructor stub
	}

	public BeeRadio(String text, Icon icon, boolean selected) {
		super(text, icon, selected);
		// TODO Auto-generated constructor stub
	}

	public BeeRadio(String text, Icon icon) {
		super(text, icon);
		// TODO Auto-generated constructor stub
	}

	public BeeRadio(String text) {
		super(text);
		// TODO Auto-generated constructor stub
	}

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

}

package com.linkstec.bee.UI.spective.basic.config.utils;

import javax.swing.JButton;

import com.linkstec.bee.core.fw.editor.BProject;

public class ActionButton extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9186181883436662158L;
	private String fixedValue;
	private BProject project;
	private String type;
	private boolean nameDotted = false;

	public ActionButton(String text) {
		super(text);

	}

	public boolean isNameDotted() {
		return nameDotted;
	}

	public void setNameDotted(boolean nameDotted) {
		this.nameDotted = nameDotted;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFixedValue() {
		return fixedValue;
	}

	public void setFixedValue(String fixedValue) {
		this.fixedValue = fixedValue;
	}

	public BProject getProject() {
		return project;
	}

	public void setProject(BProject project) {
		this.project = project;
	}

}

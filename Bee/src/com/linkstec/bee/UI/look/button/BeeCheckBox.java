package com.linkstec.bee.UI.look.button;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.icon.BeeIcon;

public class BeeCheckBox extends JCheckBox {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2682017643302979133L;
	private ImageIcon userIcon;

	public BeeCheckBox() {
		super();
		this.setIcons();
	}

	public BeeCheckBox(Action a) {
		super(a);
		this.setIcons();
	}

	public ImageIcon getUserIcon() {
		return userIcon;
	}

	public void setUserIcon(ImageIcon userIcon) {
		this.userIcon = userIcon;
	}

	public BeeCheckBox(Icon icon, boolean selected) {
		super(icon, selected);
		this.setIcons();
	}

	public BeeCheckBox(Icon icon) {
		super(icon);
		this.setIcons();
	}

	public BeeCheckBox(String text, boolean selected) {
		super(text, selected);
		this.setIcons();
	}

	public BeeCheckBox(String text, Icon icon, boolean selected) {
		super(text, icon, selected);
		this.setIcons();
	}

	public BeeCheckBox(String text, Icon icon) {
		super(text, icon);
		this.setIcons();
	}

	public BeeCheckBox(String text) {
		super(text);
		this.setIcons();
	}

	private Object userObject;

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	private void setIcons() {
		BeeIcon un = (BeeIcon) BeeConstants.CHECKBOX_UNSELECTED_ICON;
		un.setTopMargin(BeeUIUtils.getDefaultFontSize() / 4);

		BeeIcon d = (BeeIcon) BeeConstants.CHECKBOX_DISABLED_ICON;
		d.setTopMargin(BeeUIUtils.getDefaultFontSize() / 4);

		BeeIcon s = (BeeIcon) BeeConstants.CHECKBOX_SELECTED_ICON;
		s.setTopMargin(BeeUIUtils.getDefaultFontSize() / 4);

		this.setIcon(BeeConstants.CHECKBOX_UNSELECTED_ICON);
		this.setDisabledIcon(BeeConstants.CHECKBOX_DISABLED_ICON);
		this.setSelectedIcon(BeeConstants.CHECKBOX_SELECTED_ICON);
		this.setDisabledSelectedIcon(BeeConstants.CHECKBOX_DISABLED_ICON);

	}

}

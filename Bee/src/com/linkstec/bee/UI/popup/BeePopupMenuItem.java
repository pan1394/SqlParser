package com.linkstec.bee.UI.popup;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.core.fw.editor.BPopItem;

public class BeePopupMenuItem extends JLabel implements BPopItem {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7298908411690767950L;
	private String name;
	private Object value;
	private ImageIcon icon;
	private boolean selected;
	private Color selectedColor = Color.decode("#CDE8E7");
	private Color unselectedColor = BeePopUI.BACK_COLOR;

	public BeePopupMenuItem() {
		this.setOpaque(true);
		setSize(new Dimension(BeeUIUtils.getDefaultFontSize() * 3, BeeUIUtils.getDefaultFontSize() * 2));
		setBorder(new EmptyBorder(3, 3, 3, 3));
		this.setBackground(unselectedColor);
		this.setFocusable(true);
		this.setIcon(BeeConstants.VAR_ICON);
		this.setFont(BeeUIUtils.getDefaultFont());
	}

	@Override
	protected void paintBorder(Graphics g) {

	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		if (this.selected) {
			if (this.getParent() != null) {
				this.setSize(new Dimension(this.getParent().getWidth(), this.getHeight()));
			}
			this.setOpaque(true);
			this.setBackground(selectedColor);
		} else {
			this.setOpaque(false);
			this.setBackground(unselectedColor);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		super.setText(name);
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public ImageIcon getIcon() {
		return icon;
	}

	public void setIcon(ImageIcon icon) {
		super.setIcon(icon);
		this.icon = icon;
	}

}

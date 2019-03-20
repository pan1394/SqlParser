package com.linkstec.bee.UI.look.button;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import com.linkstec.bee.UI.BeeUIUtils;

public class BeeIconButton {
	private ImageIcon icon;
	private Dimension size;
	private int spaceIconTop = 0;
	private int spaceIconLeft = 2;
	private String name;
	private Rectangle bounds;
	private boolean mouseOver = false;
	private Color backColor = Color.decode("#D4E7FC");
	private Color borderColor = Color.decode("#7AB1EB");
	private List<BeeIconButtonAction> actions = new ArrayList<BeeIconButtonAction>();
	private Object userObject;
	private String tipText;
	// for tabbedButton
	private JComponent target;
	public static final int SIZE = (int) (BeeUIUtils.getDefaultFontSize() * 1.6);

	public BeeIconButton(ImageIcon icon) {
		this.icon = icon;
		size = new Dimension(SIZE, SIZE);
		bounds = new Rectangle();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getUserObject() {
		return userObject;
	}

	public String getTipText() {
		return tipText;
	}

	public void setTipText(String tipText) {
		this.tipText = tipText;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	public ImageIcon getIcon() {
		return this.icon;
	}

	public Dimension getSize() {
		return this.size;
	}

	public boolean isMouseOver() {
		return mouseOver;
	}

	public void setMouseOver(boolean mouseOver) {
		this.mouseOver = mouseOver;
	}

	public int getSpaceIconTop() {
		return spaceIconTop;
	}

	public void setSpaceIconTop(int spaceIconTop) {
		this.spaceIconTop = spaceIconTop;
	}

	public int getSpaceIconLeft() {
		return spaceIconLeft;
	}

	public void setSpaceIconLeft(int spaceIconLeft) {
		this.spaceIconLeft = spaceIconLeft;
	}

	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}

	public void setSize(Dimension size) {
		this.size = size;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	public Color getBackColor() {
		return backColor;
	}

	public void setBackColor(Color backColor) {
		this.backColor = backColor;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	public List<BeeIconButtonAction> getActions() {
		return actions;
	}

	public void setActions(List<BeeIconButtonAction> actions) {
		this.actions = actions;
	}

	public void execute(JComponent comp) {
		for (BeeIconButtonAction action : actions) {
			action.execute(comp);
		}
	}

	public JComponent getTarget() {
		return target;
	}

	public void setTarget(JComponent target) {
		this.target = target;
	}
}

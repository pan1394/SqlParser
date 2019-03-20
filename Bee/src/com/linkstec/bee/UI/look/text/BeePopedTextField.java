package com.linkstec.bee.UI.look.text;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.Serializable;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTextField;

import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.popup.BeePopupMenu;

public class BeePopedTextField extends JTextField implements Serializable, FocusListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8283109524849019194L;

	private Object userObject;

	private BeePopupMenu pop;
	private ImageIcon icon;
	private String backgroundText = null;
	private int inclTab = BeeUIUtils.getRoundCornerSize();

	private boolean roundBorder = false;
	private boolean validate = true;

	public BeePopedTextField() {
		this.addFocusListener(this);
		pop = new BeePopupMenu(this);
		this.pop.getContainer().setLayout(new BorderLayout());
		this.setMargin(new Insets(0, 0, 0, 0));
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	public boolean isRoundBorder() {
		return roundBorder;
	}

	public void setRoundBorder(boolean roundBorder) {
		this.roundBorder = roundBorder;
	}

	public String getBackgroundText() {
		return backgroundText;
	}

	@Override
	protected void paintBorder(Graphics g) {
		if (this.roundBorder) {
			g.setColor(Color.LIGHT_GRAY);
			g.drawPolygon(this.getShape(this.getWidth() - 1, this.getHeight() - 1));
		} else {
			super.paintBorder(g);
		}

	}

	private Polygon getShape(int w, int h) {
		int[] xp = new int[] { 0, 0, inclTab, w - inclTab, w, w, w - inclTab, inclTab };
		int[] yp = new int[] { h - inclTab, inclTab, 0, 0, inclTab, h - inclTab, h, h };
		Polygon shape = new Polygon(xp, yp, xp.length);
		return shape;
	}

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	public ImageIcon getIcon() {
		return icon;
	}

	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}

	public void setPopConstants(JComponent comp) {
		this.pop.getContainer().add(comp, BorderLayout.CENTER);
	}

	public void hidePop() {
		this.pop.setVisible(false);
	}

	public void showPop() {
		this.pop.showPop(this.getHeight());
	}

	@Override
	public void focusGained(FocusEvent e) {
		showPop();
	}

	@Override
	public void focusLost(FocusEvent e) {

	}

}

package com.linkstec.bee.UI.look.text;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JLabel;

import com.linkstec.bee.UI.BeeUIUtils;

public class BeeLabel extends JLabel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3965700802788993020L;
	private boolean required = false;

	public BeeLabel() {
		super();
	}

	public BeeLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
	}

	public BeeLabel(Icon image) {
		super(image);
	}

	public BeeLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
	}

	public BeeLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
	}

	public BeeLabel(String text) {
		super(text);
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	Insets insets = new Insets(0, 0, 0, BeeUIUtils.getDefaultFontSize());

	@Override
	public void paint(Graphics g) {

		super.paint(g);
		if (required) {
			g.setColor(Color.RED);
			g.drawString("*", this.getWidth() - BeeUIUtils.getDefaultFontSize(),
					this.getHeight() - this.getFont().getSize());
		}
	}

	@Override
	public Insets getInsets() {
		return insets;
	}
}

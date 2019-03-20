package com.linkstec.bee.UI.look.combox;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComboBox;
import javax.swing.border.EmptyBorder;

import com.linkstec.bee.UI.BeeUIUtils;

public class BeeComBox<E> extends JComboBox<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 83313567209600359L;
	private int margin = BeeUIUtils.getDefaultFontSize() / 3;

	public BeeComBox() {
		super();
		this.init();
	}

	public BeeComBox(E[] items) {

		super(items);
		this.init();
	}

	private void init() {
		this.setOpaque(false);
		this.setFont(BeeUIUtils.getDefaultFont());
		this.getEditor().getEditorComponent().setFont(BeeUIUtils.getDefaultFont());
		this.getEditor().getEditorComponent().setBackground(Color.WHITE);

		this.setBorder(new EmptyBorder(margin, margin, margin, margin));
	}

	@Override
	protected void paintBorder(Graphics g) {
		int width = this.getWidth() - 1;
		int height = this.getHeight() - 1;
		if (this.isEnabled()) {
			g.setColor(Color.WHITE);
			g.fillRoundRect(0, 0, width, height, height, height);
		}
		g.setColor(Color.LIGHT_GRAY);
		g.drawRoundRect(0, 0, width, height, height, height);

	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
	}

}

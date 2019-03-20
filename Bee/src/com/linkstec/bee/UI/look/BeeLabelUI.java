package com.linkstec.bee.UI.look;

import java.awt.Graphics;
import java.awt.Polygon;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.ComponentUI;

import com.linkstec.bee.UI.BeeUIUtils;
import com.sun.java.swing.plaf.windows.WindowsLabelUI;

public class BeeLabelUI extends WindowsLabelUI {
	private int inclTab = BeeUIUtils.getRoundCornerSize();

	public static ComponentUI createUI(JComponent c) {
		return new BeeLabelUI();
	}

	@Override
	protected void paintEnabledText(JLabel l, Graphics g, String s, int textX, int textY) {
		if (l.isOpaque()) {
			g.setColor(l.getParent().getBackground());
			g.fillRect(0, 0, l.getWidth(), l.getHeight());
			g.setColor(l.getBackground());
			g.fillPolygon(this.getShape(0, 0, l.getWidth() - 5, l.getHeight() - 5));
		}
		super.paintEnabledText(l, g, s, textX, textY);
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		super.paint(g, c);
	}

	private Polygon getShape(int x, int y, int w, int h) {

		int[] xp = new int[] { x, x, x + inclTab, x + w - inclTab, x + w, x + w, x + w - inclTab, x + inclTab };
		int[] yp = new int[] { y + h - inclTab, y + inclTab, y, y, y + inclTab, y + h - inclTab, y + h, y + h };
		Polygon shape = new Polygon(xp, yp, xp.length);
		return shape;
	}

}

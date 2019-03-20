package com.linkstec.bee.UI.look;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;

import com.sun.java.swing.plaf.windows.WindowsButtonUI;

public class BeeIconButtonUI extends BasicButtonUI {

	public static ComponentUI createUI(JComponent c) {
		if (c instanceof JButton) {
			c.setOpaque(false);
			return new BeeIconButtonUI();
		} else {
			return new WindowsButtonUI();
		}
	}

	@Override
	protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect,
			Rectangle iconRect) {

	}

	@Override
	public void paint(Graphics g, JComponent c) {

		JButton b = (JButton) c;
		if (b.isOpaque()) {
			b.setOpaque(false);
		}

		super.paint(g, c);
	}

}

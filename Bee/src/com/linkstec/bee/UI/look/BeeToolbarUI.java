package com.linkstec.bee.UI.look;

import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.linkstec.bee.UI.BeeConstants;
import com.sun.java.swing.plaf.windows.WindowsToolBarUI;

public class BeeToolbarUI extends WindowsToolBarUI {
	public static ComponentUI createUI(JComponent c) {
		return new BeeToolbarUI();
	}

	@Override
	public void paint(Graphics g, JComponent c) {

		Graphics2D g2d = (Graphics2D) g;
		GradientPaint grdp = new GradientPaint(0, 0, BeeConstants.TOOLBAR_GREDIENT_UP, 0, c.getHeight(),
				BeeConstants.TOOLBAR_GREDIENT_DOWN);
		g2d.setPaint(grdp);
		g2d.fillRect(0, 0, c.getWidth(), c.getHeight());
		super.paint(g, c);
	}

}

package com.linkstec.bee.UI.look;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;

import com.linkstec.bee.UI.BeeConstants;
import com.sun.java.swing.plaf.windows.WindowsButtonUI;

public class BeeButtonUI extends BasicButtonUI {

	public static ComponentUI createUI(JComponent c) {
		if (c instanceof JButton) {
			c.setOpaque(false);
			return new BeeButtonUI();
		} else {
			return new WindowsButtonUI();
		}
	}

	@Override
	protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect,
			Rectangle iconRect) {

	}

	@Override
	protected void paintButtonPressed(Graphics g, AbstractButton c) {
		Graphics2D g2 = (Graphics2D) g;

		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.addRenderingHints(rh);

		Color upcolor = BeeConstants.TOOLBAR_GREDIENT_DOWN;
		Color color = BeeConstants.TOOLBAR_GREDIENT_UP;
		if (!c.isEnabled()) {
			c.setForeground(Color.LIGHT_GRAY);
		} else {
			c.setForeground(Color.BLACK);
		}

		GradientPaint gp = new GradientPaint(0, 0, upcolor, 0, c.getHeight(), color);

		g2.setPaint(gp);
		g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), c.getHeight(), c.getHeight());
		g2.setColor(Color.LIGHT_GRAY);
		g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, c.getHeight(), c.getHeight());
		super.paintButtonPressed(g2, c);
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		Graphics2D g2 = (Graphics2D) g;

		JButton b = (JButton) c;
		if (b.isOpaque()) {
			b.setOpaque(false);
		}

		Color upcolor = BeeConstants.TOOLBAR_GREDIENT_UP;
		Color color = BeeConstants.TOOLBAR_GREDIENT_DOWN;
		if (!b.isEnabled()) {
			b.setForeground(Color.LIGHT_GRAY);
		} else {
			b.setForeground(Color.BLACK);
		}

		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.addRenderingHints(rh);

		GradientPaint gp = new GradientPaint(0, 0, upcolor, 0, c.getHeight(), color);

		g2.setPaint(gp);
		g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), c.getHeight(), c.getHeight());
		g2.setColor(Color.LIGHT_GRAY);
		g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, c.getHeight(), c.getHeight());

		super.paint(g, c);
	}

	protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text) {
		if (b.isEnabled()) {
			Rectangle rect = textRect;
			rect.x = rect.x + 1;
			rect.y = rect.y + 1;
			b.setForeground(Color.LIGHT_GRAY);
			paintText(g, (JComponent) b, rect, text);
			b.setForeground(Color.BLACK);
		}
		paintText(g, (JComponent) b, textRect, text);
	}

}

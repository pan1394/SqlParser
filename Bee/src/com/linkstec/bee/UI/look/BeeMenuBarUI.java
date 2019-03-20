package com.linkstec.bee.UI.look;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.sun.java.swing.plaf.windows.WindowsMenuBarUI;

public class BeeMenuBarUI extends WindowsMenuBarUI {
	public static ComponentUI createUI(JComponent c) {
		return new BeeMenuBarUI();
	}

	public void paint(Graphics g, JComponent c) {
		int height = c.getHeight();
		int width = c.getWidth();
		Color up = Color.decode("#F7F9FD");

		// CCCCCC
		Color down = Color.decode("#F4F7FC");

		// B0B0B0
		Graphics2D g2d = (Graphics2D) g;
		GradientPaint grdp = new GradientPaint(0, 0, up, 0, height, down);
		g2d.setPaint(grdp);

		g2d.fillRect(0, 0, width, height);
	}
}

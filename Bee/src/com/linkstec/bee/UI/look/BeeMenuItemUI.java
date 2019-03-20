package com.linkstec.bee.UI.look;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;

import com.sun.java.swing.plaf.windows.WindowsGraphicsUtils;
import com.sun.java.swing.plaf.windows.WindowsMenuItemUI;

public class BeeMenuItemUI extends WindowsMenuItemUI {

	public static ComponentUI createUI(JComponent c) {
		return new BeeMenuItemUI();
	}

	protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text) {

		ButtonModel model = menuItem.getModel();
		Color oldColor = g.getColor();
		// g.setColor(Color.LIGHT_GRAY);
		// WindowsGraphicsUtils.paintText(g, menuItem, textRect, text, 0);

		if (model.isEnabled() && (model.isArmed() || (menuItem instanceof JMenu && model.isSelected()))) {
			g.setColor(selectionForeground); // Uses protected field.
		}
		// textRect.x = textRect.x - 1;
		// textRect.y = textRect.y - 1;
		WindowsGraphicsUtils.paintText(g, menuItem, textRect, text, 0);

		g.setColor(oldColor);
	}

}

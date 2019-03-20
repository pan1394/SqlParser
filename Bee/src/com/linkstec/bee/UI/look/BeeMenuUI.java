package com.linkstec.bee.UI.look;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;

import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;
import javax.swing.plaf.ComponentUI;

import com.sun.java.swing.plaf.windows.WindowsMenuUI;;

public class BeeMenuUI extends WindowsMenuUI {
	public static ComponentUI createUI(JComponent c) {
		return new BeeMenuUI();
	}

	@Override
	protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
		boolean test = true;
		if (!test) {
			super.paintBackground(g, menuItem, bgColor);
			return;
		}
		JMenu menu = (JMenu) menuItem;
		ButtonModel model = menu.getModel();
		int menuWidth = menu.getWidth();
		int menuHeight = menu.getHeight();
		// if (menu.isOpaque()) {
		boolean paint = false;
		if (model.isArmed() || model.isSelected()) {
			paint = true;
		} else if (model.isRollover()) {
			paint = true;
		}
		// menu.setForeground(Color.BLACK);

		if (paint) {
			// Only paint rollover if no other menu on menubar is selected
			boolean otherMenuSelected = false;
			Container s = menu.getParent();
			if (s instanceof JMenuBar) {
				MenuElement[] menus = ((JMenuBar) s).getSubElements();
				for (int i = 0; i < menus.length; i++) {
					if (((JMenuItem) menus[i]).isSelected()) {
						otherMenuSelected = true;
						break;
					}
				}

				if (!otherMenuSelected) {
					// menu.setForeground(Color.WHITE);
					// Draw a raised bevel border
					g.setColor(Color.decode("#C6E8FF"));
					g.fillRoundRect(0, menuHeight / 4, menuWidth, menuHeight * 2, menuHeight / 4, menuHeight / 4);

				}
			}
		}

	}

}

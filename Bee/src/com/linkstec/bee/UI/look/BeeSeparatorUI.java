package com.linkstec.bee.UI.look;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.plaf.ComponentUI;

import com.linkstec.bee.UI.BeeUIUtils;
import com.sun.java.swing.plaf.windows.WindowsSeparatorUI;

public class BeeSeparatorUI extends WindowsSeparatorUI {
	public static ComponentUI createUI(JComponent c) {

		return new BeeSeparatorUI();
	}

	@Override
	public void paint(Graphics g, JComponent c) {

		Dimension s = c.getSize();

		if (((JSeparator) c).getOrientation() == JSeparator.VERTICAL) {
			g.setColor(Color.LIGHT_GRAY);
			int w = BeeUIUtils.getDefaultFontSize() / 5;
			int offset = w * 2;
			int y = BeeUIUtils.getDefaultFontSize() / 6;
			int height = s.height - y * 2;
			int count = (int) (height / (w * 1.3));

			for (int i = 0; i < count; i++) {
				g.fill3DRect(offset + 2, y + (int) (w * i * 1.5), w, w, true);
			}

			g.setColor(c.getForeground());
			g.drawLine(offset + w * 2, y, offset + w * 2, height);

			g.setColor(c.getBackground());
			g.drawLine(offset + w * 2 + 1, y, offset + w * 2 + 1, height);

		} else // HORIZONTAL
		{
			g.setColor(c.getForeground());
			g.drawLine(0, 0, s.width, 0);

			g.setColor(c.getBackground());
			g.drawLine(0, 1, s.width, 1);
		}
	}

}

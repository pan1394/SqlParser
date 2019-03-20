package com.linkstec.bee.UI.look;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.look.button.BeeCheckBox;
import com.sun.java.swing.plaf.windows.WindowsCheckBoxUI;
import com.sun.java.swing.plaf.windows.WindowsGraphicsUtils;

public class BeeCheckButtonUI extends WindowsCheckBoxUI {
	private JComponent c;

	public BeeCheckButtonUI(JComponent c) {
		this.c = c;
	}

	public static ComponentUI createUI(JComponent c) {
		c.setOpaque(false);
		c.setFont(BeeUIUtils.getDefaultFont());
		return new BeeCheckButtonUI(c);
	}

	protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text) {
		int off = getTextShiftOffset();
		if (c instanceof BeeCheckBox) {
			BeeCheckBox bee = (BeeCheckBox) c;
			textRect.x = textRect.x + c.getHeight() / 2;
			g.setClip(textRect.x, textRect.y, textRect.width, textRect.height);
			if (bee.getUserIcon() != null) {
				Icon icon = bee.getUserIcon();
				icon.paintIcon(bee, g, textRect.x, (int) (textRect.y));
				int add = (int) (icon.getIconWidth() * 1.2);
				textRect.x = (int) (textRect.x + add);
				Rectangle rect = g.getClipBounds();
				rect.width = rect.width + add;
				g.setClip(rect.x, rect.y, rect.width, rect.height);

			}
		}
		WindowsGraphicsUtils.paintText(g, b, textRect, text, off);
	}

}

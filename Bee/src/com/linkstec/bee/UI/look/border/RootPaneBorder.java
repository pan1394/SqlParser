package com.linkstec.bee.UI.look.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.border.EmptyBorder;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;

public class RootPaneBorder extends EmptyBorder {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2506256681460606345L;

	private static int width = BeeUIUtils.getDefaultFontSize();

	public RootPaneBorder(Color color) {
		super(width, width, width, width);
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
		Color color = BeeConstants.TOOLBAR_GREDIENT_DOWN;

		Graphics2D g2 = (Graphics2D) g;
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.addRenderingHints(rh);
		int width = RootPaneBorder.width * 2;
		x = x + width - 1;
		y = y + width - 1;
		w = w - width * 2 + 2;
		h = h - width * 2 + 2;
		int arc = BeeUIUtils.getDefaultFontSize();

		for (int i = 0; i < width; i++) {
			g.setColor(color);
			RoundRectangle2D rect = new RoundRectangle2D.Double(x, y, w, h / 2, arc, arc);
			Rectangle2D downRect = new Rectangle2D.Double(x, y + h / 3, w, h * 2 / 3);
			Area area = new Area(rect);
			Area downarea = new Area(downRect);
			area.add(downarea);
			g2.draw(area);
			color = BeeUIUtils.brighter(color, width / 80, 255 * (width - i) / width);
			x--;
			y--;
			w = w + 2;
			h = h + 2;
		}

		g.setColor(Color.LIGHT_GRAY);

		int titlePaneHeight = BeeUIUtils.getDefaultFontSize() * 2;
		RoundRectangle2D rect = new RoundRectangle2D.Double(RootPaneBorder.width - 1, RootPaneBorder.width - 1,
				c.getWidth() - RootPaneBorder.width - RootPaneBorder.width + 1,
				c.getHeight() - titlePaneHeight - RootPaneBorder.width, arc, arc);
		Rectangle2D downRect = new Rectangle2D.Double(RootPaneBorder.width - 1, titlePaneHeight + RootPaneBorder.width,
				c.getWidth() - RootPaneBorder.width - RootPaneBorder.width + 1,
				c.getHeight() - titlePaneHeight - RootPaneBorder.width - RootPaneBorder.width);
		Area area = new Area(rect);
		Area downarea = new Area(downRect);
		area.add(downarea);

		g2.draw(area);

	}

}

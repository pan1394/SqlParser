package com.linkstec.bee.UI.look;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

import com.linkstec.bee.UI.BeeUIUtils;

//import com.sun.java.swing.plaf.windows.WindowsScrollBarUI;

public class BeeScrollBarUI extends BasicScrollBarUI {
	private static Color backColor = Color.decode("#F8F8F8");

	public static ComponentUI createUI(JComponent c) {

		return new BeeScrollBarUI();
	}

	protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {

		g.setColor(backColor);
		g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);

		if (trackHighlight == DECREASE_HIGHLIGHT) {
			paintDecreaseHighlight(g);
		} else if (trackHighlight == INCREASE_HIGHLIGHT) {
			paintIncreaseHighlight(g);
		}
	}

	protected JButton createDecreaseButton(int orientation) {
		return new ActionButton(false, scrollbar.getOrientation() == JScrollBar.VERTICAL);
	}

	protected JButton createIncreaseButton(int orientation) {
		return new ActionButton(true, scrollbar.getOrientation() == JScrollBar.VERTICAL);
	}

	protected void paintDecreaseHighlight(Graphics g) {

	}

	protected void paintIncreaseHighlight(Graphics g) {

	}

	protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
		if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
			return;
		}

		int w = thumbBounds.width;
		int h = thumbBounds.height;

		g.translate(thumbBounds.x, thumbBounds.y);
		Color color = Color.decode("#C0C1D1");
		g.setColor(color);

		Color cc = new Color(color.getRed(), color.getGreen(), color.getBlue(), 20);
		g.setColor(cc);

		int min = Math.min(w, h) / 2;

		if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {

			int width = w / 2;

			BeeUIUtils.fillTextureHorizonalRoundRec((Graphics2D) g, color, (w - width) / 2, 0, width, h, width, width);
			g.setColor(Color.LIGHT_GRAY);
			g.drawRoundRect((w - width) / 2, 0, width, h - 1, min, min);
		} else {
			int height = h / 2;
			BeeUIUtils.fillTextureRoundRec((Graphics2D) g, color, 0, (h - height) / 2, w, height, height, height);

			g.drawRoundRect(0, (h - height) / 2, w, height, min, min);
		}

		g.translate(-thumbBounds.x, -thumbBounds.y);
	}

	public static class ActionButton extends JButton {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4882494082047816088L;
		private boolean left;
		private boolean vertical;

		public ActionButton(boolean left, boolean vertical) {
			this.left = left;
			this.vertical = vertical;

			int size = UIManager.getInt("ScrollBar.width");
			if (size <= 0) {
				size = 16;
			}

			this.setPreferredSize(new Dimension(size, size));
			this.setSize(this.getPreferredSize());
		}

		@Override
		public void paint(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			int x = 0;
			int y = 0;
			int width = this.getWidth();
			int height = this.getHeight();

			BasicStroke stroke = new BasicStroke(BeeUIUtils.getDefaultFontSize() / 7);
			g.setColor(backColor);
			g2d.fillRect(x, y, width, height);

			g.setColor(Color.GRAY);
			g2d.setStroke(stroke);

			if (vertical) {
				x = width / 3;
				y = height * 2 / 7;
				height = (int) (height - y * 2.5);
				width = width - x * 2;
				if (left) {
					g2d.drawLine(x, y, x + width / 2, y + height);
					g2d.drawLine(x + width, y, x + width / 2, y + height);
				} else {
					g2d.drawLine(x, y + height, x + width / 2, y);
					g2d.drawLine(x + width, y + height, x + width / 2, y);
				}
			} else {
				x = width * 2 / 7;
				y = height * 3 / 9;
				width = (int) (width - x * 1.5);
				height = height - y * 2;
				if (!left) {
					g2d.drawLine(width, y, x, y + height / 2);
					g2d.drawLine(width, y + height, x, y + height / 2);

				} else {
					g2d.drawLine(x, y, width, y + height / 2);
					g2d.drawLine(x, y + height, width, y + height / 2);
				}
			}
		}

	}
}

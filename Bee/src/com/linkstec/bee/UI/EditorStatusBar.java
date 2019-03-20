package com.linkstec.bee.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class EditorStatusBar extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -333337563087323550L;
	private JLabel info;
	private JLabel progress;
	private JPanel contents;

	public EditorStatusBar() {
		this.setBackground(BeeConstants.BACKGROUND_COLOR);
		setPreferredSize(new Dimension(0, BeeUIUtils.getDefaultFontSize() * 2));

		setLayout(new BorderLayout());

		JPanel left = new JPanel();
		FlowLayout llayout = new FlowLayout();
		llayout.setAlignment(FlowLayout.LEFT);
		left.setLayout(llayout);
		left.setOpaque(false);

		info = new JLabel();
		info.setPreferredSize(new Dimension(BeeUIUtils.getDefaultFontSize() * 50, BeeUIUtils.getDefaultFontSize() * 2));
		left.add(info);
		left.add(new Memory());

		add(left, BorderLayout.WEST);

		contents = new JPanel();
		contents.setOpaque(false);
		add(contents, BorderLayout.CENTER);
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.RIGHT);
		contents.setLayout(layout);

		progress = new JLabel();
		add(progress, BorderLayout.EAST);
	}

	public void setMessag(String text) {
		info.setText(text);
	}

	public void startProgress(String text) {

		progress.setText(text);
		progress.setIcon(BeeConstants.PROGRESS_ICON);

	}

	public void endProgress() {

		progress.setText(null);
		progress.setIcon(null);

	}

	public static class Memory extends JPanel implements Runnable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		int width = BeeUIUtils.getDefaultFontSize() * 5;

		public Memory() {
			this.setOpaque(false);
			this.setPreferredSize(new Dimension(width, (int) (BeeUIUtils.getDefaultFontSize())));
			new Thread(this).start();
		}

		@Override
		public void run() {
			while (true) {

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				this.repaint();
			}
		}

		@Override
		public void paint(Graphics g) {
			int w = this.getWidth();
			int h = this.getHeight();

			// g.fillRect(0, 0, w, h);

			BeeUIUtils.fillTextureRoundRec((Graphics2D) g, Color.LIGHT_GRAY, 0, 0, w, h, h, h);
			// g.setColor(Color.gray);

			long total = Runtime.getRuntime().totalMemory();
			long free = Runtime.getRuntime().freeMemory();

			int width = (int) (w * (total - free) / total);
			Color color = BeeConstants.BACKGROUND_COLOR;

			RoundRectangle2D rect = new RoundRectangle2D.Double(0, 0, w, h, h, h);
			Rectangle2D remove = new Rectangle2D.Double(width, 0, w - width, h);
			Area area = new Area(rect);
			area.subtract(new Area(remove));
			Graphics2D gg = (Graphics2D) g;

			BeeUIUtils.setAntiAliasing(gg, true);

			GradientPaint gp = new GradientPaint(0, 0, BeeUIUtils.getColor(color, h, h, h), 0, h, color);
			gg.setPaint(gp);
			gg.fill(area);

			if (this.getMousePosition() != null) {

				String t = "" + total / 1000 / 1000 + "M";
				String f = "" + (total - free) / 1000 / 1000 + "M";
				g.setColor(Color.GRAY);
				g.setFont(BeeUIUtils.getDefaultFont().deriveFont((float) (h * 0.7)));
				g.drawString(f + "/" + t, h / 2, g.getFontMetrics().getAscent());
			}

		}

	}
}

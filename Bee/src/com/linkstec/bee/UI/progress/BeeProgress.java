package com.linkstec.bee.UI.progress;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;

public class BeeProgress extends JPanel implements IBeeProgress {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4171831111507887892L;

	private double d;
	private Color color = Color.GREEN.darker();

	private long start = -1;

	public BeeProgress() {
		this.setOpaque(false);
	}

	public void setValue(double d) {

		if (d == 0) {
			start = System.currentTimeMillis();
		} else {
			if (d < this.d) {
				return;
			}
		}

		this.d = d;
		this.repaint();
	}

	public double getValue() {
		return d;
	}

	@Override
	public void paint(Graphics g) {
		int width = this.getWidth();
		int height = this.getHeight();
		long span = -1;
		if (start > 0) {
			span = System.currentTimeMillis() - start;
		}
		if (d > 0) {
			g.setColor(BeeConstants.BACKGROUND_COLOR);
			g.fillRect(0, 0, width, height);

			g.setColor(color);

			int p = (int) (width * d);
			g.fillRect(0, 0, p, height);

			g.setFont(BeeUIUtils.getDefaultFont());

			String s = d * 100 + "";
			if (s.indexOf(".") > 0) {
				s = s.substring(0, s.indexOf("."));
			}
			s = s + "%";
			if (span > 0) {
				long remain = (long) (span / d - span);
				long mi = remain / 1000;

				long spent = span / 1000;
				String sS = spent + "秒";
				if (spent > 60) {
					spent = spent / 60;
					sS = spent + "分" + spent % 60 + "秒";
				}

				String left = "";
				if (mi > 60) {
					long second = mi / 60;
					if (second > 60) {
						left = second / 60 + "時間";
						left = left + second % 60 + "分";
					} else {
						left = second + "分";
					}
				} else {
					left = remain / 1000 + "秒";
				}

				s = s + "(" + sS + "経過、残" + left + ")";
			}
			s = s + " ";
			int swidth = g.getFontMetrics().stringWidth(s);
			if (swidth > p) {
				g.setColor(Color.LIGHT_GRAY);
				g.drawString(s, p, g.getFontMetrics().getAscent());
			} else {
				g.setColor(Color.WHITE);
				g.drawString(s, p - swidth, g.getFontMetrics().getAscent());
			}
		}
	}

}

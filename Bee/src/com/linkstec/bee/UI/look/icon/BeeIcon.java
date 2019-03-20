package com.linkstec.bee.UI.look.icon;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;

public class BeeIcon extends ImageIcon implements Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4373812060218160651L;

	private static ImageIcon errorIcon = new ImageIcon(BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/error.gif"));
	private static ImageIcon alertIcon = new ImageIcon(BeeConstants.class.getResource("/com/linkstec/bee/UI/images/icons/alert_obj.gif"));
	private int topMargin = 0;
	private int leftMargin = 0;

	public BeeIcon(URL url) {
		super(url);
	}

	public int getTopMargin() {
		return topMargin;
	}

	public void setTopMargin(int topMargin) {
		this.topMargin = topMargin;
	}

	public BeeIcon(Image img, int width, int height) {
		super(img);
		this.setImage(this.getImage().getScaledInstance(width, height, Image.SCALE_FAST));
	}

	public BeeIcon(URL url, int width, int height) {

		super(url);
		this.setImage(this.getImage().getScaledInstance(width, height, Image.SCALE_FAST));

	}

	private int imageHeight = (int) (BeeUIUtils.getDefaultFontSize());
	private double scale = imageHeight / 11;
	private Image img;

	public void setLeftMargin(int leftMargin) {
		this.leftMargin = leftMargin;
	}

	@Override
	public synchronized void paintIcon(Component c, Graphics g, int x, int y) {

		if (scale == 0) {
			scale = 1;
		}
		Image img = this.getImage();
		int w = img.getWidth(c);
		int h = img.getHeight(c);
		int my = 0;

		boolean error = false;
		boolean alert = false;
		if (c instanceof JLabel) {
			JLabel label = (JLabel) c;
			if (label.getName() != null && label.getName().equals("ERROR")) {
				error = true;
			} else if (label.getName() != null && label.getName().equals("ALERT")) {
				alert = true;
			}

			int height = label.getHeight();

			my = (int) ((height - h * scale) / 2);
		} else {
			if (h < 16) {
				my = (int) ((y + (imageHeight - h * scale) / 2));
				if (my < 0) {
					my = 0;
				}
			} else {
				my = (int) (my + 1 * scale);
			}
		}
		int iw = (int) (w * scale);
		int ih = (int) (h * scale);
		my = topMargin + my;
		x = x + this.leftMargin;
		g.drawImage(this.getImage(), x, my, iw, ih, c);
		if (error) {
			g.drawImage(errorIcon.getImage(), x - iw / 20, my + ih / 2, iw / 2, ih / 2, c);
		} else if (alert) {
			g.drawImage(alertIcon.getImage(), x - iw / 20, my + ih / 2, iw / 2, ih / 2, c);
		}

	}

	@Override
	public int getIconWidth() {
		return (int) (16 * scale);
	}

	@Override
	public Image getImage() {
		return super.getImage();
	}

	@Override
	public int getIconHeight() {
		return (int) (16 * scale);
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}

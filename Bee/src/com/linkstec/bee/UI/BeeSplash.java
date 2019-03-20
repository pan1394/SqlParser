package com.linkstec.bee.UI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;

public class BeeSplash extends JWindow {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6524772826691625791L;
	private Image image = null;

	public BeeSplash() {
		// this.setOpacity(0.9f);
		ImageIcon icon = new ImageIcon(BeeConstants.class.getResource("/com/linkstec/bee/UI/images/splash.png"));
		JLabel label = new JLabel();
		label.setIcon(icon);
		this.setSize(new Dimension(500, 250));
		// this.setSize(new Dimension(BeeUIUtils.getDefaultFontSize() * 25,
		// BeeUIUtils.getDefaultFontSize() * 25));
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(label, BorderLayout.CENTER);
	}

	@Override
	public void paint(Graphics g) {
		// BeeUIUtils.fillTextureRoundRec((Graphics2D) g, BeeConstants.BACKGROUND_COLOR,
		// 0, 0, this.getWidth(),
		// this.getHeight(), this.getHeight(), this.getHeight());
		super.paint(g);
	}

}

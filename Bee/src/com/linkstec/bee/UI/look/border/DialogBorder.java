package com.linkstec.bee.UI.look.border;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.border.EmptyBorder;

import com.linkstec.bee.UI.BeeUIUtils;

public class DialogBorder extends EmptyBorder {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2506256681460606345L;

	private int width = 0;

	public DialogBorder(int width) {
		super(0, 0, width, width);
		this.width = width;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
		BeeUIUtils.drawRectShadow(g, x, y, w, h, width);

	}

}

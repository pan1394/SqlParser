package com.linkstec.bee.UI.look;

import java.awt.Graphics;

import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.sun.java.swing.plaf.windows.WindowsSplitPaneDivider;

public class BeeDivider extends WindowsSplitPaneDivider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6116574852208835591L;

	public BeeDivider(BasicSplitPaneUI ui) {
		super(ui);
		this.removeAll();
		this.setDividerSize(BeeUIUtils.getDefaultFontSize() / 4);
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(BeeConstants.BACKGROUND_COLOR);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

	}

	@Override
	public Border getBorder() {
		return null;
	}

	@Override
	public void setBorder(Border border) {

	}

}

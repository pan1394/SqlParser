package com.linkstec.bee.UI.look;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;

import com.sun.java.swing.plaf.windows.WindowsSplitPaneUI;;

public class BeeSpliteUI extends WindowsSplitPaneUI {

	public static ComponentUI createUI(JComponent c) {
		c.setBorder(null);
		return new BeeSpliteUI();
	}

	@Override
	public void paint(Graphics g, JComponent jc) {
		super.paint(g, jc);
	}

	@Override
	public BasicSplitPaneDivider createDefaultDivider() {
		return new BeeDivider(this);
	}

}

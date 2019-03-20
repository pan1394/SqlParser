package com.linkstec.bee.UI.look;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.linkstec.bee.UI.BeeUIUtils;
import com.sun.java.swing.plaf.windows.WindowsRadioButtonUI;

public class BeeRadioButtonUI extends WindowsRadioButtonUI {
	public BeeRadioButtonUI() {

	}

	public static ComponentUI createUI(JComponent c) {
		c.setOpaque(false);
		c.setFont(BeeUIUtils.getDefaultFont());
		return new BeeRadioButtonUI();
	}

}

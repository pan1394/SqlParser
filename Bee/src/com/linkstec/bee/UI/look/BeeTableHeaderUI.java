package com.linkstec.bee.UI.look;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.sun.java.swing.plaf.windows.WindowsTableHeaderUI;

public class BeeTableHeaderUI extends WindowsTableHeaderUI {

	public static ComponentUI createUI(JComponent c) {
		return new BeeTableHeaderUI();
	}

}

package com.linkstec.bee.UI.look;

import java.awt.Container;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.sun.java.swing.plaf.windows.WindowsOptionPaneUI;

import sun.swing.DefaultLookup;;

public class BeeOptionPaneUI extends WindowsOptionPaneUI {
	private static int margin = BeeUIUtils.getDefaultFontSize() * 3;

	public static ComponentUI createUI(JComponent c) {
		return new BeeOptionPaneUI();
	}

	@Override
	protected Container createMessageArea() {

		Container c = super.createMessageArea();
		if (c instanceof JPanel) {
			JPanel pane = (JPanel) c;
			pane.setBorder(new EmptyBorder(margin, margin, margin, margin));
		}
		return c;
	}

	protected Icon getIconForType(int messageType) {
		if (messageType < 0 || messageType > 3)
			return null;
		String propertyName = null;
		switch (messageType) {
		case 0:
			return BeeConstants.ERROR_ICON;

		case 1:
			return BeeConstants.PROPERTY_ICON;
		case 2:
			return BeeConstants.WARNING_ICON;
		case 3:
			propertyName = "OptionPane.questionIcon";
			break;
		}
		if (propertyName != null) {
			return (Icon) DefaultLookup.get(optionPane, this, propertyName);
		}
		return null;
	}

}

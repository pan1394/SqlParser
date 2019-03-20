package com.linkstec.bee.UI.look;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;

public class BeeLook {
	static {
		try {
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
			UIManager.setLookAndFeel(new BeeLookAndFeel());
		} catch (Exception e) {
			e.printStackTrace();
		}
		addUI("TreeUI", BeeTreeUI.class.getName());
		addUI("TabbedPaneUI", BeeTabbedPaneUI.class.getName());
		addUI("SplitPaneUI", BeeSpliteUI.class.getName());
		addUI("ToolBarUI", BeeToolbarUI.class.getName());
		addUI("RadioButtonUI", BeeRadioButtonUI.class.getName());
		addUI("CheckBoxUI", BeeCheckButtonUI.class.getName());
		addUI("ToolBarSeparatorUI", BeeSeparatorUI.class.getName());
		addUI("ScrollBarUI", BeeScrollBarUI.class.getName());
		addUI("MenuBarUI", BeeMenuBarUI.class.getName());
		addUI("MenuItemUI", BeeMenuItemUI.class.getName());
		addUI("RootPaneUI", BeeRootPaneUI.class.getName());
		addUI("OptionPaneUI", BeeOptionPaneUI.class.getName());
		addUI("FileChooserUI", BeeFileChooserUI.class.getName());
		// FileChooserUI
	}

	private static void addUI(String uiName, String uiClassName) {
		UIManager.getDefaults().put(uiName, uiClassName);
		UIManager.getLookAndFeelDefaults().put(uiName, uiClassName);
	}
}

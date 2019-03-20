package com.linkstec.bee.UI.look.tab;

import javax.swing.JTabbedPane;

public interface BeeTabClosingListener {
	/**
	 * @param aTabIndex
	 *            the index of the tab that is about to be closed
	 * @return true if the tab can be really closed
	 */
	public boolean tabClosing(int index, JTabbedPane pane);

	/**
	 * @param aTabIndex
	 *            the index of the tab that is about to be closed
	 * @return true if the tab should be selected before closing
	 */
	public boolean selectTabBeforeClosing(int aTabIndex, JTabbedPane pane);
}

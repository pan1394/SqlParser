package com.linkstec.bee.UI.spective.detail.edit;

import com.linkstec.bee.UI.popup.BeePopupTreeMenu;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.core.fw.BValuable;

public abstract class TypeAction implements ValueAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8111761145139977694L;

	public boolean canAct(BValuable value) {
		return true;
	}

	public void afterActFalse(BeePopupTreeMenu menu, BeeGraphSheet sheet) {

	}
}

package com.linkstec.bee.UI.spective.detail.edit;

import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;

public abstract class BDropAction implements DropAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7911980138466914580L;

	@Override
	public BasicNode beforeDrop(BasicNode source, BeeGraphSheet sheet) {
		return null;
	}

}

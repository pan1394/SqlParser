package com.linkstec.bee.UI.spective.basic.logic.edit;

import com.linkstec.bee.UI.spective.basic.logic.BasicModel;
import com.linkstec.bee.UI.spective.basic.logic.model.BGroupLogic;
import com.linkstec.bee.UI.spective.basic.logic.node.BDesignHeader;
import com.linkstec.bee.UI.spective.basic.logic.node.BGroupNode;
import com.linkstec.bee.core.fw.basic.BPath;
import com.mxgraph.model.mxCell;

public class BPatternModel extends BasicModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3868799747581586863L;
	protected BPath actionPath;

	public BPatternModel(BPath path) {
		super(((BActionModel) path.getAction()).getSubSystem());
		this.actionPath = path;
		BActionModel action = (BActionModel) path.getAction();
		this.setName(action.getName());
		BDesignHeader header = new BDesignHeader(((BActionModel) path.getAction()).getSubSystem(), this);
		mxCell cell = (mxCell) this.getRoot();
		cell.insert(header);

	}

	public BPath getActionPath() {
		return this.actionPath;
	}

	@Override
	public BGroupNode getGroupNode() {
		BGroupLogic logic = new BGroupLogic(this.actionPath);
		return (BGroupNode) logic.getPath().getCell();
	}
}

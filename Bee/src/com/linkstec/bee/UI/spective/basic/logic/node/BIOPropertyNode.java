package com.linkstec.bee.UI.spective.basic.logic.node;

import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants.ProcessType;
import com.linkstec.bee.UI.spective.basic.logic.model.NewLayerClassLogic;

public class BIOPropertyNode extends BActionPropertyNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4088100912741921577L;

	public BIOPropertyNode(NewLayerClassLogic logic) {
		super(logic);

	}

	@Override
	public ProcessType getDetailEditName(BasicLogicSheet sheet) {
		return ProcessType.TYPE_TABLE;
	}

}

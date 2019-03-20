package com.linkstec.bee.UI.spective.basic.logic.model.data;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.logic.node.BLogicNode;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.impl.basic.BasicLogic;

public class BasicDataLogic extends BasicLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5210034308646425127L;

	protected BVariable var;
	protected BPath parent;

	protected BasicDataLogic(BPath parent, BVariable var) {

		super(parent, null);
		this.var = var;
		this.parent = parent;

		BLogicNode node = new BLogicNode(this);
		this.getPath().setCell(node);

	}

	public BVariable getData() {
		return var;
	}

	@Override
	public List<BParameter> getOutputs() {
		List<BParameter> outputs = new ArrayList<BParameter>();
		return outputs;
	}

}
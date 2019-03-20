package com.linkstec.bee.UI.spective.basic.tree;

import com.linkstec.bee.UI.spective.basic.logic.edit.BActionModel;
import com.linkstec.bee.UI.spective.basic.logic.node.BLogicNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BProcessTypeNode;
import com.linkstec.bee.UI.spective.detail.tree.ValueNode;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.mxgraph.model.mxCell;

public class BasicTreeNode extends ValueNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2213154447252667113L;

	private BActionModel model;
	private int layer;

	@Override
	public mxCell getTransferNode() {
		Object obj = this.getUserObject();
		if (obj instanceof BLogic) {
			BLogic logic = (BLogic) obj;

			if (logic.getEditor() == null) {
				BLogicNode node = new BLogicNode(logic);
				return node;
			}
		} else if (obj instanceof BProcessTypeNode) {
			BProcessTypeNode node = (BProcessTypeNode) obj;
			return node;
		}
		return null;
	}

	public BActionModel getModel() {
		return model;
	}

	public void setModel(BActionModel model) {
		this.model = model;
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

}

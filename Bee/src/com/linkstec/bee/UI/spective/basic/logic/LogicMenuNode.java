package com.linkstec.bee.UI.spective.basic.logic;

import com.linkstec.bee.UI.look.tree.BeeTreeNode;
import com.linkstec.bee.UI.node.view.ObjectNode;
import com.linkstec.bee.core.fw.editor.BProject;
import com.mxgraph.model.mxCell;

public class LogicMenuNode extends BeeTreeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 660493183657083064L;

	public LogicMenuNode(BProject data) {
		super(data);
		this.setProject(data);
	}

	@Override
	public mxCell getTransferNode() {
		Object obj = this.getUserObject();
		if (obj instanceof mxCell) {
			return (mxCell) obj;
		} else if (obj instanceof LogicMenuMessage) {
			ObjectNode node = new ObjectNode();
			node.setValue(obj);
			return node;
		}
		return null;
	}

}

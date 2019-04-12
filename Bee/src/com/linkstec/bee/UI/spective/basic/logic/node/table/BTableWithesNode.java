package com.linkstec.bee.UI.spective.basic.logic.node.table;

import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.core.fw.basic.BPath;

public class BTableWithesNode extends BTableTargetTablesNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -27584986572380753L;

	public BTableWithesNode(BPath path) {
		super(path);
		this.setTitle("WITH");
		this.setDeletable(true);
	}

	protected String getSqlTitle() {
		return "WITH ";
	}

	protected String getSqlTitleExp() {
		return "WITH ";
	}

	@Override
	public void childAdded(BNode node, BasicLogicSheet sheet) {
		if (node instanceof BTableWithSelectNode) {
			super.childAdded(node, sheet);
		} else {
			node.removeFromParent();
		}
	}

	@Override
	public int getSQLPriority() {
		return -1;
	}
}

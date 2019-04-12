package com.linkstec.bee.UI.spective.basic.logic.node.table;

import java.util.List;

import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.logic.node.BLogicNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BTansferHolderNode;
import com.linkstec.bee.core.fw.basic.BJudgeLogic;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ITableSql;

public class BTableWhereNode extends BTableRecordListNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8188549585231307630L;
	int width = 850;
	int height = 400;

	public BTableWhereNode(BPath parent) {
		super(parent);
		this.setTitle("抽出条件");
		this.getGeometry().setWidth(width);
		this.getGeometry().setHeight(height);
	}

	@Override
	public boolean isDropTarget(BNode source) {
		if (source instanceof BLogicNode) {
			return true;
		}
		return false;
	}

	@Override
	public void childAdded(BNode node, BasicLogicSheet sheet) {
		if (node instanceof BLogicNode) {
			BLogicNode n = (BLogicNode) node;
			BLogic logic = n.getLogic();
			if (logic instanceof BJudgeLogic) {
				BJudgeLogic judge = (BJudgeLogic) logic;
				BTableCondionNode cn = new BTableCondionNode(judge);
				this.insert(cn);
			}
			node.removeFromParent();
		} else if (node instanceof BTansferHolderNode) {
			BTansferHolderNode t = (BTansferHolderNode) node;
			List<BNode> nodes = t.getNodes();
			for (BNode n : nodes) {
				childAdded(n, sheet);
			}
			node.removeFromParent();
		} else if (node instanceof BFixedValueNode) {
			BFixedValueNode n = (BFixedValueNode) node;
			this.insert(n);
		} else {
			super.childAdded(node, sheet);
		}

	}

	@Override
	public String getSQL(ITableSql tsql) {
		List<BTableValueNode> list = this.getRecords();
		String where = "WHERE";

		return this.getSqlItemValue(where, " AND ", list, tsql);
	}

	@Override
	public String getSQLExp(ITableSql tsql) {
		List<BTableValueNode> list = this.getRecords();
		String where = "検索条件：";
		return this.getSqlItemExp(where, " AND ", list, tsql);
	}

	@Override
	public int getSQLPriority() {
		return 2;
	}

}

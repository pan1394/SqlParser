package com.linkstec.bee.UI.spective.basic.logic.node.table;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableListHelper.SqlResult;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;

public class BTableRecordListNode extends BTableGroupNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2037858848996250306L;

	public BTableRecordListNode(BPath parent) {
		super(parent);
		this.getGeometry().setHeight(200);
		this.getGeometry().setWidth(850);

	}

	@Override
	public void childAdded(BNode node, BasicLogicSheet sheet) {
		BTableListHelper.childAdded(this, node, sheet);
		super.childAdded(node, sheet);
	}

	public void inserInvoker(BInvoker invoker, mxPoint offset) {
		BTableListHelper.inserInvoker(this, invoker, offset);
	}

	@Override
	public boolean isDropTarget(BNode source) {
		if (source instanceof BTableRecordNode || source instanceof BTableObjectNode) {
			return true;
		} else {
			return false;
		}
	}

	public List<BTableValueNode> getRecords() {

		return BTableListHelper.getRecords(this);
	}

	@Override
	public void layout(BasicLogicSheet sheet) {
		new BTableNodeLayout(this, sheet);
	}

	private int totalRow = 0;

	protected String getSqlItemValue(String start, String splitter, List<BTableValueNode> list, ITableSql tsql) {

		SqlResult result = BTableListHelper.getSqlItemValue(start, splitter, list, tsql);
		this.totalRow = result.getRow();
		return result.getSql();

	}

	@Override
	public void paint(Graphics g, mxCellState state, double scale) {
		super.paint(g, state, scale);

		Rectangle rect = state.getRectangle();

		if (this.totalRow != 0) {

			for (int i = 0; i <= totalRow; i++) {
				g.setFont(g.getFont().deriveFont((float) (7 * scale)));
				g.drawString((i + 1) + "", (int) (rect.getX()) + 2, (int) ((rect.getY() + (63 + 35 * i) * scale)));
			}
		}
	}

	protected String getSqlItemExp(String start, String splitter, List<BTableValueNode> list, ITableSql tsql) {
		return BTableListHelper.getSqlItemExp(start, splitter, list, tsql);
	}

}

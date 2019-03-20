package com.linkstec.bee.UI.spective.basic.logic.node.table;

import java.util.List;

import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;

public class BTableSortNode extends BTableRecordListNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2037858848996250306L;

	public BTableSortNode(BPath parent) {
		super(parent);
		this.setTitle("ソート項目");
	}

	@Override
	public void layout(BasicLogicSheet sheet) {
		int count = this.getChildCount();

		double y = space + 40;
		double x = 12;
		for (int i = 0; i < count; i++) {
			mxICell child = this.getChildAt(i);
			if (child instanceof BTableRecordNode) {
				mxGeometry geo = child.getGeometry();

				if (x + geo.getWidth() > this.getGeometry().getWidth() - space) {
					x = 12;
					y = y + geo.getHeight() + space;
				}
				geo.setX(x);
				geo.setY(y);

				x = x + geo.getWidth() + space;

			}
		}

		this.fitHeight();
	}

	@Override
	public String getSQL(ITableSql tsql) {

		String sort = "SORT BY";
		List<BTableValueNode> list = this.getRecords();
		if (!list.isEmpty()) {
			tsql.getInfo().setSortBy();
		}
		return this.getSqlItemValue(sort, ",", list, tsql);
	}

	@Override
	public String getSQLExp(ITableSql tsql) {

		String sort = "ソート順：";
		List<BTableValueNode> list = this.getRecords();
		if (!list.isEmpty()) {
			tsql.getInfo().setSortBy();
		}
		return this.getSqlItemExp(sort, ",", list, tsql);
	}

	@Override
	public int getSQLPriority() {
		return 4;
	}
}

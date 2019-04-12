package com.linkstec.bee.UI.spective.basic.logic.node.table;

import java.util.List;

import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ITableSql;

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

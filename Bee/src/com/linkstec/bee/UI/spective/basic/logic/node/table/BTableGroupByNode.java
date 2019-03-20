package com.linkstec.bee.UI.spective.basic.logic.node.table;

import java.util.List;

import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ITableSql;

public class BTableGroupByNode extends BTableSortNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9175800166953413090L;

	public BTableGroupByNode(BPath parent) {
		super(parent);
		this.setTitle("集計項目");
	}

	@Override
	public String getSQL(ITableSql sql) {
		String sort = "GROUP BY ";
		List<BTableValueNode> list = this.getRecords();
		if (!list.isEmpty()) {
			sql.getInfo().setGroupBy();
		}
		return this.getSqlItemValue(sort, ",", list, sql);
	}

	@Override
	public String getSQLExp(ITableSql tsql) {

		String sort = "集計項目：";
		List<BTableValueNode> list = this.getRecords();
		if (!list.isEmpty()) {
			tsql.getInfo().setGroupBy();
		}

		return this.getSqlItemExp(sort, ",", list, tsql);
	}

	@Override
	public int getSQLPriority() {
		return 3;
	}

}

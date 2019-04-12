package com.linkstec.bee.UI.spective.basic.logic.node.table;

import java.util.List;

import com.linkstec.bee.UI.thread.BeeThread;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.logic.BInvoker;

public class BTableSelectItemsNode extends BTableRecordListNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5007360818105347924L;

	public BTableSelectItemsNode(BPath parent) {
		super(parent);
		this.setTitle("抽出対象項目");

	}

	@Override
	public String getSQL(ITableSql tsql) {

		String select = "SELECT";
		List<BTableValueNode> list = this.getRecords();

		if (list.isEmpty()) {
			if (tsql.isFormat()) {
				return select + "\r\n\t*";
			}
			return "SELECT * ";
		}

		Thread t = Thread.currentThread();
		if (t instanceof BeeThread) {
			BeeThread bee = (BeeThread) t;
			bee.addUserAttribute("PICKUP_SELECT", "PICKUP_SELECT");
		}

		String sql = this.getSqlItemValue(select, ",", list, tsql);

		if (t instanceof BeeThread) {
			BeeThread bee = (BeeThread) t;
			Object obj = bee.getUserAttribute("PICKUP_SELECT");
			if (obj instanceof List) {
				@SuppressWarnings("unchecked")
				List<BInvoker> selects = (List<BInvoker>) obj;
				tsql.getSelectInfos().addAll(selects);

			}
			bee.removeUserAttribute("PICKUP_SELECT");
		}

		return sql;

	}

	@Override
	public String getSQLExp(ITableSql tsql) {
		String select = "検索項目：";
		List<BTableValueNode> list = this.getRecords();

		if (list.isEmpty()) {
			if (tsql.isFormat()) {
				return select + "\r\n\t全項目";
			} else {
				return select + " 全項目";
			}
		}
		return this.getSqlItemExp(select, ",", list, tsql);
	}

	@Override
	public int getSQLPriority() {
		return 0;
	}
}

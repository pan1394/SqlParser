package com.linkstec.bee.UI.spective.basic.logic.model.table;

import com.linkstec.bee.UI.spective.basic.logic.node.table.BFixedValueNode;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.impl.basic.BasicLogic;

public class BFixedValueLogic extends BasicLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = -434979811162599367L;

	public BFixedValueLogic(BPath parent, ILogicCell cell) {
		super(parent, cell);
	}

	public String getSQL(ITableSql tsql) {
		BFixedValueNode node = (BFixedValueNode) this.getPath().getCell();
		return node.getSQL(tsql);
	}

}

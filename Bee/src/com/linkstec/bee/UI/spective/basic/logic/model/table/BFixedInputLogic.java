package com.linkstec.bee.UI.spective.basic.logic.model.table;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.logic.node.table.BFixedInputValueNode;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.impl.basic.BasicLogic;

public class BFixedInputLogic extends BasicLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = -434979811162599367L;

	public BFixedInputLogic(BPath parent, ILogicCell cell) {
		super(parent, cell);
	}

	public String getSQL(ITableSql tsql) {
		BFixedInputValueNode node = (BFixedInputValueNode) this.getPath().getCell();
		return node.getSQL(tsql);
	}

	@Override
	public List<BLogicUnit> createUnit() {
		List<BLogicUnit> units = new ArrayList<BLogicUnit>();
		BFixedInputValueNode node = (BFixedInputValueNode) this.getPath().getCell();
		units.add(node.getParameteredInvoker());
		return units;
	}

}

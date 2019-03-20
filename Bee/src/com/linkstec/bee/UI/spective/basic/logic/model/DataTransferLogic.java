package com.linkstec.bee.UI.spective.basic.logic.model;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableRowValueCellValueNode;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.impl.basic.BasicLogic;

public class DataTransferLogic extends BasicLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6355636081798036147L;

	public DataTransferLogic(BPath parent, ILogicCell cell) {
		super(parent, cell);
	}

	@Override
	public List<BLogicUnit> createUnit() {
		List<BLogicUnit> units = new ArrayList<BLogicUnit>();
		BTableRowValueCellValueNode node = (BTableRowValueCellValueNode) this.getPath().getCell();
		units.add(node.getTransferLogic());
		return units;
	}

}

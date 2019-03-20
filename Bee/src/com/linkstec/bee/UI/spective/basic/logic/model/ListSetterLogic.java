package com.linkstec.bee.UI.spective.basic.logic.model;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableNode;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.impl.basic.BasicLogic;

public class ListSetterLogic extends BasicLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6266475505529163510L;

	public ListSetterLogic(BPath parent, ILogicCell cell) {
		super(parent, cell);
	}

	@Override
	public List<BLogicUnit> createUnit() {
		BTableNode node = (BTableNode) this.getPath().getCell();
		List<BLogic> logics = node.getSetterLogics();
		List<BLogicUnit> units = new ArrayList<BLogicUnit>();
		for (BLogic logic : logics) {
			if (logic != null) {
				units.addAll(logic.createUnit());
			}
		}

		return units;
	}

}

package com.linkstec.bee.UI.spective.basic.logic.model;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.core.codec.basic.BasicGenUtils;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.basic.ISetterLogicCell;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.impl.basic.BasicLogic;
import com.mxgraph.model.mxICell;

public class SetterLogic extends BasicLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5113977653478361120L;

	public SetterLogic(BPath parent, ISetterLogicCell cell) {
		super(parent, cell);
	}

	@Override
	public String getName() {
		mxICell cell = (mxICell) this.getPath().getCell();
		return (String) cell.getValue();
	}

	@Override
	public String getDesc() {
		mxICell cell = (mxICell) this.getPath().getCell();
		return (String) cell.getValue();
	}

	@Override
	public List<BLogicUnit> createUnit() {
		List<BLogicUnit> units = new ArrayList<BLogicUnit>();

		ILogicCell c = this.getPath().getCell();
		if (c instanceof ISetterLogicCell) {

			ISetterLogicCell cell = (ISetterLogicCell) c;
			ILogicCell start = cell.getStart();

			List<BLogic> logics = new ArrayList<BLogic>();

			BasicGenUtils.makeLogics(start, logics, true);
			for (BLogic logic : logics) {
				List<BLogicUnit> uts = logic.createUnit();
				if (uts != null) {
					units.addAll(uts);
				}
			}
		}

		return units;
	}

}

package com.linkstec.bee.UI.spective.basic.logic.model;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.util.BValueUtils;
import com.linkstec.bee.core.fw.BNote;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.IAssignCell;
import com.linkstec.bee.core.fw.logic.BAssignExpression;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.impl.basic.BasicLogic;
import com.mxgraph.model.mxICell;

public class AssignLogic extends BasicLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5113977653478361120L;

	public AssignLogic(BPath parent, IAssignCell cell) {
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
		IPatternCreator view = PatternCreatorFactory.createView();
		List<BLogicUnit> units = new ArrayList<BLogicUnit>();

		IAssignCell cell = (IAssignCell) this.getPath().getCell();
		BValuable value = cell.getAssignValue();
		BValuable var = cell.getVariable();

		if (value != null) {
			BNote note = view.createComment();
			note.setNote(
					BValueUtils.createValuable(var, false) + "に" + BValueUtils.createValuable(value, false) + "に設定する");
			units.add(note);

			if (var instanceof BInvoker) {
				BInvoker bin = (BInvoker) var;
				bin.addParameter(value);
				units.add(bin);
			} else {
				BAssignExpression ex = view.createAssignExpression();
				ex.setLeft(var);
				ex.setRight(value, null);
				units.add(ex);
			}
		}

		return units;
	}
}

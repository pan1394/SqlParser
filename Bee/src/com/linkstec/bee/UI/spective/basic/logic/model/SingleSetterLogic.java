package com.linkstec.bee.UI.spective.basic.logic.model;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.core.Debug;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BNote;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ISingleSetterLogicCell;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.impl.basic.BasicLogic;
import com.mxgraph.model.mxICell;

public class SingleSetterLogic extends BasicLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5113977653478361120L;
	private BVariable target;

	public SingleSetterLogic(BPath parent, ISingleSetterLogicCell cell, BVariable target) {
		super(parent, cell);
		this.target = target;
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

		ISingleSetterLogicCell cell = (ISingleSetterLogicCell) this.getPath().getCell();
		BValuable value = cell.getSetterParameter();

		if (value == null) {
			Debug.a();
		}
		BInvoker invoker = cell.getSetterParent();

		BValuable child = invoker.getInvokeChild();
		BValuable parent = invoker.getInvokeParent();
		if (child instanceof BVariable && parent instanceof BVariable) {
			BVariable var = (BVariable) child;
			BVariable p = (BVariable) parent;
			BNote note = view.createComment();
			note.setNote(p.getName() + "の" + var.getName() + "編集");
			units.add(note);
		}

		if (value != null) {
			if (value instanceof BParameter) {
				BParameter b = (BParameter) value;

				BParameter para = view.createParameter();
				para.setBClass(b.getBClass());
				para.setLogicName(b.getLogicName());
				para.setName(b.getName());
				value = para;
			}
			invoker.addParameter((BValuable) value.cloneAll());
			units.add(invoker);
		} else {
			invoker.addParameter(CodecUtils.getNullValue());
			units.add(invoker);
		}

		return units;
	}

}

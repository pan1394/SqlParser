package com.linkstec.bee.UI.spective.basic.logic.model.data;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.logic.model.LogicList;
import com.linkstec.bee.UI.spective.basic.logic.model.var.VarLogicList;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BPath;

public class PluralDataLogicList extends LogicList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3074514749984271278L;
	private List<BLogic> list = new ArrayList<BLogic>();

	public PluralDataLogicList(BPath parent, BVariable var) {

		LoopLogic loop = new LoopLogic(parent, var);
		// VarNullCheckLogic nullcheck = new VarNullCheckLogic(parent, var, var);
		// list.add(nullcheck);
		list.add(loop);

		VarLogicList logicList = new VarLogicList(var, var);
		list.addAll(logicList.getList(parent));

	}

	@Override
	public List<BLogic> getList(BPath parent) {
		return list;
	}

}

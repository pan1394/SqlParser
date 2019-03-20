package com.linkstec.bee.UI.spective.basic.logic.model.common;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.node.NoteNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BFlowStart;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.impl.basic.BasicLogic;

public class BasicStartLogic extends BasicLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1007669084928717243L;

	public BasicStartLogic(BPath parent) {
		super(parent, null);
		BFlowStart node = new BFlowStart(this);
		this.getPath().setCell(node);
	}

	@Override
	public List<BLogicUnit> createUnit() {
		List<BLogicUnit> units = new ArrayList<BLogicUnit>();
		NoteNode note = new NoteNode();
		note.setValue("処理開始");
		units.add(note);
		return units;
	}

}

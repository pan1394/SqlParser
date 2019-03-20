package com.linkstec.bee.UI.spective.basic.logic.edit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.logic.node.BActionPropertyNode;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.editor.BWorkSpace;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;

public class BLogicModel extends BPatternModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5181950163414866046L;

	public BLogicModel(BPath action) {
		super(action);
	}

	public List<BActionModel> getActions() {
		List<BActionModel> actions = new ArrayList<BActionModel>();
		mxCell root = (mxCell) this.getRoot();
		this.addAction(root, actions);
		return actions;
	}

	private void addAction(mxICell cell, List<BActionModel> list) {
		int count = cell.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell sub = cell.getChildAt(i);
			if (sub instanceof BActionPropertyNode) {
				BActionPropertyNode node = (BActionPropertyNode) sub;
				list.add((BActionModel) node.getLogic().getPath().getAction());
			} else {
				this.addAction(sub, list);
			}
		}
	}

	@Override
	public BEditor getEditor(BProject project, File file, BWorkSpace space) {
		BLogicSheet sheet = new BLogicSheet(project, this);
		return sheet;
	}
}

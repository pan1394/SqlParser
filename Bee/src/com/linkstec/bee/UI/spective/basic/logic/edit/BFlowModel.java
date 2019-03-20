package com.linkstec.bee.UI.spective.basic.logic.edit;

import java.io.File;

import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.UI.spective.basic.logic.BasicModel;
import com.linkstec.bee.UI.spective.basic.logic.node.BDataGroupNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BDesignHeader;
import com.linkstec.bee.UI.spective.basic.logic.node.BGroupNode;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.editor.BWorkSpace;
import com.mxgraph.model.mxCell;

public class BFlowModel extends BasicModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -410130977469361065L;

	public BFlowModel(SubSystem sub) {
		super(sub);
		BDesignHeader header = new BDesignHeader(sub, this);
		mxCell cell = (mxCell) this.getRoot();
		cell.insert(header);
	}

	public void modelNameChanged(String name) {
		this.setLogicName(name);
	}

	@Override
	public BEditor getEditor(BProject project, File file, BWorkSpace space) {
		BFlowSheet c = new BFlowSheet(project, this.getSubSystem(), this);
		return c;
	}

	@Override
	public BGroupNode getGroupNode() {

		return new BDataGroupNode();
	}

}

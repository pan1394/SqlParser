package com.linkstec.bee.UI.spective.basic.logic.edit;

import com.linkstec.bee.UI.spective.basic.data.BasicComponentModel;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableNode;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.editor.BProject;
import com.mxgraph.model.mxICell;

public class BTableInserSheet extends BTableUpdateSheet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1219704791221191050L;

	public BTableInserSheet(BProject project, BTableInsertModel model) {
		super(project, model);
	}

	@Override
	public void addTarget(BPath path, BasicComponentModel model) {

		BTableNode node = new BTableNode(path, model);
		this.getRoot().insert(node);
	}

	@Override
	public void insertTableObjects(BPath path, mxICell root) {

	}
}

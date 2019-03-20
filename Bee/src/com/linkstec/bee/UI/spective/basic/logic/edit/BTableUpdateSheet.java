package com.linkstec.bee.UI.spective.basic.logic.edit;

import java.util.List;

import com.linkstec.bee.UI.spective.basic.data.BasicComponentModel;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableTargetTablesNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableWhereNode;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.editor.BProject;
import com.mxgraph.model.mxICell;

public class BTableUpdateSheet extends BTableSheet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7523163210202161522L;

	public BTableUpdateSheet(BProject project, BTableUpdateModel model) {
		super(project, model);
	}

	public void insertStart(BPath path) {
		BActionModel action = (BActionModel) path.getAction();
		mxICell root = this.getRoot();

		this.insertTableObjects(path, root);

		List<BasicComponentModel> models = action.getInputModels();
		for (BasicComponentModel model : models) {
			this.addTarget(path, model);
		}
		models = action.getOutputModels();
		for (BasicComponentModel model : models) {
			this.addTarget(path, model);
		}

	}

	public void insertTableObjects(BPath path, mxICell root) {
		BTableTargetTablesNode tables = new BTableTargetTablesNode(path);
		root.insert(tables);
	}

	public void addTarget(BPath path, BasicComponentModel model) {
		BTableNode node = new BTableNode(path, model);
		this.getGraph().addCell(node);
		BTableWhereNode where = new BTableWhereNode(path);
		where.setTitle("更新条件");
		this.getGraph().addCell(where);
	}

}

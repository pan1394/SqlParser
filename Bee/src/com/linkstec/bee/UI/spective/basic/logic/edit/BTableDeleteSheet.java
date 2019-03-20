package com.linkstec.bee.UI.spective.basic.logic.edit;

import com.linkstec.bee.UI.spective.basic.data.BasicComponentModel;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableWhereNode;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.editor.BProject;

public class BTableDeleteSheet extends BTableUpdateSheet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8470876053613407105L;

	public BTableDeleteSheet(BProject project, BTableDeleteModel model) {
		super(project, model);
	}

	@Override
	public void addTarget(BPath path, BasicComponentModel model) {

		BTableWhereNode where = new BTableWhereNode(path);
		where.setTitle("削除条件");
		this.getGraph().addCell(where);
	}

}

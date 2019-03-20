package com.linkstec.bee.UI.spective.basic.logic.edit;

import java.io.File;

import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.editor.BWorkSpace;

public class BTableDeleteModel extends BTableUpdateModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5781907802495377291L;

	public BTableDeleteModel(BPath action) {
		super(action);
	}

	@Override
	public BEditor getEditor(BProject project, File file, BWorkSpace space) {
		BTableDeleteSheet c = new BTableDeleteSheet(project, this);
		return c;
	}

	@Override
	public String getSQL(ITableSql tsql) {
		return "DELETE " + super.getSQL(tsql);
	}

}

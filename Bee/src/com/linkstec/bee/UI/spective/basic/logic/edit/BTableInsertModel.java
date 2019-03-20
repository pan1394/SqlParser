package com.linkstec.bee.UI.spective.basic.logic.edit;

import java.io.File;

import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.editor.BWorkSpace;

public class BTableInsertModel extends BTableUpdateModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4982053643552628309L;

	public BTableInsertModel(BPath action) {
		super(action);
	}

	@Override
	public BEditor getEditor(BProject project, File file, BWorkSpace space) {
		BTableInserSheet c = new BTableInserSheet(project, this);
		return c;
	}

}

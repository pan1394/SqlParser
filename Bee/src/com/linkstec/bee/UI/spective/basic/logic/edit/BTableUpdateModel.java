package com.linkstec.bee.UI.spective.basic.logic.edit;

import java.io.File;

import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.editor.BWorkSpace;

public class BTableUpdateModel extends BTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5991310601118104393L;

	public BTableUpdateModel(BPath action) {
		super(action);
	}

	@Override
	public BEditor getEditor(BProject project, File file, BWorkSpace space) {
		BTableUpdateSheet c = new BTableUpdateSheet(project, this);
		return c;
	}

}

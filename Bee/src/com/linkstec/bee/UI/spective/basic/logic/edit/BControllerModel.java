package com.linkstec.bee.UI.spective.basic.logic.edit;

import java.io.File;

import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.editor.BWorkSpace;

public class BControllerModel extends BPatternModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2915326595470693565L;

	public BControllerModel(BPath action) {
		super(action);
	}

	@Override
	public BEditor getEditor(BProject project, File file, BWorkSpace space) {
		BControllerSheet c = new BControllerSheet(project, this);
		return c;
	}
}

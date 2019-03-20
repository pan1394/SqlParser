package com.linkstec.bee.UI.spective.basic.logic.edit;

import java.io.File;

import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.UI.spective.basic.logic.BasicModel;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.editor.BWorkSpace;

public class BCoverModel extends BasicModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1964851320946485540L;

	public BCoverModel(SubSystem sub) {
		super(sub);
	}

	@Override
	public BEditor getEditor(BProject project, File file, BWorkSpace space) {
		BCoverSheet c = new BCoverSheet(this, project, this.getSubSystem());
		return c;
	}
}

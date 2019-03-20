package com.linkstec.bee.UI.spective.basic.logic.edit;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.basic.logic.model.common.BasicStartLogic;
import com.linkstec.bee.UI.spective.basic.logic.node.BFlowStart;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.editor.BProject;

public class BControllerSheet extends BPatternSheet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8927743141820989298L;

	public BControllerSheet(BProject project, BControllerModel model) {
		super(project, model);
		this.getGraph().setKeepEdgesInForeground(true);
	}

	public void insertStart(BPath parent) {
		BasicStartLogic logic = new BasicStartLogic(parent);
		BFlowStart start = (BFlowStart) logic.getPath().getCell();
		logic.getPath().setCell(start);

		this.getRoot().insert(start);
		start.getGeometry().setX(400);
		start.getGeometry().setY(100);
		this.getGraph().refresh();
	}

	@Override
	public ImageIcon getImageIcon() {
		return BeeConstants.FLOW_MODEL_ICON;
	}
}

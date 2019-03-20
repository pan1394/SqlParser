package com.linkstec.bee.UI.spective.basic.logic.edit;

import java.util.List;

import javax.swing.tree.DefaultTreeModel;

import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.data.BasicComponentModel;
import com.linkstec.bee.UI.spective.basic.logic.model.common.BasicStartLogic;
import com.linkstec.bee.UI.spective.basic.logic.node.BFlowStart;
import com.linkstec.bee.UI.spective.basic.logic.node.BLogicConnector;
import com.linkstec.bee.UI.spective.basic.tree.BasicDataSelectionNode;
import com.linkstec.bee.UI.spective.basic.tree.BasicEditNode;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.editor.BProject;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;

public class BPatternSheet extends BasicLogicSheet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4992081199612542686L;

	public BPatternSheet(BProject project, BPatternModel model) {
		super(project, ((BActionModel) model.getActionPath().getAction()).getSubSystem());
		this.setModel(model);
	}

	public void insertStart(BPath path) {
		BasicStartLogic logic = new BasicStartLogic(path);
		BFlowStart node = (BFlowStart) logic.getPath().getCell();
		this.getRoot().insert(node);
		this.getGraph().refresh();
	}

	@Override
	protected void cellConnected(mxICell connector) {
		super.cellConnected(connector);
		if (connector instanceof BLogicConnector) {
			BLogicConnector logic = (BLogicConnector) connector;
			logic.connected();
		}
	}

	@Override
	protected mxCell getConnector(mxICell source) {
		if (source instanceof ILogicCell) {
			BLogicConnector connector = new BLogicConnector();
			return connector;
		}
		return null;
	}

	@Override
	public void makeLogicModel(BasicEditNode parent, BProject project, DefaultTreeModel model) {
		BPatternModel p = (BPatternModel) this.getEditorModel();
		this.makeActionLogic(p.getActionPath(), parent);
	}

	public void makeDataSelectionIO(BasicDataSelectionNode parent, BActionModel action) {
		List<BasicComponentModel> ins = action.getInputModels();
		List<BasicComponentModel> outs = action.getOutputModels();
		for (BasicComponentModel bm : ins) {
			BasicDataSelectionNode c = new BasicDataSelectionNode(parent.getProject());
			c.setUserObject(bm);
			c.setDisplay(bm.getName());
			c.setImageIcon(bm.getIcon());
			c.setLeaf(false);
			parent.add(c);
		}
		for (BasicComponentModel bm : outs) {
			BasicDataSelectionNode c = new BasicDataSelectionNode(parent.getProject());
			c.setUserObject(bm);
			c.setDisplay(bm.getName());
			c.setImageIcon(bm.getIcon());
			c.setLeaf(false);
			parent.add(c);
		}
	}

	@Override
	public BPath getPath() {
		BPatternModel m = (BPatternModel) this.getEditorModel();
		return m.getActionPath();
	}

}

package com.linkstec.bee.UI.spective.basic.logic.edit;

import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeModel;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.UI.spective.basic.config.model.ActionModel;
import com.linkstec.bee.UI.spective.basic.config.model.ConfigModel;
import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants.ProcessType;
import com.linkstec.bee.UI.spective.basic.data.BasicComponentModel;
import com.linkstec.bee.UI.spective.basic.logic.node.BActionNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BActionPropertyNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BComponentNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.UI.spective.basic.tree.BasicEditNode;
import com.linkstec.bee.core.fw.editor.BProject;
import com.mxgraph.model.mxICell;

public class BFlowSheet extends BasicLogicSheet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8365807035664027456L;

	public BFlowSheet(BProject project, SubSystem sub, BFlowModel model) {
		super(project, sub);
		this.setModel(model);

	}

	@Override
	protected void cellConnected(mxICell connector) {
		if (connector instanceof BActionNode) {
			BActionNode node = (BActionNode) connector;
			mxICell source = node.getSource();
			mxICell target = node.getTarget();
			if (source instanceof BComponentNode && target instanceof BComponentNode) {
				BComponentNode s = (BComponentNode) source;
				BComponentNode t = (BComponentNode) target;
				BasicComponentModel from = s.getModel();
				BasicComponentModel to = t.getModel();

				ConfigModel config = ConfigModel.load(this.getProject());
				ActionModel action = config.getAction(from.getType(), to.getType());
				node.addProperty(from, this, action, ProcessType.TYPE_FLOW, null);
			}
		}
	}

	@Override
	public boolean tabCloseable() {
		return false;
	}

	@Override
	public void makeLogicModel(BasicEditNode parent, BProject project, DefaultTreeModel model) {
		super.makeLogicModel(parent, project, model);

		BFlowModel flow = (BFlowModel) this.getEditorModel();
		List<BNode> list = flow.getBNodes();
		for (BNode node : list) {

			BActionNode action = (BActionNode) node;
			BActionPropertyNode p = action.getProperty();
			if (p != null) {
				this.makeActionLogic(p.getLogic().getPath(), parent);
			}
		}
	}

	@Override
	public ImageIcon getImageIcon() {
		return BeeConstants.FLOW_ICON;
	}

}

package com.linkstec.bee.UI.spective.basic.logic.node;

import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.config.model.ActionModel;
import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants.ProcessType;
import com.linkstec.bee.UI.spective.basic.data.BasicComponentModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BActionModel;
import com.linkstec.bee.UI.spective.basic.logic.model.NewLayerClassLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;

public class BActionNode extends BNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6647623520410668573L;

	public BActionNode() {
		this.setEdge(true);
		this.setStyle("strokeColor=black;edgeStyle=sideToSideEdgeStyle;strokeWidth=0.5;rounded=1;dashed=false;fontSize="
				+ BeeUIUtils.getDefaultFontSize());
		mxGeometry geo = new mxGeometry();
		geo.setRelative(true);
		this.setGeometry(geo);
	}

	public void addProperty(BasicComponentModel input, BasicLogicSheet sheet, ActionModel action, ProcessType type,
			BPath parentPath) {
		BActionPropertyNode node;
		BActionModel newAction;
		BPath path = new BPath(parentPath, null);
		if (parentPath == null) {
			BActionPropertyNode previous = this.getPrevious();
			if (previous != null) {
				parentPath = previous.getLogic().getPath();
				path = new BPath(parentPath, null);
			}
			newAction = new BActionModel(action, null, type);

			newAction.setName(input.getName());
			newAction.setLogicName("execute");
			newAction.setSubSystem(sheet.getSub());
			path.setProject(sheet.getProject());
		} else {
			newAction = new BActionModel(action, (BActionModel) parentPath.getAction(), type);
			newAction.setName(input.getName());
			String logicName = input.getLogicName();
			logicName = logicName.substring(0, 1).toUpperCase() + logicName.substring(1, logicName.length());
			newAction.setLogicName("do" + logicName);
		}
		newAction.setSubSystem(sheet.getSub());
		path.setProject(sheet.getProject());

		// path.setAction(newAction);
		NewLayerClassLogic logic = new NewLayerClassLogic(path, input, null);
		logic.getPath().setAction(newAction);

		node = new BActionPropertyNode(logic);

		logic.getPath().setCell(node);
		this.insert(node);
	}

	public BActionPropertyNode getPrevious() {
		mxICell source = this.getSource();
		if (source != null) {
			int count = source.getEdgeCount();
			for (int i = 0; i < count; i++) {
				mxICell child = source.getEdgeAt(i);
				mxICell t = child.getTerminal(false);
				mxICell s = child.getTerminal(true);
				if (t != null && s != null) {
					if (!source.equals(s) && t.equals(source)) {
						if (child instanceof BActionNode) {
							BActionNode node = (BActionNode) child;
							return node.getProperty();
						}
					}
				}
			}
		}
		return null;
	}

	public BActionPropertyNode getProperty() {
		if (this.getChildCount() == 1) {
			mxICell s = this.getChildAt(0);
			if (s instanceof BActionPropertyNode) {
				BActionPropertyNode node = (BActionPropertyNode) s;
				return node;
			}
		}
		return null;
	}

	@Override
	public boolean isValidDropTarget(Object[] cells) {
		return true;
	}

}

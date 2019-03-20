package com.linkstec.bee.UI.node.view;

import javax.swing.ImageIcon;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.node.TypeNode;
import com.linkstec.bee.UI.node.layout.HorizonalLayout;
import com.linkstec.bee.core.fw.BVariable;
import com.mxgraph.model.mxGeometry;

public class ThrowsNode extends BasicNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4949625294919105327L;

	public ThrowsNode() {
		HorizonalLayout layout = new HorizonalLayout();
		this.setTitled();
		this.setLayout(layout);
		this.setRelative();

		mxGeometry g = new mxGeometry(0, 0, BeeConstants.SEGMENT_MAX_WIDTH, BeeConstants.LINE_HEIGHT);
		g.setRelative(true);
		this.setGeometry(g);

		LabelNode node = new LabelNode();
		node.setValue("投げエラー");
		node.setFixedWidth(100);
		node.setOpaque(false);

		layout.addNode(node);
	}

	public void setVariable(BVariable var) {
		TypeNode type = new TypeNode(var);
		this.getLayout().addNode(type);
	}

	public BVariable getVariable() {
		int count = this.getChildCount();
		for (int i = 0; i < count; i++) {
			Object obj = this.getChildAt(i);
			if (obj instanceof TypeNode) {
				TypeNode type = (TypeNode) obj;
				return type.getObject();
			}
		}
		return null;
	}

	@Override
	public ImageIcon getIcon() {
		return BeeConstants.THROW_ICON;
	}

}

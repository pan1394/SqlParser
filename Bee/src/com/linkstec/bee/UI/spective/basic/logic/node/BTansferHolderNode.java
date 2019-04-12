package com.linkstec.bee.UI.spective.basic.logic.node;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.mxgraph.model.mxICell;
import com.mxgraph.view.mxCellState;

public class BTansferHolderNode extends BNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5060662406841590898L;

	private List<BNode> nodes;

	public BTansferHolderNode(List<BNode> nodes) {
		this.nodes = nodes;
		this.setVertex(true);
		this.getGeometry().setHeight(110);
		this.getGeometry().setWidth(200);
		this.setStyle("strokeColor=gray;strokWidth=1");
	}

	public List<BNode> getNodes() {
		return this.nodes;
	}

	@Override
	public void added(BasicLogicSheet sheet) {
		mxICell parent = this.getParent();
		if (parent != null) {
			for (BNode node : nodes) {
				if (parent instanceof BNode) {
					BNode b = (BNode) parent;
					if (b.isDropTarget(node)) {
						parent.insert(node);
						b.childAdded(node, sheet);
					}
				} else {
					parent.insert(node);
				}
			}
			this.removeFromParent();
		}
	}

	@Override
	public void paint(Graphics g, mxCellState state, double scale) {
		Rectangle rect = state.getRectangle();
		g.setColor(Color.BLACK);
		g.drawRect(rect.x + 5, rect.y + 5, rect.width - 10, 20);
		g.drawRect(rect.x + 5, rect.y + 45, rect.width - 10, 20);
		g.drawRect(rect.x + 5, rect.y + 85, rect.width - 10, 20);
	}

}

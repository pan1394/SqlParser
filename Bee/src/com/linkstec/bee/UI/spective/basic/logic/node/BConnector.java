package com.linkstec.bee.UI.spective.basic.logic.node;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;

public class BConnector extends mxCell {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8183284972608713364L;

	public BConnector() {
		this.setGeometry(new mxGeometry());
		this.setEdge(true);
		this.setStyle("rounded=1;edgeStyle=sideToSideEdgeStyle;strokeWidth=0.5;strokeColor=gray;dashed=false");
	}

	public boolean isValidDropTarget(Object[] cells) {
		return true;
	}
}

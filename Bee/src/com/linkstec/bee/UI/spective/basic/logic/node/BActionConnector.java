package com.linkstec.bee.UI.spective.basic.logic.node;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;

public class BActionConnector extends mxCell {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2819158216656574629L;

	public BActionConnector() {

		this.setGeometry(new mxGeometry());
		this.getGeometry().setRelative(true);
		this.setEdge(true);

		this.setStyle("dashed=false;strokeWidth=0.5;strokeColor=gray;edgeStyle=topToBottomEdgeStyle");
	}
}

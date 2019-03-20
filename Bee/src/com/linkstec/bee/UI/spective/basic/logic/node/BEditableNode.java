package com.linkstec.bee.UI.spective.basic.logic.node;

import com.mxgraph.model.mxGeometry;

public class BEditableNode extends BNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2207267992313028034L;

	public BEditableNode() {
		this.setVertex(true);
		mxGeometry geo = this.getGeometry();
		geo.setRelative(true);
		this.setEditable(true);
		this.setConnectable(false);
		this.setStyle("opacity=0;align=center");
	}
}

package com.linkstec.bee.UI.spective.basic.logic.node;

import com.mxgraph.model.mxGeometry;

public class BEnd extends BNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5244944245402925120L;

	public BEnd() {
		this.setVertex(true);
		this.setStyle("rounded=1;strokeWidth=0.5;strokeColor=gray;align=center");
		mxGeometry geo = this.getGeometry();
		geo.setWidth(50);
		geo.setHeight(50);
		geo.setRelative(true);
		geo.setX(0.5);
		geo.setY(0);
		geo.getOffset().setX(-25);
		this.setValue("E");
	}
}

package com.linkstec.bee.UI.spective.basic.logic.node;

import com.linkstec.bee.UI.BeeConstants;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;

public class BNodeNumber extends BNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7329367359623174988L;

	public BNodeNumber() {
		mxGeometry geo = new mxGeometry();
		geo.setHeight(BeeConstants.LINE_HEIGHT);
		geo.setWidth(BeeConstants.LINE_HEIGHT);
		geo.setRelative(true);
		geo.setOffset(new mxPoint(-BeeConstants.LINE_HEIGHT, 0));
		this.setGeometry(geo);
		this.setVertex(true);

		this.setStyle("strokeWidth=0.5;strokeColor=gray;fillColor=white;align=center");

	}
}

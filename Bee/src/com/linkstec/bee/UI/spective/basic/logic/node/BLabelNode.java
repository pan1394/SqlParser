package com.linkstec.bee.UI.spective.basic.logic.node;

public class BLabelNode extends BNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4087147677218985792L;

	public BLabelNode() {
		this.setVertex(true);
		this.setConnectable(false);
		this.getGeometry().setRelative(true);
		this.setStyle("strokeColor=gray;strokeWidth=0.5;align=center");
	}
}

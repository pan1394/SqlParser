package com.linkstec.bee.UI.spective.basic.logic.node;

import com.linkstec.bee.UI.spective.basic.logic.model.common.BasicStartLogic;
import com.mxgraph.util.mxPoint;

public class BFlow extends BNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4205446680030277100L;

	public BFlow(BasicStartLogic logic, String name) {

		this.setVertex(true);
		this.getGeometry().setWidth(850);
		this.getGeometry().setHeight(200);

		this.setStyle("strokeWidth=0.5;strokeColor=gray;");
		this.setConnectable(false);

		if (logic != null) {
			BFlowStart start = new BFlowStart(logic);
			this.insert(start);
			start.getGeometry().setOffset(new mxPoint(75, 75));
		}

	}

}

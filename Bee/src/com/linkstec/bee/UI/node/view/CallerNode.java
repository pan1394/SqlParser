package com.linkstec.bee.UI.node.view;

import java.io.Serializable;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

public class CallerNode extends mxCell implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2806154462057902341L;

	public CallerNode(mxCell source, mxCell target, mxGraph graph) {

		this.setEdge(true);
		this.setSource(source);
		this.setTarget(target);
		this.setStyle("textOpacity=0;");
		// mxCell m = (mxCell) target.getParent().insert(caller);

		// mxGeometry maker = new mxGeometry(0, 0.5, 10, 10);
		// maker.setOffset(new mxPoint(-5, -5));
		// maker.setRelative(true);
		// mxCell mxMakder = new mxCell(null, maker,
		// "shape=ellipse;strokeWidth=0.5;perimter=ellipsePerimeter;fillColor=lightgray;");
		// mxMakder.setVertex(true);
		// mxMakder.setConnectable(false);
		// m.insert(mxMakder);
		// Object value = source.getValue();
		// if (value != null) {
		// target.setValue(value);
		// //m.setValue(value);
		// }
		if (graph != null) {
			graph.insertEdge(null, null, "", source, target);
		}
	}

}

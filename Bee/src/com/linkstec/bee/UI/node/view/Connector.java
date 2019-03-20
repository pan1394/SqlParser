package com.linkstec.bee.UI.node.view;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;

public class Connector extends mxCell {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6722421541786669327L;

	public Connector() {
		this.setEdge(true);
		this.setGeometry(new mxGeometry());
		this.getGeometry().setRelative(true);
		this.setStyle(
				"strokeColor=gray;strokeWidth=0.5;edgeStyle=topToBottomEdgeStyle;orthogonal=1;labelBackgroundColor=EEEEEE;rounded=1");
	}

	public void setEntityConnnector() {
		this.addStyle("edgeStyle=entityRelationEdgeStyle");
	}

	public void addStyle(String style) {
		String s = this.getStyle();
		String[] values = style.split("=");

		if (s != null) {
			String[] ss = s.split(";");
			String newStyle = "";
			for (String astyle : ss) {
				if (!astyle.contains(values[0] + "=")) {
					if (newStyle.equals("")) {
						newStyle = astyle;
					} else {
						newStyle = newStyle + ";" + astyle;
					}
				}
			}
			newStyle = newStyle + ";" + style;
			this.setStyle(newStyle);
		} else {
			this.setStyle(style);
		}
	}
}

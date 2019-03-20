package com.linkstec.bee.UI.spective.basic.logic.node;

import com.linkstec.bee.core.fw.BClass;
import com.mxgraph.util.mxPoint;

public class BCoverClassNode extends BNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5701834739319676189L;

	public BCoverClassNode(int index, BClass bclass) {
		this.setVertex(true);
		this.setConnectable(false);
		BLabelNode last = this.makeRow(index, bclass);
		this.getGeometry().setWidth(last.getGeometry().getX() + last.getGeometry().getWidth());
		this.getGeometry().setHeight(last.getGeometry().getHeight());
	}

	private BLabelNode makeRow(int index, BClass bclass) {
		BLabelNode node = this.makeCell(index + "", 20, 30, 0, 0);
		node = this.makeCell(bclass.getPackage(), 300, 30,
				node.getGeometry().getWidth() + node.getGeometry().getOffset().getX(), 0);
		node = this.makeCell(bclass.getLogicName(), 200, 30,
				node.getGeometry().getWidth() + node.getGeometry().getOffset().getX(), 0);
		node.setEditable(true);
		node = this.makeCell(bclass.getName(), 200, 30,
				node.getGeometry().getOffset().getX() + node.getGeometry().getWidth(), 0);
		node = this.makeCell((String) bclass.getUserAttribute("PROCESS_TYPE"), 100, 30,
				node.getGeometry().getOffset().getX() + node.getGeometry().getWidth(), 0);
		return node;

	}

	private BLabelNode makeCell(String title, double width, double height, double x, double y) {
		BLabelNode cell = new BLabelNode();
		cell.setStyle("align=left;strokeColor=gray;strokeWidth=0.5;");
		cell.setValue(title);
		cell.getGeometry().setWidth(width);
		cell.getGeometry().setHeight(height);
		cell.getGeometry().setOffset(new mxPoint(x, y));
		this.insert(cell);
		return cell;
	}
}

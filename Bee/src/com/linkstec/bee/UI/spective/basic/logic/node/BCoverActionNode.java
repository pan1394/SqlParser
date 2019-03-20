package com.linkstec.bee.UI.spective.basic.logic.node;

import com.linkstec.bee.UI.spective.basic.logic.edit.BActionModel;
import com.mxgraph.util.mxPoint;

public class BCoverActionNode extends BNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7501534279208123906L;

	public BCoverActionNode(int index, BActionModel model) {
		this.setVertex(true);
		this.setConnectable(false);
		BLabelNode last = this.makeRow(index, model);
		this.getGeometry().setWidth(last.getGeometry().getX() + last.getGeometry().getWidth());
		this.getGeometry().setHeight(last.getGeometry().getHeight());
	}

	private BLabelNode makeRow(int index, BActionModel model) {
		BLabelNode node = this.makeCell(index + "", 20, 30, 0, 0);
		String declearedClass = "";
		if (model.getDeclearedClass() != null) {
			declearedClass = model.getDeclearedClass().getName();
		}
		node = this.makeCell(declearedClass, 300, 30, node.getGeometry().getWidth() + node.getGeometry().getOffset().getX(), 0);
		node = this.makeCell(model.getLogicName(), 200, 30, node.getGeometry().getWidth() + node.getGeometry().getOffset().getX(), 0);
		node = this.makeCell(model.getName(), 200, 30, node.getGeometry().getOffset().getX() + node.getGeometry().getWidth(), 0);
		node = this.makeCell(model.getProcessType().getTitle(), 100, 30, node.getGeometry().getOffset().getX() + node.getGeometry().getWidth(), 0);
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

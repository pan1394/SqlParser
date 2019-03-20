package com.linkstec.bee.UI.spective.basic.logic.node;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxCellState;

public class BStart extends BNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1415508801377681623L;

	private String nameBID, loigcNameBID;

	public BStart(String name, String logicName) {
		int width = 200;
		this.setVertex(true);
		this.setStyle("rounded=1;strokeWidth=0.5;strokeColor=gray;align=center;fillColor=" + BeeConstants.ELEGANT_BRIGHTER_GREEN_COLOR);
		mxGeometry geo = this.getGeometry();
		geo.setWidth(width);
		geo.setHeight(60);
		geo.setRelative(true);
		geo.setX(0.5);
		geo.setY(0);
		geo.getOffset().setX(-width / 2);
		geo.getOffset().setY(-25);
		this.setEditable(false);

		BEditableNode nameNode = new BEditableNode() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -7520810900945975959L;

			@Override
			public void setValue(Object value) {
				super.setValue(value);
				if (value instanceof String) {
					nameChanged((String) value);
				}
			}

		};
		nameNode.setValue(name);
		nameNode.getGeometry().setWidth(200);
		nameNode.getGeometry().setHeight(30);
		nameNode.getGeometry().setOffset(new mxPoint(0, 0));
		this.nameBID = nameNode.getId();

		BEditableNode logicNode = new BEditableNode() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -7520810900945975959L;

			@Override
			public void setValue(Object value) {
				super.setValue(value);
				if (value instanceof String) {
					logicNameChanged((String) value);
				}
			}

		};
		logicNode.getGeometry().setWidth(200);
		logicNode.getGeometry().setHeight(30);
		logicNode.getGeometry().setOffset(new mxPoint(0, 30));

		logicNode.setEnglishInput();
		logicNode.setValue(logicName);
		this.loigcNameBID = logicNode.getId();

		this.insert(nameNode);
		this.insert(logicNode);

	}

	public void nameChanged(String name) {

	}

	public void logicNameChanged(String name) {

	}

	public String getName() {
		return (String) this.getCellById(nameBID).getValue();
	}

	public String getLogicName() {
		return (String) this.getCellById(loigcNameBID).getValue();
	}

	@Override
	public boolean isEditable() {
		return true;
	}

	@Override
	public void paint(Graphics g, mxCellState state) {

		mxRectangle box = state.getBoundingBox();
		Rectangle rect = box.getRectangle();

		g.setFont(BeeUIUtils.getDefaultFont());

		int y = rect.y + rect.height / 2;
		int x = rect.x;
		g.setColor(Color.GRAY);

		int left = 20;
		g.drawLine(x + left, y, x + rect.width - left * 2, y);

	}

}

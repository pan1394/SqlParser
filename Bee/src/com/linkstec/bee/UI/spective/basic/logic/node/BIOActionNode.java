package com.linkstec.bee.UI.spective.basic.logic.node;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.spective.basic.logic.edit.BActionModel;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxCellState;

public class BIOActionNode extends BNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4688595302692918581L;
	private BActionModel model;

	public BIOActionNode(BActionModel model) {
		this.model = model;
		this.setVertex(true);
		this.getGeometry().setWidth(150);
		this.getGeometry().setHeight(60);
		this.setStyle("rectangle;shape=doubleRectangle;strokeWidth=0.5;strokeColor=gray;fillColor=none");

		this.getGeometry().setRelative(false);
		this.getGeometry().setX(200);
		this.getGeometry().setY(200);
	}

	@Override
	public void paintBefore(Graphics g, mxCellState state, double scale) {
		if (model != null) {
			mxRectangle box = state.getBoundingBox();
			Rectangle rect = box.getRectangle();
			g.setColor(BeeConstants.BACKGROUND_COLOR);
			g.fillRect(rect.x, rect.y, rect.width, rect.height);
		}
	}

	@Override
	public void paint(Graphics g, mxCellState state, double scale) {
		if (model != null) {
			mxRectangle box = state.getBoundingBox();
			Rectangle rect = box.getRectangle();

			g.setFont(BeeUIUtils.getDefaultFont());
			String name = model.getName();
			FontMetrics m = g.getFontMetrics();
			int width = m.stringWidth(name);

			int y = rect.y + rect.height / 2 - m.getHeight() + m.getAscent();
			int x = rect.x + (rect.width - width) / 2;
			g.setColor(Color.BLACK);
			g.drawString(name, x, y);
			int y2 = y + m.getDescent();
			g.drawLine(x, y2, x + width, y2);

			name = model.getLogicName();
			width = m.stringWidth(name);
			y = y2 + m.getHeight();
			x = rect.x + (rect.width - width) / 2;
			g.drawString(name, x, y);
		}
	}

	public BActionModel getModel() {
		return this.model;
	}
}

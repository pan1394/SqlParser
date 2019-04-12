package com.linkstec.bee.UI.spective.basic.logic.node;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.linkstec.bee.UI.BeeUIUtils;
import com.linkstec.bee.UI.spective.basic.logic.model.BClassEnd;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.IEndCell;
import com.linkstec.bee.core.fw.basic.IExceptionCell;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxCellState;

public class BFlowEnd extends BNode implements IEndCell {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1467695367864124769L;

	private String name;

	private BClassEnd logic;

	public BFlowEnd(String name, BClassEnd logic) {
		this.name = name;
		this.setStyle("rounded=1;strokeWidth=0.5;strokeColor=gray");
		this.setVertex(true);
		this.getGeometry().setWidth(100);
		this.getGeometry().setHeight(50);
		this.logic = logic;
	}

	@Override
	public void paint(Graphics g, mxCellState state, double scale) {
		if (name != null) {
			mxRectangle box = state.getBoundingBox();
			Rectangle rect = box.getRectangle();

			g.setFont(BeeUIUtils.getDefaultFont());
			FontMetrics m = g.getFontMetrics();
			int width = m.stringWidth(name);

			int y = rect.y + 25 + m.getAscent();
			int x = rect.x + (rect.width - width) / 2;
			g.setColor(Color.BLACK);
			g.drawString(name, x, y);
			int y2 = y + m.getDescent();
			g.drawLine(x, y2, x + width, y2);
		}
	}

	@Override
	public IExceptionCell getExcetion() {
		return null;
	}

	@Override
	public BLogic getLogic() {
		return logic;
	}

}

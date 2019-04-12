package com.linkstec.bee.UI.spective.basic.logic.node;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.look.tab.BeeTabbedPane;
import com.linkstec.bee.UI.spective.basic.BasicBook;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants;
import com.linkstec.bee.UI.spective.basic.data.BasicComponentModel;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxCellState;

public class BComponentNode extends BNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3337997567109036190L;
	private BasicComponentModel model;

	public BComponentNode() {
		this.getGeometry().setWidth(100);
		this.getGeometry().setHeight(50);
		this.setStyle("strokeWidth=0.5;strokeColor=gray");
		this.setVertex(true);
	}

	public BasicComponentModel getModel() {
		return model;
	}

	public void setModel(BasicComponentModel model) {
		this.model = model;
		if (model.getType().getIconPath().equals(ModelConstants.APPLICATION_ICON)) {
			this.setStyle("rounded=1;" + this.getStyle());
		} else if (model.getType().getIconPath().equals(ModelConstants.FILE_ICON)) {
			this.setStyle("rectangle;shape=doubleRectangle;" + this.getStyle());
		} else if (model.getType().getIconPath().equals(ModelConstants.DB_ICON)) {
			this.setStyle(
					"shape=cylinder;fillColor=" + BeeConstants.ELEGANT_BRIGHTER_GREEN_COLOR + ";" + this.getStyle());
		}
	}

	@Override
	public void paint(Graphics g, mxCellState state, double scale) {
		if (model != null) {
			mxRectangle box = state.getBoundingBox();
			Rectangle rect = box.getRectangle();

			// g.setFont(BeeUIUtils.getDefaultFont());
			String name = model.getName();
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
	public void doubleClicked(BasicLogicSheet sheet) {
		BeeTabbedPane pane = sheet.findTopPane();
		if (pane != null && pane instanceof BasicBook) {
			// BasicDataSheet data = BasicActions.openDictionary(sheet.getProject(),
			// sheet.getSub());
			// data.addData(model);
		}
	}

}

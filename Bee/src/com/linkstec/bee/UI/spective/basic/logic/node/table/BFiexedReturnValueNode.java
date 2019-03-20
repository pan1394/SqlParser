package com.linkstec.bee.UI.spective.basic.logic.node.table;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.mxgraph.view.mxCellState;

public class BFiexedReturnValueNode extends BFixedValueNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7718829358684743495L;

	public BFiexedReturnValueNode() {
		this.setStyle("opacity=0");
		this.setValue("  ");
	}

	@Override
	public void paint(Graphics g, mxCellState state) {
		Rectangle rect = state.getRectangle();
		FontMetrics mericts = g.getFontMetrics();
		int height = mericts.getHeight();
		Image img = BeeConstants.NEXT_ICON.getImage();
		g.drawImage(img, rect.x + height / 3, rect.y + 10, height, height, null);
	}

	@Override
	public String getSQL(ITableSql tsql) {
		return "";
	}
}

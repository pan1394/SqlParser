package com.linkstec.bee.UI.spective.basic.logic;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Map;

import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;

public class BasicCanvas extends mxInteractiveCanvas {

	@Override
	public Object drawCell(mxCellState state) {
		Object cell = state.getCell();
		if (cell instanceof BNode) {
			BNode node = (BNode) cell;
			Map<String, Object> style = state.getStyle();
			Graphics2D previousGraphics = g;

			g = createTemporaryGraphics(style, 100, null);

			Font font = mxUtils.getFont(style, this.getScale());
			g.setFont(font);
			node.paintBefore(g, state);

			g.dispose();
			g = previousGraphics;
		}

		Object obj = super.drawCell(state);

		if (cell instanceof BNode) {
			BNode node = (BNode) cell;
			Map<String, Object> style = state.getStyle();
			Graphics2D previousGraphics = g;

			g = createTemporaryGraphics(style, 100, null);
			Font font = mxUtils.getFont(style, this.getScale());
			g.setFont(font);
			node.paint(g, state);

			g.dispose();
			g = previousGraphics;
		}
		return obj;
	}

}

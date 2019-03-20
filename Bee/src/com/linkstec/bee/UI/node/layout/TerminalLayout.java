package com.linkstec.bee.UI.node.layout;

import java.io.Serializable;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.node.BasicNode;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxRectangle;

public class TerminalLayout extends LayoutBasic implements Serializable, ILayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7055310750940845663L;

	@Override
	public void addNode(BasicNode node) {

	}

	@Override
	public void addNode(BasicNode node, double height, int index) {

	}

	@Override
	public void setSpacing(double spacing) {

	}

	@Override
	public void removeNode(BasicNode node) {

	}

	@Override
	public void layout() {
		BasicNode node = this.getContainer();
		int count = node.getChildCount();
		double w = 0;
		double h = 0;
		for (int i = 0; i < count; i++) {
			mxCell cell = (mxCell) node.getChildAt(i);
			double width = cell.getGeometry().getWidth() + cell.getGeometry().getOffset().getX();
			double height = cell.getGeometry().getHeight() + cell.getGeometry().getOffset().getY();
			w = Math.max(w, width + BeeConstants.NODE_SPACING);
			h = Math.max(h, height + BeeConstants.NODE_SPACING);
		}
		node.getGeometry().setWidth(w);
		node.getGeometry().setHeight(h);

		for (int i = 0; i < count; i++) {
			mxCell cell = (mxCell) node.getChildAt(i);
			if (cell instanceof BasicNode) {
				BasicNode c = (BasicNode) cell;
				if (c.getLayout() != null) {
					c.getLayout().layout();
				}
			}
		}
	}

	@Override
	public void layout(mxRectangle rect) {

	}

	@Override
	public void addLayoutListener(LayoutListener listener) {

	}

	@Override
	public void removeLayoutListener(LayoutListener listener) {

	}

	@Override
	public void clearLayoutListener() {

	}

	@Override
	public double getBetweenSpacing() {
		return 0;
	}

	@Override
	public void setBetweenSpacing(double betweenSpacing) {

	}

	@Override
	public void setMaxWidth(double width) {

	}

	@Override
	public double getMaxWidth() {

		return 0;
	}

	@Override
	public void addNode(BasicNode node, int index) {
		// TODO Auto-generated method stub

	}

}

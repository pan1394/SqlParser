package com.linkstec.bee.UI.spective.basic.logic.node.table;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.core.fw.BParameter;
import com.mxgraph.model.mxICell;

public class BTableRowCellNode extends BNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2170726007583904727L;

	protected BParameter target = null;

	public BTableRowCellNode(Object value, int width) {
		this.setVertex(true);
		this.getGeometry().setRelative(true);
		this.getGeometry().setWidth(width);
		this.getGeometry().setHeight(BeeConstants.LINE_HEIGHT);
		if (value instanceof BParameter) {
			this.target = (BParameter) value;
		} else {
			this.setValue(value);
		}
		this.setConnectable(false);
		this.setDeletable(false);
		this.setStyle("strokeWidth=0.5;strokeColor=gray");
	}

	@Override
	public void resized(BasicLogicSheet sheet) {
		double height = this.getGeometry().getHeight();
		double width = this.getGeometry().getWidth();
		mxICell parent = this.getParent();
		if (parent instanceof BTableRowNode) {
			BTableRowNode node = (BTableRowNode) parent;
			int index = node.getIndex(this);

			mxICell table = node.getParent();
			if (table instanceof BTableNode) {
				int c = table.getChildCount();
				for (int j = 0; j < c; j++) {
					mxICell n = table.getChildAt(j);
					int count = n.getChildCount();
					double x = 0;
					for (int i = 0; i < count; i++) {
						mxICell cell = n.getChildAt(i);
						if (n.equals(node)) {
							cell.getGeometry().setHeight(height);
						}
						if (i > index) {
							cell.getGeometry().getOffset().setX(x);
						}
						if (i == index) {
							cell.getGeometry().setWidth(width);
						}
						x = cell.getGeometry().getOffset().getX() + cell.getGeometry().getWidth();
					}
				}
			}

		}
	}

	@Override
	public boolean isValidDropTarget(Object[] cells) {
		return false;
	}

}

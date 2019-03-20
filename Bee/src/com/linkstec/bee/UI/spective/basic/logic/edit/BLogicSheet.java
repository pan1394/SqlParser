package com.linkstec.bee.UI.spective.basic.logic.edit;

import java.awt.FontMetrics;
import java.awt.Point;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.logic.node.BConnector;
import com.linkstec.bee.UI.spective.basic.logic.node.BEnd;
import com.linkstec.bee.UI.spective.basic.logic.node.BLogicNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BNodeNumber;
import com.linkstec.bee.UI.spective.basic.logic.node.BProcessNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BStart;
import com.linkstec.bee.UI.spective.basic.logic.node.layout.BLogicLayout;
import com.linkstec.bee.core.fw.editor.BProject;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;

public class BLogicSheet extends BPatternSheet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9182546804248315711L;

	public BLogicSheet(BProject project, BLogicModel model) {
		super(project, model);
	}

	public void insertStart() {

	}

	public void changeTabName(String name) {
		this.setTabName(name);
	}

	public void setModelName(String name) {
		BLogicModel model = (BLogicModel) this.getGraph().getModel();
		model.setName(name);
	}

	public void setModelLogicName(String name) {
		BLogicModel model = (BLogicModel) this.getGraph().getModel();
		model.setLogicName(name);
	}

	@Override
	public Object[] importCells(Object[] cells, double dx, double dy, Object target, Point location) {

		Object[] objects = super.importCells(cells, dx, dy, target, location);

		return objects;
	}

	@Override
	public void onSelected() {
		super.onSelected();
		BLogicLayout.layoutNodes(this);
	}

	@Override
	public double layoutNode() {
		mxICell root = this.getRoot();
		int count = root.getChildCount();
		double width = BeeConstants.PAGE_SPACING_LEFT;

		double max = 0;

		for (int i = 0; i < count; i++) {

			mxICell obj = root.getChildAt(i);
			if (obj instanceof BProcessNode) {
				if (obj.isVertex()) {
					BProcessNode node = (BProcessNode) obj;

					this.LayoutProcess(node, this);
					node.getGeometry().setX(BeeConstants.NODE_SPACING + width);
					width = width + node.getGeometry().getWidth() + BeeConstants.NODE_SPACING;

					BStart start = node.getStart();
					BLogicNode parent = null;

					if (start == null) {
						continue;
					}

					int ecount = start.getEdgeCount();
					for (int j = 0; j < ecount; j++) {
						mxICell edge = start.getEdgeAt(j);
						if (edge instanceof BConnector) {
							BConnector c = (BConnector) edge;
							mxICell source = c.getSource();
							if (source instanceof BLogicNode) {
								parent = (BLogicNode) source;
							}
						}
					}
					if (parent != null) {
						mxICell nodeParent = parent.getParent();
						if (parent.getGeometry().getOffset() != null && nodeParent != null) {
							if (nodeParent.getGeometry() != null) {
								double y = parent.getGeometry().getOffset().getY() + nodeParent.getGeometry().getY()
										+ parent.getGeometry().getHeight() / 2;

								node.getGeometry().setY(y);
							}
						}

					} else {
						node.getGeometry().setY(BeeConstants.PAGE_SPACING_TOP * 7);
					}

					max = Math.max(max, node.getGeometry().getY() + node.getGeometry().getHeight());

				}
			}
		}
		return max;
	}

	public double LayoutProcess(BProcessNode p, BasicLogicSheet sheet) {
		int count = p.getChildCount();
		double height = BeeConstants.PAGE_SPACING_TOP * 4;
		p.getGeometry().setY(height);
		// BStart start = null;
		BEnd end = null;
		int number = 1;
		for (int i = 0; i < count; i++) {
			mxICell obj = p.getChildAt(i);
			if (obj instanceof BNode) {
				if (obj.isVertex()) {
					BNode node = (BNode) obj;

					mxCellState state = sheet.getGraph().getView().getState(node);
					FontMetrics metrics = mxUtils.getFontMetrics(mxUtils.getFont(state.getStyle()));

					node.reshape(metrics);
					if (node instanceof BLogicNode) {
						BLogicNode logic = (BLogicNode) node;

						BNodeNumber n = logic.getNumber();
						if (n != null) {
							n.setValue(number);
							number++;
						}
					}

					if (node instanceof BEnd) {
						end = (BEnd) node;

					} else if (node instanceof BStart) {
						// start = (BStart) node;
					} else {
						double x = (p.getGeometry().getWidth() - node.getGeometry().getWidth()) / 2;
						double y = height;
						node.getGeometry().setOffset(new mxPoint(x, y));
						height = height + node.getGeometry().getHeight() + BeeConstants.PAGE_SEGMENT_GAP;
					}
				}
			}
		}
		if (end != null) {
			end.getGeometry().getOffset().setY(height + 100);
			height = height + 100 + end.getGeometry().getHeight() / 2;
		}

		p.getGeometry().setHeight(height);

		return height;
	}

}

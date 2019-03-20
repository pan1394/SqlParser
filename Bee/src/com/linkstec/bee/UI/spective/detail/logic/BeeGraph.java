package com.linkstec.bee.UI.spective.detail.logic;

import java.beans.PropertyChangeSupport;

import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.spective.detail.BeeGraphSheet;
import com.linkstec.bee.UI.spective.detail.action.BeeActions;
import com.linkstec.bee.UI.spective.detail.action.BeeHandlers;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.logic.BLogicBody;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.view.mxGraph;

/**
 * グラフ制御
 *
 */
public class BeeGraph extends mxGraph {
	/**
	 * Holds the edge to be used as a template for inserting new edges.
	 */
	protected Object edgeTemplate;

	/**
	 * Custom graph that defines the alternate edge style to be used when the middle
	 * control point of edges is double clicked (flipped).
	 */
	public BeeGraph() {
		super(new BeeModel());
		setAlternateEdgeStyle("edgeStyle=mxEdgeStyle.ElbowConnector;elbow=vertical");

		addListener(mxEvent.CELLS_RESIZED, new BeeHandlers.ResizeHandler());

		addListener(mxEvent.CELLS_REMOVED, new BeeHandlers.CellRemovedHandler(this));
		getView().addListener(mxEvent.SCALE, Application.getInstance().getEditor().getToolbar().getScaleTracker());
		getView().addListener(mxEvent.SCALE_AND_TRANSLATE,
				Application.getInstance().getEditor().getToolbar().getScaleTracker());

		setAllowNegativeCoordinates(false);
		setCellsResizable(true);
		setMultigraph(true);
		// setHtmlLabels(true);

		setPortsEnabled(true);
		// Do not change the scale and translation after files have been loaded
		setResetViewOnRootChange(false);

	}

	public void installHandlers(BeeGraphSheet sheet) {
		addListener(mxEvent.CELLS_ADDED, new BeeHandlers.SelectedHandler(sheet));
		addListener(mxEvent.MOVE_CELLS, new BeeHandlers.CellMoveHandler(sheet));
		getSelectionModel().addListener(mxEvent.CHANGE, new BeeHandlers.SelectedHandler(sheet));
	}

	/**
	 * グラフのテンプレートを設定する。
	 * 
	 * @param template
	 *            template
	 */
	public void setEdgeTemplate(Object template) {
		edgeTemplate = template;
	}

	@Override
	public boolean isCellFoldable(Object cell, boolean collapse) {

		// if (cell instanceof ReferNode) {
		// ReferNode node = (ReferNode) cell;
		// if (node.isLinker()) {
		// return true;
		// }
		// }
		return false;
	}

	@Override
	public boolean isCellEditable(Object cell) {
		if (cell instanceof BasicNode) {
			BasicNode b = (BasicNode) cell;
			return b.isEditable();
		}
		return super.isCellEditable(cell);
	}

	@Override
	public boolean isValidDropTarget(Object cell, Object[] cells) {

		boolean valid = BeeActions.isValidDropTarget(cell, cells);
		return valid;
	}

	public Object createEdge(Object parent, String id, Object value, Object source, Object target, String style) {
		if (edgeTemplate != null) {
			mxCell edge = (mxCell) cloneCells(new Object[] { edgeTemplate })[0];
			edge.setId(id);

			return edge;
		}

		return super.createEdge(parent, id, value, source, target, style);
	}

	public PropertyChangeSupport getChangeSupport() {
		return this.changeSupport;
	}

	@Override
	public boolean isCellResizable(Object cell) {
		if (cell instanceof IUnit) {
			return true;
		}
		return super.isCellResizable(cell);
	}

	@Override
	public boolean isCellConnectable(Object cell) {
		return false;
	}

	@Override
	public boolean isCellMovable(Object cell) {
		if (cell instanceof IUnit || cell instanceof BValuable) {
			return true;
		}
		return super.isCellMovable(cell);
	}

	@Override
	public boolean isCellSelectable(Object cell) {
		if (cell instanceof mxCell) {
			mxCell mx = (mxCell) cell;
			String style = mx.getStyle();
			if (style != null && style.contains("name=indicator")) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isCellDeletable(Object cell) {

		if (cell instanceof BasicNode) {
			BasicNode b = (BasicNode) cell;
			if (cell instanceof IUnit) {
				if (b.getParent() instanceof BLogicBody) {
					return true;
				}
			}
			return b.isDeleteable();
		} else if (cell instanceof mxCell) {
			mxCell mx = (mxCell) cell;
			String style = mx.getStyle();
			if (style != null && style.contains("name=indicator")) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void cellsRemoved(Object[] cells) {
		if (cells != null && cells.length == 1) {
			Object obj = cells[0];
			if (obj instanceof BasicNode) {
				BasicNode b = (BasicNode) obj;
				Object parent = b.getParent();
				if (parent != null && parent instanceof BasicNode) {
					BasicNode p = (BasicNode) parent;
					if (!p.childRemovable(b, this)) {
						fireEvent(new mxEventObject(mxEvent.CELLS_REMOVED, "cells", cells));
						return;
					}
				}

				b.beforeRemoved(this);
			}
		}
		super.cellsRemoved(cells);
	}

	@Override
	public Object getDefaultParent() {
		mxIGraphModel model = this.getModel();
		Object obj = BeeActions.foundRoot((mxICell) model.getRoot());
		if (obj != null) {
			return obj;
		} else {
			return model.getRoot();
		}
		// return super.getDefaultParent();
	}

}

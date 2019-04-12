package com.linkstec.bee.UI.spective.basic.logic;

import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.UI.spective.basic.logic.node.BGroupNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.core.Application;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxGraph;

public class BasicGraph extends mxGraph {
	private transient BasicLogicSheet sheet;

	public BasicGraph(SubSystem sub) {
		setAlternateEdgeStyle("edgeStyle=mxEdgeStyle.ElbowConnector;");
		BasicModel model = new BasicModel(sub);
		this.setModel(model);

		getView().addListener(mxEvent.SCALE, Application.getInstance().getEditor().getToolbar().getScaleTracker());
		getView().addListener(mxEvent.SCALE_AND_TRANSLATE,
				Application.getInstance().getEditor().getToolbar().getScaleTracker());
	}

	public BasicLogicSheet getSheet() {
		return sheet;
	}

	public void setSheet(BasicLogicSheet sheet) {
		this.sheet = sheet;
	}

	@Override
	public void cellLabelChanged(Object cell, Object value, boolean autoSize) {

		if (cell instanceof BNode) {
			BNode node = (BNode) cell;
			if (value instanceof String) {
				if (node.labelChanged((String) value)) {
					this.refresh();
					return;
				}
			}
		}
		super.cellLabelChanged(cell, value, autoSize);
	}

	@Override
	public Object addCell(Object cell, Object parent) {
		Object obj = super.addCell(cell, parent);

		if (parent instanceof BNode) {
			BNode node = (BNode) parent;
			if (cell instanceof BNode) {
				BNode child = (BNode) cell;
				node.childAdded(child, sheet);
			}

		}

		return obj;
	}

	@Override
	public boolean isCellEditable(Object cell) {
		if (cell instanceof BNode) {
			BNode node = (BNode) cell;
			return node.isEditable();
		}
		return false;
	}

	@Override
	public void cellsRemoved(Object[] cells) {
		// TODO Auto-generated method stub
		super.cellsRemoved(cells);
	}

	@Override
	public boolean isTerminalPointMovable(Object cell, boolean source) {
		return true;// super.isTerminalPointMovable(cell, source);
	}

	@Override
	public boolean isEdgeLabelsMovable() {
		return true;// super.isEdgeLabelsMovable();
	}

	@Override
	public boolean isValidDropTarget(Object cell, Object[] cells) {
		if (cells == null) {
			return false;
		}
		if (cells.length == 1) {
			if (cells[0].equals(cell)) {
				return false;
			}
		}
		if (cell instanceof BNode) {
			BNode node = (BNode) cell;
			return node.isValidDropTarget(cells);
		}
		return super.isValidDropTarget(cell, cells);
	}

	public Object createGroupCell(Object[] cells) {
		mxIGraphModel model = this.getModel();

		if (model instanceof BasicModel) {
			BasicModel basic = (BasicModel) model;
			BGroupNode group = basic.getGroupNode();
			return group;
		}
		return null;
	}

	@Override
	public boolean isCellFoldable(Object cell, boolean collapse) {
		if (cell instanceof BGroupNode) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isCellLocked(Object cell) {
		if (cell instanceof BNode) {
			BNode node = (BNode) cell;
			return node.isLocked();
		}
		return super.isCellLocked(cell);
	}

	public Object getDefaultParent() {
		return super.getDefaultParent();
	}

	@Override
	public Object[] foldCells(boolean collapse, boolean recurse, Object[] cells, boolean checkFoldable) {

		Object[] folds = super.foldCells(collapse, recurse, cells, checkFoldable);
		for (Object cell : folds) {
			if (cell instanceof BNode) {
				BNode node = (BNode) cell;
				node.cellFolded(this, !collapse);
			}
		}

		return folds;
	}

	@Override
	public boolean isCellResizable(Object cell) {
		if (cell instanceof BNode) {
			BNode node = (BNode) cell;
			return node.isResizable();
		}
		return true;
	}

	@Override
	public boolean isCellConnectable(Object cell) {

		boolean b = super.isCellConnectable(cell);
		return b;
	}

	@Override
	public boolean isCellSelectable(Object cell) {
		if (cell instanceof BNode) {
			BNode node = (BNode) cell;
			return node.isSelectable();
		}
		return true;
	}

	@Override
	public boolean isCellDeletable(Object cell) {
		if (cell instanceof BNode) {
			BNode node = (BNode) cell;
			return node.isDeletable();
		}
		return true;
	}

	@Override
	public boolean isCellMovable(Object cell) {
		if (cell instanceof BNode) {
			BNode node = (BNode) cell;
			return node.isMoveable();
		}
		return true;
	}

}

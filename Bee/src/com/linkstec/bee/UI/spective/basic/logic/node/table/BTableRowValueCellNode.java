package com.linkstec.bee.UI.spective.basic.logic.node.table;

import java.util.List;

import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.logic.model.SetterLogic;
import com.linkstec.bee.UI.spective.basic.logic.model.var.SQLMakeUtils;
import com.linkstec.bee.UI.spective.basic.logic.node.BDetailNodeWrapper;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BTansferHolderNode;
import com.linkstec.bee.core.codec.basic.BasicGenUtils;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.IExceptionCell;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.basic.ISetterLogicCell;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.mxgraph.model.mxICell;

public class BTableRowValueCellNode extends BTableRowCellNode implements ISetterLogicCell {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5621462774308227511L;
	private SetterLogic logic;
	private BInvoker invoker;
	private boolean isSqlEdtior = false;

	public BTableRowValueCellNode(BPath path, BInvoker invoker, int width) {
		super(invoker.getInvokeChild(), width);
		logic = new SetterLogic(path, this);
		this.invoker = invoker;
	}

	@Override
	public IExceptionCell getExcetion() {
		return null;
	}

	@Override
	public BLogic getLogic() {
		return logic;
	}

	@Override
	public boolean isValidDropTarget(Object[] cells) {
		if (target != null) {
			return true;
		}
		return false;
	}

	@Override
	public void childAdded(BNode node, BasicLogicSheet sheet) {
		if (this.isSqlEdtior) {
			BTableListHelper.childAdded(this, node, sheet);
			super.childAdded(node, sheet);
			return;
		}
		if (node instanceof BDetailNodeWrapper) {
			BDetailNodeWrapper wrapper = (BDetailNodeWrapper) node;
			BasicNode basic = wrapper.getNode();

			this.cellAdded(basic);
			node.removeFromParent();
		} else if (node instanceof BFixedValueNode) {
			node.setValue("");
			BNode title = new BNode();
			title.setValue("<SQL MODE>");
			title.setMoveable(false);
			title.setConnectable(false);
			title.setSelectable(false);
			title.setStyle("opacity=0");
			title.setVertex(true);
			title.getGeometry().setWidth(100);
			title.getGeometry().setHeight(30);
			title.getGeometry().setX(5);
			this.insert(title);
			this.isSqlEdtior = true;
		} else if (node instanceof BTansferHolderNode) {
			BTansferHolderNode n = (BTansferHolderNode) node;
			List<BNode> nodes = n.getNodes();
			for (BNode nd : nodes) {
				this.childAdded(nd, sheet);
			}
			node.removeFromParent();
		}
		if (node instanceof ILogicCell) {

		} else {
			node.removeFromParent();
		}

	}

	@Override
	public Object getValue() {
		if (this.isSqlEdtior) {
			if (this.getChildCount() == 1) {
				this.getChildAt(0).removeFromParent();
				isSqlEdtior = false;
			}
		}
		return super.getValue();
	}

	public boolean isSqlEdtior() {
		return isSqlEdtior;
	}

	public void setSqlEdtior(boolean isSqlEdtior) {
		this.isSqlEdtior = isSqlEdtior;
	}

	@Override
	public void cellAdded(mxICell cell) {

		if (cell instanceof BValuable) {
			BValuable v = (BValuable) cell;
			BTableRowValueCellValueNode node = new BTableRowValueCellValueNode(this.logic.getPath(), invoker, v);

			this.insert(node);
			cell.removeFromParent();
		} else if (cell instanceof BAssignment) {
			BAssignment a = (BAssignment) cell;
			BValuable value = a.getRight();
			if (value.getBClass() == null) {
				value = a.getLeft();
			}
			if (value instanceof mxICell) {
				cellAdded((mxICell) value);
			}
		}
	}

	@Override
	public ILogicCell getStart() {
		return BasicGenUtils.getStart(this);
	}

	public String getSQL(ITableSql sql) {

		if (this.isSqlEdtior) {
			return BTableListHelper.getSqlItemValue("", " ", BTableListHelper.getRecords(this), sql).getSql();
		} else {
			int count = this.getChildCount();
			for (int i = 0; i < count; i++) {
				mxICell child = this.getChildAt(i);
				if (child instanceof BTableRowValueCellValueNode) {
					BTableRowValueCellValueNode node = (BTableRowValueCellValueNode) child;
					BValuable value = node.getSetterParam();
					// TODO ,set invoker to sql info?
					return SQLMakeUtils.getLogicValueText(value, sql.getInvokers(), null);
				}
			}
		}
		return null;
	}

	public String getSQLExp(ITableSql sql) {
		if (this.isSqlEdtior) {
			return BTableListHelper.getSqlItemExp("", " ", BTableListHelper.getRecords(this), sql);
		} else {
			int count = this.getChildCount();
			for (int i = 0; i < count; i++) {
				mxICell child = this.getChildAt(i);
				if (child instanceof BTableRowValueCellValueNode) {
					BTableRowValueCellValueNode node = (BTableRowValueCellValueNode) child;
					BValuable value = node.getSetterParam();
					// TODO ,set invoker to sql info?
					return SQLMakeUtils.getValueText(value, false, null);
				}
			}
		}
		return null;
	}
}

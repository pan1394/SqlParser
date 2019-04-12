package com.linkstec.bee.UI.spective.basic.logic.node;

import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.logic.model.AssignLogic;
import com.linkstec.bee.UI.spective.basic.logic.model.var.JudgeLogic;
import com.linkstec.bee.UI.spective.basic.logic.node.layout.BLogicLayout;
import com.linkstec.bee.core.codec.util.BValueUtils;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.IAssignCell;
import com.linkstec.bee.core.fw.basic.IExceptionCell;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.mxgraph.model.mxICell;

public class BAssignNode extends BNode implements IAssignCell {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5621462774308227511L;
	private AssignLogic logic;

	private BValuable definedValue;
	private BValuable target;

	public BAssignNode(BPath path, BValuable var) {
		this.setStyle("align=center;strokeWidth=0.5;strokeColor=gray;fillColor=yellow;rounded=1");
		logic = new AssignLogic(path, this);
		this.definedValue = var;
		this.setVertex(true);
		this.getGeometry().setWidth(200);
		this.getGeometry().setHeight(40);
		this.makeValue();
	}

	@Override
	public IExceptionCell getExcetion() {
		return null;
	}

	@Override
	public void resized(BasicLogicSheet sheet) {
		this.makeValue();
	}

	@Override
	public BLogic getLogic() {
		return logic;
	}

	@Override
	public boolean isValidDropTarget(Object[] cells) {
		return true;
	}

	@Override
	public void childAdded(BNode node, BasicLogicSheet sheet) {
		if (node instanceof BDetailNodeWrapper) {
			BDetailNodeWrapper n = (BDetailNodeWrapper) node;
			BasicNode b = n.getNode();
			this.cellAdded(b);
		} else if (node instanceof BLogicNode) {
			BLogicNode n = (BLogicNode) node;
			BLogic logic = n.getLogic();
			if (logic instanceof JudgeLogic) {
				JudgeLogic judge = (JudgeLogic) logic;
				BValuable value = judge.getExpression(null);
				if (value instanceof mxICell) {
					this.cellAdded((mxICell) value);
				}
			}
		}
		node.removeFromParent();
	}

	@Override
	public void cellAdded(mxICell cell) {
		if (cell instanceof BValuable) {
			BValuable value = (BValuable) cell;
			this.target = value;
			this.setStyle("align=center;strokeWidth=0.5;strokeColor=gray;fillColor=white;rounded=1");
		} else if (cell instanceof BAssignment) {
			BAssignment a = (BAssignment) cell;
			BValuable value = a.getRight();
			if (value instanceof mxICell) {
				if (value.getBClass() == null) {
					value = a.getLeft();
				}
				this.cellAdded((mxICell) value);
			}
		} else {
			this.setStyle("align=center;strokeWidth=0.5;strokeColor=gray;fillColor=yellow;rounded=1");
		}
		cell.removeFromParent();
		this.logic.getPath().setCell(this);
		this.makeValue();

	}

	public void makeValue() {
		String data = BValueUtils.createValuable(definedValue, false);
		String s = null;
		if (this.target == null) {
			s = data + "\r\nに設定する値をここへドラッグしてください";
		} else {
			s = data + "=" + this.getValueName(target);
		}
		BNode node = BLogicLayout.reshape(this, s);
		this.setGeometry(node.getGeometry());
		this.setValue(node.getValue());
	}

	private String getValueName(BValuable value) {
		return BValueUtils.createValuable(value, false);
	}

	@Override
	public BValuable getVariable() {
		return this.definedValue;
	}

	@Override
	public BValuable getAssignValue() {
		return this.target;
	}

}

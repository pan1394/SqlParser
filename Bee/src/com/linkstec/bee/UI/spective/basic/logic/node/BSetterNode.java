package com.linkstec.bee.UI.spective.basic.logic.node;

import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.logic.model.SingleSetterLogic;
import com.linkstec.bee.UI.spective.basic.logic.node.layout.BLogicLayout;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.util.BValueUtils;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.IExceptionCell;
import com.linkstec.bee.core.fw.basic.ISingleSetterLogicCell;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.mxgraph.model.mxICell;

public class BSetterNode extends BNode implements ISingleSetterLogicCell {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5621462774308227511L;
	private SingleSetterLogic logic;
	private BInvoker invoker;
	private BValuable target;

	public BSetterNode(BPath path, BValuable parent, BVariable value) {
		this.setStyle("align=center;strokeWidth=0.5;strokeColor=gray;fillColor=yellow;rounded=1");
		logic = new SingleSetterLogic(path, this, value);
		IPatternCreator view = PatternCreatorFactory.createView();
		invoker = view.createMethodInvoker();
		invoker.setInvokeParent(parent);
		invoker.setInvokeChild(value);
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
		String data = this.getValueName(invoker);
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
	public BInvoker getSetterParent() {
		return invoker;
	}

	@Override
	public BValuable getSetterParameter() {
		return this.target;
	}
}

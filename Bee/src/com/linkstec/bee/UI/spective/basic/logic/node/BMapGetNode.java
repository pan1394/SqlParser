package com.linkstec.bee.UI.spective.basic.logic.node;

import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.logic.model.var.MapGetLogic;
import com.linkstec.bee.UI.spective.basic.logic.node.layout.BLogicLayout;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.IExceptionCell;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.mxgraph.model.mxICell;

public class BMapGetNode extends BNode implements ILogicCell {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4301030782833490217L;
	private MapGetLogic logic;

	public BMapGetNode() {
		this.setStyle("align=center;strokeWidth=0.5;strokeColor=gray;rounded=1");
		this.setVertex(true);
		this.getGeometry().setWidth(200);
		this.getGeometry().setHeight(40);
	}

	@Override
	public Object getValue() {
		String s = this.logic.getDesc();
		BNode node = BLogicLayout.reshape(this, s);
		return node.getValue();
	}

	@Override
	public boolean isValidDropTarget(Object[] cells) {
		return true;
	}

	@Override
	public IExceptionCell getExcetion() {
		return null;
	}

	@Override
	public BLogic getLogic() {
		return logic;
	}

	public void setLogic(MapGetLogic logic) {
		this.logic = logic;
	}

	@Override
	public void childAdded(BNode node, BasicLogicSheet sheet) {
		if (node instanceof BDetailNodeWrapper) {
			BDetailNodeWrapper wrapper = (BDetailNodeWrapper) node;
			BasicNode detail = wrapper.getNode();
			this.cellAdded(detail);
		}
		node.removeFromParent();
	}

	@Override
	public void cellAdded(mxICell cell) {
		if (cell == null) {
			return;
		}
		if (cell instanceof BValuable) {
			BValuable value = (BValuable) cell;
			BClass bclass = value.getBClass();
			if (bclass != null) {
				logic.setKey(value);
			}
		} else if (cell instanceof BAssignment) {
			BAssignment assign = (BAssignment) cell;
			cellAdded((mxICell) assign.getLeft());
		}
		cell.removeFromParent();
	}

}

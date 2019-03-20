package com.linkstec.bee.UI.spective.basic.logic.node;

import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.logic.model.var.ListAddLogic;
import com.linkstec.bee.UI.spective.basic.logic.node.layout.BLogicLayout;
import com.linkstec.bee.UI.thread.BeeThread;
import com.linkstec.bee.core.codec.basic.BasicUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.IExceptionCell;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.mxgraph.model.mxICell;

public class BListAddNode extends BNode implements ILogicCell {

	/**
	 * 
	 */
	private static final long serialVersionUID = -35333297701425349L;
	private ListAddLogic logic;

	public BListAddNode() {
		this.setStyle("align=center;strokeWidth=0.5;strokeColor=gray;rounded=1");
		this.setVertex(true);
		this.getGeometry().setWidth(200);
		this.getGeometry().setHeight(40);
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
	public Object getValue() {
		String s = this.logic.getDesc();
		BNode node = BLogicLayout.reshape(this, s);
		return node.getValue();
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
				logic.setAddValue(value);
				BClass b = bclass.cloneAll();
				if (value instanceof BVariable) {
					BVariable v = (BVariable) value;
					b.setName(v.getName());
				}

				//////////////////////////
				new BeeThread(new Runnable() {

					@Override
					public void run() {
						BasicUtils.findDefinedList(b, logic.getTarget(), logic.getPath());
					}

				}).start();

			}
		} else if (cell instanceof BAssignment) {
			BAssignment assign = (BAssignment) cell;
			cellAdded((mxICell) assign.getLeft());
		}
		cell.removeFromParent();
	}

	@Override
	public boolean isValidDropTarget(Object[] cells) {
		return true;
	}

	@Override
	public IExceptionCell getExcetion() {
		return null;
	}

	public void setLogic(ListAddLogic logic) {
		this.logic = logic;
	}

	@Override
	public BLogic getLogic() {
		return this.logic;
	}

}

package com.linkstec.bee.UI.spective.basic.logic.node.table;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.IExceptionCell;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxPoint;

public class BTableRowNode extends BNode implements ILogicCell {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3527036606522782L;

	public BTableRowNode(BPath path, BInvoker invoker, int index) {
		this.setVertex(true);
		this.getGeometry().setRelative(true);
		this.makeData(path, invoker, index);
	}

	public BTableRowNode() {
		this.makeHeaderCell("No.", 30);
		this.makeHeaderCell("項目名", 150);
		this.makeHeaderCell("英名", 150);
		this.makeHeaderCell("編集", 520);

	}

	private void makeHeaderCell(String name, int width) {
		BNode node = new BTableRowCellNode(name, width);
		node.setStyle(node.getStyle() + ";align=center;fontColor=white;fillColor=" + BeeConstants.ELEGANT_BLUE_COLOR);
		this.insert(node);
	}

	public void makeData(BPath path, BInvoker invoker, int index) {

		BNode number = new BTableRowCellNode(index + "", 30);
		number.setStyle(number.getStyle() + ";align=center;fillColor=f2f3f4");
		number.setMoveable(false);
		this.insert(number);

		BVariable var = (BVariable) invoker.getInvokeChild();
		BNode name = new BTableRowCellNode(var.getName() + "", 150);
		name.setStyle(name.getStyle() + ";fillColor=f2f3f4");
		name.setMoveable(false);
		this.insert(name);

		BNode logicname = new BTableRowCellNode(var.getLogicName() + "", 150);
		logicname.setStyle(logicname.getStyle() + ";fillColor=f2f3f4");
		logicname.setMoveable(false);
		this.insert(logicname);

		BNode node = this.makeCell(path, invoker, 520);
		node.setEditable(true);
	}

	private BNode makeCell(BPath path, BInvoker invoker, int width) {
		BNode node = new BTableRowValueCellNode(path, invoker, width);
		this.insert(node);
		return node;
	}

	public void doLayout() {
		int count = this.getChildCount();
		double height = 0;
		double width = 0;
		for (int i = 0; i < count; i++) {
			mxICell child = this.getChildAt(i);
			if (child instanceof BNode) {
				BNode node = (BNode) child;
				node.getGeometry().setOffset(new mxPoint(width, 0));
				width = width + node.getGeometry().getWidth();
				height = Math.max(node.getGeometry().getHeight(), height);
			}
		}
		this.getGeometry().setHeight(height);
		this.getGeometry().setWidth(width);
	}

	public BValuable getEditValue() {
		return CodecUtils.getNullValue();
	}

	@Override
	public IExceptionCell getExcetion() {
		return null;
	}

	@Override
	public BLogic getLogic() {
		int count = this.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell child = this.getChildAt(i);
			if (child instanceof BTableRowValueCellNode) {
				BTableRowValueCellNode node = (BTableRowValueCellNode) child;
				return node.getLogic();
			}
		}
		return null;
	}
}

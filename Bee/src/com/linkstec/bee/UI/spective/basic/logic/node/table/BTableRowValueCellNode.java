package com.linkstec.bee.UI.spective.basic.logic.node.table;

import java.util.List;

import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.logic.model.SetterLogic;
import com.linkstec.bee.UI.spective.basic.logic.node.BDetailNodeWrapper;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BTansferHolderNode;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.basic.BasicGenUtils;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.IExceptionCell;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.basic.ISetterLogicCell;
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
		if (node instanceof BDetailNodeWrapper) {
			BDetailNodeWrapper wrapper = (BDetailNodeWrapper) node;
			BasicNode basic = wrapper.getNode();

			this.cellAdded(basic);
			node.removeFromParent();
		} else if (node instanceof BFixedValueNode) {
			IPatternCreator view = PatternCreatorFactory.createView();
			BVariable var = view.createVariable();
			var.setName("\"固定値を編集してください\"");
			var.setLogicName("\"固定値を編集してください\"");
			var.setBClass(CodecUtils.BString());
			cellAdded((mxICell) var);
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
	public void cellAdded(mxICell cell) {

		if (cell instanceof BValuable) {
			BValuable v = (BValuable) cell;
			BTableRowValueCellValueNode node = new BTableRowValueCellValueNode(this.logic.getPath(), invoker, v);

			this.insert(node);
			cell.removeFromParent();
		} else if (cell instanceof BAssignment) {
			BAssignment a = (BAssignment) cell;
			BValuable value = a.getRight();
			if (value instanceof mxICell) {
				cellAdded((mxICell) value);
			}
		}
	}

	@Override
	public ILogicCell getStart() {
		return BasicGenUtils.getStart(this);
	}
}

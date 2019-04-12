package com.linkstec.bee.UI.spective.basic.logic.node.table;

import java.util.List;

import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.logic.model.DataTransferLogic;
import com.linkstec.bee.UI.spective.basic.logic.model.var.SQLMakeUtils;
import com.linkstec.bee.UI.spective.basic.logic.node.BDetailNodeWrapper;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BTansferHolderNode;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BLogicProvider;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.IExceptionCell;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxPoint;

public class BTableRowValueCellValueNode extends BNode implements ILogicCell {

	/**
	 * 
	 */
	private static final long serialVersionUID = -455929959370910629L;

	private BInvoker setter;
	private BValuable setterParam;
	private DataTransferLogic logic;

	public BTableRowValueCellValueNode(BPath parent, BInvoker setter, BValuable value) {
		this.setter = setter;
		this.setterParam = value;
		this.setVertex(true);
		this.setStyle("rounded=1;strokeColor=gray;strokeWidth=0.5;fillColor=f2f3f4");
		mxGeometry geo = this.getGeometry();
		geo.setOffset(new mxPoint());
		geo.setWidth(250);
		geo.setHeight(30);
		geo.setX(10);
		geo.setY(10);
		this.logic = new DataTransferLogic(parent, this);
	}

	@Override
	public void childAdded(BNode node, BasicLogicSheet sheet) {
		if (node instanceof BDetailNodeWrapper) {
			BDetailNodeWrapper wrapper = (BDetailNodeWrapper) node;
			BasicNode basic = wrapper.getNode();

			this.cellAdded(basic);
		} else if (node instanceof BTansferHolderNode) {
			BTansferHolderNode n = (BTansferHolderNode) node;
			List<BNode> nodes = n.getNodes();
			for (BNode nd : nodes) {
				this.childAdded(nd, sheet);
			}
		}
		node.removeFromParent();
	}

	public BValuable getSetterParam() {
		return setterParam;
	}

	@Override
	public void cellAdded(mxICell cell) {

		if (cell instanceof BAssignment) {
			BAssignment a = (BAssignment) cell;
			if (setterParam instanceof BVariable) {
				BVariable var = (BVariable) setterParam;
				BValuable right = a.getRight();
				boolean type = false;
				if (right instanceof BVariable) {
					BVariable v = (BVariable) right;

					if (v.getBClass() == null) {
						BClass bclass = a.getLeft().getBClass();
						var.setBClass(bclass);
						type = true;
					}

				}
				if (!type) {
					setterParam = a.getLeft();
				}
			}
		} else if (cell instanceof BValuable) {
			setterParam = (BValuable) cell;
		}
		cell.removeFromParent();
	}

	@Override
	public boolean isValidDropTarget(Object[] cells) {
		return true;
	}

	@Override
	public boolean isDropTarget(BNode source) {
		return true;
	}

	@Override
	public boolean isEditable() {
		return true;
	}

	@Override
	public boolean labelChanged(String label) {
		if (setterParam instanceof BVariable) {
			BVariable var = (BVariable) setterParam;
			CodecUtils.setVarValue(var, label);
			return true;
		}
		return super.labelChanged(label);
	}

	public Object getValue() {
		if (setterParam == null) {
			return "nullを設定する";
		}
		if (setterParam.getBClass() == null) {
			setterParam = CodecUtils.getNullValue();
			return "nullを設定する";
		}
		String s = SQLMakeUtils.getValueText(setterParam, false, null);

		if (setterParam instanceof BVariable) {
			BVariable var = (BVariable) setterParam;

			s = s + "[" + var.getBClass().getName() + "]";
		}
		s = s + "を設定する";

		// if (setterParam instanceof BVariable) {
		// s = s + "\r\n" + "※値タイプを変えるには右側のリソースからクラスをドラッグしてください";
		// }

		return s;
	}

	@Override
	public IExceptionCell getExcetion() {
		return null;
	}

	@Override
	public BLogic getLogic() {
		return logic;
	}

	public BInvoker getTransferLogic() {
		BParameter maybechangedValue = (BParameter) this.getLogic().getPath().getProvider().getProperties()
				.getThreadScopeAttribute("TABLE_LIST_PARENT_PARAMTER");
		if (maybechangedValue != null) {
			this.setter.setInvokeParent((BValuable) maybechangedValue.cloneAll());
		}
		BInvoker invoker = (BInvoker) this.setter.cloneAll();
		BValuable para = (BValuable) setterParam.cloneAll();

		BLogicProvider p = this.logic.getPath().getProvider();
		if (!p.onDataTransfer(invoker, para)) {
			invoker.addParameter(para);
		}
		return invoker;
	}

}

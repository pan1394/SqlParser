package com.linkstec.bee.UI.spective.basic.logic.node.table;

import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.logic.model.table.BFixedInputLogic;
import com.linkstec.bee.UI.spective.basic.logic.model.var.SQLMakeUtils;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.util.BValueUtils;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.mxgraph.model.mxICell;

public class BFixedInputValueNode extends BFixedValueNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1128850982394242452L;

	private BInvoker invoker;
	private BFixedInputLogic logic;
	private BValuable editedValue = null;

	public BFixedInputValueNode(BInvoker invoker) {
		this.invoker = invoker;
		this.setStyle("strokeColor=gray;strokeWidth=0.5;fillColor=f8c471");
	}

	@Override
	public void childAdded(BNode node, BasicLogicSheet sheet) {
		node.removeFromParent();
	}

	@Override
	public void cellAdded(mxICell cell) {
		if (cell instanceof BValuable) {
			this.editedValue = (BValuable) cell;
		}
		cell.removeFromParent();
	}

	@Override
	public void added(BasicLogicSheet sheet) {
		IPatternCreator view = PatternCreatorFactory.createView();
		BVariable var = view.createVariable();
		var.setBClass(CodecUtils.BString());
		var.setLogicName("value");
		var.setName("value");

		this.editedValue = var;
		super.added(sheet);
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof String) {
			String name = (String) value;
			IPatternCreator view = PatternCreatorFactory.createView();
			BVariable var = view.createVariable();
			var.setBClass(CodecUtils.BString());
			var.setLogicName(name);
			var.setName(name);
			this.editedValue = var;
		} else if (value instanceof BValuable) {
			editedValue = (BValuable) value;
		}

	}

	public BInvoker getInvoker() {
		return invoker;
	}

	public void setLogic(BFixedInputLogic logic) {
		this.logic = logic;
	}

	@Override
	public BLogic getLogic() {
		return this.logic;
	}

	@Override
	public Object getValue() {
		String value = "";
		if (editedValue != null) {
			value = BValueUtils.createValuable(editedValue, false);
		}
		BVariable var = (BVariable) invoker.getInvokeChild();
		return value + "[" + var.getName() + "]";

	}

	public BInvoker getParameteredInvoker() {
		BInvoker invoker = (BInvoker) this.invoker.cloneAll();

		if (editedValue instanceof BVariable) {
			BVariable var = (BVariable) editedValue;
			if (var.getBClass().getQualifiedName().equals(String.class.getName())) {
				String logicName = var.getLogicName();
				if (!logicName.startsWith("\"")) {
					logicName = "\"" + logicName;
				}
				if (!logicName.endsWith("\"")) {
					logicName = logicName + "\"";
				}
				var.setLogicName(logicName);
			}
		}

		if (editedValue != null) {

			editedValue.addUserAttribute("FIXED_INPUT_VALUE_NAME", invoker.getInvokeChild());
			invoker.addParameter(editedValue);
		} else {
			invoker.addParameter(CodecUtils.getNullValue());
		}
		return invoker;
	}

	@Override
	public String getSQL(ITableSql tsql) {
		return SQLMakeUtils.getInjectValue((BVariable) invoker.getInvokeChild(), null);
	}

	@Override
	public BVariable getListTargetVar() {
		return (BVariable) this.invoker.getInvokeParent();
	}

	@Override
	public void onListTargetChange(BParameter var) {
		this.invoker.setInvokeChild((BValuable) var.cloneAll());
	}

}

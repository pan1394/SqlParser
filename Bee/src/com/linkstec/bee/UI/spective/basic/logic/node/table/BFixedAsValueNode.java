package com.linkstec.bee.UI.spective.basic.logic.node.table;

import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.logic.BInvoker;

public class BFixedAsValueNode extends BFixedValueNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1128850982394242452L;

	private BInvoker invoker;

	// for effectiveness
	private String logicName;

	public BFixedAsValueNode(BInvoker invoker) {

		this.invoker = invoker;
		this.setStyle("strokeColor=gray;strokeWidth=0.5;fillColor=f8c471");
	}

	public BInvoker getInvoker() {
		return invoker;
	}

	@Override
	public BVariable getListTargetVar() {
		return (BVariable) this.invoker.getInvokeParent();
	}

	@Override
	public void onListTargetChange(BParameter var) {
		this.invoker.setInvokeChild((BValuable) var.cloneAll());
	}

	@Override
	public Object getValue() {

		BVariable var = (BVariable) invoker.getInvokeChild();
		logicName = "AS " + var.getName();

		return logicName;
	}

	@Override
	public String getSQL(ITableSql tsql) {
		BVariable var = (BVariable) invoker.getInvokeChild();
		tsql.getInfo().setFixedValue();
		return "AS " + var.getLogicName();
	}

}

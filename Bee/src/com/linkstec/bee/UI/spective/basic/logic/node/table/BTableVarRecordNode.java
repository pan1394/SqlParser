package com.linkstec.bee.UI.spective.basic.logic.node.table;

import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.logic.model.var.SQLMakeUtils;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;

public class BTableVarRecordNode extends BTableValueNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2808370280544498987L;

	private BVariable parent;
	private BParameter child;
	private BInvoker invoker;

	public BTableVarRecordNode(BInvoker invoker) {
		this.parent = (BVariable) invoker.getInvokeParent();
		this.child = (BParameter) invoker.getInvokeChild();
		this.invoker = invoker;
		this.setVertex(true);

		this.setStyle("strokeColor=gray;strokeWidth=0.5");
		mxGeometry geo = this.getGeometry();
		geo.setOffset(new mxPoint());
		geo.setWidth(250);
		geo.setHeight(30);

	}

	public BInvoker getInvoker() {
		return invoker;
	}

	@Override
	public void doLayout(BasicLogicSheet sheet) {

	}

	@Override
	public Object getValue() {
		return parent.getName() + "[" + parent.getBClass().getName() + "].[" + child.getName() + "]";
	}

	@Override
	public String getSQL(ITableSql tsql) {
		tsql.getInvokers().add(invoker);
		return SQLMakeUtils.getInjectValue(child, null);
	}

	@Override
	public String getLogicName() {
		return child.getLogicName();
	}

	@Override
	public String getName() {
		return child.getName();
	}

	@Override
	public String getSQLExp(ITableSql tsql) {

		return SQLMakeUtils.getInjectValueExp(child, null);
	}

	@Override
	public BVariable getListTargetVar() {
		return (BVariable) this.invoker.getInvokeParent();
	}

	@Override
	public boolean isEditable() {
		return true;
	}

	@Override
	public void onListTargetChange(BParameter var) {
		this.invoker.setInvokeChild((BValuable) var.cloneAll());
	}

}

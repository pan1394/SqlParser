package com.linkstec.bee.UI.spective.basic.logic.node.table;

import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.logic.model.var.SQLMakeUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;

public class BTableRecordNode extends BTableValueNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2808370280544498987L;

	private BInvoker invoker;

	public BTableRecordNode(BInvoker invoker) {
		this.invoker = invoker;
		this.setVertex(true);
		this.setStyle("strokeColor=gray;strokeWidth=0.5");
		mxGeometry geo = this.getGeometry();
		geo.setOffset(new mxPoint());
		geo.setWidth(250);
		geo.setHeight(30);

	}

	@Override
	public void doLayout(BasicLogicSheet sheet) {

	}

	public BInvoker getRecord() {
		return this.invoker;
	}

	@Override
	public Object getValue() {
		BVariable parent = (BVariable) invoker.getInvokeParent();
		BVariable child = (BVariable) invoker.getInvokeChild();
		BClass bclass = parent.getBClass();
		return parent.getLogicName() + "[" + bclass.getName() + "].[" + child.getName() + "]";
	}

	@Override
	public String getSQL(ITableSql tsql) {
		return SQLMakeUtils.getTableReference(invoker);
	}

	@Override
	public String getLogicName() {
		BVariable child = (BVariable) invoker.getInvokeChild();
		return child.getLogicName();
	}

	@Override
	public String getName() {
		BVariable child = (BVariable) invoker.getInvokeChild();
		return child.getName();
	}

	@Override
	public String getSQLExp(ITableSql tsql) {
		return SQLMakeUtils.getTableReferenceExp(invoker);
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

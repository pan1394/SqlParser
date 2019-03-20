package com.linkstec.bee.UI.spective.basic.logic.node.table;

import com.linkstec.bee.UI.spective.basic.logic.model.table.BFixedValueLogic;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.IExceptionCell;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.mxgraph.model.mxGeometry;

public class BFixedValueNode extends BTableValueNode implements ILogicCell {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2733404082829171255L;
	private BFixedValueLogic logic;

	public BFixedValueNode() {
		this.setStyle("strokeColor=gray;strokeWidth=0.5");
		this.setVertex(true);
		this.setValue("AND");
		this.setConnectable(false);
		this.setEditable(true);
		mxGeometry geo = this.getGeometry();
		geo.setWidth(150);
		geo.setHeight(30);
	}

	public void setLogic(BFixedValueLogic logic) {
		this.logic = logic;
	}

	@Override
	public IExceptionCell getExcetion() {
		return null;
	}

	@Override
	public BLogic getLogic() {
		return this.logic;
	}

	@Override
	public String getSQL(ITableSql sql) {
		sql.getInfo().setFixedValue();
		return (String) this.getValue();
	}

	@Override
	public String getLogicName() {
		return (String) this.getValue();
	}

	@Override
	public String getName() {
		return (String) this.getValue();
	}

	@Override
	public String getSQLExp(ITableSql sql) {
		sql.getInfo().setFixedValue();
		return (String) this.getValue();
	}

}

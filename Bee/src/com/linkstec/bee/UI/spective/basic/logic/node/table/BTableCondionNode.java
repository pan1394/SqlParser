package com.linkstec.bee.UI.spective.basic.logic.node.table;

import java.util.List;

import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.basic.BJudgeLogic;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.mxgraph.model.mxGeometry;

public class BTableCondionNode extends BTableValueNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4543078066695628011L;

	private BJudgeLogic expression;

	public BTableCondionNode(BJudgeLogic expression) {
		this.setVertex(true);
		this.expression = expression;

		this.setStyle("strokeColor=gray;strokeWidth=0.5");
		mxGeometry geo = this.getGeometry();
		geo.setWidth(350);
		geo.setHeight(30);
	}

	public BJudgeLogic getExpression() {
		return expression;
	}

	public void setExpression(BJudgeLogic expression) {
		this.expression = expression;
	}

	@Override
	public Object getValue() {
		expression.addUserAttribute("FOR_DISPLAY", "FOR_DISPLAY");
		return expression.getSql();
	}

	@Override
	public String getSQL(ITableSql sql) {
		String s = expression.getLogicSql(sql);
		List<BInvoker> invokers = expression.getInvokers();
		if (invokers != null && invokers.size() > 0) {
			sql.getInvokers().addAll(invokers);
		}
		return s;
	}

	@Override
	public String getLogicName() {
		return expression.getLogicSql(null);
	}

	@Override
	public String getName() {
		return expression.getSql();
	}

	@Override
	public String getSQLExp(ITableSql sql) {
		return expression.getSql();
	}

	public void clicked(BasicLogicSheet sheet) {
		Application.getInstance().getBasicSpective().getPropeties().setTarget(this.expression, null);

	}

}

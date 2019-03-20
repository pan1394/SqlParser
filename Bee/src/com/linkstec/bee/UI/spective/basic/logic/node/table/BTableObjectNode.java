package com.linkstec.bee.UI.spective.basic.logic.node.table;

import java.util.List;

import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.basic.ITableObject;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.mxgraph.model.mxGeometry;

public class BTableObjectNode extends BNode implements ITableObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3576792620097785543L;
	private BParameter parameter;

	public BTableObjectNode(BParameter parameter) {
		this.parameter = parameter;
		this.setVertex(true);
		this.setStyle("shape=cylinder;strokeColor=gray;strokeWidth=0.5;align=center");
		mxGeometry geo = this.getGeometry();
		geo.setWidth(150);
		geo.setHeight(50);
	}

	public Object getValue() {
		BClass model = parameter.getBClass();
		String s = model.getName() + "\n\r(" + model.getLogicName() + ")";

		String varName = parameter.getLogicName();
		if (varName != null) {
			s = s + "  " + varName;
		}

		return s;
	}

	@Override
	public BParameter getModel(List<BEditorModel> models) {
		return this.parameter;
	}

	@Override
	public String getSQL(ITableSql tsql) {
		BParameter param = this.getModel(null);
		BClass bclass = param.getBClass();

		String sql = tsql.getProvider().getTableName(bclass.getLogicName());
		if (sql == null) {
			sql = bclass.getLogicName().toUpperCase() + " " + param.getLogicName();
		} else {
			sql = "${" + sql + "TableName}";
		}
		return sql;
	}

	@Override
	public String getSQLExp(ITableSql tsql) {
		BParameter param = this.getModel(null);
		BClass bclass = param.getBClass();
		String sql = tsql.getProvider().getTableName(bclass.getLogicName());
		if (sql == null) {
			sql = bclass.getName() + " " + param.getLogicName();
		} else {
			sql = "パラメーターの" + sql.toUpperCase() + "テーブル名 " + param.getLogicName();
		}
		return sql;
	}

	@Override
	public BParameter getParameter() {
		return parameter;
	}

}

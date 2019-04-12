package com.linkstec.bee.UI.spective.basic.logic.node.table;

import java.util.List;

import com.linkstec.bee.UI.node.BasicNode;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.logic.node.BDetailNodeWrapper;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BTansferHolderNode;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.basic.ITableObject;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;

public class BTableObjectNode extends BNode implements ITableObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3576792620097785543L;
	private BParameter parameter;
	private boolean hasAs = false;

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
			if (this.hasAs) {
				s = s + " AS " + parameter.getName();
			} else {
				s = s + "  " + varName;
			}
		}

		return s;
	}

	@Override
	public BParameter getModel(List<BEditorModel> models) {
		return this.parameter;
	}

	@Override
	public void childAdded(BNode node, BasicLogicSheet sheet) {
		if (node instanceof BTansferHolderNode) {
			BTansferHolderNode t = (BTansferHolderNode) node;
			List<BNode> list = t.getNodes();
			for (BNode n : list) {
				this.childAdded(n, sheet);
			}
			node.removeFromParent();
			return;
		} else if (node instanceof BDetailNodeWrapper) {
			BDetailNodeWrapper wrapper = (BDetailNodeWrapper) node;
			wrapper.removeFromParent();
			BasicNode basic = wrapper.getNode();
			this.cellAdded(basic);
		}
	}

	@Override
	public void cellAdded(mxICell cell) {
		if (cell instanceof BInvoker) {
			BInvoker bin = (BInvoker) cell;
			BValuable child = bin.getInvokeChild();
			if (child instanceof BVariable) {
				BVariable var = (BVariable) child;
				this.parameter.setName(var.getName());
				this.parameter.setLogicName(var.getLogicName());
				this.hasAs = true;
			}
		} else if (cell instanceof BVariable) {
			BVariable var = (BVariable) cell;
			this.parameter.setName(var.getName());
			this.parameter.setLogicName(var.getLogicName());
			this.hasAs = true;
		} else if (cell instanceof BAssignment) {
			BAssignment assign = (BAssignment) cell;
			BParameter var = assign.getLeft();
			this.parameter.setName(var.getName());
			this.parameter.setLogicName(var.getLogicName());
			this.hasAs = true;
		}
	}

	@Override
	public String getSQL(ITableSql tsql) {
		BParameter param = this.getModel(null);
		BClass bclass = param.getBClass();

		String sql = tsql.getProvider().getTableName(bclass.getLogicName());
		if (sql == null) {
			sql = bclass.getLogicName().toUpperCase();
		} else {
			sql = "${" + sql + "TableName}";
		}

		if (hasAs) {
			sql = sql + " AS " + param.getName();
		} else {
			sql = sql + " " + param.getLogicName();
		}

		return sql;
	}

	@Override
	public boolean isValidDropTarget(Object[] cells) {
		return true;
	}

	@Override
	public String getSQLExp(ITableSql tsql) {
		BParameter param = this.getModel(null);
		BClass bclass = param.getBClass();
		String sql = tsql.getProvider().getTableName(bclass.getLogicName());
		if (sql == null) {
			sql = bclass.getName();
		} else {
			sql = "パラメーターの" + sql.toUpperCase() + "テーブル名";
		}

		if (hasAs) {
			sql = sql + " AS " + param.getName();
		} else {
			sql = sql + " " + param.getName();
		}
		return sql;
	}

	@Override
	public BParameter getParameter() {
		return parameter;
	}

	@Override
	public String getAsParamName() {
		if (this.hasAs) {
			return this.parameter.getName();
		}
		return null;
	}

	@Override
	public String getAsParamLogicName() {
		if (this.hasAs) {
			return this.parameter.getLogicName();
		}
		return null;
	}

}

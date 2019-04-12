package com.linkstec.bee.UI.spective.basic.logic.node.table;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.data.BasicComponentModel;
import com.linkstec.bee.UI.spective.basic.data.BasicDataModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BActionModel;
import com.linkstec.bee.UI.spective.basic.logic.model.ListSetterLogic;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.basic.BasicGenUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BLogicProvider;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.BTableElement;
import com.linkstec.bee.core.fw.basic.IExceptionCell;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxPoint;

public class BTableNode extends BNode implements ILogicCell, BTableElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2087938593916177885L;
	private ListSetterLogic logic;

	public BTableNode(BPath path, BasicDataModel model) {
		this.setVertex(true);
		List<BAssignment> list = model.getVariables();
		int i = 0;
		this.insert(new BTableRowNode());

		BActionModel action = (BActionModel) path.getAction();
		BLogicProvider provider = path.getProvider();

		List<BasicComponentModel> models = new ArrayList<BasicComponentModel>();
		models.addAll(action.getInputModels());
		models.addAll(action.getOutputModels());

		IPatternCreator view = PatternCreatorFactory.createView();
		BVariable var = view.createVariable();

		model.setLogicName(BasicGenUtils.makeName(model.getLogicName()));
		var.setBClass(model);

		BClass mainClass = BasicGenUtils.createClass(action, path.getProject());
		List<BClass> targets = new ArrayList<BClass>();
		targets.add(mainClass);
		provider.manageGenerableClass(models, targets);

		for (BClass bclass : targets) {
			if (!bclass.getQualifiedName().equals(mainClass.getQualifiedName())) {
				var.setBClass(bclass);
				break;
			}
		}

		var.setLogicName(BasicGenUtils.getInputDtoInstanceName(var.getBClass(), path.getAction().getLogicName()));

		for (BAssignment a : list) {
			i++;
			this.makeRow(path, (BVariable) var.cloneAll(), a, i);
		}
		logic = new ListSetterLogic(path, this);
	}

	private void makeRow(BPath path, BVariable var, BAssignment assign, int index) {
		IPatternCreator view = PatternCreatorFactory.createView();
		BInvoker invoker = view.createMethodInvoker();

		invoker.setInvokeParent(var);
		invoker.setInvokeChild(assign.getLeft());
		BTableRowNode node = new BTableRowNode(path, invoker, index);
		this.insert(node);
	}

	@Override
	public void doLayout(BasicLogicSheet sheet) {
		int count = this.getChildCount();
		double height = 0;
		double width = 0;
		for (int i = 0; i < count; i++) {
			mxICell child = this.getChildAt(i);
			if (child instanceof BTableRowNode) {
				BTableRowNode node = (BTableRowNode) child;
				node.doLayout(sheet);
				node.getGeometry().setOffset(new mxPoint(0, height));
				height = height + node.getGeometry().getHeight();
				width = Math.max(node.getGeometry().getWidth(), width);
			}
		}
		this.getGeometry().setHeight(height);
		this.getGeometry().setWidth(width);
	}

	public List<BLogic> getSetterLogics() {
		List<BLogic> units = new ArrayList<BLogic>();
		int count = this.getChildCount();

		for (int i = 0; i < count; i++) {
			mxICell child = this.getChildAt(i);
			if (child instanceof BTableRowNode) {
				BTableRowNode node = (BTableRowNode) child;
				BLogic logic = node.getLogic();
				if (logic != null) {
					units.add(logic);
				}
			}
		}

		return units;

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
	public String getSQL(ITableSql tsql) {

		tsql.getProvider().getProperties().addThreadScopeAttribute("UP_INSER", "UP_INSER");

		String sql = "";
		int count = this.getChildCount();

		for (int i = 0; i < count; i++) {
			mxICell child = this.getChildAt(i);
			if (child instanceof BTableRowNode) {
				BTableRowNode node = (BTableRowNode) child;
				String s = node.getSQL(tsql);
				if (s != null) {
					sql = sql + s + ",";
					if (tsql.isFormat()) {
						sql = sql + "\r\n\t";
					}
				}
			}
		}
		sql = sql.trim();
		if (sql.length() > 2) {
			sql = sql.substring(0, sql.length() - 1);
		}

		tsql.getProvider().getProperties().addThreadScopeAttribute("UP_INSER", "");

		return sql;
	}

	@Override
	public String getSQLExp(ITableSql tsql) {
		tsql.getProvider().getProperties().addThreadScopeAttribute("UP_INSER", "UP_INSER");

		String sql = "";
		int count = this.getChildCount();

		for (int i = 0; i < count; i++) {
			mxICell child = this.getChildAt(i);
			if (child instanceof BTableRowNode) {
				BTableRowNode node = (BTableRowNode) child;
				String s = node.getSQLExp(tsql);
				if (s != null) {
					sql = sql + s + ",";
					if (tsql.isFormat()) {
						sql = sql + "\r\n";
					}
				}
			}
		}
		tsql.getProvider().getProperties().addThreadScopeAttribute("UP_INSER", "");

		return sql;
	}

}

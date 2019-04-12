package com.linkstec.bee.UI.spective.basic.logic.edit;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.logic.model.NewLayerClassLogic;
import com.linkstec.bee.UI.spective.basic.logic.node.BDataCopyNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BDataGroupNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BGroupNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BFixedInputValueNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableGroupNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableTargetTablesNode;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BModule;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BLogicProvider;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.BSQLModel;
import com.linkstec.bee.core.fw.basic.ITableObject;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.basic.ITableSqlInfo;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.editor.BWorkSpace;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.mxgraph.model.mxICell;

public class BTableModel extends BPatternModel implements BSQLModel {

	private static String[] names = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
			"Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "AA", "BB", "CC", "DD", "EE", "FF", "GG", "HH", "II",
			"JJ", "KK", "LL", "MM", "NN", "OO", "PP", "QQ", "RR", "SS", "TT", "UU", "VV", "WW", "XX", "YY", "ZZ" };

	public BTableModel(BPath action) {
		super(action);
	}

	public List<BClass> getTables() {
		List<BClass> tables = new ArrayList<BClass>();
		BActionModel model = (BActionModel) this.actionPath.getAction();
		tables.addAll(model.getInputModels());
		tables.addAll(model.getOutputModels());
		return tables;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7801969422190442926L;
	private int parentNameGeneIndex = 0;

	@Override
	public BEditor getEditor(BProject project, File file, BWorkSpace space) {
		BTableSheet c = new BTableSheet(project, this);
		return c;
	}

	@Override
	public BGroupNode getGroupNode() {
		return new BDataGroupNode();
	}

	public String generaParentName() {
		if (parentNameGeneIndex < names.length) {
			String s = names[parentNameGeneIndex];
			parentNameGeneIndex++;
			return s;
		} else {
			int re = parentNameGeneIndex % names.length;
			int num = parentNameGeneIndex / names.length;
			String s = names[re];
			String name = "";
			for (int i = 0; i < num; i++) {
				name = name + s;
			}
			return name;
		}
	}

	public List<BLogicUnit> getSetterLogics() {
		List<BLogicUnit> units = new ArrayList<BLogicUnit>();
		mxICell root = ((mxICell) this.getRoot()).getChildAt(0);
		BLogic logic = this.getActionPath().getLogic();
		BParameter para = null;
		if (logic instanceof NewLayerClassLogic) {
			NewLayerClassLogic nl = (NewLayerClassLogic) logic;
			List<BParameter> paras = nl.getParameters();
			if (paras != null && paras.size() == 1) {
				para = paras.get(0);
				if (para != null) {
					this.getActionPath().getProvider().getProperties()
							.addThreadScopeAttribute("TABLE_LIST_PARENT_PARAMTER", para);
				}
			}
		}

		makeSetterLogic(root, units);
		return units;
	}

	private void makeSetterLogic(mxICell root, List<BLogicUnit> units) {
		int count = root.getChildCount();
		for (int i = 0; i < count; i++) {

			mxICell obj = root.getChildAt(i);
			if (obj instanceof BTableNode) {
				BTableNode node = (BTableNode) obj;
				BLogic logic = node.getLogic();
				if (logic != null) {
					units.addAll(logic.createUnit());
				}

			} else if (obj instanceof BFixedInputValueNode) {
				BFixedInputValueNode node = (BFixedInputValueNode) obj;
				BLogic logic = node.getLogic();
				if (logic != null) {
					units.addAll(logic.createUnit());
				}
			} else if (obj instanceof BDataCopyNode) {
				BDataCopyNode copy = (BDataCopyNode) obj;
				units.addAll(0, copy.getLogic().createUnit());

			}
			makeSetterLogic(obj, units);
		}

	}

	public String getSQLExp(ITableSql tsql) {
		boolean format = tsql.isFormat();
		mxICell cell = ((mxICell) this.getRoot()).getChildAt(0);
		int count = cell.getChildCount();

		List<BTableGroupNode> list = new ArrayList<BTableGroupNode>();

		BTableNode table = null;
		for (int i = 0; i < count; i++) {
			mxICell node = cell.getChildAt(i);
			if (node instanceof BTableGroupNode) {
				BTableGroupNode g = (BTableGroupNode) node;
				list.add(g);
			} else if (node instanceof BTableNode) {
				table = (BTableNode) node;
			}
		}

		Collections.sort(list, new Comparator<BTableGroupNode>() {

			@Override
			public int compare(BTableGroupNode o1, BTableGroupNode o2) {
				int a1 = o1.getSQLPriority();
				int a2 = o2.getSQLPriority();
				int a = a1 - a2;
				if (a > 0) {
					return 1;
				} else if (a < 0) {
					return -1;
				} else {
					return 0;
				}
			}

		});

		String sql = "";

		String tableName = null;
		for (BTableGroupNode g : list) {
			if (g instanceof BTableTargetTablesNode) {
				BTableTargetTablesNode tables = (BTableTargetTablesNode) g;
				List<ITableObject> objs = tables.getObjects();
				if (objs.size() == 1) {
					tableName = objs.get(0).getModel(tsql.getEditors()).getBClass().getLogicName();
				}
			}
		}

		if (tableName != null) {
			String s = tsql.getProvider().getTableName(tableName);
			if (s != null) {

				tableName = "パラメーターの" + s.toUpperCase() + "テーブル名";
			}
		}

		if (table != null & tableName != null) {

			String s = table.getSQLExp(tsql);

			if (this instanceof BTableInsertModel) {
				sql = "追加テーブル " + tableName;
			} else if (this instanceof BTableUpdateModel) {
				sql = "更新テーブル " + tableName;
				if (tsql.isFormat()) {
					sql = sql + "\r\n　" + "\r\n\t" + s;
				} else {
					sql = sql + " " + s;
				}
			}
		} else {

			if (this instanceof BTableDeleteModel) {
				sql = "削除テーブル " + tableName;
			}
		}

		for (BTableGroupNode g : list) {
			boolean doSql = true;
			if (g instanceof BTableTargetTablesNode) {
				if (this instanceof BTableSelectModel) {

				} else {
					doSql = false;
				}
			}
			if (doSql) {
				sql = sql + g.getSQLExp(tsql);
			}
		}

		return sql;
	}

	public String getSQL(ITableSql tsql) {

		mxICell cell = ((mxICell) this.getRoot()).getChildAt(0);
		int count = cell.getChildCount();

		List<BTableGroupNode> list = new ArrayList<BTableGroupNode>();

		BTableNode table = null;
		for (int i = 0; i < count; i++) {
			mxICell node = cell.getChildAt(i);
			if (node instanceof BTableGroupNode) {
				BTableGroupNode g = (BTableGroupNode) node;
				list.add(g);
			} else if (node instanceof BTableNode) {
				table = (BTableNode) node;
			}
		}

		Collections.sort(list, new Comparator<BTableGroupNode>() {

			@Override
			public int compare(BTableGroupNode o1, BTableGroupNode o2) {
				int a1 = o1.getSQLPriority();
				int a2 = o2.getSQLPriority();
				int a = a1 - a2;
				if (a > 0) {
					return 1;
				} else if (a < 0) {
					return -1;
				} else {
					return 0;
				}
			}

		});

		String sql = "";

		String tableName = null;
		for (BTableGroupNode g : list) {
			if (g instanceof BTableTargetTablesNode) {
				BTableTargetTablesNode tables = (BTableTargetTablesNode) g;
				List<ITableObject> objs = tables.getObjects();
				if (objs.size() == 1) {
					tableName = objs.get(0).getModel(tsql.getEditors()).getBClass().getLogicName();
				}
			}
		}

		if (tableName != null) {
			String s = tsql.getProvider().getTableName(tableName);
			if (s != null) {

				tableName = "${" + s + "TableName}";
			}
		}

		if (table != null & tableName != null) {

			String s = table.getSQL(tsql);

			if (this instanceof BTableInsertModel) {
				sql = "INSERT INTO " + tableName;
			} else if (this instanceof BTableUpdateModel) {
				sql = "Update " + tableName;
				if (tsql.isFormat()) {
					sql = sql + "\r\nSET" + "\r\n\t" + s;
				} else {
					sql = sql + " SET " + s;
				}
			}
		} else {

			if (this instanceof BTableDeleteModel) {
				sql = "DELETE FROM " + tableName;
			}
		}

		for (BTableGroupNode g : list) {
			boolean doSql = true;
			if (g instanceof BTableTargetTablesNode) {
				if (this instanceof BTableSelectModel) {

				} else {
					doSql = false;
				}
			}
			if (doSql) {

				sql = sql + g.getSQL(tsql);

			}
		}

		return sql;
	}

	public BSqlModel getSqlModel(BModule module, BLogicProvider provider) {

		List<BEditorModel> models = module.getList();
		BSqlModel bmodel = new BSqlModel(models, true, provider);
		this.getSQL(bmodel);
		return bmodel;
	}

	@Override
	public ITableSqlInfo getSqlInfo(BModule module, BLogicProvider provider) {
		List<BEditorModel> models = module.getList();
		BSqlModel bmodel = new BSqlModel(models, true, provider);
		this.getSQL(bmodel);
		return bmodel.getInfo();
	}

	@Override
	public int getReturnType() {
		if (this.getActionPath().getSelfAction() == null) {
			return -1;
		}
		BActionModel model = (BActionModel) this.actionPath.getAction();
		if (model.getReturnType() != null) {
			return model.getReturnType().getType();
		}
		return -1;
	}
}

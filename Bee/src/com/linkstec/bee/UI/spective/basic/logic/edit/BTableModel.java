package com.linkstec.bee.UI.spective.basic.logic.edit;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.logic.node.BDataGroupNode;
import com.linkstec.bee.UI.spective.basic.logic.node.BGroupNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BFixedInputValueNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableGroupNode;
import com.linkstec.bee.UI.spective.basic.logic.node.table.BTableNode;
import com.linkstec.bee.core.fw.BModule;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BLogicProvider;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.BSQLModel;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.basic.ITableSqlInfo;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.editor.BWorkSpace;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.mxgraph.model.mxICell;

public class BTableModel extends BPatternModel implements BSQLModel {

	public BTableModel(BPath action) {
		super(action);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7801969422190442926L;

	@Override
	public BEditor getEditor(BProject project, File file, BWorkSpace space) {
		BTableSheet c = new BTableSheet(project, this);
		return c;
	}

	@Override
	public BGroupNode getGroupNode() {
		return new BDataGroupNode();
	}

	public List<BLogicUnit> getSetterLogics() {
		List<BLogicUnit> units = new ArrayList<BLogicUnit>();
		mxICell root = ((mxICell) this.getRoot()).getChildAt(0);
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
			}
			makeSetterLogic(obj, units);
		}

	}

	public String getSQLExp(ITableSql tsql) {
		boolean format = tsql.isFormat();
		mxICell cell = ((mxICell) this.getRoot()).getChildAt(0);
		int count = cell.getChildCount();

		List<BTableGroupNode> list = new ArrayList<BTableGroupNode>();

		for (int i = 0; i < count; i++) {
			mxICell node = cell.getChildAt(i);
			if (node instanceof BTableGroupNode) {
				BTableGroupNode g = (BTableGroupNode) node;
				list.add(g);
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

		for (BTableGroupNode g : list) {
			if (!format) {
				sql = sql + " ";
			}
			sql = sql + g.getSQLExp(tsql);
		}

		return sql;
	}

	public String getSQL(ITableSql tsql) {
		mxICell cell = ((mxICell) this.getRoot()).getChildAt(0);
		int count = cell.getChildCount();

		List<BTableGroupNode> list = new ArrayList<BTableGroupNode>();

		for (int i = 0; i < count; i++) {
			mxICell node = cell.getChildAt(i);
			if (node instanceof BTableGroupNode) {
				BTableGroupNode g = (BTableGroupNode) node;
				list.add(g);
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

		for (BTableGroupNode g : list) {
			sql = sql + g.getSQL(tsql);
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
}

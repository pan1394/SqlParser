package com.linkstec.bee.UI.spective.basic.logic.node.table;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.logic.node.BNode;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ITableObject;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;

public class BTableTargetTablesNode extends BTableGroupNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -27584986572380753L;
	private String[] names = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
			"S", "T", "U", "V", "W", "X", "Y", "Z", "AA", "BB", "CC", "DD", "EE", "FF", "GG", "HH", "II", "JJ", "KK",
			"LL", "MM", "NN", "OO", "PP", "QQ", "RR", "SS", "TT", "UU", "VV", "WW", "XX", "YY", "ZZ" };
	private String parentName;

	public BTableTargetTablesNode(BPath path) {
		super(path);
		this.setTitle("対象コンポーネント");
		this.getGeometry().setHeight(200);
		this.getGeometry().setWidth(850);
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	@Override
	public void childAdded(BNode node, BasicLogicSheet sheet) {
		super.childAdded(node, sheet);
		if (node instanceof BTableObjectNode) {

		} else if (node instanceof BTableNesSelectNode) {

		} else if (node instanceof BTableWhereNode) {

		} else {
			node.removeFromParent();
		}
	}

	@Override
	public boolean isDropTarget(BNode node) {

		return true;

	}

	public List<BTableConnectWhereNode> getJoins() {
		List<BTableConnectWhereNode> list = new ArrayList<BTableConnectWhereNode>();

		int count = this.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell child = this.getChildAt(i);
			if (child instanceof BTableConnectWhereNode) {
				BTableConnectWhereNode node = (BTableConnectWhereNode) child;
				list.add(node);
			}
		}
		return list;
	}

	public List<ITableObject> getObjects() {
		List<ITableObject> list = new ArrayList<ITableObject>();

		int count = this.getChildCount();
		for (int i = 0; i < count; i++) {
			mxICell child = this.getChildAt(i);
			if (child instanceof ITableObject) {
				ITableObject node = (ITableObject) child;
				list.add(node);
			}
		}
		return list;
	}

	public void addTableObjects(List<BTableObjectNode> list) {
		for (BTableObjectNode node : list) {
			this.insert(node);
		}
	}

	@Override
	public void layout(BasicLogicSheet sheet) {
		int count = this.getChildCount();
		List<BEditorModel> models = sheet.findBook().getALLModels();
		double y = space + 40;
		double x = 12;

		int index = 0;
		for (int i = 0; i < count; i++) {
			mxICell child = this.getChildAt(i);
			boolean isNode = false;
			if (child instanceof BTableObjectNode) {
				BTableObjectNode node = (BTableObjectNode) child;
				String name = names[index];
				if (parentName != null) {
					name = parentName + name;
				}
				if (node.getAsParamLogicName() == null) {
					node.getModel(models).setLogicName(name);
					node.getModel(models).setName(name);
				}
				isNode = true;
			} else if (child instanceof BTableNesSelectNode) {
				BTableNesSelectNode node = (BTableNesSelectNode) child;
				String name = generateName(index);
				if (parentName != null) {
					name = parentName + name;
				}
				node.setAsName(name);
				isNode = true;
			}
			if (isNode) {
				mxGeometry geo = child.getGeometry();

				if (x + geo.getWidth() > this.getGeometry().getWidth() - space) {
					x = space;
					y = y + geo.getHeight() + space;
				}
				geo.setX(x);
				geo.setY(y);

				x = x + geo.getWidth() + space;

				index++;
			}

		}

		this.getGeometry().setWidth(850);

		this.fitHeight();
	}

	public String generateName(int index) {
		if (index < names.length) {
			String s = names[index];
			return s;
		} else {
			int re = index % names.length;
			int num = index / names.length;
			String s = names[re];
			String name = "";
			for (int i = 0; i < num; i++) {
				name = name + s;
			}
			return name;
		}
	}

	@Override
	public String getSQL(ITableSql tsql) {
		boolean format = tsql.isFormat();
		List<BTableConnectWhereNode> joins = this.getJoins();
		String from = this.getSqlTitle();
		String space = " ";
		if (format) {
			space = "";
		}
		from = space + from;
		if (format) {
			from = "\r\n" + from;
		}
		List<ITableObject> list = this.getObjects();

		int index = 0;

		for (ITableObject target : list) {
			// format it will

			BVariable model = target.getModel(tsql.getEditors());
			String name = model.getLogicName();
			boolean toBreak = false;
			for (BTableConnectWhereNode join : joins) {

				if (join.getConnnectTarget().getModel(tsql.getEditors()).getName().equals(name)) {
					from = from + join.getSQL(tsql);
					toBreak = true;
					break;
				}

			}
			if (toBreak) {
				continue;
			}

			if (index != 0) {
				if (format) {
					from = from + "\t";
				}
				from = from + ",";
			} else {
				if (format) {
					from = from + "\r\n\t";
				}
			}

			from = from + target.getSQL(tsql);

			// format it will
			if (format) {
				from = from + "\r\n";
			}
			index++;

		}
		return from;
	}

	protected String getSqlTitle() {
		return "FROM ";
	}

	protected String getSqlTitleExp() {
		return "検索テーブル：";
	}

	@Override
	public String getSQLExp(ITableSql tsql) {
		boolean format = tsql.isFormat();
		List<BTableConnectWhereNode> joins = this.getJoins();
		String from = this.getSqlTitleExp();
		String space = " ";
		if (format) {
			space = "";
		}
		from = space + from;
		if (format) {
			from = "\r\n" + from;
		}
		List<ITableObject> list = this.getObjects();

		int index = 0;

		for (ITableObject target : list) {

			BVariable model = target.getModel(tsql.getEditors());
			String name = model.getLogicName();

			boolean toBreak = false;
			for (BTableConnectWhereNode join : joins) {

				if (join.getConnnectTarget().getModel(tsql.getEditors()).getName().equals(name)) {
					from = from + join.getSQLExp(tsql);
					toBreak = true;
					break;
				}

			}
			if (toBreak) {
				continue;
			}

			// format it will

			if (index != 0) {
				if (format) {
					from = from + "\t";
				}
				from = from + ",";
			} else {
				if (format) {
					from = from + "\r\n\t";
				}
			}
			from = from + target.getSQLExp(tsql);

			// format it will
			if (format) {
				from = from + "\r\n";
			}
			index++;

		}
		return from;
	}

	@Override
	public int getSQLPriority() {
		return 1;
	}
}

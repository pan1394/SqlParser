package com.linkstec.bee.UI.spective.basic.logic.node.table;

import java.util.List;

import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.logic.edit.BTableSelectSheet;
import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ITableObject;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.editor.BEditorModel;

public class BTableConnectWhereNode extends BTableWhereNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8697655421570632485L;
	private int type = -1;
	public static int TYPE_INNER = 1;
	public static int TYPE_LEFT = 2;
	private ITableObject connnectTarget;
	private String tempName = null;

	public BTableConnectWhereNode(BPath parent, int type, ITableObject target) {
		super(parent);
		this.type = type;
		// this.connnectTarget = target;
		this.getGeometry().setWidth(800);
		this.getGeometry().setHeight(100);
		this.setMoveable(true);
		this.setDeletable(true);

		List<BEditorModel> models = Application.getInstance().getBasicSpective().getSelection().getBookModel()
				.getEditors();

		String logicName = target.getModel(models).getName();
		tempName = logicName;
		if (type == TYPE_INNER) {
			this.setTitle("INNER JOIN結合条件(" + logicName + ")");
		} else if (type == TYPE_LEFT) {
			this.setTitle("LEFT JOIN結合条件(" + logicName + ")");
		}
	}

	@Override
	public void added(BasicLogicSheet sheet) {
		super.added(sheet);

		boolean goodPlace = false;
		if (sheet instanceof BTableSelectSheet) {
			BTableSelectSheet select = (BTableSelectSheet) sheet;
			List<ITableObject> tables = select.getDefinedTables();
			List<BEditorModel> models = Application.getInstance().getBasicSpective().getSelection().getBookModel()
					.getEditors();

			for (ITableObject table : tables) {
				String logicName = table.getModel(models).getLogicName();
				if (logicName.equals(tempName)) {
					this.connnectTarget = table;
					goodPlace = true;
					break;
				}
			}
		}
		if (!goodPlace) {
			this.removeFromParent();
		}
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public ITableObject getConnnectTarget() {
		return connnectTarget;
	}

	public void setConnnectTarget(ITableObject connnectTarget) {
		this.connnectTarget = connnectTarget;
	}

	@Override
	public String getSQL(ITableSql tsql) {
		boolean format = tsql.isFormat();
		String s = "";
		if (type == TYPE_INNER) {
			s = s + "INNER JOIN";
		} else if (type == TYPE_LEFT) {
			s = s + "LEFT JOIN";
		}
		if (format) {
			s = s + "\r\n\t";
		} else {
			s = s + " ";
		}
		s = s + this.connnectTarget.getSQL(tsql);
		if (format) {
			s = s + "\r\n";
		} else {
			s = s + " ";
		}

		String asLogic = this.connnectTarget.getAsParamLogicName();
		if (asLogic != null) {
			s = s + " AS " + asLogic + " ";
		} else {
			// BVariable param = this.connnectTarget.getModel(tsql.getEditors());
			// if (param != null) {
			// s = s + " " + param.getLogicName() + " ";
			// }
		}

		s = s + "ON";

		List<BTableValueNode> list = this.getRecords();
		if (list.size() > 1) {

			if (format) {
				s = s + "\r\n(";
			} else {
				s = s + "(";
			}
		}
		String sql = this.getSqlItemValue("", " AND ", list, tsql);
		if (sql.indexOf("\r\n") >= 0) {
			sql = sql.substring(2);
		}

		sql = "\t" + sql;
		s = s + sql;

		if (list.size() > 1) {

			if (format) {
				s = s + "\r\n)";
			} else {
				s = s + ")";
			}
		}

		return s;
	}

	@Override
	public String getSQLExp(ITableSql tsql) {
		boolean format = tsql.isFormat();
		String s = "結合条件：";
		if (format) {
			s = s + "\r\n\t";
		} else {
			s = s + " ";
		}
		s = s + this.connnectTarget.getSQLExp(tsql);

		String asName = this.connnectTarget.getAsParamName();
		if (asName != null) {
			s = s + " AS " + asName;
		} else {
			// BVariable param = this.connnectTarget.getModel(tsql.getEditors());
			// if (param != null) {
			// s = s + " " + param.getName();
			// }
		}

		if (type == TYPE_INNER) {
			s = s + "    ----INNER JOIN";
		} else if (type == TYPE_LEFT) {
			s = s + "    ----LEFT JOIN";
		}
		if (format) {
			s = s + "\r\n";
		} else {
			s = s + " ";
		}
		// ON
		s = s + "　";
		if (format) {
			s = s + "\r\n\t";
		} else {
			s = s + " ";
		}

		List<BTableValueNode> list = this.getRecords();

		if (list.size() > 1) {

			if (format) {
				s = s + "\r\n(";
			} else {
				s = s + "(";
			}
		}

		String sql = super.getSqlItemExp("", " AND ", list, tsql);
		if (sql.indexOf("\r\n") >= 0) {
			sql = sql.substring(2);
		}
		sql = "\t" + sql;
		s = s + sql;

		if (list.size() > 1) {

			if (format) {
				s = s + "\r\n)";
			} else {
				s = s + ")";
			}
		}

		return s;

	}

}

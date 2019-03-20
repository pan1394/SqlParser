package com.linkstec.bee.UI.spective.basic.logic.node.table;

import java.util.List;

import com.linkstec.bee.UI.BeeConstants;
import com.linkstec.bee.UI.spective.basic.BasicBook;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants.ProcessType;
import com.linkstec.bee.UI.spective.basic.data.BasicDataModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BLogicEditActions;
import com.linkstec.bee.UI.spective.basic.logic.edit.BPatternModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BPatternSheet;
import com.linkstec.bee.UI.spective.basic.logic.edit.BTableSelectModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BTableSelectSheet;
import com.linkstec.bee.UI.spective.basic.logic.model.table.BSegmentLogic;
import com.linkstec.bee.core.Debug;
import com.linkstec.bee.core.codec.PatternCreatorFactory;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IPatternCreator;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.IExceptionCell;
import com.linkstec.bee.core.fw.basic.ITableObject;
import com.linkstec.bee.core.fw.basic.ITableSegmentCell;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.mxgraph.model.mxGeometry;

public class BTableNesSelectNode extends BTableValueNode implements ITableSegmentCell, ITableObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1179946029878867826L;
	protected BSegmentLogic logic;
	private String asName;
	private int type = 0;

	public static final int TYPE_TABLE_OBJECT = 1;
	public static final int TYPE_VALUE = 2;
	private String editedName = null;

	public BTableNesSelectNode(BPath path) {
		logic = new BSegmentLogic(path, this);
	}

	public String getAsName() {
		return asName;
	}

	public void setAsName(String asName) {
		this.asName = asName;
	}

	@Override
	public void added(BasicLogicSheet sheet) {

		if (sheet instanceof BPatternSheet) {
			BPatternSheet pattern = (BPatternSheet) sheet;
			BPatternModel model = (BPatternModel) pattern.getEditorModel();
			BPath parent = model.getActionPath();
			if (parent != null) {
				this.getLogic().getPath().setParent(parent);
			}
		}

		if (this.getParent() instanceof BTableTargetTablesNode) {
			type = TYPE_TABLE_OBJECT;
			this.setStyle("shape=cylinder;strokeColor=gray;strokeWidth=0.5;align=center;fontColor=white;fillColor="
					+ BeeConstants.ELEGANT_BLUE_COLOR);

			mxGeometry geo = this.getGeometry();
			geo.setWidth(150);
			geo.setHeight(50);
		} else {
			type = TYPE_VALUE;
			this.setStyle(
					"strokeColor=gray;strokeWidth=0.5;fontColor=white;fillColor=" + BeeConstants.ELEGANT_BLUE_COLOR);
			mxGeometry geo = this.getGeometry();
			geo.setWidth(250);
			geo.setHeight(30);
		}
	}

	@Override
	public Object getValue() {
		if (this.editedName != null) {
			return this.editedName;
		}
		if (type == TYPE_TABLE_OBJECT) {
			return "[子SELECT]ダブルクリックし\r\n編集する" + (this.asName == null ? "" : "\r\n" + asName);

		} else if (type == TYPE_VALUE) {
			return "[子SELECT]ダブルクリックし,編集する";
		}
		return super.getValue();
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof String) {
			this.editedName = (String) value;
			BasicBook.changeEditorName(editedName, this.getLogic().getPath());
		}
	}

	@Override
	public void doubleClicked(BasicLogicSheet sheet) {
		BEditorModel model = sheet.getEditorModel();
		if (model instanceof BTableSelectModel) {
			BTableSelectModel select = (BTableSelectModel) model;
			String parentName = select.generaParentName();

			BasicBook book = sheet.findBook();
			if (book != null) {
				if (this.editedName != null) {
					logic.setName(editedName);
				} else {
					logic.setName(sheet.getEditorModel().getName() + "[子SELECT]");
				}
				BTableSelectSheet editor = (BTableSelectSheet) BLogicEditActions.addNewTypeEditor(sheet.getProject(),
						logic.getPath(), book, ProcessType.TYPE_TABLE);
				BEditorModel generated = editor.getEditorModel();
				if (model instanceof BTableSelectModel) {
					BTableSelectModel gen = (BTableSelectModel) generated;
					if (gen.getParentName() == null) {
						gen.setParentName(parentName);
					}
				}

			}
		}
	}

	@Override
	public String getSQL(ITableSql tsql) {
		BTableSelectModel model = null;
		List<BEditorModel> models = tsql.getEditors();
		boolean format = tsql.isFormat();
		for (BEditorModel m : models) {
			if (m instanceof BTableSelectModel) {
				BTableSelectModel select = (BTableSelectModel) m;

				if (select.getActionPath().getUniqueKey() == this.getLogic().getPath().getUniqueKey()) {
					model = select;
					break;
				}
			}
		}
		if (model == null) {
			return "";
		}
		tsql.getInfo().setNesSelect();
		String nes = model.getSQL(tsql);
		String sql = "(" + nes + ")";

		if (format) {
			nes = "\t" + nes;
			nes = nes.replace("\r\n", "\r\n\t");
			sql = "(\r\n" + nes + "\r\n)";
		}

		if (asName != null) {
			sql = sql + " " + asName;
		}
		if (format) {
			sql = sql.replace("\r\n", "\r\n\t");
		}
		return sql;
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
	public List<BInvoker> getParameters() {
		return null;
	}

	@Override
	public BVariable getModel(List<BEditorModel> models) {
		if (this.asName != null) {
			IPatternCreator view = PatternCreatorFactory.createView();
			BVariable var = view.createVariable();
			BasicDataModel bclass = new BasicDataModel(null);
			bclass.setName("子SELECT");
			bclass.setLogicName(asName);
			var.setBClass(bclass);
			var.setLogicName(asName);
			var.setName(asName);
			BTableSelectModel model = null;
			if (models == null) {
				Debug.a();
			}
			for (BEditorModel m : models) {
				if (m instanceof BTableSelectModel) {
					BTableSelectModel select = (BTableSelectModel) m;
					if (select.getActionPath().getUniqueKey() == this.getLogic().getPath().getUniqueKey()) {
						model = select;
						break;
					}
				}
			}
			if (model != null) {
				List<BTableValueNode> targets = model.getSelectTargets();
				for (BTableValueNode value : targets) {
					BAssignment assign = view.createAssignment();
					BParameter left = view.createParameter();
					if (value != null) {
						left.setLogicName(value.getLogicName());
						left.setName(value.getName());
						left.setBClass(CodecUtils.BString());
						assign.setLeft(left);
						bclass.addVar(assign);
					} else {
						Debug.a();
					}
				}
			}
			return var;
		}
		return null;
	}

	@Override
	public String getLogicName() {
		return "SELECT PHASE";
	}

	@Override
	public String getName() {
		return "SELECT PHASE";
	}

	@Override
	public String getSQLExp(ITableSql tsql) {
		BTableSelectModel model = null;
		List<BEditorModel> models = tsql.getEditors();
		boolean format = tsql.isFormat();
		for (BEditorModel m : models) {
			if (m instanceof BTableSelectModel) {
				BTableSelectModel select = (BTableSelectModel) m;
				if (select.getActionPath().getUniqueKey() == this.getLogic().getPath().getUniqueKey()) {
					model = select;
					break;
				}
			}
		}
		if (model == null) {
			return "";
		}
		tsql.getInfo().setNesSelect();
		String sql = "(" + model.getSQLExp(tsql) + ")";
		if (format) {
			sql = "(\r\n" + model.getSQLExp(tsql) + "\r\n)";
		}

		if (asName != null) {
			sql = sql + " " + asName;
		}
		if (format) {
			sql = sql.replace("\r\n", "\r\n\t");
		}
		return sql;
	}

	@Override
	public BParameter getParameter() {
		return null;
	}

}

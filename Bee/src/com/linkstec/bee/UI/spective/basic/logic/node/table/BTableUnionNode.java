package com.linkstec.bee.UI.spective.basic.logic.node.table;

import java.util.List;

import com.linkstec.bee.UI.spective.basic.BasicBook;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants.ProcessType;
import com.linkstec.bee.UI.spective.basic.logic.edit.BLogicEditActions;
import com.linkstec.bee.UI.spective.basic.logic.edit.BPatternModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BPatternSheet;
import com.linkstec.bee.UI.spective.basic.logic.edit.BTableSelectModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BTableSelectSheet;
import com.linkstec.bee.UI.spective.basic.logic.model.table.BSegmentLogic;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.mxgraph.model.mxICell;

public class BTableUnionNode extends BTableGroupNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4823760798398937842L;
	private String editedName;

	public BTableUnionNode(BPath path) {
		super(path);
		// this.remove(0);
		this.setStyle("dashed=false;strokeColor=black;strokeWidth=0.5;spacingLeft=10;fillColor=F0F8FF");
		String title = "UNION(ダブルクリックし、編集してください)";
		this.setValue(title);
		this.setDeletable(true);
		this.setTitle(title);
		this.getGeometry().setHeight(50);
		this.getGeometry().setWidth(850);
	}

	@Override
	public void cellAdded(mxICell cell) {
		cell.removeFromParent();
	}

	public String getTitle() {
		return (String) this.getValue();
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

		BasicBook book = sheet.findBook();
		if (book != null) {
			BPath path = this.getLogic().getPath();
			BSegmentLogic logic = (BSegmentLogic) this.getLogic();
			if (this.editedName == null) {
				logic.setName(sheet.getEditorModel().getName() + "[UNION]");
			} else {
				logic.setName(editedName);
			}

			BTableSelectSheet editor = (BTableSelectSheet) BLogicEditActions.addNewTypeEditor(sheet.getProject(), path,
					book, ProcessType.TYPE_TABLE);

		}
	}

	@Override
	public String getSQL(ITableSql tsql) {
		BPath path = this.getLogic().getPath();
		boolean format = tsql.isFormat();
		BTableSelectModel model = null;
		List<BEditorModel> models = tsql.getEditors();
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
		tsql.getInfo().setUnion();
		String sql = getUnionTitle();
		if (format) {
			sql = "\r\n" + sql + "\r\n";
		} else {
			sql = " " + sql + " ";
		}
		return sql + model.getSQL(tsql);
	}

	@Override
	public String getSQLExp(ITableSql tsql) {
		BPath path = this.getLogic().getPath();
		boolean format = tsql.isFormat();
		BTableSelectModel model = null;
		List<BEditorModel> models = tsql.getEditors();
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
		tsql.getInfo().setUnion();
		String sql = getUnionTitle();
		if (format) {
			sql = "\r\n" + sql + "\r\n";
		} else {
			sql = " " + sql + " ";
		}
		return sql + model.getSQLExp(tsql);
	}

	@Override
	public void resized(BasicLogicSheet sheet) {

	}

	@Override
	public int getSQLPriority() {
		return 100;
	}

	protected String getUnionTitle() {
		return "UNION";
	}
}

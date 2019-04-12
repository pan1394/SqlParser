package com.linkstec.bee.UI.spective.basic.logic.node.table;

import com.linkstec.bee.UI.spective.basic.BasicBook;
import com.linkstec.bee.UI.spective.basic.BasicLogicSheet;
import com.linkstec.bee.UI.spective.basic.config.model.ModelConstants.ProcessType;
import com.linkstec.bee.UI.spective.basic.logic.edit.BLogicEditActions;
import com.linkstec.bee.UI.spective.basic.logic.edit.BTableSelectModel;
import com.linkstec.bee.UI.spective.basic.logic.edit.BTableSelectSheet;
import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.editor.BEditorModel;

public class BTableWithSelectNode extends BTableNesSelectNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6097217343568262308L;

	public BTableWithSelectNode(BPath path) {
		super(path);
	}

	@Override
	public Object getValue() {
		String asName = this.getAsName();
		if (asName == null) {
			return "[WITH]ダブルクリックし,編集する";
		} else {
			String name = this.getAsParamName();
			if (name != null) {
				asName = name;
			}
			return "WITH " + asName + "\r\nダブルクリックし,編集する";
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

				String name = this.getAsName();
				if (this.getAsParamName() != null) {
					name = this.getAsParamName();
				}

				logic.setName(sheet.getEditorModel().getName() + "[WITH " + name + "]");

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

	protected String getHeader() {
		String asNameParamter = this.getAsParamLogicName();
		if (asNameParamter != null) {
			return asNameParamter + " AS ";
		} else {
			String asName = this.getAsName();
			if (asName != null) {
				return asName + " AS ";
			}
		}
		return "";
	}

	protected String getHeaderExp() {
		String asNameParamter = this.getAsParamName();
		if (asNameParamter != null) {
			return asNameParamter + " AS ";
		} else {
			String asName = this.getAsName();
			if (asName != null) {
				return asName + " AS ";
			}
		}
		return "";
	}

	protected String getFooter(String sql) {
		return "";
	}

	protected String getFooterExp(String sql) {
		return "";
	}

}

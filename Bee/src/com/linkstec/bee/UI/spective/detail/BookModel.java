package com.linkstec.bee.UI.spective.detail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.spective.detail.action.BeeActions;
import com.linkstec.bee.UI.spective.detail.logic.BeeModel;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BModule;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BEditorModel;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.editor.BWorkSpace;

public class BookModel implements BModule, BEditorModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7238800802375479254L;

	private List<BEditorModel> list = new ArrayList<BEditorModel>();
	private String name;
	private String logicName;
	private boolean modified;

	public List<BEditorModel> getList() {
		return list;
	}

	public void setList(List<BEditorModel> list) {
		this.list = list;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogicName() {
		return logicName;
	}

	public void setLogicName(String logicName) {
		this.logicName = logicName;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}

	public boolean isModfied() {
		return this.modified;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public List<BClass> getClassList() {
		List<BClass> list = new ArrayList<BClass>();
		List<BEditorModel> models = this.getList();

		for (BEditorModel model : models) {
			if (!model.isAnonymous()) {
				if (model instanceof BeeModel) {
					BeeModel bee = (BeeModel) model;

					if (bee.getParentView() == null) {
						list.add((BClass) model);
					}
				} else {
					list.add((BClass) model);
				}
			}
		}

		return list;

	}

	public EditorBook getBook(BookModel model, BProject project, File file, BWorkSpace space) {
		EditorBook book = BeeActions.getBookByModel(model, project, space);
		book.setFile(file);
		return book;
	}

	@Override
	public BEditor getEditor(BProject project, File file, BWorkSpace space) {

		return this.getBook(this, project, file, space);
	}

	@Override
	public boolean isAnonymous() {
		return false;
	}

	@Override
	public Object doSearch(String keyword) {
		return null;
	}

	@Override
	public BEditor getSheet(BProject project) {

		return null;
	}
}

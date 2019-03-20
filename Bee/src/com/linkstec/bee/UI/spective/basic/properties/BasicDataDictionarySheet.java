package com.linkstec.bee.UI.spective.basic.properties;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.BasicBook;
import com.linkstec.bee.UI.spective.basic.BasicBookModel;
import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.UI.spective.basic.data.BasicComponentModel;
import com.linkstec.bee.UI.spective.basic.data.BasicDataModel;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BAssignment;

public class BasicDataDictionarySheet extends BasicDataDictionary {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6068278840657007264L;

	public BasicDataDictionarySheet(BasicDataDictionaryModel model, BProject project, SubSystem sub) {
		super(model, project, sub);

	}

	@Override
	public void onSelected() {
		this.deleteAllModelData();
		BasicBook book = this.findBook();
		if (book != null) {
			BasicBookModel bm = book.getBookModel();

			this.makeDict(book);
			List<BClass> clses = bm.getDatas();
			for (BClass bclass : clses) {
				if (bclass.isData()) {

					this.addData(bclass);

				}
			}
		}

		super.onSelected();
	}

	private void makeDict(BasicBook book) {

		BasicDataModel model = this.getModel();
		model.clearBlank();
		List<BAssignment> exists = model.getVariables();

		Hashtable<String, BasicComponentModel> list = book.getAllComponents();
		Enumeration<String> keys = list.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			BasicComponentModel b = list.get(key);
			List<BAssignment> vars = b.getVariables();
			for (BAssignment var : vars) {
				String left = var.getLeft().getLogicName();
				boolean added = false;
				for (BAssignment exsit : exists) {
					String exLeft = exsit.getLeft().getLogicName();
					if (left.equals(exLeft)) {
						added = true;
						break;
					}
				}
				if (!added) {
					exists.add(var);
					model.addVariable((BAssignment) var.cloneAll(), false);
				}
			}

		}
		model.add100Blank();
		this.table.updateUI();
	}

	@Override
	public File save() {
		this.setModified(false);
		BasicBook book = this.findBook();
		if (book != null) {
			this.beforeModelSave(false);
			return book.save();
		} else {
			return null;
		}
	}

	@Override
	public boolean tabCloseable() {
		return false;
	}

}

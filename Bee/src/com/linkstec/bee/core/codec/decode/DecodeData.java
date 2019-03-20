package com.linkstec.bee.core.codec.decode;

import com.linkstec.bee.UI.spective.detail.BookModel;
import com.linkstec.bee.UI.spective.detail.data.BeeDataModel;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.editor.BProject;
import com.sun.source.tree.LineMap;
import com.sun.source.util.DocTrees;

public class DecodeData extends DecodeSource {

	private boolean initialized = false;
	private BeeDataModel data = new BeeDataModel();

	public DecodeData(DecodeGen gen, BProject project, LineMap map, DocTrees trees, SourceInfo sourceInfo, BookModel model) {
		super(gen, project, map, trees, sourceInfo, model);
	}

	@Override
	public BClass getBeeModel() {
		if (!this.initialized) {

			data.setLogicName(sheetModel.getLogicName());
			data.setName(sheetModel.getName());
			data.setSuperClass(sheetModel.getSuperClass());
			data.setAnonymous(sheetModel.isAnonymous());
			data.setImports(sheetModel.getImports());
			data.setModifier(sheetModel.getModifier());
			data.setPackage(sheetModel.getPackage());
			data.setSuperClass(sheetModel.getSuperClass());
			data.setInnerParentClassName(sheetModel.getInnerParentClassName());
			data.setMethods(sheetModel.getMethods());
			data.setConstructors(sheetModel.getConstructors());
			data.getRoot().getChildren().clear();
			data.setVariables(sheetModel.getVariables(), true);
			data.addEditableRows();

			model.getList().remove(sheetModel);
			model.getList().add(0, data);

			initialized = true;
		}
		return data;
	}

	@Override
	public BookModel getBookModel() {
		getBeeModel();
		return this.model;
	}
}

package com.linkstec.bee.UI.spective.basic.properties;

import com.linkstec.bee.UI.spective.basic.BasicSystemModel.SubSystem;
import com.linkstec.bee.UI.spective.basic.config.model.ComponentTypeModel;
import com.linkstec.bee.UI.spective.basic.data.BasicDataModel;
import com.linkstec.bee.core.fw.editor.BEditor;
import com.linkstec.bee.core.fw.editor.BProject;

public class BasicDataDictionaryModel extends BasicDataModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4219146746219589646L;
	private SubSystem sub;

	public BasicDataDictionaryModel(ComponentTypeModel type, SubSystem sub) {
		super(type);
		this.sub = sub;
	}

	@Override
	public BEditor getSheet(BProject project) {

		BasicDataDictionarySheet sheet = new BasicDataDictionarySheet(this, project, this.sub);
		return sheet;
	}

}

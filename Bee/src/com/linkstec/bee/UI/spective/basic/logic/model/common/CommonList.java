package com.linkstec.bee.UI.spective.basic.logic.model.common;

import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.UI.spective.basic.logic.edit.BActionModel;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.editor.BProject;

public class CommonList {
	public static List<BLogic> getList(BActionModel model, BProject project) {
		int layer = model.getActionDepth();
		List<BLogic> list = new ArrayList<BLogic>();
		if (layer < 0) {
			return list;
		}

		return list;
	}
}

package com.linkstec.bee.core.impl.basic;

import com.linkstec.bee.core.fw.basic.BSQLModel;
import com.linkstec.bee.core.fw.basic.BSQLSet;
import com.linkstec.bee.core.fw.logic.BMethod;

public class BSqlSetImpl implements BSQLSet {
	private BSQLModel model;
	private BMethod method;

	public BSQLModel getModel() {
		return model;
	}

	public void setModel(BSQLModel model) {
		this.model = model;
	}

	public BMethod getMethod() {
		return method;
	}

	public void setMethod(BMethod method) {
		this.method = method;
	}

}
